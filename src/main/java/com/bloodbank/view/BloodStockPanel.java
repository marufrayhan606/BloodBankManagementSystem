package com.bloodbank.view;

import com.bloodbank.dao.BloodStockDAO;
import com.bloodbank.model.BloodStock;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BloodStockPanel extends JPanel {
    private BloodStockDAO bloodStockDAO;
    private JPanel cardsPanel;

    public BloodStockPanel() {
        bloodStockDAO = new BloodStockDAO();
        initializeUI();
        loadBloodStock();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Blood Stock Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadBloodStock());
        headerPanel.add(refreshButton, BorderLayout.EAST);

        // Create cards panel with grid layout
        cardsPanel = new JPanel(new GridLayout(0, 4, 10, 10)); // 4 cards per row
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        
        // Wrap cards panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadBloodStock() {
        cardsPanel.removeAll();
        List<BloodStock> stockList = bloodStockDAO.getAllBloodStock();
        
        for (BloodStock stock : stockList) {
            JPanel card = createStockCard(stock);
            cardsPanel.add(card);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createStockCard(BloodStock stock) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        // Blood group label with large font
        JLabel bloodGroupLabel = new JLabel(stock.getBloodGroup());
        bloodGroupLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        bloodGroupLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bloodGroupLabel.setForeground(new Color(231, 76, 60)); // Red color for blood group

        // Quantity with medium font
        JLabel quantityLabel = new JLabel(stock.getQuantity() + " units");
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Last updated with small font
        JLabel lastUpdatedLabel = new JLabel("Last updated: " + stock.getLastUpdated());
        lastUpdatedLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lastUpdatedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lastUpdatedLabel.setForeground(Color.GRAY);

        // Add components to card
        card.add(bloodGroupLabel, BorderLayout.NORTH);
        card.add(quantityLabel, BorderLayout.CENTER);
        card.add(lastUpdatedLabel, BorderLayout.SOUTH);

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        return card;
    }
} 