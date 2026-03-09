package com.ipc2.view;

import com.ipc2.dao.RankingDAO;
import com.ipc2.model.Ranking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class RankingFrame extends JFrame {

    private JTable tablaRanking;
    private DefaultTableModel modeloTabla;

    public RankingFrame() {
        setTitle("Ranking Global");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Ranking Global de Jugadores", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Posición");
        modeloTabla.addColumn("Jugador");
        modeloTabla.addColumn("Puntaje");
        modeloTabla.addColumn("Nivel");
        modeloTabla.addColumn("Sucursal");

        tablaRanking = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaRanking);
        add(scrollPane, BorderLayout.CENTER);

        cargarRanking();
    }

    private void cargarRanking() {
        modeloTabla.setRowCount(0);

        RankingDAO rankingDAO = new RankingDAO();
        ArrayList<Ranking> listaRanking = rankingDAO.obtenerRankingGlobal();

        int posicion = 1;
        for (Ranking ranking : listaRanking) {
            modeloTabla.addRow(new Object[]{
                    posicion,
                    ranking.getNombreJugador(),
                    ranking.getPuntosTotales(),
                    ranking.getNivelAlcanzado(),
                    ranking.getNombreSucursal()
            });
            posicion++;
        }
    }
}