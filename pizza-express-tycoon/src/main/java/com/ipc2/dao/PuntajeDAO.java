package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PuntajeDAO {

    public boolean guardarPuntaje(int idPartida, int puntosTotales, int entregados, int cancelados, int noEntregados, int bonificaciones) {
        String sql = "INSERT INTO puntajes (id_partida, puntos_totales, pedidos_entregados, pedidos_cancelados, pedidos_no_entregados, bonificaciones_rapidas) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idPartida);
            statement.setInt(2, puntosTotales);
            statement.setInt(3, entregados);
            statement.setInt(4, cancelados);
            statement.setInt(5, noEntregados);
            statement.setInt(6, bonificaciones);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al guardar puntaje");
            e.printStackTrace();
            return false;
        }
    }
}
