package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;
import com.ipc2.model.Sucursal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SucursalDAO {

    public boolean insertarSucursal(Sucursal sucursal) {
        String sql = "INSERT INTO sucursales (nombre, direccion, activa) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, sucursal.getNombre());
            statement.setString(2, sucursal.getDireccion());
            statement.setBoolean(3, sucursal.isActiva());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar sucursal");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Sucursal> obtenerSucursales() {
        ArrayList<Sucursal> listaSucursales = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre, direccion, activa FROM sucursales";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setIdSucursal(resultSet.getInt("id_sucursal"));
                sucursal.setNombre(resultSet.getString("nombre"));
                sucursal.setDireccion(resultSet.getString("direccion"));
                sucursal.setActiva(resultSet.getBoolean("activa"));

                listaSucursales.add(sucursal);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener sucursales");
            e.printStackTrace();
        }

        return listaSucursales;
    }
}