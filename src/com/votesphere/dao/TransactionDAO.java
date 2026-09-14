package com.votesphere.dao;

import com.votesphere.model.Transaction;
import com.votesphere.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionDAO - Handles database operations for payment transactions and credit logs.
 */
public class TransactionDAO {

    /**
     * Records a new transaction into the database.
     * @param txn Transaction object containing details
     * @return true if successfully inserted, false otherwise
     */
    public boolean recordTransaction(Transaction txn) {
        String sql = "INSERT INTO transactions (transaction_id, user_id, package_id, amount_lkr, credits_added, payment_method, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, txn.getTransactionId());
            ps.setInt(2, txn.getUserId());
            if (txn.getPackageId() != null && txn.getPackageId() > 0) {
                ps.setInt(3, txn.getPackageId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setDouble(4, txn.getAmountLkr());
            ps.setInt(5, txn.getCreditsAdded());
            ps.setString(6, txn.getPaymentMethod());
            ps.setString(7, txn.getStatus() != null ? txn.getStatus() : "COMPLETED");

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[TransactionDAO Error] Failed to record transaction: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches all transaction history logs for a specific user.
     * @param userId User ID
     * @return List of Transaction objects sorted by date descending
     */
    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.user_id, t.package_id, cp.name AS package_name, " +
                     "t.amount_lkr, t.credits_added, t.payment_method, t.status, t.transaction_date " +
                     "FROM transactions t " +
                     "LEFT JOIN credit_packages cp ON t.package_id = cp.package_id " +
                     "WHERE t.user_id = ? ORDER BY t.transaction_date DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction txn = new Transaction();
                    txn.setTransactionId(rs.getString("transaction_id"));
                    txn.setUserId(rs.getInt("user_id"));
                    int pkgId = rs.getInt("package_id");
                    txn.setPackageId(rs.wasNull() ? null : pkgId);
                    txn.setPackageName(rs.getString("package_name"));
                    txn.setAmountLkr(rs.getDouble("amount_lkr"));
                    txn.setCreditsAdded(rs.getInt("credits_added"));
                    txn.setPaymentMethod(rs.getString("payment_method"));
                    txn.setStatus(rs.getString("status"));
                    txn.setTransactionDate(rs.getTimestamp("transaction_date"));
                    transactions.add(txn);
                }
            }

        } catch (SQLException e) {
            System.err.println("[TransactionDAO Error] Failed to fetch transactions for user: " + e.getMessage());
        }
        return transactions;
    }

    /**
     * Retrieves a single transaction record by its unique reference ID.
     * @param transactionId Transaction ID (e.g. VSL-TXN-9842105)
     * @return Transaction object or null
     */
    public Transaction getTransactionById(String transactionId) {
        String sql = "SELECT t.transaction_id, t.user_id, t.package_id, cp.name AS package_name, " +
                     "t.amount_lkr, t.credits_added, t.payment_method, t.status, t.transaction_date " +
                     "FROM transactions t " +
                     "LEFT JOIN credit_packages cp ON t.package_id = cp.package_id " +
                     "WHERE t.transaction_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction txn = new Transaction();
                    txn.setTransactionId(rs.getString("transaction_id"));
                    txn.setUserId(rs.getInt("user_id"));
                    int pkgId = rs.getInt("package_id");
                    txn.setPackageId(rs.wasNull() ? null : pkgId);
                    txn.setPackageName(rs.getString("package_name"));
                    txn.setAmountLkr(rs.getDouble("amount_lkr"));
                    txn.setCreditsAdded(rs.getInt("credits_added"));
                    txn.setPaymentMethod(rs.getString("payment_method"));
                    txn.setStatus(rs.getString("status"));
                    txn.setTransactionDate(rs.getTimestamp("transaction_date"));
                    return txn;
                }
            }

        } catch (SQLException e) {
            System.err.println("[TransactionDAO Error] Failed to fetch transaction by ID: " + e.getMessage());
        }
        return null;
    }
}
