package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;
import com.ipc2.model.Ranking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RankingDAO {

    public ArrayList<Ranking> obtenerRankingGlobal() {
        ArrayList<Ranking> listaRanking = new ArrayList<>();

        String sql = """
                SELECT u.nombre_completo AS jugador,
                       p.puntos_totales,
                       na.id_nivel,
                       s.nombre AS sucursal
                FROM puntajes p
                INNER JOIN partidas pa ON p.id_partida = pa.id_partida
                INNER JOIN usuarios u ON pa.id_jugador = u.id_usuario
                INNER JOIN sucursales s ON pa.id_sucursal = s.id_sucursal
                INNER JOIN niveles_alcanzados na ON pa.id_partida = na.id_partida
                ORDER BY p.puntos_totales DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Ranking ranking = new Ranking();
                ranking.setNombreJugador(resultSet.getString("jugador"));
                ranking.setPuntosTotales(resultSet.getInt("puntos_totales"));
                ranking.setNivelAlcanzado(resultSet.getInt("id_nivel"));
                ranking.setNombreSucursal(resultSet.getString("sucursal"));

                listaRanking.add(ranking);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener ranking global");
            e.printStackTrace();
        }

        return listaRanking;
    }
}