package com.bloodbank.view;

import com.bloodbank.dao.DonationDAO;
import com.bloodbank.dao.DonorDAO;
import com.bloodbank.model.Donation;
import com.bloodbank.model.Donor;
import com.bloodbank.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DonationHistoryPanel extends JPanel {
    private DonationDAO donationDAO;
    private User currentUser;
    private JPanel historyPanel;
    private DonorDAO donorDAO;

    public DonationHistoryPanel(User user) {
        this.currentUser = user;
        this.donationDAO = new DonationDAO();
        this.donorDAO = new DonorDAO();
        initializeUI();
        loadDonations();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(245, 245, 245));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout(15, 15));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("My Donation History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        // Refresh Button
        JButton refreshButton = new JButton("Refresh History");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadDonations());

        // Add New Donation Button (Only for non-admin users)
        JButton addNewDonationButton = null;
        if (!currentUser.getRole().equals("admin")) {  // Assuming User class has an isAdmin() method
            addNewDonationButton = new JButton("Add New Donation");
            addNewDonationButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            addNewDonationButton.setBackground(new Color(52, 152, 219));
            addNewDonationButton.setForeground(Color.WHITE);
            addNewDonationButton.setFocusPainted(false);
            addNewDonationButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            addNewDonationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Check if 2 months have passed since the last donation
            Donor donor = donorDAO.getDonorByUserId(currentUser.getUserId());
            if (donor != null && donor.getLastDonationDate() != null) {
                java.sql.Date lastDonationDate = java.sql.Date.valueOf(donor.getLastDonationDate());
                long timeSinceLastDonation = System.currentTimeMillis() - lastDonationDate.getTime();
                long twoMonthsInMillis = 60L * 24 * 60 * 60 * 1000; // 60 days in milliseconds

                if (timeSinceLastDonation < twoMonthsInMillis) {
                    addNewDonationButton.setEnabled(false);
                    addNewDonationButton.setToolTipText("You cannot donate again before 2 months have passed since your last donation.");
                }
            }

            // Add action listener to handle donation addition
            addNewDonationButton.addActionListener(e -> showAddDonationDialog(currentUser));
        }

        // Add components to header
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        if (addNewDonationButton != null) {
            buttonPanel.add(addNewDonationButton);
        }

        buttonPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // History Panel
        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setOpaque(false);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(historyPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDonations() {
        historyPanel.removeAll();

        // First get the donor ID for the current user
        Donor donor = donorDAO.getDonorByUserId(currentUser.getUserId());

        if (donor == null) {
            // Show message if donor profile is not found
            JPanel emptyPanel = createEmptyStatePanel();
            JLabel messageLabel = new JLabel("Please complete your donor profile first");
            messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            messageLabel.setForeground(new Color(231, 76, 60));
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyPanel.add(messageLabel, BorderLayout.SOUTH);
            historyPanel.add(emptyPanel);
        } else {
            // Get donations using donor ID
            List<Donation> donations = donationDAO.getDonationsByDonorId(donor.getDonorId());

            if (donations.isEmpty()) {
                JPanel emptyPanel = createEmptyStatePanel();
                historyPanel.add(emptyPanel);
            } else {
                for (Donation donation : donations) {
                    JPanel donationCard = createDonationCard(donation);
                    historyPanel.add(donationCard);
                    historyPanel.add(Box.createVerticalStrut(15)); // Add spacing between cards
                }
            }
        }

        historyPanel.revalidate();
        historyPanel.repaint();
    }

    private JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel messageLabel = new JLabel("No donations yet");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(new Color(150, 150, 150));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subMessageLabel = new JLabel("Your donation history will appear here");
        subMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subMessageLabel.setForeground(new Color(150, 150, 150));
        subMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(messageLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(subMessageLabel);

        panel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDonationCard(Donation donation) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Left panel for blood group and quantity
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(120, 0));

        JLabel bloodGroupLabel = new JLabel(donation.getBloodGroup());
        bloodGroupLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        bloodGroupLabel.setForeground(new Color(231, 76, 60));
        bloodGroupLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel quantityLabel = new JLabel(donation.getQuantity() + " units");
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        quantityLabel.setForeground(new Color(100, 100, 100));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);

        leftPanel.add(bloodGroupLabel);
        leftPanel.add(quantityLabel);

        // Center panel for date and ID
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setOpaque(false);

        JLabel dateLabel = new JLabel("Donation Date: " + donation.getDonationDate());
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel idLabel = new JLabel("Donation ID: " + donation.getDonationId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        idLabel.setForeground(new Color(150, 150, 150));

        centerPanel.add(dateLabel);
        centerPanel.add(idLabel);

        // Right panel for status
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JLabel statusLabel = createStatusLabel(donation.getStatus());
        rightPanel.add(statusLabel);

        // Add all panels to card
        card.add(leftPanel, BorderLayout.WEST);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(250, 250, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JLabel createStatusLabel(String status) {
        JLabel label = new JLabel(status.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        label.setOpaque(true);

        switch (status.toLowerCase()) {
            case "approved":
                label.setBackground(new Color(46, 204, 113, 20));
                label.setForeground(new Color(46, 204, 113));
                break;
            case "pending":
                label.setBackground(new Color(241, 196, 15, 20));
                label.setForeground(new Color(241, 196, 15));
                break;
            case "rejected":
                label.setBackground(new Color(231, 76, 60, 20));
                label.setForeground(new Color(231, 76, 60));
                break;
            default:
                label.setBackground(new Color(149, 165, 166, 20));
                label.setForeground(new Color(149, 165, 166));
        }

        return label;
    }

    private void showAddDonationDialog(User currentUser) {
        Donor donor = donorDAO.getDonorByUserId(currentUser.getUserId());
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
        JComboBox<String> bloodGroupCombo = new JComboBox<>(new String[] {donor.getBloodGroup()});

        // Quantity input
        JLabel quantityLabel = new JLabel("Quantity (units):");
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

                if (donor == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Donor profile not found. Please complete your donor profile first.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Donation donation = new Donation();
                donation.setDonorId(donor.getDonorId()); // Use donor's ID
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
}
