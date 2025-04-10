package com.bloodbank.model;

public class Donation {
    private int donationId;
    private int donorId;
    private String bloodGroup;
    private int quantity;
    private java.sql.Date donationDate;
    private String status;
    private int adminId;

    public Donation() {}

    public Donation(int donationId, int donorId, String bloodGroup, int quantity, 
                   String donationDate, String status, int adminId) {
        this.donationId = donationId;
        this.donorId = donorId;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.donationDate = java.sql.Date.valueOf(donationDate);
        this.status = status;
        this.adminId = adminId;
    }

    // Getters and Setters
    public int getDonationId() {
        return donationId;
    }

    public void setDonationId(int donationId) {
        this.donationId = donationId;
    }

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public java.sql.Date getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(java.sql.Date donationDate) {
        this.donationDate = donationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
} 