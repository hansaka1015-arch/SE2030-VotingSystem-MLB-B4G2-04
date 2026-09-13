package com.votingsystem.dao;

import com.votingsystem.model.Contestant;
import com.votingsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContestantDAO {

    // Retrieve all contestants for a specific show
    public List<Contestant> getContestantsByShow(int showId) {
        List<Contestant> list = new ArrayList<>();
        String sql = "SELECT * FROM contestants WHERE show_id = ? ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Contestant c = new Contestant();
                c.setId(rs.getInt("id"));
                c.setContestantCode(rs.getString("contestant_code"));
                c.setFullName(rs.getString("full_name"));
                c.setBioSummary(rs.getString("bio_summary"));
                c.setProfileImageUrl(rs.getString("profile_image_url"));
                c.setStatus(rs.getString("status"));
                c.setShowId(rs.getInt("show_id"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add new contestant
    public boolean addContestant(Contestant contestant) {
        String sql = "INSERT INTO contestants (contestant_code, full_name, bio_summary, profile_image_url, status, show_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, contestant.getContestantCode());
            stmt.setString(2, contestant.getFullName());
            stmt.setString(3, contestant.getBioSummary());
            stmt.setString(4, contestant.getProfileImageUrl());
            stmt.setString(5, contestant.getStatus());
            stmt.setInt(6, contestant.getShowId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update contestant status (ACTIVE, SAFE, AT RISK, ELIMINATED)
    public boolean updateContestantStatus(int contestantId, String newStatus) {
        String sql = "UPDATE contestants SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, contestantId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update contestant bio
    public boolean updateContestantBio(int contestantId, String newBio) {
        String sql = "UPDATE contestants SET bio_summary = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newBio);
            stmt.setInt(2, contestantId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
