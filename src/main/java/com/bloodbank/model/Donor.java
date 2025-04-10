package com.bloodbank.model;

public class Donor {
    private int donorId;
    private int userId;
    private String name;
    private String bloodGroup;
    private String dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String lastDonationDate;

    public Donor() {}

    public Donor(int donorId, int userId, String name, String bloodGroup, String dateOfBirth, 
                String gender, String phone, String address, String lastDonationDate) {
        this.donorId = donorId;
        this.userId = userId;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.lastDonationDate = lastDonationDate;
    }

    // Getters and Setters
    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(String lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }
} 