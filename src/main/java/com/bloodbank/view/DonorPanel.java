package com.bloodbank.view;

import com.bloodbank.dao.DonorDAO;
import com.bloodbank.dao.DonationDAO;
import com.bloodbank.model.Donor;
import com.bloodbank.model.Donation;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DonorPanel extends JPanel {
    private DonorDAO donorDAO;
    private DonationDAO donationDAO;
    private JTable donorTable;
    private DefaultTableModel tableModel;

    public DonorPanel() {
        donorDAO = new DonorDAO();
        donationDAO = new DonationDAO();
        initializeUI();
        loadDonors();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Donor Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addDonationButton = new JButton("Add Donation");
        JButton editButton = new JButton("Edit Donor");
        JButton deleteButton = new JButton("Delete Donor");

        // Style buttons
        addDonationButton.setBackground(new Color(39, 174, 96));
        addDonationButton.setForeground(Color.WHITE);
        addDonationButton.setFocusPainted(false);
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        // Add action listeners
        addDonationButton.addActionListener(e -> showAddDonationDialog());
        editButton.addActionListener(e -> editDonor());
        deleteButton.addActionListener(e -> deleteDonor());

        buttonPanel.add(addDonationButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Create table
        String[] columnNames = {"ID", "Name", "Blood Group", "Date of Birth", 
                              "Gender", "Phone", "Address", "Last Donation"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donorTable = new JTable(tableModel);
        donorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(donorTable);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDonors() {
        tableModel.setRowCount(0);
        List<Donor> donors = donorDAO.getAllDonors();
        for (Donor donor : donors) {
            tableModel.addRow(new Object[]{
                donor.getDonorId(),
                donor.getName(),
                donor.getBloodGroup(),
                donor.getDateOfBirth(),
                donor.getGender(),
                donor.getPhone(),
                donor.getAddress(),
                donor.getLastDonationDate()
            });
        }
    }

    private void showAddDonationDialog() {
        int selectedRow = donorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a donor to add donation",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int donorId = (int) tableModel.getValueAt(selectedRow, 0);
        String bloodGroup = (String) tableModel.getValueAt(selectedRow, 2);
        String lastDonationDateStr = (String) tableModel.getValueAt(selectedRow, 7);

        if (lastDonationDateStr != null && !lastDonationDateStr.isEmpty()) {
            LocalDate lastDonationDate = LocalDate.parse(lastDonationDateStr);
            LocalDate currentDate = LocalDate.now();
            if (java.time.temporal.ChronoUnit.MONTHS.between(lastDonationDate, currentDate) < 2) {
                JOptionPane.showMessageDialog(this,
                    "Cannot add donation. Two months have not passed since the last donation.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Donation", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Quantity input
        JLabel quantityLabel = new JLabel("Quantity (ml):");
        JTextField quantityField = new JTextField(10);
        
        formPanel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid quantity",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Donation donation = new Donation();
                donation.setDonorId(donorId);
                donation.setBloodGroup(bloodGroup);
                donation.setQuantity(quantity);
                donation.setDonationDate(new java.sql.Date(System.currentTimeMillis()));
                donation.setStatus("pending");

                if (donationDAO.addDonation(donation)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Donation request submitted successfully!\nWaiting for admin approval.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Failed to submit donation request",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Please enter a valid quantity",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteDonor() {
        int selectedRow = donorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a donor to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int donorId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this donor?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (donorDAO.deleteDonor(donorId)) {
                JOptionPane.showMessageDialog(this,
                    "Donor deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadDonors();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete donor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDonor() {
        int selectedRow = donorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a donor to edit",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int donorId = (int) tableModel.getValueAt(selectedRow, 0);
        Donor donor = donorDAO.getDonorByDonorId(donorId);

        if (donor == null) {
            JOptionPane.showMessageDialog(this,
                "Failed to retrieve donor details",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Donor", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name input
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(donor.getName(), 15);
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Blood Group input
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel bloodGroupLabel = new JLabel("Blood Group:");
        JTextField bloodGroupField = new JTextField(donor.getBloodGroup(), 15);
        formPanel.add(bloodGroupLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(bloodGroupField, gbc);

        // Date of Birth input
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel dobLabel = new JLabel("Date of Birth:");
        JTextField dobField = new JTextField(donor.getDateOfBirth(), 15);
        formPanel.add(dobLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(dobField, gbc);

        // Gender input
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel genderLabel = new JLabel("Gender:");
        JTextField genderField = new JTextField(donor.getGender(), 15);
        formPanel.add(genderLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(genderField, gbc);

        // Phone input
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField(donor.getPhone(), 15);
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Address input
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(donor.getAddress(), 15);
        formPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            donor.setName(nameField.getText());
            donor.setBloodGroup(bloodGroupField.getText());
            donor.setDateOfBirth(dobField.getText());
            donor.setGender(genderField.getText());
            donor.setPhone(phoneField.getText());
            donor.setAddress(addressField.getText());

            if (donorDAO.updateDonor(donor)) {
                JOptionPane.showMessageDialog(dialog,
                    "Donor updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadDonors();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to update donor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}