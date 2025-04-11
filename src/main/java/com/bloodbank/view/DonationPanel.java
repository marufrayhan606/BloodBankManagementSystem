package com.bloodbank.view;

import com.bloodbank.dao.DonationDAO;
import com.bloodbank.dao.BloodStockDAO;
import com.bloodbank.dao.DonorDAO;
import com.bloodbank.model.Donation;
import com.bloodbank.model.BloodStock;
import com.bloodbank.model.User;
import com.bloodbank.util.UIUtils;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.materialdesign2.*;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.bloodbank.Application.*;
import static com.bloodbank.util.UIUtils.*;

public class DonationPanel extends JPanel {
    private DonationDAO donationDAO;
    private BloodStockDAO bloodStockDAO;
    private DonorDAO donorDAO;
    private JTable donationTable;
    private DefaultTableModel tableModel;
    private User currentUser;
    private JTextField searchField;

    public DonationPanel(User user) {
        this.donationDAO = new DonationDAO();
        this.bloodStockDAO = new BloodStockDAO();
        this.donorDAO = new DonorDAO();
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]15[grow]"));
        setBackground(BACKGROUND_COLOR);

        if (currentUser.getRole().equals("admin")) {
            initializeAdminView();
        } else {
            initializeDonorView();
        }
    }

    private void initializeAdminView() {
        // Top panel with title and search
        JPanel topPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]"));
        topPanel.setOpaque(false);
        
        // Title with icon
        JPanel titlePanel = new JPanel(new MigLayout("insets 0", "[]10[]"));
        titlePanel.setOpaque(false);
        
        FontIcon donationIcon = FontIcon.of(MaterialDesignH.HAND_HEART, 24, ACCENT_COLOR);
        JLabel titleLabel = UIUtils.createTitleLabel("Donation Requests");
        
        titlePanel.add(new JLabel(donationIcon));
        titlePanel.add(titleLabel);
        
        // Search panel
        JPanel searchPanel = new JPanel(new MigLayout("insets 0", "[]5[]"));
        searchPanel.setOpaque(false);
        
        FontIcon searchIcon = FontIcon.of(MaterialDesignM.MAGNIFY, 18, LIGHT_TEXT);
        searchField = new JTextField(18);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            searchField.getBorder(),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        searchPanel.add(new JLabel(searchIcon));
        searchPanel.add(searchField);
        
        topPanel.add(titlePanel, "left");
        topPanel.add(searchPanel, "right");

        // Action buttons panel
        JPanel actionPanel = new JPanel(new MigLayout("insets 0", "[]10[]10[]"));
        actionPanel.setOpaque(false);
        
        // Approve button
        FontIcon approveIcon = FontIcon.of(MaterialDesignC.CHECK_CIRCLE, 18, Color.WHITE);
        JButton approveButton = UIUtils.createSuccessButton("Approve");
        
        // Decline button
        FontIcon declineIcon = FontIcon.of(MaterialDesignC.CLOSE_CIRCLE, 18, Color.WHITE);
        JButton declineButton = UIUtils.createSecondaryButton("Decline");
        
        // Refresh button
        JButton refreshButton = UIUtils.createPrimaryButton("Refresh");
        
        // Add action listeners
        approveButton.addActionListener(e -> updateDonationStatus("approved"));
        declineButton.addActionListener(e -> updateDonationStatus("rejected"));
        refreshButton.addActionListener(e -> loadDonations());
        
        actionPanel.add(approveButton);
        actionPanel.add(declineButton);
        actionPanel.add(refreshButton);
        
        // Main content panel for table
        JPanel mainPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]10[grow]"));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(CARD_BORDER);
        
        // Table panel
        JPanel tableHeaderPanel = new JPanel(new MigLayout("fillx, insets 10", "[grow][]"));
        tableHeaderPanel.setOpaque(false);
        
        JLabel tableTitle = UIUtils.createSubtitleLabel("All Donation Requests");
        tableHeaderPanel.add(tableTitle, "left");
        tableHeaderPanel.add(actionPanel, "right");
        
        // Create table
        String[] columnNames = {"ID", "Donor ID", "Blood Group", "Quantity", "Donation Date", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        donationTable = new JTable(tableModel);
        UIUtils.styleTable(donationTable);
        
        // Set column widths
        donationTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        donationTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        donationTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        donationTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        donationTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        donationTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Custom renderers
        // Status column renderer
        donationTable.getColumnModel().getColumn(5).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel statusLabel = UIUtils.createStatusBadge(value.toString());
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return statusLabel;
        });
        
        // Actions column renderer
        donationTable.getColumnModel().getColumn(6).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            actionsPanel.setOpaque(isSelected);
            if (isSelected) {
                actionsPanel.setBackground(table.getSelectionBackground());
            }
            
            String status = table.getValueAt(row, 5).toString().toLowerCase();
            
            FontIcon viewIcon = FontIcon.of(MaterialDesignE.EYE, 16, PRIMARY_COLOR);
            JLabel viewLabel = new JLabel(viewIcon);
            viewLabel.setToolTipText("View Details");
            viewLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            actionsPanel.add(viewLabel);
            
            if (status.equals("pending")) {
                FontIcon approveSmallIcon = FontIcon.of(MaterialDesignC.CHECK_CIRCLE_OUTLINE, 16, ACCENT_COLOR);
                JLabel approveLabel = new JLabel(approveSmallIcon);
                approveLabel.setToolTipText("Approve");
                approveLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                FontIcon declineSmallIcon = FontIcon.of(MaterialDesignC.CLOSE_CIRCLE_OUTLINE, 16, SECONDARY_COLOR);
                JLabel declineLabel = new JLabel(declineSmallIcon);
                declineLabel.setToolTipText("Decline");
                declineLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                actionsPanel.add(approveLabel);
                actionsPanel.add(declineLabel);
            }
            
            return actionsPanel;
        });
        
        // Blood group column renderer
        donationTable.getColumnModel().getColumn(2).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            cellPanel.setOpaque(isSelected);
            if (isSelected) {
                cellPanel.setBackground(table.getSelectionBackground());
            }
            
            String bloodGroup = value.toString();
            Color bloodColor;
            
            switch (bloodGroup) {
                case "A+":
                case "A-":
                    bloodColor = new Color(220, 53, 69);
                    break;
                case "B+":
                case "B-":
                    bloodColor = new Color(0, 123, 255);
                    break;
                case "AB+":
                case "AB-":
                    bloodColor = new Color(111, 66, 193);
                    break;
                default:
                    bloodColor = new Color(40, 167, 69);
            }
            
            FontIcon bloodIcon = FontIcon.of(MaterialDesignW.WATER, 16, bloodColor);
            JLabel iconLabel = new JLabel(bloodIcon);
            
            JLabel textLabel = new JLabel(bloodGroup);
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            textLabel.setForeground(bloodColor);
            
            cellPanel.add(iconLabel);
            cellPanel.add(textLabel);
            
            return cellPanel;
        });
        
        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            private void filterTable() {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                donationTable.setRowSorter(sorter);
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(donationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        mainPanel.add(tableHeaderPanel, "growx, wrap");
        mainPanel.add(scrollPane, "grow");

        add(topPanel, "growx, wrap");
        add(mainPanel, "grow");

        // Initial load of donations
        loadDonations();
    }

    private void initializeDonorView() {
        // Top panel
        JPanel topPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]"));
        topPanel.setOpaque(false);
        
        // Title with icon
        JPanel titlePanel = new JPanel(new MigLayout("insets 0", "[]10[]"));
        titlePanel.setOpaque(false);
        
        FontIcon donationIcon = FontIcon.of(MaterialDesignH.HAND_HEART, 24, ACCENT_COLOR);
        JLabel titleLabel = UIUtils.createTitleLabel("My Donations");
        
        titlePanel.add(new JLabel(donationIcon));
        titlePanel.add(titleLabel);
        
        // Add donation button
        FontIcon addIcon = FontIcon.of(MaterialDesignP.PLUS_CIRCLE, 18, Color.WHITE);
        JButton addDonationButton = UIUtils.createSuccessButton("New Donation");
        addDonationButton.addActionListener(e -> showAddDonationDialog());
        
        topPanel.add(titlePanel, "left");
        topPanel.add(addDonationButton, "right");
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(CARD_BORDER);
        
        // Create table
        String[] columnNames = {"ID", "Blood Group", "Quantity", "Donation Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        donationTable = new JTable(tableModel);
        UIUtils.styleTable(donationTable);
        
        // Set column widths
        donationTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        donationTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        donationTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        donationTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        donationTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        // Custom renderer for status
        donationTable.getColumnModel().getColumn(4).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel statusLabel = UIUtils.createStatusBadge(value.toString());
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return statusLabel;
        });
        
        // Custom renderer for blood group
        donationTable.getColumnModel().getColumn(1).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel cellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            cellPanel.setOpaque(isSelected);
            if (isSelected) {
                cellPanel.setBackground(table.getSelectionBackground());
            }
            
            String bloodGroup = value.toString();
            Color bloodColor;
            
            switch (bloodGroup) {
                case "A+":
                case "A-":
                    bloodColor = new Color(220, 53, 69);
                    break;
                case "B+":
                case "B-":
                    bloodColor = new Color(0, 123, 255);
                    break;
                case "AB+":
                case "AB-":
                    bloodColor = new Color(111, 66, 193);
                    break;
                default:
                    bloodColor = new Color(40, 167, 69);
            }
            
            FontIcon bloodIcon = FontIcon.of(MaterialDesignW.WATER, 16, bloodColor);
            JLabel iconLabel = new JLabel(bloodIcon);
            
            JLabel textLabel = new JLabel(bloodGroup);
            textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            textLabel.setForeground(bloodColor);
            
            cellPanel.add(iconLabel);
            cellPanel.add(textLabel);
            
            return cellPanel;
        });
        
        JScrollPane scrollPane = new JScrollPane(donationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 10", "[]push[]"));
        headerPanel.setOpaque(false);
        
        JLabel tableTitle = UIUtils.createSubtitleLabel("Donation History");
        FontIcon refreshIcon = FontIcon.of(MaterialDesignR.REFRESH, 16, PRIMARY_COLOR);
        JButton refreshButton = UIUtils.createOutlineButton("Refresh", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> loadDonorDonations());
        
        headerPanel.add(tableTitle);
        headerPanel.add(refreshButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(topPanel, "growx, wrap");
        add(mainPanel, "grow");
        
        // Load donor's donations
        loadDonorDonations();
    }

    private void showAddDonationDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Donation", true);
        dialog.setLayout(new MigLayout("fill, insets 0", "[grow]", "[30%][70%]"));
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Header panel
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 30", "[center]", "[center]"));
        headerPanel.setBackground(ACCENT_COLOR);
        
        FontIcon donationIcon = FontIcon.of(MaterialDesignH.HAND_HEART, 48, Color.WHITE);
        JLabel iconLabel = new JLabel(donationIcon);
        
        JLabel titleLabel = new JLabel("New Blood Donation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Complete the form below to submit your donation");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        headerPanel.add(iconLabel, "wrap, center");
        headerPanel.add(titleLabel, "wrap, center");
        headerPanel.add(subtitleLabel, "center");

        // Form panel
        JPanel formPanel = new JPanel(new MigLayout("fillx, insets 30", "[][grow,fill]", "[]15[]15[]25[]"));
        formPanel.setBackground(Color.WHITE);

        // Blood group selection
        JLabel bloodGroupLabel = new JLabel("Blood Group");
        bloodGroupLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JComboBox<String> bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodGroupCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JLabel)bloodGroupCombo.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        
        // Quantity input
        JLabel quantityLabel = new JLabel("Quantity (units)");
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextField quantityField = new JTextField();
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            quantityField.getBorder(),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Date panel (read-only - today's date)
        JLabel dateLabel = new JLabel("Donation Date");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setEditable(false);
        dateField.setBorder(BorderFactory.createCompoundBorder(
            dateField.getBorder(),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        dateField.setBackground(new Color(245, 245, 245));

        // Add components to form
        formPanel.add(bloodGroupLabel, "right");
        formPanel.add(bloodGroupCombo, "growx, wrap");
        
        formPanel.add(quantityLabel, "right");
        formPanel.add(quantityField, "growx, wrap");
        
        formPanel.add(dateLabel, "right");
        formPanel.add(dateField, "growx, wrap");

        // Button panel
        JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        buttonPanel.setOpaque(false);
        
        FontIcon cancelIcon = FontIcon.of(MaterialDesignC.CLOSE, 16, SECONDARY_COLOR);
        JButton cancelButton = UIUtils.createOutlineButton("Cancel", SECONDARY_COLOR);
        
        FontIcon submitIcon = FontIcon.of(MaterialDesignC.CHECK, 16, Color.WHITE);
        JButton submitButton = UIUtils.createSuccessButton("Submit Donation");

        // Add action listeners
        cancelButton.addActionListener(e -> dialog.dispose());
        
        submitButton.addActionListener(e -> {
            try {
                // Retrieve the donor ID
                int donorId = donorDAO.getDonorIdByUserId(currentUser.getId());

                // Check if the donor is eligible to donate
                java.sql.Date lastDonationDate = donorDAO.getLastDonationDate(donorId);
                if (lastDonationDate != null) {
                    long timeSinceLastDonation = System.currentTimeMillis() - lastDonationDate.getTime();
                    long twoMonthsInMillis = 60L * 24 * 60 * 60 * 1000; // 60 days in milliseconds

                    if (timeSinceLastDonation < twoMonthsInMillis) {
                        // Error panel
                        JPanel errorPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
                        errorPanel.setOpaque(false);
                        
                        FontIcon errorIcon = FontIcon.of(MaterialDesignA.ALERT_CIRCLE, 24, SECONDARY_COLOR);
                        JLabel iconErrLabel = new JLabel(errorIcon);
                        
                        JLabel messageLabel = new JLabel("<html>You cannot donate again before 2 months have passed<br>since your last donation.</html>");
                        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        
                        errorPanel.add(iconErrLabel);
                        errorPanel.add(messageLabel);
                        
                        JOptionPane.showMessageDialog(dialog,
                            errorPanel,
                            "Donation Not Allowed",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String bloodGroup = (String) bloodGroupCombo.getSelectedItem();
                int quantity;
                
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                    if (quantity <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    // Error panel
                    JPanel errorPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
                    errorPanel.setOpaque(false);
                    
                    FontIcon errorIcon = FontIcon.of(MaterialDesignA.ALERT_CIRCLE, 24, SECONDARY_COLOR);
                    JLabel iconErrLabel = new JLabel(errorIcon);
                    
                    JLabel messageLabel = new JLabel("Please enter a valid quantity (positive number)");
                    messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    
                    errorPanel.add(iconErrLabel);
                    errorPanel.add(messageLabel);
                    
                    JOptionPane.showMessageDialog(dialog,
                        errorPanel,
                        "Invalid Input",
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
                    // Success panel
                    JPanel successPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
                    successPanel.setOpaque(false);
                    
                    FontIcon successIcon = FontIcon.of(MaterialDesignC.CHECK_CIRCLE, 24, ACCENT_COLOR);
                    JLabel iconSuccLabel = new JLabel(successIcon);
                    
                    JLabel messageLabel = new JLabel("<html>Donation request submitted successfully!<br>Waiting for admin approval.</html>");
                    messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    
                    successPanel.add(iconSuccLabel);
                    successPanel.add(messageLabel);
                    
                    JOptionPane.showMessageDialog(dialog,
                        successPanel,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadDonorDonations();
                } else {
                    // Error panel
                    JPanel errorPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
                    errorPanel.setOpaque(false);
                    
                    FontIcon errorIcon = FontIcon.of(MaterialDesignA.ALERT_CIRCLE, 24, SECONDARY_COLOR);
                    JLabel iconErrLabel = new JLabel(errorIcon);
                    
                    JLabel messageLabel = new JLabel("Failed to submit donation request");
                    messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    
                    errorPanel.add(iconErrLabel);
                    errorPanel.add(messageLabel);
                    
                    JOptionPane.showMessageDialog(dialog,
                        errorPanel,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "An unexpected error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton, "width 45%");
        buttonPanel.add(submitButton, "width 45%");

        formPanel.add(buttonPanel, "span 2, growx");
        
        dialog.add(headerPanel, "growx, wrap");
        dialog.add(formPanel, "grow");
        
        dialog.getRootPane().setDefaultButton(submitButton);
        dialog.setVisible(true);
    }

    private void loadDonorDonations() {
        tableModel.setRowCount(0);
        List<Donation> donations = donationDAO.getDonationsByDonorId(currentUser.getId());
        for (Donation donation : donations) {
            tableModel.addRow(new Object[]{
                donation.getDonationId(),
                donation.getBloodGroup(),
                donation.getQuantity() + " units",
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
                donation.getQuantity() + " units",
                donation.getDonationDate(),
                donation.getStatus(),
                "actions" // Placeholder for actions column
            });
        }
    }

    private void updateDonationStatus(String status) {
        int selectedRow = donationTable.getSelectedRow();
        if (selectedRow == -1) {
            // Error notification panel
            JPanel errorPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
            errorPanel.setOpaque(false);
            
            FontIcon errorIcon = FontIcon.of(MaterialDesignA.ALERT_CIRCLE, 24, SECONDARY_COLOR);
            JLabel iconLabel = new JLabel(errorIcon);
            
            JLabel messageLabel = new JLabel("Please select a donation to " + status);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            errorPanel.add(iconLabel);
            errorPanel.add(messageLabel);
            
            JOptionPane.showMessageDialog(this,
                errorPanel,
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert row index if table is sorted
        if (donationTable.getRowSorter() != null) {
            selectedRow = donationTable.getRowSorter().convertRowIndexToModel(selectedRow);
        }

        int donationId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

        // Check if donation is already approved or rejected
        if (!currentStatus.toLowerCase().equals("pending")) {
            // Error notification panel
            JPanel errorPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
            errorPanel.setOpaque(false);
            
            FontIcon errorIcon = FontIcon.of(MaterialDesignA.ALERT_CIRCLE, 24, SECONDARY_COLOR);
            JLabel iconLabel = new JLabel(errorIcon);
            
            JLabel messageLabel = new JLabel("Only pending donations can be approved or rejected");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            
            errorPanel.add(iconLabel);
            errorPanel.add(messageLabel);
            
            JOptionPane.showMessageDialog(this,
                errorPanel,
                "Status Update Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm dialog
        JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            status.equals("approved") ? "Approve Donation" : "Decline Donation", true);
        confirmDialog.setLayout(new MigLayout("fill, insets 20", "[center]", "[]20[]"));
        confirmDialog.setSize(400, 180);
        confirmDialog.setLocationRelativeTo(this);
        
        JPanel messagePanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
        messagePanel.setOpaque(false);
        
        FontIcon questionIcon = FontIcon.of(MaterialDesignH.HELP_CIRCLE, 24, 
            status.equals("approved") ? ACCENT_COLOR : SECONDARY_COLOR);
        JLabel iconLabel = new JLabel(questionIcon);
        
        JLabel messageLabel = new JLabel("<html>Are you sure you want to <b>" + status + "</b> this donation?</html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        messagePanel.add(iconLabel);
        messagePanel.add(messageLabel);
        
        JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        buttonPanel.setOpaque(false);
        
        FontIcon cancelIcon = FontIcon.of(MaterialDesignC.CLOSE, 16, PRIMARY_COLOR);
        JButton cancelButton = UIUtils.createOutlineButton("Cancel", PRIMARY_COLOR);
        
        FontIcon confirmIcon = status.equals("approved") ? 
            FontIcon.of(MaterialDesignC.CHECK, 16, Color.WHITE) :
            FontIcon.of(MaterialDesignC.CLOSE, 16, Color.WHITE);
        
        JButton confirmButton = status.equals("approved") ?
            UIUtils.createSuccessButton("Approve") :
            UIUtils.createSecondaryButton("Decline");
        
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        confirmButton.addActionListener(e -> {
            confirmDialog.dispose();
            processDonationStatus(donationId, status);
        });
        
        buttonPanel.add(cancelButton, "width 45%");
        buttonPanel.add(confirmButton, "width 45%");
        
        confirmDialog.add(messagePanel, "wrap");
        confirmDialog.add(buttonPanel);
        confirmDialog.setVisible(true);
    }
    
    private void processDonationStatus(int donationId, String status) {
        Donation donation = donationDAO.getDonationById(donationId);
        BloodStock stock = bloodStockDAO.getBloodStockByGroup(donation.getBloodGroup());

        if (donation != null) {
            if (donationDAO.updateDonationStatus(donationId, status, currentUser.getUserId())) {
                if (status.equals("approved")) {
                    // Update blood stock
                    if (bloodStockDAO.updateBloodStock(donation.getBloodGroup(), stock.getQuantity() + donation.getQuantity())) {
                        // Update donor's last donation date
                        donorDAO.updateLastDonationDate(donation.getDonorId(), donation.getDonationDate());
                        
                        // Success notification
                        JPanel successPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
                        successPanel.setOpaque(false);
                        
                        FontIcon successIcon = FontIcon.of(MaterialDesignC.CHECK_CIRCLE, 24, ACCENT_COLOR);
                        JLabel iconLabel = new JLabel(successIcon);
                        
                        JLabel messageLabel = new JLabel("<html>Donation approved and blood stock updated successfully!<br>" +
                            "Added " + donation.getQuantity() + " units of " + donation.getBloodGroup() + " blood.</html>");
                        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        
                        successPanel.add(iconLabel);
                        successPanel.add(messageLabel);
                        
                        JOptionPane.showMessageDialog(this,
                            successPanel,
                            "Donation Approved",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to update blood stock",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Success notification for rejection
                    JPanel successPanel = new JPanel(new MigLayout("fillx, insets 0", "[]10[]"));
                    successPanel.setOpaque(false);
                    
                    FontIcon successIcon = FontIcon.of(MaterialDesignC.CHECK_CIRCLE, 24, PRIMARY_COLOR);
                    JLabel iconLabel = new JLabel(successIcon);
                    
                    JLabel messageLabel = new JLabel("Donation declined successfully");
                    messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    
                    successPanel.add(iconLabel);
                    successPanel.add(messageLabel);
                    
                    JOptionPane.showMessageDialog(this,
                        successPanel,
                        "Donation Declined",
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