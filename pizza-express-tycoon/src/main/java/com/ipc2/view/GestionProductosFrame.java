package com.ipc2.view;

import com.ipc2.dao.ProductoDAO;
import com.ipc2.dao.SucursalDAO;
import com.ipc2.model.Producto;
import com.ipc2.model.Sucursal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class GestionProductosFrame extends JFrame {

    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtPrecio;
    private JComboBox<Sucursal> cbSucursal;
    private JCheckBox chkActivo;
    private JButton btnGuardar;

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    public GestionProductosFrame() {
        setTitle("Gestion de Productos");
        setSize(850, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Descripcion:"));
        txtDescripcion = new JTextField();
        panelFormulario.add(txtDescripcion);

        panelFormulario.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Sucursal:"));
        cbSucursal = new JComboBox<>();
        panelFormulario.add(cbSucursal);

        panelFormulario.add(new JLabel("Activo:"));
        chkActivo = new JCheckBox();
        chkActivo.setSelected(true);
        panelFormulario.add(chkActivo);

        btnGuardar = new JButton("Guardar Producto");

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnGuardar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBoton, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Descripcion");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Activo");
        modeloTabla.addColumn("Sucursal");

        tablaProductos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        add(scrollPane, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardarProducto());

        cargarSucursales();
        cargarProductos();
    }

    private void cargarSucursales() {
        SucursalDAO sucursalDAO = new SucursalDAO();
        ArrayList<Sucursal> listaSucursales = sucursalDAO.obtenerSucursales();

        cbSucursal.removeAllItems();
        for (Sucursal sucursal : listaSucursales) {
            cbSucursal.addItem(sucursal);
        }
    }

    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String precioTexto = txtPrecio.getText().trim();
        Sucursal sucursalSeleccionada = (Sucursal) cbSucursal.getSelectedItem();
        boolean activo = chkActivo.isSelected();

        if (nombre.isEmpty() || descripcion.isEmpty() || precioTexto.isEmpty() || sucursalSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser numerico");
            return;
        }

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setActivo(activo);
        producto.setIdSucursal(sucursalSeleccionada.getIdSucursal());

        ProductoDAO productoDAO = new ProductoDAO();
        boolean guardado = productoDAO.insertarProducto(producto);

        if (guardado) {
            JOptionPane.showMessageDialog(this, "Producto guardado correctamente");
            limpiarCampos();
            cargarProductos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar producto");
        }
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);

        ProductoDAO productoDAO = new ProductoDAO();
        ArrayList<Producto> listaProductos = productoDAO.obtenerProductos();

        for (Producto producto : listaProductos) {
            modeloTabla.addRow(new Object[]{
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.isActivo() ? "Si" : "No",
                    producto.getIdSucursal()
            });
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        chkActivo.setSelected(true);
        if (cbSucursal.getItemCount() > 0) {
            cbSucursal.setSelectedIndex(0);
        }
    }
}