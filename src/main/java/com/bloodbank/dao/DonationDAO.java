package com.bloodbank.dao;

import com.bloodbank.model.Donation;
import com.bloodbank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {
    public boolean addDonation(Donation donation) {
        String sql = "INSERT INTO donations (donor_id, blood_group, quantity, donation_date, status) " +
                    "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, donation.getDonorId());
            pstmt.setString(2, donation.getBloodGroup());
            pstmt.setInt(3, donation.getQuantity());
            pstmt.setDate(4, donation.getDonationDate());
            pstmt.setString(5, donation.getStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Donation> getAllDonations() {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donations";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                donations.add(new Donation(
                    rs.getInt("donation_id"),
                    rs.getInt("donor_id"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("donation_date"),
                    rs.getString("status"),
                    rs.getInt("admin_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public List<Donation> getDonationsByDonorId(int donorId) {
        List<Donation> donations = new ArrayList<>();
        String sql = "SELECT * FROM donations WHERE donor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, donorId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                donations.add(new Donation(
                    rs.getInt("donation_id"),
                    rs.getInt("donor_id"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("donation_date"),
                    rs.getString("status"),
                    rs.getInt("admin_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donations;
    }

    public boolean updateDonationStatus(int donationId, String status, int adminId) {
        String sql = "UPDATE donations SET status = ?, admin_id = ? WHERE donation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, adminId);
            pstmt.setInt(3, donationId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Donation getDonationById(int donationId) {
        String sql = "SELECT * FROM donations WHERE donation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, donationId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Donation(
                    rs.getInt("donation_id"),
                    rs.getInt("donor_id"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("donation_date"),
                    rs.getString("status"),
                    rs.getInt("admin_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
} 