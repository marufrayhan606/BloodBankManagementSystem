package com.bloodbank.view;

import com.bloodbank.dao.DonorDAO;
import com.bloodbank.model.Donor;
import com.bloodbank.model.User;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DonorProfilePanel extends JPanel {
    private final DonorDAO donorDAO = new DonorDAO();
    private final User currentUser;
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
        this.donor = donorDAO.getDonorByUserId(user.getUserId());
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("My Donor Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219)),
                "Profile Information",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(41, 128, 185)
        ));
        formPanel.setBackground(Color.WHITE);

        GroupLayout layout = new GroupLayout(formPanel);
        formPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Fields
        nameField = createField(donor != null ? donor.getName() : "");
        bloodGroupField = createField(donor != null ? donor.getBloodGroup() : "");
        dateOfBirthField = createField(donor != null ? donor.getDateOfBirth() : "");
        genderField = createField(donor != null ? donor.getGender() : "");
        phoneField = createField(donor != null ? donor.getPhone() : "");
        addressField = createField(donor != null ? donor.getAddress() : "");
        lastDonationField = createField(donor != null ? donor.getLastDonationDate() : "");
        lastDonationField.setEditable(false);

        // Labels
        JLabel nameLabel = new JLabel("Name:");
        JLabel bloodLabel = new JLabel("Blood Group:");
        JLabel dobLabel = new JLabel("Date of Birth:");
        JLabel genderLabel = new JLabel("Gender:");
        JLabel phoneLabel = new JLabel("Phone:");
        JLabel addressLabel = new JLabel("Address:");
        JLabel lastDonationLabel = new JLabel("Last Donation:");

        setLabelStyle(nameLabel, bloodLabel, dobLabel, genderLabel, phoneLabel, addressLabel, lastDonationLabel);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(nameLabel)
                        .addComponent(bloodLabel)
                        .addComponent(dobLabel)
                        .addComponent(genderLabel)
                        .addComponent(phoneLabel)
                        .addComponent(addressLabel)
                        .addComponent(lastDonationLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nameField)
                        .addComponent(bloodGroupField)
                        .addComponent(dateOfBirthField)
                        .addComponent(genderField)
                        .addComponent(phoneField)
                        .addComponent(addressField)
                        .addComponent(lastDonationField))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(nameLabel).addComponent(nameField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(bloodLabel).addComponent(bloodGroupField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(dobLabel).addComponent(dateOfBirthField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(genderLabel).addComponent(genderField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(phoneLabel).addComponent(phoneField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(addressLabel).addComponent(addressField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lastDonationLabel).addComponent(lastDonationField))
        );

        JButton saveButton = new JButton("Save Changes");
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(150, 35));
        saveButton.addActionListener(e -> saveProfile());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);

        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField createField(String text) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setText(text);
        return field;
    }

    private void setLabelStyle(JLabel... labels) {
        for (JLabel label : labels) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
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

        boolean success = donorDAO.updateDonor(donor);
        String message = success ? "Profile updated successfully!" : "Failed to update profile.";
        int type = success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(this, message, "Update Status", type);
    }
}
