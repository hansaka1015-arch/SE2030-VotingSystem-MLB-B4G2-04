package com.votingsystem.dao;

import com.votingsystem.model.Contestant;
import com.votingsystem.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContestantDAO {

    private Contestant mapResultSet(ResultSet rs) throws SQLException {
        Contestant c = new Contestant();

        // 1. ID
        try {
            c.setId(rs.getInt("id"));
        } catch (SQLException e) {
            try {
                c.setId(rs.getInt("contestant_id"));
            } catch (SQLException ignored) {}
        }

        // 2. Contestant Code
        try {
            c.setContestantCode(rs.getString("contestant_code"));
        } catch (SQLException ignored) {}

        // 3. Show ID
        try {
            c.setShowId(rs.getInt("show_id"));
        } catch (SQLException ignored) {}

        // 4. Full Name
        try {
            c.setFullName(rs.getString("full_name"));
        } catch (SQLException e) {
            try {
                c.setFullName(rs.getString("name"));
            } catch (SQLException ignored) {}
        }

        // 5. Bio Summary
        try {
            c.setBioSummary(rs.getString("bio_summary"));
        } catch (SQLException e) {
            try {
                c.setBioSummary(rs.getString("bio"));
            } catch (SQLException ignored) {}
        }

        // 6. Profile Image URL
        try {
            c.setProfileImageUrl(rs.getString("profile_image_url"));
        } catch (SQLException e) {
            try {
                c.setProfileImageUrl(rs.getString("image_url"));
            } catch (SQLException ignored) {}
        }

        // 7. Status
        try {
            c.setStatus(rs.getString("status"));
        } catch (SQLException ignored) {}

        // 8. Timestamps
        try {
            c.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (SQLException ignored) {}
        try {
            c.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException ignored) {}

        return c;
    }

    public List<Contestant> getContestantsByShow(int showId) {
        List<Contestant> list = new ArrayList<>();
        String sql = "SELECT * FROM contestants WHERE show_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Contestant getContestantById(int id) {
        String sqlPrimary = "SELECT * FROM contestants WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPrimary)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            // Fallback for legacy column name
            String sqlFallback = "SELECT * FROM contestants WHERE contestant_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            } catch (SQLException ignored) {}
        }
        return null;
    }

    // Add new contestant (Create)
    public boolean addContestant(Contestant contestant) {
        String sqlPrimary = "INSERT INTO contestants (show_id, contestant_code, full_name, bio_summary, profile_image_url, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPrimary, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, contestant.getShowId() > 0 ? contestant.getShowId() : 1);
            stmt.setString(2, contestant.getContestantCode());
            stmt.setString(3, contestant.getFullName());
            stmt.setString(4, contestant.getBioSummary());
            stmt.setString(5, contestant.getProfileImageUrl());
            stmt.setString(6, contestant.getStatus() != null ? contestant.getStatus() : "ACTIVE");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contestant.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            // Fallback query for alternative schema column names
            String sqlFallback = "INSERT INTO contestants (contestant_code, name, bio, image_url, status, show_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                stmt.setString(1, contestant.getContestantCode());
                stmt.setString(2, contestant.getFullName());
                stmt.setString(3, contestant.getBioSummary());
                stmt.setString(4, contestant.getProfileImageUrl());
                stmt.setString(5, contestant.getStatus() != null ? contestant.getStatus() : "ACTIVE");
                stmt.setInt(6, contestant.getShowId() > 0 ? contestant.getShowId() : 1);

                return stmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    // Update full contestant details (Update)
    public boolean updateContestant(Contestant contestant) {
        String sqlPrimary = "UPDATE contestants SET contestant_code = ?, full_name = ?, bio_summary = ?, profile_image_url = ?, status = ?, show_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPrimary)) {
            stmt.setString(1, contestant.getContestantCode());
            stmt.setString(2, contestant.getFullName());
            stmt.setString(3, contestant.getBioSummary());
            stmt.setString(4, contestant.getProfileImageUrl());
            stmt.setString(5, contestant.getStatus());
            stmt.setInt(6, contestant.getShowId() > 0 ? contestant.getShowId() : 1);
            stmt.setInt(7, contestant.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlFallback = "UPDATE contestants SET contestant_code = ?, name = ?, bio = ?, image_url = ?, status = ?, show_id = ?, updated_at = CURRENT_TIMESTAMP WHERE contestant_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                stmt.setString(1, contestant.getContestantCode());
                stmt.setString(2, contestant.getFullName());
                stmt.setString(3, contestant.getBioSummary());
                stmt.setString(4, contestant.getProfileImageUrl());
                stmt.setString(5, contestant.getStatus());
                stmt.setInt(6, contestant.getShowId() > 0 ? contestant.getShowId() : 1);
                stmt.setInt(7, contestant.getId());

                return stmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    // Update contestant status (ACTIVE, SAFE, AT RISK, ELIMINATED)
    public boolean updateContestantStatus(int contestantId, String newStatus) {
        String sqlPrimary = "UPDATE contestants SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPrimary)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, contestantId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlFallback = "UPDATE contestants SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE contestant_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                stmt.setString(1, newStatus);
                stmt.setInt(2, contestantId);

                return stmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    // Update contestant bio
    public boolean updateContestantBio(int contestantId, String newBio) {
        String sqlPrimary = "UPDATE contestants SET bio_summary = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPrimary)) {
            stmt.setString(1, newBio);
            stmt.setInt(2, contestantId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlFallback = "UPDATE contestants SET bio = ?, updated_at = CURRENT_TIMESTAMP WHERE contestant_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                stmt.setString(1, newBio);
                stmt.setInt(2, contestantId);

                return stmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    // Delete contestant (Delete)
    public boolean deleteContestant(int contestantId) {
        String sqlPrimary = "DELETE FROM contestants WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPrimary)) {
            stmt.setInt(1, contestantId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlFallback = "DELETE FROM contestants WHERE contestant_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFallback)) {
                stmt.setInt(1, contestantId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}