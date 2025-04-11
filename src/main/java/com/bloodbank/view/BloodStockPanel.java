package com.bloodbank.view;

import com.bloodbank.dao.BloodStockDAO;
import com.bloodbank.model.BloodStock;
import com.bloodbank.util.UIUtils;
import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bloodbank.Application.*;
import static com.bloodbank.util.UIUtils.*;

public class BloodStockPanel extends JPanel {
    private BloodStockDAO bloodStockDAO;
    private JPanel cardsPanel;
    private ChartPanel chartPanel;
    private JTable stockTable;
    
    // Blood group color map
    private final Map<String, Color> bloodGroupColors = new HashMap<>();

    public BloodStockPanel() {
        bloodStockDAO = new BloodStockDAO();
        initializeBloodGroupColors();
        initializeUI();
        loadBloodStock();
    }

    private void initializeBloodGroupColors() {
        // Colors for different blood groups
        bloodGroupColors.put("A+", new Color(220, 53, 69));  // Red
        bloodGroupColors.put("A-", new Color(220, 53, 69, 180));  // Light Red
        bloodGroupColors.put("B+", new Color(0, 123, 255));  // Blue
        bloodGroupColors.put("B-", new Color(0, 123, 255, 180));  // Light Blue
        bloodGroupColors.put("AB+", new Color(111, 66, 193)); // Purple
        bloodGroupColors.put("AB-", new Color(111, 66, 193, 180)); // Light Purple
        bloodGroupColors.put("O+", new Color(40, 167, 69));  // Green
        bloodGroupColors.put("O-", new Color(40, 167, 69, 180));  // Light Green
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]15[]15[]"));
        setBackground(BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create card panel with stock information
        cardsPanel = new JPanel(new MigLayout("fillx, wrap 4, gap 15", "[grow, fill][grow, fill][grow, fill][grow, fill]", "[]"));
        cardsPanel.setOpaque(false);
        
        // Create visualization panel
        JPanel visualizationPanel = createVisualizationPanel();

        add(headerPanel, "growx, wrap");
        add(cardsPanel, "growx, wrap");
        add(visualizationPanel, "grow");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "[grow][]"));
        panel.setOpaque(false);

        // Title with icon
        JPanel titlePanel = new JPanel(new MigLayout("insets 0", "[]10[]"));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = UIUtils.createTitleLabel("Blood Stock Management");
        
        titlePanel.add(titleLabel);

        // Refresh button
        JButton refreshButton = UIUtils.createPrimaryButton("Refresh");
        refreshButton.addActionListener(e -> loadBloodStock());

        panel.add(titlePanel, "left");
        panel.add(refreshButton, "right");

        return panel;
    }
    
    private JPanel createVisualizationPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[40%][60%]", "[grow]"));
        panel.setOpaque(false);
        
        // Create table panel for detailed view
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(CARD_BORDER);
        
        // Table header
        JPanel tableHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableHeader.setOpaque(false);
        
        JLabel tableTitle = UIUtils.createSubtitleLabel("Blood Stock Inventory");
        tableHeader.add(tableTitle);
        
        // Create table
        String[] columns = {"Blood Group", "Quantity", "Last Updated", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        stockTable = new JTable(model);
        UIUtils.styleTable(stockTable);
        
        // Custom renderer for blood group colors
        stockTable.getColumnModel().getColumn(0).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel cellPanel = new JPanel(new BorderLayout());
            cellPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            String bloodGroup = value.toString();
            Color bloodColor = bloodGroupColors.getOrDefault(bloodGroup, SECONDARY_COLOR);
            
            // Blood drop icon
            JPanel iconPanel = new JPanel();
            iconPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            JLabel bloodIcon = new JLabel(); // Placeholder for icon
            iconPanel.add(bloodIcon);
            
            // Blood group text
            JLabel label = new JLabel(bloodGroup);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(bloodColor);
            
            cellPanel.add(iconPanel, BorderLayout.WEST);
            cellPanel.add(label, BorderLayout.CENTER);
            cellPanel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            
            return cellPanel;
        });
        
        // Status column renderer
        stockTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            String status = value.toString();
            JLabel statusLabel = new JLabel(status);
            statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            statusLabel.setOpaque(true);
            
            if (status.equalsIgnoreCase("Low")) {
                statusLabel.setBackground(new Color(255, 193, 7));
                statusLabel.setForeground(Color.BLACK);
            } else if (status.equalsIgnoreCase("Critical")) {
                statusLabel.setBackground(SECONDARY_COLOR);
                statusLabel.setForeground(Color.WHITE);
            } else {
                statusLabel.setBackground(ACCENT_COLOR);
                statusLabel.setForeground(Color.WHITE);
            }
            
            if (isSelected) {
                statusLabel.setBackground(table.getSelectionBackground());
                statusLabel.setForeground(table.getSelectionForeground());
            }
            
            return statusLabel;
        });
        
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableContainer.add(tableHeader, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tableContainer, "grow, span"); // Adjusted to take full width
        
        return panel;
    }

    private void loadBloodStock() {
        cardsPanel.removeAll(); // Clear cards panel
        
        // Get blood stock data
        List<BloodStock> stockList = bloodStockDAO.getAllBloodStock();
        
        // Debug: Log the size of the stock list
        System.out.println("Debug: Number of blood stock entries: " + stockList.size());

        // Clear and prepare table model
        DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
        model.setRowCount(0);
        
        // Add blood cards to the cards panel
        for (BloodStock stock : stockList) {
            // Debug: Log each blood stock entry
            System.out.println("Debug: Adding card for blood group: " + stock.getBloodGroup() + ", Quantity: " + stock.getQuantity());

            JPanel card = createBloodCard(stock);
            cardsPanel.add(card);
        }

        // Refresh the cards panel
        cardsPanel.revalidate();
        cardsPanel.repaint();

        // Process blood stock data
        for (BloodStock stock : stockList) {
            // Debug: Log each blood stock entry
            System.out.println("Debug: Processing blood group: " + stock.getBloodGroup() + ", Quantity: " + stock.getQuantity());

            // Add row to table
            String status = determineStockStatus(stock.getQuantity());
            model.addRow(new Object[]{
                stock.getBloodGroup(),
                stock.getQuantity() + " units",
                stock.getLastUpdated(),
                status
            });
        }
    }

    private JPanel createBloodCard(BloodStock stock) {
        int quantity = stock.getQuantity();
        String status = determineStockStatus(quantity);
        Color statusColor = ACCENT_COLOR; // Default normal
        
        if (status.equals("Low")) {
            statusColor = new Color(255, 193, 7); // Warning yellow
        } else if (status.equals("Critical")) {
            statusColor = SECONDARY_COLOR; // Danger red
        }
        
        JPanel card = new JPanel(new MigLayout("fill, insets 15", "[center]", "[]10[]10[]"));
        card.setBackground(Color.WHITE);
        card.setBorder(CARD_BORDER);
        
        // Blood group indicator
        JPanel bloodIndicator = new JPanel(new BorderLayout());
        bloodIndicator.setOpaque(false);
        
        // Blood drop icon
        Color bloodColor = bloodGroupColors.getOrDefault(stock.getBloodGroup(), SECONDARY_COLOR);
        JLabel bloodIconLabel = new JLabel(); // Placeholder for icon
        bloodIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Blood group label
        JLabel bloodGroupLabel = new JLabel(stock.getBloodGroup());
        bloodGroupLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        bloodGroupLabel.setForeground(bloodColor);
        bloodGroupLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Quantity with background
        JLabel quantityLabel = new JLabel(stock.getQuantity() + " units");
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantityLabel.setForeground(TEXT_COLOR);
        
        // Status indicator
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(statusColor);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        card.add(bloodIconLabel, "wrap");
        card.add(bloodGroupLabel, "wrap");
        card.add(quantityLabel, "wrap");
        card.add(statusLabel, "width 80!");

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
    
    private String determineStockStatus(int quantity) {
        if (quantity <= 2) {
            return "Critical";
        } else if (quantity <= 5) {
            return "Low";
        } else {
            return "Normal";
        }
    }
}