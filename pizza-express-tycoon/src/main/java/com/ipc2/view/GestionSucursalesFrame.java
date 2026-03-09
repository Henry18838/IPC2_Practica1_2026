package com.ipc2.view;

import com.ipc2.dao.SucursalDAO;
import com.ipc2.model.Sucursal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class GestionSucursalesFrame extends JFrame {

    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JCheckBox chkActiva;
    private JButton btnGuardar;

    private JTable tablaSucursales;
    private DefaultTableModel modeloTabla;

    public GestionSucursalesFrame() {
        setTitle("Gestion de Sucursales");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Direccion:"));
        txtDireccion = new JTextField();
        panelFormulario.add(txtDireccion);

        panelFormulario.add(new JLabel("Activa:"));
        chkActiva = new JCheckBox();
        chkActiva.setSelected(true);
        panelFormulario.add(chkActiva);

        btnGuardar = new JButton("Guardar Sucursal");
        panelFormulario.add(new JLabel());
        panelFormulario.add(btnGuardar);

        add(panelFormulario, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Direccion");
        modeloTabla.addColumn("Activa");

        tablaSucursales = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaSucursales);

        add(scrollPane, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardarSucursal());

        cargarSucursales();
    }

    private void guardarSucursal() {
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        boolean activa = chkActiva.isSelected();

        if (nombre.isEmpty() || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
            return;
        }

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(nombre);
        sucursal.setDireccion(direccion);
        sucursal.setActiva(activa);

        SucursalDAO sucursalDAO = new SucursalDAO();
        boolean guardado = sucursalDAO.insertarSucursal(sucursal);

        if (guardado) {
            JOptionPane.showMessageDialog(this, "Sucursal guardada correctamente");
            limpiarCampos();
            cargarSucursales();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la sucursal");
        }
    }

    private void cargarSucursales() {
        modeloTabla.setRowCount(0);

        SucursalDAO sucursalDAO = new SucursalDAO();
        ArrayList<Sucursal> listaSucursales = sucursalDAO.obtenerSucursales();

        for (Sucursal sucursal : listaSucursales) {
            modeloTabla.addRow(new Object[]{
                    sucursal.getIdSucursal(),
                    sucursal.getNombre(),
                    sucursal.getDireccion(),
                    sucursal.isActiva() ? "Si" : "No"
            });
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDireccion.setText("");
        chkActiva.setSelected(true);
    }
}