package com.example.findit.dao;

import com.example.findit.model.ActivityLog;
import com.example.findit.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {
    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Manila");

    public void log(int userId, String action, String description) {
        String sql = """
        INSERT INTO activity_logs
        (user_id, action, description, created_at)
        VALUES (?, ?, ?, ?)
        """;

        try {
            DatabaseBootstrap.ensureApplicationSchema();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, description);
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(
                    LocalDateTime.now(APP_ZONE)
            ));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordIngressEgress(int userId, String eventType, String description) {
        log(userId, eventType, description);
    }
    public List<ActivityLog> getIngressEgressLogs() {
        List<ActivityLog> list = new ArrayList<>();
        String sql = """
                SELECT * FROM activity_logs
                WHERE action IN ('CHECK_IN', 'CHECK_OUT')
                ORDER BY created_at DESC
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setAction(rs.getString("action"));
                log.setDescription(rs.getString("description"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                log.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
                list.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countTodayByAction(String action) {
        String sql = """
                SELECT COUNT(*) FROM activity_logs
                WHERE action = ?
                  AND created_at >= ?
                  AND created_at < ?
                """;
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDate today = LocalDate.now(APP_ZONE);
            ps.setString(1, action);
            ps.setTimestamp(2, Timestamp.valueOf(today.atStartOfDay()));
            ps.setTimestamp(3, Timestamp.valueOf(today.plusDays(1).atStartOfDay()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
