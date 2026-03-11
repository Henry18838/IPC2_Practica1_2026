package com.ipc2.view;

import com.ipc2.dao.*;
import com.ipc2.model.Pedido;
import com.ipc2.model.Producto;
import com.ipc2.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class JugadorFrame extends JFrame {

    private final Usuario usuario;

    private JLabel lblBienvenida;
    private JLabel lblEstadoPartida;
    private JLabel lblPuntaje;
    private JLabel lblNivel;
    private JLabel lblTiempoPartida;
    private JLabel lblInfoPedidos;

    private JButton btnIniciarPartida;
    private JButton btnAvanzarEstado;
    private JButton btnCancelarPedido;
    private JButton btnFinalizarPartida;
    private JButton btnCerrarSesion;

    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;

    private int idPartidaActual = -1;
    private int puntaje = 0;
    private int pedidosEntregados = 0;
    private int pedidosCancelados = 0;
    private int pedidosNoEntregados = 0;
    private int bonificaciones = 0;
    private int nivelActual = 1;
    private int tiempoPartidaRestante = 180;

    private Timer timerPartida;
    private Timer timerGeneracionPedidos;

    private final Random random = new Random();

    private static final int MAX_PEDIDOS_ACTIVOS = 5;
    private static final int INTERVALO_GENERACION_MS = 8000;

    public JugadorFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Modo Jugador");
        setSize(1100, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new GridLayout(3, 2));

        lblBienvenida = new JLabel("Bienvenido Jugador: " + usuario.getNombreCompleto());
        lblEstadoPartida = new JLabel("Partida: No iniciada");
        lblPuntaje = new JLabel("Puntaje: 0");
        lblNivel = new JLabel("Nivel: 1");
        lblTiempoPartida = new JLabel("Tiempo de partida: 180 s");
        lblInfoPedidos = new JLabel("Pedidos automáticos activados");

        panelSuperior.add(lblBienvenida);
        panelSuperior.add(lblEstadoPartida);
        panelSuperior.add(lblPuntaje);
        panelSuperior.add(lblNivel);
        panelSuperior.add(lblTiempoPartida);
        panelSuperior.add(lblInfoPedidos);

        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout());

        btnIniciarPartida = new JButton("Iniciar Partida");
        btnAvanzarEstado = new JButton("Avanzar Estado");
        btnCancelarPedido = new JButton("Cancelar Pedido");
        btnFinalizarPartida = new JButton("Finalizar Partida");
        btnCerrarSesion = new JButton("Cerrar Sesion");

        panelBotones.add(btnIniciarPartida);
        panelBotones.add(btnAvanzarEstado);
        panelBotones.add(btnCancelarPedido);
        panelBotones.add(btnFinalizarPartida);
        panelBotones.add(btnCerrarSesion);

        add(panelBotones, BorderLayout.SOUTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID Pedido");
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Estado");
        modeloTabla.addColumn("Tiempo Limite");
        modeloTabla.addColumn("Tiempo Restante");

        tablaPedidos = new JTable(modeloTabla);
        add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        btnIniciarPartida.addActionListener(e -> iniciarPartida());
        btnAvanzarEstado.addActionListener(e -> avanzarEstadoPedido());
        btnCancelarPedido.addActionListener(e -> cancelarPedido());
        btnFinalizarPartida.addActionListener(e -> finalizarPartidaManual());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    private void iniciarPartida() {
        if (idPartidaActual != -1) {
            JOptionPane.showMessageDialog(this, "Ya hay una partida en curso");
            return;
        }

        PartidaDAO partidaDAO = new PartidaDAO();
        idPartidaActual = partidaDAO.iniciarPartida(usuario.getIdUsuario(), usuario.getIdSucursal(), 180);

        if (idPartidaActual != -1) {
            puntaje = 0;
            pedidosEntregados = 0;
            pedidosCancelados = 0;
            pedidosNoEntregados = 0;
            bonificaciones = 0;
            nivelActual = 1;
            tiempoPartidaRestante = 180;
            modeloTabla.setRowCount(0);

            actualizarEtiquetas();
            lblEstadoPartida.setText("Partida: En curso (ID " + idPartidaActual + ")");

            iniciarTimerPartida();
            iniciarGeneracionAutomaticaPedidos();

            JOptionPane.showMessageDialog(this, "Partida iniciada correctamente");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo iniciar la partida");
        }
    }

    private void iniciarTimerPartida() {
        if (timerPartida != null && timerPartida.isRunning()) {
            timerPartida.stop();
        }

        timerPartida = new Timer(1000, e -> {
            tiempoPartidaRestante--;
            lblTiempoPartida.setText("Tiempo de partida: " + tiempoPartidaRestante + " s");

            actualizarTemporizadoresPedidos();

            if (tiempoPartidaRestante <= 0) {
                timerPartida.stop();
                finalizarPartidaAutomatica();
            }
        });

        timerPartida.start();
    }

    private void iniciarGeneracionAutomaticaPedidos() {
        if (timerGeneracionPedidos != null && timerGeneracionPedidos.isRunning()) {
            timerGeneracionPedidos.stop();
        }

        timerGeneracionPedidos = new Timer(INTERVALO_GENERACION_MS, e -> {
            if (idPartidaActual == -1) {
                return;
            }

            if (contarPedidosActivos() >= MAX_PEDIDOS_ACTIVOS) {
                return;
            }

            generarPedidoAutomatico();
        });

        timerGeneracionPedidos.start();
    }

    private int contarPedidosActivos() {
        int activos = 0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String estado = modeloTabla.getValueAt(i, 2).toString();

            if (!esEstadoFinal(estado)) {
                activos++;
            }
        }

        return activos;
    }

    private void generarPedidoAutomatico() {
        ProductoDAO productoDAO = new ProductoDAO();
        ArrayList<Producto> productos = productoDAO.obtenerProductosActivosPorSucursal(usuario.getIdSucursal());

        if (productos.isEmpty()) {
            return;
        }

        Producto productoAleatorio = productos.get(random.nextInt(productos.size()));
        int tiempo = obtenerTiempoPorNivel();

        Pedido pedido = new Pedido();
        pedido.setIdPartida(idPartidaActual);
        pedido.setEstadoActual("RECIBIDA");
        pedido.setTiempoLimiteSegundos(tiempo);
        pedido.setTiempoRestanteSegundos(tiempo);

        PedidoDAO pedidoDAO = new PedidoDAO();
        int idPedido = pedidoDAO.insertarPedido(pedido, productoAleatorio.getIdProducto());

        if (idPedido != -1) {
            modeloTabla.addRow(new Object[]{
                    idPedido,
                    productoAleatorio.getNombre(),
                    "RECIBIDA",
                    tiempo,
                    tiempo
            });
        }
    }

    private void actualizarTemporizadoresPedidos() {
        PedidoDAO pedidoDAO = new PedidoDAO();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String estado = modeloTabla.getValueAt(i, 2).toString();

            if (esEstadoFinal(estado)) {
                continue;
            }

            int idPedido = (int) modeloTabla.getValueAt(i, 0);
            int tiempoRestante = (int) modeloTabla.getValueAt(i, 4);

            tiempoRestante--;
            if (tiempoRestante < 0) {
                tiempoRestante = 0;
            }

            modeloTabla.setValueAt(tiempoRestante, i, 4);
            pedidoDAO.actualizarTiempoRestantePedido(idPedido, tiempoRestante);

            if (tiempoRestante == 0) {
                boolean actualizado = pedidoDAO.actualizarEstadoPedido(idPedido, estado, "NO_ENTREGADO");
                if (actualizado) {
                    modeloTabla.setValueAt("NO_ENTREGADO", i, 2);
                    puntaje -= 50;
                    pedidosNoEntregados++;
                    actualizarEtiquetas();
                }
            }
        }
    }

    private void avanzarEstadoPedido() {
        int filaSeleccionada = tablaPedidos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido");
            return;
        }

        int idPedido = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = modeloTabla.getValueAt(filaSeleccionada, 2).toString();

        if (esEstadoFinal(estadoActual)) {
            JOptionPane.showMessageDialog(this, "Este pedido ya no se puede modificar");
            return;
        }

        String nuevoEstado = obtenerSiguienteEstado(estadoActual);

        if (nuevoEstado == null) {
            JOptionPane.showMessageDialog(this, "Este pedido no puede avanzar más");
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();
        boolean actualizado = pedidoDAO.actualizarEstadoPedido(idPedido, estadoActual, nuevoEstado);

        if (actualizado) {
            modeloTabla.setValueAt(nuevoEstado, filaSeleccionada, 2);

            if ("ENTREGADA".equals(nuevoEstado)) {
                int tiempoLimite = (int) modeloTabla.getValueAt(filaSeleccionada, 3);
                int tiempoRestante = (int) modeloTabla.getValueAt(filaSeleccionada, 4);

                puntaje += 100;
                pedidosEntregados++;

                if (tiempoRestante >= (tiempoLimite / 2)) {
                    puntaje += 50;
                    bonificaciones++;
                }

                actualizarNivel();
                actualizarEtiquetas();
            }

            JOptionPane.showMessageDialog(this, "Pedido actualizado a: " + nuevoEstado);
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar pedido");
        }
    }

    private void cancelarPedido() {
        int filaSeleccionada = tablaPedidos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pedido");
            return;
        }

        int idPedido = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = modeloTabla.getValueAt(filaSeleccionada, 2).toString();

        if (!estadoActual.equals("RECIBIDA") && !estadoActual.equals("PREPARANDO")) {
            JOptionPane.showMessageDialog(this, "Solo puedes cancelar en RECIBIDA o PREPARANDO");
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();
        boolean actualizado = pedidoDAO.actualizarEstadoPedido(idPedido, estadoActual, "CANCELADA");

        if (actualizado) {
            modeloTabla.setValueAt("CANCELADA", filaSeleccionada, 2);
            puntaje -= 30;
            pedidosCancelados++;
            actualizarEtiquetas();
            JOptionPane.showMessageDialog(this, "Pedido cancelado");
        } else {
            JOptionPane.showMessageDialog(this, "Error al cancelar pedido");
        }
    }

    private void finalizarPartidaManual() {
        if (idPartidaActual == -1) {
            JOptionPane.showMessageDialog(this, "No hay partida activa");
            return;
        }

        if (timerPartida != null && timerPartida.isRunning()) {
            timerPartida.stop();
        }

        if (timerGeneracionPedidos != null && timerGeneracionPedidos.isRunning()) {
            timerGeneracionPedidos.stop();
        }

        finalizarPartidaEnBD("Partida finalizada");
    }

    private void finalizarPartidaAutomatica() {
        if (timerGeneracionPedidos != null && timerGeneracionPedidos.isRunning()) {
            timerGeneracionPedidos.stop();
        }

        marcarPedidosPendientesComoNoEntregados();
        finalizarPartidaEnBD("Tiempo terminado. Partida finalizada automáticamente");
    }

    private void marcarPedidosPendientesComoNoEntregados() {
        PedidoDAO pedidoDAO = new PedidoDAO();

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String estado = modeloTabla.getValueAt(i, 2).toString();

            if (esEstadoFinal(estado)) {
                continue;
            }

            int idPedido = (int) modeloTabla.getValueAt(i, 0);
            boolean actualizado = pedidoDAO.actualizarEstadoPedido(idPedido, estado, "NO_ENTREGADO");

            if (actualizado) {
                modeloTabla.setValueAt("NO_ENTREGADO", i, 2);
                modeloTabla.setValueAt(0, i, 4);
                puntaje -= 50;
                pedidosNoEntregados++;
            }
        }

        actualizarEtiquetas();
    }

    private void finalizarPartidaEnBD(String mensaje) {
        PartidaDAO partidaDAO = new PartidaDAO();
        PuntajeDAO puntajeDAO = new PuntajeDAO();
        NivelAlcanzadoDAO nivelDAO = new NivelAlcanzadoDAO();

        boolean partidaFinalizada = partidaDAO.finalizarPartida(idPartidaActual);
        boolean puntajeGuardado = puntajeDAO.guardarPuntaje(
                idPartidaActual,
                puntaje,
                pedidosEntregados,
                pedidosCancelados,
                pedidosNoEntregados,
                bonificaciones
        );
        boolean nivelGuardado = nivelDAO.guardarNivelAlcanzado(idPartidaActual, nivelActual);

        if (partidaFinalizada && puntajeGuardado && nivelGuardado) {
            JOptionPane.showMessageDialog(this, mensaje);

            idPartidaActual = -1;
            lblEstadoPartida.setText("Partida: No iniciada");
            lblTiempoPartida.setText("Tiempo de partida: 180 s");

            FinPartidaFrame finPartidaFrame = new FinPartidaFrame(
                    puntaje,
                    pedidosEntregados,
                    pedidosCancelados,
                    pedidosNoEntregados,
                    bonificaciones,
                    nivelActual
            );
            finPartidaFrame.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Hubo un problema al finalizar la partida");
        }
    }

    private void cerrarSesion() {
        if (timerPartida != null && timerPartida.isRunning()) {
            timerPartida.stop();
        }

        if (timerGeneracionPedidos != null && timerGeneracionPedidos.isRunning()) {
            timerGeneracionPedidos.stop();
        }

        dispose();
        new LoginFrame().setVisible(true);
    }

    private boolean esEstadoFinal(String estado) {
        return estado.equals("ENTREGADA") || estado.equals("CANCELADA") || estado.equals("NO_ENTREGADO");
    }

    private String obtenerSiguienteEstado(String estadoActual) {
        switch (estadoActual) {
            case "RECIBIDA":
                return "PREPARANDO";
            case "PREPARANDO":
                return "EN_HORNO";
            case "EN_HORNO":
                return "ENTREGADA";
            default:
                return null;
        }
    }

    private int obtenerTiempoPorNivel() {
        switch (nivelActual) {
            case 2:
                return 50;
            case 3:
                return 40;
            default:
                return 60;
        }
    }

    private void actualizarNivel() {
        if (puntaje >= 1000) {
            nivelActual = 3;
        } else if (puntaje >= 500) {
            nivelActual = 2;
        } else {
            nivelActual = 1;
        }
    }

    private void actualizarEtiquetas() {
        lblPuntaje.setText("Puntaje: " + puntaje);
        lblNivel.setText("Nivel: " + nivelActual);
        lblTiempoPartida.setText("Tiempo de partida: " + tiempoPartidaRestante + " s");
    }
}