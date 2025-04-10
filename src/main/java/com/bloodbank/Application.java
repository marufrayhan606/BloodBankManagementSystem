package com.bloodbank;

import com.bloodbank.view.UserInterface;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            UserInterface frame = new UserInterface();
            frame.setVisible(true);
        });
    }
} 