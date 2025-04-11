package com.bloodbank.view;

import com.bloodbank.model.User;
import com.bloodbank.util.SessionManager;
import com.bloodbank.util.UIUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

import static com.bloodbank.Application.*;
import static com.bloodbank.util.UIUtils.*;

public class UserInterface extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTabbedPane tabbedPane;
    private User currentUser;

    public UserInterface() {
        setTitle("Blood Bank Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1024, 768));

        // Initialize UI
        initializeUI();
    }

    private void initializeUI() {
        // Set up card layout for switching between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);

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
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[30%][70%]"));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Header banner panel
        JPanel bannerPanel = new JPanel(new MigLayout("fill, insets 40 60 40 60", "[center]", "[center]"));
        bannerPanel.setBackground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("Blood Bank Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Donate Blood, Save Lives");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        bannerPanel.add(titleLabel, "wrap, center");
        bannerPanel.add(subtitleLabel, "center");
        
        // Options panel
        JPanel optionsPanel = new JPanel(new MigLayout("fill, insets 40", "[center, grow]", "[]20[]20[]20[]"));
        optionsPanel.setBackground(BACKGROUND_COLOR);

        // Blood Request Button
        JButton bloodRequestButton = UIUtils.createSecondaryButton("Request Blood");
        bloodRequestButton.addActionListener(e -> cardLayout.show(mainPanel, "BLOOD_REQUEST"));

        // Admin Login Button
        JButton adminLoginButton = UIUtils.createPrimaryButton("Admin Login");
        adminLoginButton.addActionListener(e -> {
            LoginDialog adminLoginDialog = new LoginDialog(this, "admin");
            adminLoginDialog.setVisible(true);
            if (adminLoginDialog.isLoginSuccessful()) {
                currentUser = SessionManager.getInstance().getCurrentUser();
                showMainInterface();
            }
        });

        // Donor Login Button
        JButton donorLoginButton = UIUtils.createPrimaryButton("Donor Login");
        donorLoginButton.addActionListener(e -> {
            LoginDialog donorLoginDialog = new LoginDialog(this, "donor");
            donorLoginDialog.setVisible(true);
            if (donorLoginDialog.isLoginSuccessful()) {
                currentUser = SessionManager.getInstance().getCurrentUser();
                showMainInterface();
            }
        });

        // Donor Register Button
        JButton donorRegisterButton = UIUtils.createSuccessButton("Donor Registration");
        donorRegisterButton.addActionListener(e -> cardLayout.show(mainPanel, "DONOR_REGISTER"));

        optionsPanel.add(bloodRequestButton, "width 300!, height 50!, wrap");
        optionsPanel.add(adminLoginButton, "width 300!, height 50!, wrap");
        optionsPanel.add(donorLoginButton, "width 300!, height 50!, wrap");
        optionsPanel.add(donorRegisterButton, "width 300!, height 50!");

        panel.add(bannerPanel, "grow, wrap");
        panel.add(optionsPanel, "grow");

        return panel;
    }

    private void showMainInterface() {
        // Create main panel with padding
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 15 25 15 25", "[grow][]"));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(TEXT_COLOR);

        // Create logout button
        JButton logoutButton = UIUtils.createOutlineButton("Logout", SECONDARY_COLOR);
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(welcomeLabel, "left");
        headerPanel.add(logoutButton, "right");

        // Create main content panel with side navigation
        JPanel contentPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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

        contentPanel.add(tabbedPane, "grow");

        // Add components to main container
        containerPanel.add(headerPanel, BorderLayout.NORTH);
        containerPanel.add(contentPanel, BorderLayout.CENTER);

        // Replace the current content with the main interface
        getContentPane().removeAll();
        add(containerPanel);
        revalidate();
        repaint();
    }

    private void logout() {
        // Create custom confirmation dialog
        JDialog confirmDialog = new JDialog(this, "Logout", true);
        confirmDialog.setLayout(new MigLayout("fill, insets 20", "[center]", "[]15[]"));
        confirmDialog.setSize(350, 180);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setResizable(false);
        
        JLabel confirmLabel = new JLabel("Are you sure you want to logout?");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[]10[]"));
        buttonPanel.setOpaque(false);
        
        JButton noButton = UIUtils.createOutlineButton("No", PRIMARY_COLOR);
        JButton yesButton = UIUtils.createPrimaryButton("Yes, Logout");
        
        noButton.addActionListener(e -> confirmDialog.dispose());
        
        yesButton.addActionListener(e -> {
            SessionManager.getInstance().logout();
            currentUser = null;
            getContentPane().removeAll();
            initializeUI();
            revalidate();
            repaint();
            confirmDialog.dispose();
        });
        
        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);
        
        confirmDialog.add(confirmLabel, "wrap");
        confirmDialog.add(buttonPanel);
        confirmDialog.setVisible(true);
    }

    public void showWelcomePanel() {
        cardLayout.show(mainPanel, "WELCOME");
    }
}