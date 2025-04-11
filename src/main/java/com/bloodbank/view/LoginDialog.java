package com.bloodbank.view;

import com.bloodbank.dao.UserDAO;
import com.bloodbank.model.User;
import com.bloodbank.util.SessionManager;
import com.bloodbank.util.UIUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.bloodbank.Application.*;
import static com.bloodbank.util.UIUtils.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loginSuccessful = false;
    private String userType;

    public LoginDialog(JFrame parent, String userType) {
        super(parent, userType.equals("admin") ? "Admin Login" : "Donor Login", true);
        this.userType = userType;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[40%][60%]"));
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Header panel with title
        JPanel headerPanel = createHeaderPanel();
        
        // Form panel
        JPanel formPanel = createFormPanel();

        add(headerPanel, "growx, wrap");
        add(formPanel, "grow");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 30", "[center]", "[center]"));
        
        // Set background color based on user type
        if (userType.equals("admin")) {
            panel.setBackground(PRIMARY_COLOR);
        } else {
            panel.setBackground(ACCENT_COLOR);
        }
        
        JLabel titleLabel = new JLabel(userType.equals("admin") ? "Admin Login" : "Donor Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Enter your credentials to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        panel.add(titleLabel, "wrap, center");
        panel.add(subtitleLabel, "center");
        
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 30 50 30 50", "[right]20[grow,fill]", "[]15[]15[]25[]"));
        panel.setBackground(Color.WHITE);

        // Username field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            usernameField.getBorder(),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            passwordField.getBorder(),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Add components to panel
        panel.add(userLabel, "");
        panel.add(usernameField, "growx, wrap");
        panel.add(passLabel, "");
        panel.add(passwordField, "growx, wrap");

        // Button panel
        JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = UIUtils.createOutlineButton("Cancel", SECONDARY_COLOR);
        JButton loginButton;
        
        if (userType.equals("admin")) {
            loginButton = UIUtils.createPrimaryButton("Login");
        } else {
            loginButton = UIUtils.createSuccessButton("Login");
        }

        // Add action listeners
        loginButton.addActionListener(e -> login());
        cancelButton.addActionListener(e -> dispose());
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);

        buttonPanel.add(cancelButton, "width 45%");
        buttonPanel.add(loginButton, "width 45%");

        panel.add(buttonPanel, "span 2, growx");

        return panel;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticate(username, password);

        if (user != null && user.getRole().equals(userType)) {
            SessionManager.getInstance().setCurrentUser(user);
            loginSuccessful = true;
            dispose();
        } else {
            // Custom error panel
            JPanel errorPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
            errorPanel.setOpaque(false);
            
            JLabel messageLabel = new JLabel("Invalid username or password");
            messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            errorPanel.add(messageLabel);
            
            JOptionPane.showMessageDialog(this,
                errorPanel,
                "Authentication Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}