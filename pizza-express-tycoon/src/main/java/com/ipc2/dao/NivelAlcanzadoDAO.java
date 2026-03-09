package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NivelAlcanzadoDAO {

    public boolean guardarNivelAlcanzado(int idPartida, int idNivel) {
        String sql = "INSERT INTO niveles_alcanzados (id_partida, id_nivel, fecha_registro) VALUES (?, ?, NOW())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idPartida);
            statement.setInt(2, idNivel);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al guardar nivel alcanzado");
            e.printStackTrace();
            return false;
        }
    }
}
