package com.bloodbank.dao;

import com.bloodbank.model.BloodStock;
import com.bloodbank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BloodStockDAO {
    public List<BloodStock> getAllBloodStock() {
        List<BloodStock> stocks = new ArrayList<>();
        String sql = "SELECT * FROM blood_stock";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                stocks.add(new BloodStock(
                    rs.getInt("stock_id"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("last_updated")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocks;
    }

    public BloodStock getBloodStockByGroup(String bloodGroup) {
        String sql = "SELECT * FROM blood_stock WHERE blood_group = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bloodGroup);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new BloodStock(
                    rs.getInt("stock_id"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("last_updated")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateBloodStock(String bloodGroup, int quantity) {
        String sql = "UPDATE blood_stock SET quantity = ?, last_updated = ? WHERE blood_group = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setInt(1, quantity);
            pstmt.setString(2, currentTime);
            pstmt.setString(3, bloodGroup);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addBloodStock(String bloodGroup, int quantity) {
        String sql = "INSERT INTO blood_stock (blood_group, quantity, last_updated) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(1, bloodGroup);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, currentTime);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalBloodStock() {
        String sql = "SELECT SUM(quantity) as total FROM blood_stock";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
} 