package com.bloodbank.model;

public class BloodRequest {
    private int requestId;
    private String recipientName;
    private String bloodGroup;
    private int quantity;
    private String hospitalName;
    private String hospitalAddress;
    private String patientCondition;
    private String requestDate;
    private String status;
    private int adminId;

    public BloodRequest() {}

    public BloodRequest(int requestId, String recipientName, String bloodGroup, int quantity,
                       String hospitalName, String hospitalAddress, String patientCondition,
                       String requestDate, String status, int adminId) {
        this.requestId = requestId;
        this.recipientName = recipientName;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.patientCondition = patientCondition;
        this.requestDate = requestDate;
        this.status = status;
        this.adminId = adminId;
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
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

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getPatientCondition() {
        return patientCondition;
    }

    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
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