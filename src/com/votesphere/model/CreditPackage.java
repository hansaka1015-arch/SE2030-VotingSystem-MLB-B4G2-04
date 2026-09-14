package com.votesphere.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * CreditPackage Model Bean representing voting package tiers.
 */
public class CreditPackage implements Serializable {
    private static final long serialVersionUID = 1L;

    private int packageId;
    private String name;
    private double priceLkr;
    private int creditAmount;
    private int bonusCredits;
    private boolean active;
    private Timestamp createdAt;

    public CreditPackage() {}

    public CreditPackage(int packageId, String name, double priceLkr, int creditAmount, int bonusCredits, boolean active) {
        this.packageId = packageId;
        this.name = name;
        this.priceLkr = priceLkr;
        this.creditAmount = creditAmount;
        this.bonusCredits = bonusCredits;
        this.active = active;
    }

    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPriceLkr() { return priceLkr; }
    public void setPriceLkr(double priceLkr) { this.priceLkr = priceLkr; }

    public int getCreditAmount() { return creditAmount; }
    public void setCreditAmount(int creditAmount) { this.creditAmount = creditAmount; }

    public int getBonusCredits() { return bonusCredits; }
    public void setBonusCredits(int bonusCredits) { this.bonusCredits = bonusCredits; }

    public int getTotalCredits() {
        return creditAmount + bonusCredits;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "CreditPackage{" +
                "packageId=" + packageId +
                ", name='" + name + '\'' +
                ", priceLkr=" + priceLkr +
                ", totalCredits=" + getTotalCredits() +
                '}';
    }
}
