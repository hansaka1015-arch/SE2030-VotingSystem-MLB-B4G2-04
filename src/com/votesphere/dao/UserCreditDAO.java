package com.votesphere.dao;

import com.votesphere.model.UserCredit;
import com.votesphere.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserCreditDAO - Handles credit balance top-ups, deductions, and wallet queries.
 */
public class UserCreditDAO {

    /**
     * Gets the credit balance for a user. Initializes balance row if not existing.
     * @param userId User ID
     * @return UserCredit object containing current balance
     */
    public UserCredit getUserCreditBalance(int userId) {
        String sql = "SELECT user_id, balance, updated_at FROM user_credits WHERE user_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserCredit uc = new UserCredit();
                    uc.setUserId(rs.getInt("user_id"));
                    uc.setBalance(rs.getInt("balance"));
                    uc.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return uc;
                } else {
                    // Create default row for new user
                    createInitialWallet(userId);
                    return new UserCredit(userId, 0);
                }
            }

        } catch (SQLException e) {
            System.err.println("[UserCreditDAO Error] Failed to fetch credit balance: " + e.getMessage());
            return new UserCredit(userId, 0);
        }
    }

    /**
     * Creates an initial credit wallet row for a new user.
     * @param userId User ID
     */
    private boolean createInitialWallet(int userId) {
        String sql = "INSERT INTO user_credits (user_id, balance) VALUES (?, 0) " +
                     "ON DUPLICATE KEY UPDATE user_id = user_id";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserCreditDAO Error] Failed to create initial wallet: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atomically adds credits to a user's wallet balance.
     * @param userId User ID
     * @param creditsToAdd Number of credits to add
     * @return true if successful
     */
    public boolean addCreditsToUser(int userId, int creditsToAdd) {
        String sql = "INSERT INTO user_credits (user_id, balance) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE balance = balance + ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, creditsToAdd);
            ps.setInt(3, creditsToAdd);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[UserCreditDAO Error] Failed to add credits to user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deducts credits from user's wallet balance if sufficient balance exists.
     * @param userId User ID
     * @param creditsToDeduct Number of credits to deduct
     * @return true if successful, false if insufficient balance or error
     */
    public boolean deductCreditsFromUser(int userId, int creditsToDeduct) {
        String sql = "UPDATE user_credits SET balance = balance - ? WHERE user_id = ? AND balance >= ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, creditsToDeduct);
            ps.setInt(2, userId);
            ps.setInt(3, creditsToDeduct);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[UserCreditDAO Error] Failed to deduct credits: " + e.getMessage());
            return false;
        }
    }
}
