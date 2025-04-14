package com.bloodbank.view;

import com.bloodbank.dao.BloodRequestDAO;
import com.bloodbank.dao.BloodStockDAO;
import com.bloodbank.model.BloodRequest;
import com.bloodbank.model.BloodStock;
import com.bloodbank.model.User;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BloodRequestPanel extends JPanel {
    private BloodRequestDAO bloodRequestDAO;
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private boolean isAdminMode;
    private User currentUser;

    public BloodRequestPanel() {
        this(null); // Public mode
    }

    public BloodRequestPanel(User user) {
        this.bloodRequestDAO = new BloodRequestDAO();
        this.currentUser = user;
        this.isAdminMode = (user != null && user.getRole().equals("admin"));
        initializeUI();
        if (isAdminMode) {
            loadRequests();
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (isAdminMode) {
            initializeAdminUI();
        } else {
            initializePublicUI();
        }
    }

    private void initializePublicUI() {
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Request Blood");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Form fields
        JTextField recipientNameField = new JTextField(20);
        JComboBox<String> bloodGroupField = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        JSpinner quantityField = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JTextField hospitalNameField = new JTextField(20);
        JTextField hospitalAddressField = new JTextField(20);
        JTextArea patientConditionArea = new JTextArea(3, 20);
        patientConditionArea.setLineWrap(true);
        patientConditionArea.setWrapStyleWord(true);

        // Add components
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Recipient Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(recipientNameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bloodGroupField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Quantity (units):"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Hospital Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hospitalNameField, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Hospital Address:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hospitalAddressField, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Patient Condition:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(patientConditionArea), gbc);

        // Submit button
        JButton submitButton = new JButton("Submit Request");
        submitButton.setBackground(new Color(39, 174, 96));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            // Validate fields
            if (recipientNameField.getText().trim().isEmpty() ||
                hospitalNameField.getText().trim().isEmpty() ||
                hospitalAddressField.getText().trim().isEmpty() ||
                patientConditionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create request
            BloodRequest request = new BloodRequest();
            request.setRecipientName(recipientNameField.getText().trim());
            request.setBloodGroup((String) bloodGroupField.getSelectedItem());
            request.setQuantity((Integer) quantityField.getValue());
            request.setHospitalName(hospitalNameField.getText().trim());
            request.setHospitalAddress(hospitalAddressField.getText().trim());
            request.setPatientCondition(patientConditionArea.getText().trim());
            request.setStatus("Pending");

            // Submit request
            if (bloodRequestDAO.addBloodRequest(request)) {
                JOptionPane.showMessageDialog(this,
                    "Blood request submitted successfully. Our team will review your request.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                // Navigate back to the home page
                SwingUtilities.getWindowAncestor(this).dispose(); // Close current panel
                new UserInterface().setVisible(true); // Open home page
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to submit blood request",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add form to panel
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeAdminUI() {
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Blood Request Management");
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
        approveButton.addActionListener(e -> updateRequestStatus("Approved"));
        declineButton.addActionListener(e -> updateRequestStatus("Declined"));
        refreshButton.addActionListener(e -> loadRequests());

        buttonPanel.add(approveButton);
        buttonPanel.add(declineButton);
        buttonPanel.add(refreshButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Create table
        String[] columnNames = {"ID", "Recipient", "Blood Group", "Quantity", 
                              "Hospital", "Address", "Condition", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestTable = new JTable(tableModel);
        requestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(requestTable);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadRequests() {
        if (!isAdminMode) return;

        tableModel.setRowCount(0);
        List<BloodRequest> requests = bloodRequestDAO.getAllBloodRequests();
        for (BloodRequest request : requests) {
            tableModel.addRow(new Object[]{
                request.getRequestId(),
                request.getRecipientName(),
                request.getBloodGroup(),
                request.getQuantity(),
                request.getHospitalName(),
                request.getHospitalAddress(),
                request.getPatientCondition(),
                request.getStatus()
            });
        }
    }

    private void updateRequestStatus(String status) {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a request to " + status.toLowerCase(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        String bloodGroup = (String) tableModel.getValueAt(selectedRow, 2);
        int requestedQuantity = (int) tableModel.getValueAt(selectedRow, 3);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 7); // Corrected column index for 'Status'

        // Check if donation is already approved or rejected
        if (!currentStatus.toLowerCase().equals("pending")) {
            JOptionPane.showMessageDialog(this,
                    "Only pending donations can be approved or rejected",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check blood stock availability if approving
        if (status.equals("Approved")) {
            BloodStockDAO bloodStockDAO = new BloodStockDAO();
            BloodStock stock = bloodStockDAO.getBloodStockByGroup(bloodGroup);
            
            if (stock == null || stock.getQuantity() < requestedQuantity) {
                JOptionPane.showMessageDialog(this,
                    "Cannot approve request: Insufficient " + bloodGroup + " blood in stock.\n" +
                    "Available: " + (stock != null ? stock.getQuantity() : 0) + " units\n" +
                    "Requested: " + requestedQuantity + " units",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update blood stock if approved
            if (bloodRequestDAO.updateRequestStatus(requestId, status, currentUser.getUserId())) {
                // Deduct the blood units from stock
                bloodStockDAO.updateBloodStock(bloodGroup, stock.getQuantity() - requestedQuantity);
                JOptionPane.showMessageDialog(this,
                    "Request approved and blood stock updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadRequests();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update request status",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // For declining requests, no need to check stock
            if (bloodRequestDAO.updateRequestStatus(requestId, status, currentUser.getUserId())) {
                JOptionPane.showMessageDialog(this,
                    "Request " + status.toLowerCase() + " successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadRequests();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update request status",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
