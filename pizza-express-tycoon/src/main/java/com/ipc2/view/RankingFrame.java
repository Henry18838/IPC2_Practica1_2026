package com.ipc2.view;

import com.ipc2.dao.RankingDAO;
import com.ipc2.model.Ranking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RankingFrame extends JFrame {

    private JTable tablaRanking;
    private DefaultTableModel modeloTabla;
    private JButton btnExportarCSV;
    private JButton btnCerrar;

    public RankingFrame() {
        setTitle("Ranking Global");
        setSize(750, 450);
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

        JPanel panelBotones = new JPanel(new FlowLayout());

        btnExportarCSV = new JButton("Exportar CSV");
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnExportarCSV);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);

        btnExportarCSV.addActionListener(e -> exportarCSV());
        btnCerrar.addActionListener(e -> dispose());

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

    private void exportarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar ranking en CSV");

        int seleccion = fileChooser.showSaveDialog(this);

        if (seleccion == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();

            if (!rutaArchivo.endsWith(".csv")) {
                rutaArchivo += ".csv";
            }

            try (FileWriter writer = new FileWriter(rutaArchivo)) {
                writer.append("Posicion,Jugador,Puntaje,Nivel,Sucursal\n");

                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    writer.append(modeloTabla.getValueAt(i, 0).toString()).append(",");
                    writer.append(modeloTabla.getValueAt(i, 1).toString()).append(",");
                    writer.append(modeloTabla.getValueAt(i, 2).toString()).append(",");
                    writer.append(modeloTabla.getValueAt(i, 3).toString()).append(",");
                    writer.append(modeloTabla.getValueAt(i, 4).toString()).append("\n");
                }

                JOptionPane.showMessageDialog(this, "Ranking exportado correctamente a CSV");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al exportar el ranking a CSV");
                e.printStackTrace();
            }
        }
    }
}