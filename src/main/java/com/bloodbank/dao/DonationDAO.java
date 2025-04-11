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
        String checkDonorSql = "SELECT COUNT(*) FROM donors WHERE donor_id = ?";
        String insertDonationSql = "INSERT INTO donations (donor_id, blood_group, quantity, donation_date, status) " +
                                   "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkDonorStmt = conn.prepareStatement(checkDonorSql);
             PreparedStatement insertDonationStmt = conn.prepareStatement(insertDonationSql)) {

            // Validate donor_id exists
            checkDonorStmt.setInt(1, donation.getDonorId());
            ResultSet rs = checkDonorStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                System.err.println("Donor ID " + donation.getDonorId() + " does not exist.");
                return false;
            }

            // Insert donation
            insertDonationStmt.setInt(1, donation.getDonorId());
            insertDonationStmt.setString(2, donation.getBloodGroup());
            insertDonationStmt.setInt(3, donation.getQuantity());
            insertDonationStmt.setDate(4, donation.getDonationDate());
            insertDonationStmt.setString(5, donation.getStatus());

            return insertDonationStmt.executeUpdate() > 0;
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