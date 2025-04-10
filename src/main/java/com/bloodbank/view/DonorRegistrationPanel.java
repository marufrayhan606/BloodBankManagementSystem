package com.bloodbank.view;

import com.bloodbank.dao.DonorDAO;
import com.bloodbank.dao.UserDAO;
import com.bloodbank.model.Donor;
import com.bloodbank.model.User;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class DonorRegistrationPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField nameField;
    private JComboBox<String> bloodGroupField;
    private JTextField dateOfBirthField;
    private JComboBox<String> genderField;
    private JTextField phoneField;
    private JTextField addressField;
    private JFrame parent;

    public DonorRegistrationPanel(JFrame parent) {
        this.parent = parent;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Account Information Section
        JLabel accountLabel = new JLabel("Account Information");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(accountLabel, gbc);
        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Personal Information Section
        JLabel personalLabel = new JLabel("Personal Information");
        personalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(personalLabel, gbc);
        gbc.gridwidth = 1;

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Blood Group
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodGroupField = new JComboBox<>(bloodGroups);
        formPanel.add(bloodGroupField, gbc);

        // Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dateOfBirthField = new JTextField(20);
        formPanel.add(dateOfBirthField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] genders = {"Male", "Female", "Other"};
        genderField = new JComboBox<>(genders);
        formPanel.add(genderField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

        // Add scroll pane for the form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        // Style buttons
        registerButton.setBackground(new Color(39, 174, 96));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        // Add action listeners
        registerButton.addActionListener(e -> register());
        cancelButton.addActionListener(e -> {
            if (parent instanceof UserInterface) {
                ((UserInterface) parent).showWelcomePanel();
            }
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add components to main panel
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void register() {
        // Validate input
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        String bloodGroup = (String) bloodGroupField.getSelectedItem();
        String dateOfBirth = dateOfBirthField.getText().trim();
        String gender = (String) genderField.getSelectedItem();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // Validate required fields
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || 
            name.isEmpty() || dateOfBirth.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if username or email is taken
        UserDAO userDAO = new UserDAO();
        if (userDAO.isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this,
                "Username is already taken",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDAO.isEmailTaken(email)) {
            JOptionPane.showMessageDialog(this,
                "Email is already registered",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole("donor");

        // Register user
        if (userDAO.register(user, password)) {
            // Get the user ID after registration
            User registeredUser = userDAO.authenticate(username, password);
            if (registeredUser != null) {
                // Create donor profile with all information
                Donor donor = new Donor();
                donor.setUserId(registeredUser.getUserId());
                donor.setName(name);
                donor.setBloodGroup(bloodGroup);
                donor.setDateOfBirth(dateOfBirth);
                donor.setGender(gender);
                donor.setPhone(phone);
                donor.setAddress(address);

                DonorDAO donorDAO = new DonorDAO();
                if (donorDAO.addDonor(donor)) {
                    JOptionPane.showMessageDialog(this,
                        "Registration successful! You can now login.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    if (parent instanceof UserInterface) {
                        ((UserInterface) parent).showWelcomePanel();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to create donor profile",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to register user",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 