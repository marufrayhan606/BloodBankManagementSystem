package com.bloodbank.view;

import com.bloodbank.dao.*;
import com.bloodbank.model.*;
import com.bloodbank.util.UIUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

import static com.bloodbank.Application.*;
import static com.bloodbank.util.UIUtils.*;

public class DashboardPanel extends JPanel {
    private BloodStockDAO bloodStockDAO;
    private DonationDAO donationDAO;
    private BloodRequestDAO bloodRequestDAO;
    private User currentUser;
    private JLabel totalBloodUnitsLabel;
    private JLabel totalDonationsLabel;
    private JLabel pendingDonationsLabel;
    private JLabel pendingRequestsLabel;
    private DefaultTableModel recentDonationsModel;
    private DefaultTableModel recentRequestsModel;
    private JTable recentDonationsTable;
    private JTable recentRequestsTable;

    public DashboardPanel(User user) {
        this.currentUser = user;
        this.bloodStockDAO = new BloodStockDAO();
        this.donationDAO = new DonationDAO();
        this.bloodRequestDAO = new BloodRequestDAO();
        initializeUI();
        loadDashboardData();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]15[grow]"));
        setBackground(BACKGROUND_COLOR);

        // Top section with welcome message and refresh button
        JPanel topPanel = createTopPanel();
        
        // Main content panel
        JPanel contentPanel = new JPanel(new MigLayout("fillx, wrap", "[grow]", "[]15[grow]"));
        contentPanel.setOpaque(false);
        
        // Statistics cards section
        JPanel statsPanel = createStatsPanel();
        
        // Recent activities section with tables
        JPanel activitiesPanel = createActivitiesPanel();
        
        contentPanel.add(statsPanel, "growx");
        contentPanel.add(activitiesPanel, "grow");

        add(topPanel, "growx, wrap");
        add(contentPanel, "grow");
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]"));
        panel.setOpaque(false);

        // Welcome message
        JLabel welcomeLabel = UIUtils.createTitleLabel("Dashboard");
        
        // Refresh button
        JButton refreshButton = UIUtils.createPrimaryButton("Refresh");
        refreshButton.addActionListener(e -> loadDashboardData());

        panel.add(welcomeLabel, "left");
        panel.add(refreshButton, "right");

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "[grow,fill][grow,fill][grow,fill][grow,fill]", "[]"));
        panel.setOpaque(false);

        // Total blood units card
        JPanel bloodUnitsCard = UIUtils.createStatCard("Total Blood Units", "0", ACCENT_COLOR);
        totalBloodUnitsLabel = (JLabel) ((JPanel)bloodUnitsCard.getComponent(0)).getComponent(1);
        
        // Total donations card
        JPanel donationsCard = UIUtils.createStatCard("Total Donations", "0", PRIMARY_COLOR);
        totalDonationsLabel = (JLabel) ((JPanel)donationsCard.getComponent(0)).getComponent(1);
        
        // Pending donations card
        JPanel pendingDonationsCard = UIUtils.createStatCard("Pending Donations", "0", new Color(155, 89, 182));
        pendingDonationsLabel = (JLabel) ((JPanel)pendingDonationsCard.getComponent(0)).getComponent(1);
        
        // Pending requests card
        JPanel pendingRequestsCard = UIUtils.createStatCard("Pending Requests", "0", SECONDARY_COLOR);
        pendingRequestsLabel = (JLabel) ((JPanel)pendingRequestsCard.getComponent(0)).getComponent(1);

        panel.add(bloodUnitsCard, "grow");
        panel.add(donationsCard, "grow");
        panel.add(pendingDonationsCard, "grow");
        panel.add(pendingRequestsCard, "grow");

        return panel;
    }

    private JPanel createActivitiesPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[grow 50][grow 50]", "[grow]"));
        panel.setOpaque(false);

        // Recent Donations Panel
        JPanel recentDonationsPanel = createActivityPanel("Recent Donations", 
            new String[]{"ID", "Blood Group", "Units", "Status"});
        
        // Recent Requests Panel
        JPanel recentRequestsPanel = createActivityPanel("Recent Blood Requests", 
            new String[]{"ID", "Recipient", "Blood Group", "Status"});

        panel.add(recentDonationsPanel, "grow");
        panel.add(recentRequestsPanel, "grow");

        return panel;
    }

    private JPanel createActivityPanel(String title, String[] columns) {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[]5[grow]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(CARD_BORDER);

        // Title panel with icon
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 0", "[]push[]"));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = UIUtils.createSubtitleLabel(title);
        
        // Search field
        JPanel searchPanel = new JPanel(new MigLayout("insets 0", "[]"));
        searchPanel.setOpaque(false);
        
        JTextField searchField = new JTextField(10);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        searchPanel.add(searchField);
        
        headerPanel.add(titleLabel);
        headerPanel.add(searchPanel, "right");

        // Create table model
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create and style table
        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        
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
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        
        // Store model reference and table
        if (title.contains("Donations")) {
            recentDonationsModel = model;
            recentDonationsTable = table;
        } else {
            recentRequestsModel = model;
            recentRequestsTable = table;
        }
        
        // Create custom status column renderer
        table.getColumnModel().getColumn(columns.length - 1).setCellRenderer((tableUI, value, isSelected, hasFocus, row, column) -> {
            String status = value.toString().toLowerCase();
            JLabel statusLabel = UIUtils.createStatusBadge(status);
            
            if (isSelected) {
                statusLabel.setOpaque(true);
            }
            
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return statusLabel;
        });
        
        // Enable sorting
        table.setAutoCreateRowSorter(true);
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(headerPanel, "growx, wrap");
        panel.add(scrollPane, "grow");

        return panel;
    }

    private void loadDashboardData() {
        // Update statistics
        List<BloodStock> bloodStock = bloodStockDAO.getAllBloodStock();
        int totalStock = bloodStock.stream()
            .mapToInt(BloodStock::getQuantity)
            .sum();
        totalBloodUnitsLabel.setText(String.valueOf(totalStock));

        List<Donation> donations = donationDAO.getAllDonations();
        totalDonationsLabel.setText(String.valueOf(donations.size()));

        long pendingDonations = donations.stream()
            .filter(d -> d.getStatus().equalsIgnoreCase("pending"))
            .count();
        pendingDonationsLabel.setText(String.valueOf(pendingDonations));

        List<BloodRequest> requests = bloodRequestDAO.getAllBloodRequests();
        long pendingRequests = requests.stream()
            .filter(r -> r.getStatus().equalsIgnoreCase("pending"))
            .count();
        pendingRequestsLabel.setText(String.valueOf(pendingRequests));

        // Update recent donations table
        recentDonationsModel.setRowCount(0);
        donations.stream()
            .limit(10)  // Show more items
            .forEach(d -> recentDonationsModel.addRow(new Object[]{
                d.getDonationId(),
                d.getBloodGroup(),
                d.getQuantity(),
                d.getStatus()
            }));

        // Update recent requests table
        recentRequestsModel.setRowCount(0);
        requests.stream()
            .limit(10)  // Show more items
            .forEach(r -> recentRequestsModel.addRow(new Object[]{
                r.getRequestId(),
                r.getRecipientName(),
                r.getBloodGroup(),
                r.getStatus()
            }));

        // Refresh UI
        revalidate();
        repaint();
    }
}