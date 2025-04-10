package com.bloodbank.model;

public class BloodStock {
    private int stockId;
    private String bloodGroup;
    private int quantity;
    private String lastUpdated;

    public BloodStock() {}

    public BloodStock(int stockId, String bloodGroup, int quantity, String lastUpdated) {
        this.stockId = stockId;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 