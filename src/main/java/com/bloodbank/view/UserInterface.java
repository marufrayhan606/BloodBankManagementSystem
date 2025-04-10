package com.bloodbank.view;

import com.bloodbank.model.User;
import com.bloodbank.util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class UserInterface extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTabbedPane tabbedPane;
    private User currentUser;

    public UserInterface() {
        setTitle("Blood Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Initialize UI
        initializeUI();
    }

    private void initializeUI() {
        // Set up card layout for switching between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create welcome panel
        JPanel welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, "WELCOME");

        // Create blood request panel (public mode)
        BloodRequestPanel bloodRequestPanel = new BloodRequestPanel(null);
        mainPanel.add(bloodRequestPanel, "BLOOD_REQUEST");

        // Create donor registration panel
        DonorRegistrationPanel donorRegistrationPanel = new DonorRegistrationPanel(this);
        mainPanel.add(donorRegistrationPanel, "DONOR_REGISTER");

        add(mainPanel);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Welcome to Blood Bank Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Center panel with options
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Blood Request Button
        JButton bloodRequestButton = new JButton("Request Blood");
        bloodRequestButton.setBackground(new Color(231, 76, 60));
        bloodRequestButton.setForeground(Color.WHITE);
        bloodRequestButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bloodRequestButton.setPreferredSize(new Dimension(300, 50));
        bloodRequestButton.addActionListener(e -> cardLayout.show(mainPanel, "BLOOD_REQUEST"));

        // Admin Login Button
        JButton adminLoginButton = new JButton("Admin Login");
        adminLoginButton.setBackground(new Color(52, 152, 219));
        adminLoginButton.setForeground(Color.WHITE);
        adminLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        adminLoginButton.setPreferredSize(new Dimension(300, 50));
        adminLoginButton.addActionListener(e -> {
            LoginDialog adminLoginDialog = new LoginDialog(this, "admin");
            adminLoginDialog.setVisible(true);
            if (adminLoginDialog.isLoginSuccessful()) {
                currentUser = SessionManager.getInstance().getCurrentUser();
                showMainInterface();
            }
        });

        // Donor Login Button
        JButton donorLoginButton = new JButton("Donor Login");
        donorLoginButton.setBackground(new Color(46, 204, 113));
        donorLoginButton.setForeground(Color.WHITE);
        donorLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        donorLoginButton.setPreferredSize(new Dimension(300, 50));
        donorLoginButton.addActionListener(e -> {
            LoginDialog donorLoginDialog = new LoginDialog(this, "donor");
            donorLoginDialog.setVisible(true);
            if (donorLoginDialog.isLoginSuccessful()) {
                currentUser = SessionManager.getInstance().getCurrentUser();
                showMainInterface();
            }
        });

        // Donor Register Button
        JButton donorRegisterButton = new JButton("Donor Registration");
        donorRegisterButton.setBackground(new Color(155, 89, 182));
        donorRegisterButton.setForeground(Color.WHITE);
        donorRegisterButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        donorRegisterButton.setPreferredSize(new Dimension(300, 50));
        donorRegisterButton.addActionListener(e -> cardLayout.show(mainPanel, "DONOR_REGISTER"));

        // Add buttons to center panel
        gbc.gridy = 0;
        centerPanel.add(bloodRequestButton, gbc);
        gbc.gridy = 1;
        centerPanel.add(adminLoginButton, gbc);
        gbc.gridy = 2;
        centerPanel.add(donorLoginButton, gbc);
        gbc.gridy = 3;
        centerPanel.add(donorRegisterButton, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showMainInterface() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // Create logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Add panels based on user role
        if (currentUser.getRole().equals("admin")) {
            tabbedPane.addTab("Dashboard", new DashboardPanel(currentUser));
            tabbedPane.addTab("Donors", new DonorPanel());
            tabbedPane.addTab("Blood Stock", new BloodStockPanel());
            tabbedPane.addTab("Donations", new DonationPanel(currentUser));
            tabbedPane.addTab("Blood Requests", new BloodRequestPanel(currentUser));
        } else if (currentUser.getRole().equals("donor")) {
            tabbedPane.addTab("Dashboard", new DashboardPanel(currentUser));
            tabbedPane.addTab("My Profile", new DonorProfilePanel(currentUser));
            tabbedPane.addTab("My Donations", new DonationHistoryPanel(currentUser));
        }

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Replace the current content with the main interface
        getContentPane().removeAll();
        add(mainPanel);
        revalidate();
        repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().logout();
            currentUser = null;
            getContentPane().removeAll();
            initializeUI();
            revalidate();
            repaint();
        }
    }

    public void showWelcomePanel() {
        cardLayout.show(mainPanel, "WELCOME");
    }
} 