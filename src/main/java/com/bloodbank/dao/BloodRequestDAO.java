package com.bloodbank.dao;

import com.bloodbank.model.BloodRequest;
import com.bloodbank.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BloodRequestDAO {
    public boolean addBloodRequest(BloodRequest request) {
        String sql = "INSERT INTO blood_requests (recipient_name, blood_group, quantity, " +
                    "hospital_name, hospital_address, patient_condition, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, request.getRecipientName());
            pstmt.setString(2, request.getBloodGroup());
            pstmt.setInt(3, request.getQuantity());
            pstmt.setString(4, request.getHospitalName());
            pstmt.setString(5, request.getHospitalAddress());
            pstmt.setString(6, request.getPatientCondition());
            pstmt.setString(7, request.getStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<BloodRequest> getAllBloodRequests() {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests ORDER BY request_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                BloodRequest request = new BloodRequest();
                request.setRequestId(rs.getInt("request_id"));
                request.setRecipientName(rs.getString("recipient_name"));
                request.setBloodGroup(rs.getString("blood_group"));
                request.setQuantity(rs.getInt("quantity"));
                request.setHospitalName(rs.getString("hospital_name"));
                request.setHospitalAddress(rs.getString("hospital_address"));
                request.setPatientCondition(rs.getString("patient_condition"));
                request.setStatus(rs.getString("status"));
                request.setAdminId(rs.getInt("admin_id"));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<BloodRequest> getAllRequests() {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                requests.add(new BloodRequest(
                    rs.getInt("request_id"),
                    rs.getString("recipient_name"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("hospital_name"),
                    rs.getString("hospital_address"),
                    rs.getString("patient_condition"),
                    rs.getString("request_date"),
                    rs.getString("status"),
                    rs.getInt("admin_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public List<BloodRequest> getRequestsByStatus(String status) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(new BloodRequest(
                    rs.getInt("request_id"),
                    rs.getString("recipient_name"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("hospital_name"),
                    rs.getString("hospital_address"),
                    rs.getString("patient_condition"),
                    rs.getString("request_date"),
                    rs.getString("status"),
                    rs.getInt("admin_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public boolean updateRequestStatus(int requestId, String status, int adminId) {
        String sql = "UPDATE blood_requests SET status = ?, admin_id = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, adminId);
            pstmt.setInt(3, requestId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public BloodRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM blood_requests WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new BloodRequest(
                    rs.getInt("request_id"),
                    rs.getString("recipient_name"),
                    rs.getString("blood_group"),
                    rs.getInt("quantity"),
                    rs.getString("hospital_name"),
                    rs.getString("hospital_address"),
                    rs.getString("patient_condition"),
                    rs.getString("request_date"),
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