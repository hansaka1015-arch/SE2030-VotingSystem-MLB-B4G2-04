package com.votesphere.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Transaction Model Bean representing financial payments & credit log entries.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private int userId;
    private Integer packageId;
    private String packageName; // Helper for UI display
    private double amountLkr;
    private int creditsAdded;
    private String paymentMethod;
    private String status; // 'COMPLETED', 'PENDING', 'FAILED'
    private Timestamp transactionDate;

    public Transaction() {}

    public Transaction(String transactionId, int userId, Integer packageId, double amountLkr, 
                       int creditsAdded, String paymentMethod, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.packageId = packageId;
        this.amountLkr = amountLkr;
        this.creditsAdded = creditsAdded;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public double getAmountLkr() { return amountLkr; }
    public void setAmountLkr(double amountLkr) { this.amountLkr = amountLkr; }

    public int getCreditsAdded() { return creditsAdded; }
    public void setCreditsAdded(int creditsAdded) { this.creditsAdded = creditsAdded; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Timestamp transactionDate) { this.transactionDate = transactionDate; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", userId=" + userId +
                ", amountLkr=" + amountLkr +
                ", creditsAdded=" + creditsAdded +
                ", status='" + status + '\'' +
                '}';
    }
}
