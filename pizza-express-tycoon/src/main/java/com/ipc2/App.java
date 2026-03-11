package com.ipc2;

import com.formdev.flatlaf.FlatDarkLaf;
import com.ipc2.view.LoginFrame;

import javax.swing.UIManager;

public class App {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el tema oscuro");
            e.printStackTrace();
        }

        new LoginFrame().setVisible(true);
    }
}