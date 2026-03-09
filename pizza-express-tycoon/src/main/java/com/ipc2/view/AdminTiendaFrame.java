package com.ipc2.view;

import javax.swing.*;
import java.awt.*;

public class AdminTiendaFrame extends JFrame {

    private JButton btnProductos;
    private JButton btnCerrarSesion;

    public AdminTiendaFrame(String nombreUsuario) {
        setTitle("Menu Administrador de Tienda");
        setSize(500, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JLabel lblBienvenida = new JLabel("Bienvenido Administrador de Tienda: " + nombreUsuario, SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(lblBienvenida, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 10, 10));

        btnProductos = new JButton("Gestionar Productos");
        btnCerrarSesion = new JButton("Cerrar Sesion");

        panelBotones.add(btnProductos);
        panelBotones.add(btnCerrarSesion);

        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        add(panelPrincipal);

        btnProductos.addActionListener(e -> new GestionProductosFrame().setVisible(true));

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}