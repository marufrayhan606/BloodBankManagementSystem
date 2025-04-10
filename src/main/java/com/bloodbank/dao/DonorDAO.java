package com.bloodbank.dao;

import com.bloodbank.model.Donor;
import com.bloodbank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {
    public boolean addDonor(Donor donor) {
        String sql = "INSERT INTO donors (user_id, name, blood_group, date_of_birth, gender, phone, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, donor.getUserId());
            pstmt.setString(2, donor.getName());
            pstmt.setString(3, donor.getBloodGroup());
            pstmt.setString(4, donor.getDateOfBirth());
            pstmt.setString(5, donor.getGender());
            pstmt.setString(6, donor.getPhone());
            pstmt.setString(7, donor.getAddress());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Donor getDonorByUserId(int userId) {
        String sql = "SELECT * FROM donors WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Donor(
                    rs.getInt("donor_id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("blood_group"),
                    rs.getString("date_of_birth"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("last_donation_date")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                donors.add(new Donor(
                    rs.getInt("donor_id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("blood_group"),
                    rs.getString("date_of_birth"),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("last_donation_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
    }

    public boolean updateDonor(Donor donor) {
        String sql = "UPDATE donors SET name = ?, blood_group = ?, date_of_birth = ?, " +
                    "gender = ?, phone = ?, address = ? WHERE donor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, donor.getName());
            pstmt.setString(2, donor.getBloodGroup());
            pstmt.setString(3, donor.getDateOfBirth());
            pstmt.setString(4, donor.getGender());
            pstmt.setString(5, donor.getPhone());
            pstmt.setString(6, donor.getAddress());
            pstmt.setInt(7, donor.getDonorId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateLastDonationDate(int donorId, java.sql.Date lastDonationDate) {
        String sql = "UPDATE donors SET last_donation_date = ? WHERE donor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, lastDonationDate);
            pstmt.setInt(2, donorId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 