package com.ipc2.view;

import javax.swing.*;
import java.awt.*;

public class FinPartidaFrame extends JFrame {

    public FinPartidaFrame(int puntaje, int entregados, int cancelados, int noEntregados, int bonificaciones, int nivelAlcanzado) {
        setTitle("Fin de Partida");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Resumen Final de la Partida", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(6, 1, 10, 10));

        panelCentro.add(new JLabel("Puntaje final: " + puntaje, SwingConstants.CENTER));
        panelCentro.add(new JLabel("Pedidos entregados: " + entregados, SwingConstants.CENTER));
        panelCentro.add(new JLabel("Pedidos cancelados: " + cancelados, SwingConstants.CENTER));
        panelCentro.add(new JLabel("Pedidos no entregados: " + noEntregados, SwingConstants.CENTER));
        panelCentro.add(new JLabel("Bonificaciones: " + bonificaciones, SwingConstants.CENTER));
        panelCentro.add(new JLabel("Nivel alcanzado: " + nivelAlcanzado, SwingConstants.CENTER));

        add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnVolverLogin = new JButton("Volver al Login");
        JButton btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnVolverLogin);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);

        btnVolverLogin.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        btnCerrar.addActionListener(e -> dispose());
    }
}