package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;
import com.ipc2.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductoDAO {

    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, activo, id_sucursal) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, producto.getNombre());
            statement.setString(2, producto.getDescripcion());
            statement.setDouble(3, producto.getPrecio());
            statement.setBoolean(4, producto.isActivo());
            statement.setInt(5, producto.getIdSucursal());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar producto");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Producto> obtenerProductos() {
        ArrayList<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT id_producto, nombre, descripcion, precio, activo, id_sucursal FROM productos";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(resultSet.getInt("id_producto"));
                producto.setNombre(resultSet.getString("nombre"));
                producto.setDescripcion(resultSet.getString("descripcion"));
                producto.setPrecio(resultSet.getDouble("precio"));
                producto.setActivo(resultSet.getBoolean("activo"));
                producto.setIdSucursal(resultSet.getInt("id_sucursal"));

                listaProductos.add(producto);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener productos");
            e.printStackTrace();
        }

        return listaProductos;
    }

    public ArrayList<Producto> obtenerProductosActivosPorSucursal(int idSucursal) {
        ArrayList<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT id_producto, nombre, descripcion, precio, activo, id_sucursal " +
                     "FROM productos WHERE activo = true AND id_sucursal = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idSucursal);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Producto producto = new Producto();
                    producto.setIdProducto(resultSet.getInt("id_producto"));
                    producto.setNombre(resultSet.getString("nombre"));
                    producto.setDescripcion(resultSet.getString("descripcion"));
                    producto.setPrecio(resultSet.getDouble("precio"));
                    producto.setActivo(resultSet.getBoolean("activo"));
                    producto.setIdSucursal(resultSet.getInt("id_sucursal"));

                    listaProductos.add(producto);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener productos por sucursal");
            e.printStackTrace();
        }

        return listaProductos;
    }

    public ArrayList<String[]> obtenerProductosConDetalle() {
    ArrayList<String[]> listaProductos = new ArrayList<>();

    String sql = """
            SELECT p.id_producto,
                   p.nombre,
                   p.descripcion,
                   p.precio,
                   p.activo,
                   s.nombre AS sucursal
            FROM productos p
            INNER JOIN sucursales s ON p.id_sucursal = s.id_sucursal
            """;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            String[] fila = new String[6];
            fila[0] = String.valueOf(resultSet.getInt("id_producto"));
            fila[1] = resultSet.getString("nombre");
            fila[2] = resultSet.getString("descripcion");
            fila[3] = String.valueOf(resultSet.getDouble("precio"));
            fila[4] = resultSet.getBoolean("activo") ? "Si" : "No";
            fila[5] = resultSet.getString("sucursal");

            listaProductos.add(fila);
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener productos con detalle");
        e.printStackTrace();
    }

    return listaProductos;
}
}
