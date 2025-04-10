package com.bloodbank.view;

import com.bloodbank.dao.DonationDAO;
import com.bloodbank.dao.BloodStockDAO;
import com.bloodbank.dao.DonorDAO;
import com.bloodbank.model.Donation;
import com.bloodbank.model.BloodStock;
import com.bloodbank.model.User;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DonationPanel extends JPanel {
    private DonationDAO donationDAO;
    private BloodStockDAO bloodStockDAO;
    private DonorDAO donorDAO;
    private JTable donationTable;
    private DefaultTableModel tableModel;
    private User currentUser;

    public DonationPanel(User user) {
        this.donationDAO = new DonationDAO();
        this.bloodStockDAO = new BloodStockDAO();
        this.donorDAO = new DonorDAO();
        this.currentUser = user;
        initializeUI();
        loadDonations();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        if (currentUser.getRole().equals("admin")) {
            initializeAdminView();
        } else {
            initializeDonorView();
        }
    }

    private void initializeAdminView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Donation Requests");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton approveButton = new JButton("Approve");
        JButton declineButton = new JButton("Decline");
        JButton refreshButton = new JButton("Refresh");

        // Style buttons
        approveButton.setBackground(new Color(46, 204, 113));
        approveButton.setForeground(Color.WHITE);
        approveButton.setFocusPainted(false);

        declineButton.setBackground(new Color(231, 76, 60));
        declineButton.setForeground(Color.WHITE);
        declineButton.setFocusPainted(false);

        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        // Add action listeners
        approveButton.addActionListener(e -> updateDonationStatus("approved"));
        declineButton.addActionListener(e -> updateDonationStatus("rejected"));
        refreshButton.addActionListener(e -> loadDonations());

        buttonPanel.add(approveButton);
        buttonPanel.add(declineButton);
        buttonPanel.add(refreshButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Create table
        String[] columnNames = {"ID", "Donor ID", "Blood Group", "Quantity (units)", "Donation Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donationTable = new JTable(tableModel);
        donationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(donationTable);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initial load of donations
        loadDonations();
    }

    private void initializeDonorView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("My Donations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add donation button
        JButton addDonationButton = new JButton("Add New Donation");
        addDonationButton.setBackground(new Color(46, 204, 113));
        addDonationButton.setForeground(Color.WHITE);
        addDonationButton.setFocusPainted(false);
        headerPanel.add(addDonationButton, BorderLayout.EAST);

        // Create table
        String[] columnNames = {"ID", "Blood Group", "Quantity (Units)", "Donation Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(donationTable);

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listener for add donation button
        addDonationButton.addActionListener(e -> showAddDonationDialog());

        // Load donor's donations
        loadDonorDonations();
    }

    private void showAddDonationDialog() {
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

        // Blood group selection
        JLabel bloodGroupLabel = new JLabel("Blood Group:");
        JComboBox<String> bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        
        // Quantity input
        JLabel quantityLabel = new JLabel("Quantity (ml):");
        JTextField quantityField = new JTextField(10);

        // Add components to form
        formPanel.add(bloodGroupLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(bloodGroupCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            try {
                String bloodGroup = (String) bloodGroupCombo.getSelectedItem();
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please enter a valid quantity",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Donation donation = new Donation();
                donation.setDonorId(currentUser.getId());
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
                    loadDonorDonations();
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

    private void loadDonorDonations() {
        tableModel.setRowCount(0);
        List<Donation> donations = donationDAO.getDonationsByDonorId(currentUser.getId());
        for (Donation donation : donations) {
            tableModel.addRow(new Object[]{
                donation.getDonationId(),
                donation.getBloodGroup(),
                donation.getQuantity(),
                donation.getDonationDate(),
                donation.getStatus()
            });
        }
    }

    private void loadDonations() {
        tableModel.setRowCount(0);
        List<Donation> donations = donationDAO.getAllDonations();
        for (Donation donation : donations) {
            tableModel.addRow(new Object[]{
                donation.getDonationId(),
                donation.getDonorId(),
                donation.getBloodGroup(),
                donation.getQuantity(),
                donation.getDonationDate(),
                donation.getStatus()
            });
        }
    }

    private void updateDonationStatus(String status) {
        int selectedRow = donationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a donation to " + status,
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int donationId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

        // Check if donation is already approved or rejected
        if (!currentStatus.toLowerCase().equals("pending")) {
            JOptionPane.showMessageDialog(this,
                "Only pending donations can be approved or rejected",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Donation donation = donationDAO.getDonationById(donationId);
        BloodStock stock = bloodStockDAO.getBloodStockByGroup(donation.getBloodGroup());

        if (donation != null) {
            if (donationDAO.updateDonationStatus(donationId, status, currentUser.getUserId())) {
                if (status.equals("approved")) {
                    // Update blood stock
                    if (bloodStockDAO.updateBloodStock(donation.getBloodGroup(), stock.getQuantity() + donation.getQuantity())) {
                        // Update donor's last donation date
                        donorDAO.updateLastDonationDate(donation.getDonorId(), donation.getDonationDate());
                        JOptionPane.showMessageDialog(this,
                            "Donation approved and blood stock updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to update blood stock",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Donation " + status + " successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                loadDonations();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update donation status",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 