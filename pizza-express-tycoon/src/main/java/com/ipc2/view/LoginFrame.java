package com.ipc2.view;

import com.ipc2.dao.UsuarioDAO;
import com.ipc2.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("Login - Pizza Express Tycoon");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnLogin = new JButton("Iniciar Sesion");
        panel.add(new JLabel());
        panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario user = usuarioDAO.login(usuario, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + user.getNombreCompleto());

            this.dispose();

            switch (user.getIdRol()) {
                case 1:
                    new JugadorFrame(user).setVisible(true);
                    break;
                case 2:
                    new AdminTiendaFrame(user.getNombreCompleto()).setVisible(true);
                    break;
                case 3:
                    new SuperAdminFrame(user.getNombreCompleto()).setVisible(true);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Rol no reconocido");
                    new LoginFrame().setVisible(true);
                    break;
            }

        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
        }
    }
}