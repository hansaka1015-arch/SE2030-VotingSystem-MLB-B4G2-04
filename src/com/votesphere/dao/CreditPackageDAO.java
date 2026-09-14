package com.votesphere.dao;

import com.votesphere.model.CreditPackage;
import com.votesphere.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CreditPackageDAO - Handles database operations for credit package tiers.
 */
public class CreditPackageDAO {

    /**
     * Retrieves all active credit package tiers sorted by price.
     * @return List of CreditPackage objects
     */
    public List<CreditPackage> getAllActivePackages() {
        List<CreditPackage> packages = new ArrayList<>();
        String sql = "SELECT package_id, name, price_lkr, credit_amount, bonus_credits, is_active, created_at " +
                     "FROM credit_packages WHERE is_active = TRUE ORDER BY price_lkr ASC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CreditPackage pkg = new CreditPackage();
                pkg.setPackageId(rs.getInt("package_id"));
                pkg.setName(rs.getString("name"));
                pkg.setPriceLkr(rs.getDouble("price_lkr"));
                pkg.setCreditAmount(rs.getInt("credit_amount"));
                pkg.setBonusCredits(rs.getInt("bonus_credits"));
                pkg.setActive(rs.getBoolean("is_active"));
                pkg.setCreatedAt(rs.getTimestamp("created_at"));
                packages.add(pkg);
            }
        } catch (SQLException e) {
            System.err.println("[CreditPackageDAO Error] Failed to fetch active packages: " + e.getMessage());
        }
        return packages;
    }

    /**
     * Retrieves a credit package by its ID.
     * @param packageId ID of the package
     * @return CreditPackage object or null if not found
     */
    public CreditPackage getPackageById(int packageId) {
        String sql = "SELECT package_id, name, price_lkr, credit_amount, bonus_credits, is_active, created_at " +
                     "FROM credit_packages WHERE package_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, packageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CreditPackage pkg = new CreditPackage();
                    pkg.setPackageId(rs.getInt("package_id"));
                    pkg.setName(rs.getString("name"));
                    pkg.setPriceLkr(rs.getDouble("price_lkr"));
                    pkg.setCreditAmount(rs.getInt("credit_amount"));
                    pkg.setBonusCredits(rs.getInt("bonus_credits"));
                    pkg.setActive(rs.getBoolean("is_active"));
                    pkg.setCreatedAt(rs.getTimestamp("created_at"));
                    return pkg;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CreditPackageDAO Error] Failed to fetch package by ID: " + e.getMessage());
        }
        return null;
    }
}
