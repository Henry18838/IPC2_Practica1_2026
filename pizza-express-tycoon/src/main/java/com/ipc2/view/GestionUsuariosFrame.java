package com.ipc2.view;

import com.ipc2.dao.RolDAO;
import com.ipc2.dao.SucursalDAO;
import com.ipc2.dao.UsuarioDAO;
import com.ipc2.model.Rol;
import com.ipc2.model.Sucursal;
import com.ipc2.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class GestionUsuariosFrame extends JFrame {

    private JTextField txtNombreCompleto;
    private JTextField txtNombreUsuario;
    private JPasswordField txtContrasena;
    private JComboBox<Rol> cbRol;
    private JComboBox<Sucursal> cbSucursal;
    private JCheckBox chkActivo;
    private JButton btnGuardar;

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    public GestionUsuariosFrame() {
        setTitle("Gestion de Usuarios");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));

        panelFormulario.add(new JLabel("Nombre completo:"));
        txtNombreCompleto = new JTextField();
        panelFormulario.add(txtNombreCompleto);

        panelFormulario.add(new JLabel("Nombre de usuario:"));
        txtNombreUsuario = new JTextField();
        panelFormulario.add(txtNombreUsuario);

        panelFormulario.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField();
        panelFormulario.add(txtContrasena);

        panelFormulario.add(new JLabel("Rol:"));
        cbRol = new JComboBox<>();
        panelFormulario.add(cbRol);

        panelFormulario.add(new JLabel("Sucursal:"));
        cbSucursal = new JComboBox<>();
        panelFormulario.add(cbSucursal);

        panelFormulario.add(new JLabel("Activo:"));
        chkActivo = new JCheckBox();
        chkActivo.setSelected(true);
        panelFormulario.add(chkActivo);

        btnGuardar = new JButton("Guardar Usuario");

        JPanel panelInferiorFormulario = new JPanel();
        panelInferiorFormulario.add(btnGuardar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelInferiorFormulario, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Usuario");
        modeloTabla.addColumn("Nombre Completo");
        modeloTabla.addColumn("Rol");
        modeloTabla.addColumn("Sucursal");
        modeloTabla.addColumn("Activo");

        tablaUsuarios = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardarUsuario());

        cargarRoles();
        cargarSucursales();
        cargarUsuarios();
    }

    private void cargarRoles() {
        RolDAO rolDAO = new RolDAO();
        ArrayList<Rol> listaRoles = rolDAO.obtenerRoles();

        cbRol.removeAllItems();
        for (Rol rol : listaRoles) {
            cbRol.addItem(rol);
        }
    }

    private void cargarSucursales() {
        SucursalDAO sucursalDAO = new SucursalDAO();
        ArrayList<Sucursal> listaSucursales = sucursalDAO.obtenerSucursales();

        cbSucursal.removeAllItems();
        for (Sucursal sucursal : listaSucursales) {
            cbSucursal.addItem(sucursal);
        }
    }

    private void guardarUsuario() {
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String nombreUsuario = txtNombreUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();
        Rol rolSeleccionado = (Rol) cbRol.getSelectedItem();
        Sucursal sucursalSeleccionada = (Sucursal) cbSucursal.getSelectedItem();
        boolean activo = chkActivo.isSelected();

        if (nombreCompleto.isEmpty() || nombreUsuario.isEmpty() || contrasena.isEmpty()
                || rolSeleccionado == null || sucursalSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setIdRol(rolSeleccionado.getIdRol());
        usuario.setIdSucursal(sucursalSeleccionada.getIdSucursal());
        usuario.setActivo(activo);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean guardado = usuarioDAO.insertarUsuario(usuario, contrasena);

        if (guardado) {
            JOptionPane.showMessageDialog(this, "Usuario guardado correctamente");
            limpiarCampos();
            cargarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar usuario");
        }
    }

   private void cargarUsuarios() {
    modeloTabla.setRowCount(0);

    UsuarioDAO usuarioDAO = new UsuarioDAO();
    ArrayList<String[]> listaUsuarios = usuarioDAO.obtenerUsuariosConDetalle();

    for (String[] fila : listaUsuarios) {
        modeloTabla.addRow(fila);
    }
}

    private void limpiarCampos() {
        txtNombreCompleto.setText("");
        txtNombreUsuario.setText("");
        txtContrasena.setText("");
        chkActivo.setSelected(true);
        if (cbRol.getItemCount() > 0) {
            cbRol.setSelectedIndex(0);
        }
        if (cbSucursal.getItemCount() > 0) {
            cbSucursal.setSelectedIndex(0);
        }
    }
}