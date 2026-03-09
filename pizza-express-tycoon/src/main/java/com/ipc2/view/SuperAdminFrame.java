package com.ipc2.view;

import javax.swing.*;
import java.awt.*;

public class SuperAdminFrame extends JFrame {

    private JButton btnSucursales;
    private JButton btnUsuarios;
    private JButton btnRanking;
    private JButton btnCerrarSesion;

    public SuperAdminFrame(String nombreUsuario) {
        setTitle("Menu Super Administrador");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JLabel lblTitulo = new JLabel("Bienvenido Super Administrador: " + nombreUsuario, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(4, 1, 10, 10));

        btnSucursales = new JButton("Gestionar Sucursales");
        btnUsuarios = new JButton("Gestionar Usuarios");
        btnRanking = new JButton("Ver Ranking");
        btnCerrarSesion = new JButton("Cerrar Sesion");

        panelBotones.add(btnSucursales);
        panelBotones.add(btnUsuarios);
        panelBotones.add(btnRanking);
        panelBotones.add(btnCerrarSesion);

        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        add(panelPrincipal);

        btnSucursales.addActionListener(e -> new GestionSucursalesFrame().setVisible(true));
        btnUsuarios.addActionListener(e -> new GestionUsuariosFrame().setVisible(true));
        btnRanking.addActionListener(e -> new RankingFrame().setVisible(true));

        btnCerrarSesion.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }
}