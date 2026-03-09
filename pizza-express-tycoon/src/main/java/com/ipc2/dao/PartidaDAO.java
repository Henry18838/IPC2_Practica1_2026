package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;

import java.sql.*;

public class PartidaDAO {

    public int iniciarPartida(int idJugador, int idSucursal, int tiempoTotalSegundos) {
        String sql = "INSERT INTO partidas (id_jugador, id_sucursal, fecha_inicio, tiempo_total_segundos, estado_partida) " +
                     "VALUES (?, ?, NOW(), ?, 'EN_CURSO')";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, idJugador);
            statement.setInt(2, idSucursal);
            statement.setInt(3, tiempoTotalSegundos);

            int filas = statement.executeUpdate();

            if (filas > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al iniciar partida");
            e.printStackTrace();
        }

        return -1;
    }

    public boolean finalizarPartida(int idPartida) {
        String sql = "UPDATE partidas SET fecha_fin = NOW(), estado_partida = 'FINALIZADA' WHERE id_partida = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idPartida);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al finalizar partida");
            e.printStackTrace();
            return false;
        }
    }
}