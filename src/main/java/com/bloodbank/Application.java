package com.bloodbank;

import com.bloodbank.view.UserInterface;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class Application {
    // Define application color theme
    public static final Color PRIMARY_COLOR = new Color(35, 97, 146);
    public static final Color SECONDARY_COLOR = new Color(220, 53, 69);
    public static final Color ACCENT_COLOR = new Color(40, 167, 69);
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    public static final Color TEXT_COLOR = new Color(33, 37, 41);
    public static final Color LIGHT_TEXT = new Color(173, 181, 189);
    
    public static void main(String[] args) {
        try {
            // Set system properties for better rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            
            // Apply FlatLaf theme
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabSeparatorsFullHeight", true);
            UIManager.put("TabbedPane.selectedBackground", new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 40));
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UserInterface frame = new UserInterface();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}