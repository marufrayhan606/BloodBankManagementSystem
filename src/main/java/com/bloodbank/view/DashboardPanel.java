package com.bloodbank.view;

import com.bloodbank.dao.*;
import com.bloodbank.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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

    public DashboardPanel(User user) {
        this.currentUser = user;
        this.bloodStockDAO = new BloodStockDAO();
        this.donationDAO = new DonationDAO();
        this.bloodRequestDAO = new BloodRequestDAO();
        initializeUI();
        loadDashboardData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(245, 245, 245));

        // Top Panel with Welcome Message and Refresh Button
        JPanel topPanel = createTopPanel();
        
        // Center Panel with Statistics and Activities
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);
        
        // Statistics Panel
        JPanel statsPanel = createStatsPanel();
        
        // Activities Panel
        JPanel activitiesPanel = createActivitiesPanel();

        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(activitiesPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);

        // Welcome message
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        welcomePanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        
        welcomePanel.add(welcomeLabel);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadDashboardData());

        panel.add(welcomePanel, BorderLayout.WEST);
        panel.add(refreshButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);

        totalBloodUnitsLabel = createStatCard("Total Blood Units", "0", new Color(46, 204, 113));
        totalDonationsLabel = createStatCard("Total Donations", "0", new Color(52, 152, 219));
        pendingDonationsLabel = createStatCard("Pending Donations", "0", new Color(155, 89, 182));
        pendingRequestsLabel = createStatCard("Pending Requests", "0", new Color(231, 76, 60));

        panel.add(totalBloodUnitsLabel.getParent());
        panel.add(totalDonationsLabel.getParent());
        panel.add(pendingDonationsLabel.getParent());
        panel.add(pendingRequestsLabel.getParent());

        return panel;
    }

    private JPanel createActivitiesPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);

        // Recent Donations Panel
        JPanel recentDonationsPanel = createActivityPanel("Recent Donations", 
            new String[]{"ID", "Blood Group", "Units", "Status"});
        
        // Recent Requests Panel
        JPanel recentRequestsPanel = createActivityPanel("Recent Blood Requests", 
            new String[]{"ID", "Recipient", "Blood Group", "Status"});

        panel.add(recentDonationsPanel);
        panel.add(recentRequestsPanel);

        return panel;
    }

    private JLabel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);

        // Title panel
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(250, 250, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return valueLabel;
    }

    private JPanel createActivityPanel(String title, String[] columns) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Title with icon
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 70, 70));
        
        headerPanel.add(titleLabel);

        // Table
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(new Color(100, 100, 100));
        table.setSelectionBackground(new Color(245, 245, 245));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        if (title.contains("Donations")) {
            recentDonationsModel = model;
        } else {
            recentRequestsModel = model;
        }

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

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
            .limit(5)
            .forEach(d -> recentDonationsModel.addRow(new Object[]{
                d.getDonationId(),
                d.getBloodGroup(),
                d.getQuantity(),
                d.getStatus()
            }));

        // Update recent requests table
        recentRequestsModel.setRowCount(0);
        requests.stream()
            .limit(5)
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