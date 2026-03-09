package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;
import com.ipc2.model.Pedido;

import java.sql.*;
import java.util.ArrayList;

public class PedidoDAO {

    public int insertarPedido(Pedido pedido, int idProducto) {
        String sqlPedido = "INSERT INTO pedidos (id_partida, estado_actual, tiempo_limite_segundos, tiempo_restante_segundos, fecha_creacion) " +
                           "VALUES (?, ?, ?, ?, NOW())";

        String sqlDetalle = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (?, ?, 1)";
        String sqlHistorial = "INSERT INTO historial_estados (id_pedido, estado_anterior, estado_nuevo, fecha_cambio) VALUES (?, ?, ?, NOW())";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            int idPedidoGenerado = -1;

            try (PreparedStatement statementPedido = connection.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                statementPedido.setInt(1, pedido.getIdPartida());
                statementPedido.setString(2, pedido.getEstadoActual());
                statementPedido.setInt(3, pedido.getTiempoLimiteSegundos());
                statementPedido.setInt(4, pedido.getTiempoRestanteSegundos());

                int filas = statementPedido.executeUpdate();

                if (filas > 0) {
                    try (ResultSet keys = statementPedido.getGeneratedKeys()) {
                        if (keys.next()) {
                            idPedidoGenerado = keys.getInt(1);
                        }
                    }
                }
            }

            if (idPedidoGenerado == -1) {
                connection.rollback();
                return -1;
            }

            try (PreparedStatement statementDetalle = connection.prepareStatement(sqlDetalle)) {
                statementDetalle.setInt(1, idPedidoGenerado);
                statementDetalle.setInt(2, idProducto);
                statementDetalle.executeUpdate();
            }

            try (PreparedStatement statementHistorial = connection.prepareStatement(sqlHistorial)) {
                statementHistorial.setInt(1, idPedidoGenerado);
                statementHistorial.setNull(2, Types.VARCHAR);
                statementHistorial.setString(3, pedido.getEstadoActual());
                statementHistorial.executeUpdate();
            }

            connection.commit();
            return idPedidoGenerado;

        } catch (SQLException e) {
            System.out.println("Error al insertar pedido");
            e.printStackTrace();
            return -1;
        }
    }

    public boolean actualizarEstadoPedido(int idPedido, String estadoAnterior, String nuevoEstado) {
        String sqlPedido = "UPDATE pedidos SET estado_actual = ?, fecha_finalizacion = CASE " +
                           "WHEN ? IN ('ENTREGADA', 'CANCELADA', 'NO_ENTREGADO') THEN NOW() ELSE fecha_finalizacion END " +
                           "WHERE id_pedido = ?";

        String sqlHistorial = "INSERT INTO historial_estados (id_pedido, estado_anterior, estado_nuevo, fecha_cambio) VALUES (?, ?, ?, NOW())";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statementPedido = connection.prepareStatement(sqlPedido)) {
                statementPedido.setString(1, nuevoEstado);
                statementPedido.setString(2, nuevoEstado);
                statementPedido.setInt(3, idPedido);
                statementPedido.executeUpdate();
            }

            try (PreparedStatement statementHistorial = connection.prepareStatement(sqlHistorial)) {
                statementHistorial.setInt(1, idPedido);
                statementHistorial.setString(2, estadoAnterior);
                statementHistorial.setString(3, nuevoEstado);
                statementHistorial.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del pedido");
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarTiempoRestantePedido(int idPedido, int nuevoTiempoRestante) {
        String sql = "UPDATE pedidos SET tiempo_restante_segundos = ? WHERE id_pedido = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, nuevoTiempoRestante);
            statement.setInt(2, idPedido);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar tiempo restante del pedido");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Pedido> obtenerPedidosPorPartida(int idPartida) {
        ArrayList<Pedido> listaPedidos = new ArrayList<>();

        String sql = "SELECT p.id_pedido, p.id_partida, p.estado_actual, p.tiempo_limite_segundos, " +
                     "p.tiempo_restante_segundos, pr.nombre AS nombre_producto " +
                     "FROM pedidos p " +
                     "INNER JOIN detalle_pedido dp ON p.id_pedido = dp.id_pedido " +
                     "INNER JOIN productos pr ON dp.id_producto = pr.id_producto " +
                     "WHERE p.id_partida = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idPartida);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setIdPedido(resultSet.getInt("id_pedido"));
                    pedido.setIdPartida(resultSet.getInt("id_partida"));
                    pedido.setEstadoActual(resultSet.getString("estado_actual"));
                    pedido.setTiempoLimiteSegundos(resultSet.getInt("tiempo_limite_segundos"));
                    pedido.setTiempoRestanteSegundos(resultSet.getInt("tiempo_restante_segundos"));
                    pedido.setNombreProducto(resultSet.getString("nombre_producto"));

                    listaPedidos.add(pedido);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener pedidos");
            e.printStackTrace();
        }

        return listaPedidos;
    }
}