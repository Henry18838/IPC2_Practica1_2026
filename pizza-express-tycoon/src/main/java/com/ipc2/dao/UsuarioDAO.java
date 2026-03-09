package com.ipc2.dao;

import com.ipc2.database.DatabaseConnection;
import com.ipc2.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioDAO {

    public Usuario login(String nombreUsuario, String contrasena) {
        String sql = "SELECT id_usuario, nombre_usuario, nombre_completo, id_rol, id_sucursal, activo " +
                     "FROM usuarios WHERE nombre_usuario = ? AND contrasena = ? AND activo = true";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nombreUsuario);
            statement.setString(2, contrasena);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(resultSet.getInt("id_usuario"));
                    usuario.setNombreUsuario(resultSet.getString("nombre_usuario"));
                    usuario.setNombreCompleto(resultSet.getString("nombre_completo"));
                    usuario.setIdRol(resultSet.getInt("id_rol"));
                    usuario.setIdSucursal(resultSet.getInt("id_sucursal"));
                    usuario.setActivo(resultSet.getBoolean("activo"));
                    return usuario;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al validar login");
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertarUsuario(Usuario usuario, String contrasena) {
        String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, nombre_completo, id_rol, id_sucursal, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usuario.getNombreUsuario());
            statement.setString(2, contrasena);
            statement.setString(3, usuario.getNombreCompleto());
            statement.setInt(4, usuario.getIdRol());
            statement.setInt(5, usuario.getIdSucursal());
            statement.setBoolean(6, usuario.isActivo());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Usuario> obtenerUsuarios() {
        ArrayList<Usuario> listaUsuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre_usuario, nombre_completo, id_rol, id_sucursal, activo FROM usuarios";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultSet.getInt("id_usuario"));
                usuario.setNombreUsuario(resultSet.getString("nombre_usuario"));
                usuario.setNombreCompleto(resultSet.getString("nombre_completo"));
                usuario.setIdRol(resultSet.getInt("id_rol"));
                usuario.setIdSucursal(resultSet.getInt("id_sucursal"));
                usuario.setActivo(resultSet.getBoolean("activo"));

                listaUsuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener usuarios");
            e.printStackTrace();
        }

        return listaUsuarios;
    }
}