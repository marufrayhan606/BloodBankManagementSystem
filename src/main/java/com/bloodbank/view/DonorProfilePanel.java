package com.bloodbank.view;

import com.bloodbank.dao.DonorDAO;
import com.bloodbank.model.Donor;
import com.bloodbank.model.User;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DonorProfilePanel extends JPanel {
    private DonorDAO donorDAO;
    private User currentUser;
    private Donor donor;
    private JTextField nameField;
    private JTextField bloodGroupField;
    private JTextField dateOfBirthField;
    private JTextField genderField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField lastDonationField;

    public DonorProfilePanel(User user) {
        this.currentUser = user;
        this.donorDAO = new DonorDAO();
        this.donor = donorDAO.getDonorByUserId(user.getUserId());
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        nameField.setText(donor != null ? donor.getName() : "");
        formPanel.add(nameField, gbc);

        // Blood group field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        bloodGroupField = new JTextField(20);
        bloodGroupField.setText(donor != null ? donor.getBloodGroup() : "");
        formPanel.add(bloodGroupField, gbc);

        // Date of birth field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dateOfBirthField = new JTextField(20);
        dateOfBirthField.setText(donor != null ? donor.getDateOfBirth() : "");
        formPanel.add(dateOfBirthField, gbc);

        // Gender field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        genderField = new JTextField(20);
        genderField.setText(donor != null ? donor.getGender() : "");
        formPanel.add(genderField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        phoneField.setText(donor != null ? donor.getPhone() : "");
        formPanel.add(phoneField, gbc);

        // Address field
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        addressField = new JTextField(20);
        addressField.setText(donor != null ? donor.getAddress() : "");
        formPanel.add(addressField, gbc);

        // Last donation field
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Last Donation:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        lastDonationField = new JTextField(20);
        lastDonationField.setText(donor != null ? donor.getLastDonationDate() : "");
        lastDonationField.setEditable(false);
        formPanel.add(lastDonationField, gbc);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(39, 174, 96));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveProfile());
        buttonPanel.add(saveButton);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveProfile() {
        if (donor == null) {
            donor = new Donor();
            donor.setUserId(currentUser.getUserId());
        }

        donor.setName(nameField.getText().trim());
        donor.setBloodGroup(bloodGroupField.getText().trim());
        donor.setDateOfBirth(dateOfBirthField.getText().trim());
        donor.setGender(genderField.getText().trim());
        donor.setPhone(phoneField.getText().trim());
        donor.setAddress(addressField.getText().trim());

        if (donorDAO.updateDonor(donor)) {
            JOptionPane.showMessageDialog(this,
                "Profile updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to update profile",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 