package com.votesphere.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * UserCredit Model Bean representing user wallet balance.
 */
public class UserCredit implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private int balance;
    private Timestamp updatedAt;

    public UserCredit() {}

    public UserCredit(int userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserCredit{" +
                "userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
