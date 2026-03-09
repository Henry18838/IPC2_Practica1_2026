package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;
import com.ipc2.model.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RolDAO {

    public ArrayList<Rol> obtenerRoles() {
        ArrayList<Rol> listaRoles = new ArrayList<>();
        String sql = "SELECT id_rol, nombre FROM roles";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Rol rol = new Rol();
                rol.setIdRol(resultSet.getInt("id_rol"));
                rol.setNombre(resultSet.getString("nombre"));
                listaRoles.add(rol);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener roles");
            e.printStackTrace();
        }

        return listaRoles;
    }
}