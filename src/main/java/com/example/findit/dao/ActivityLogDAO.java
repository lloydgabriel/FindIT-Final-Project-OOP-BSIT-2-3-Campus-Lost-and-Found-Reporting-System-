package com.example.findit.dao;
import com.example.findit.util.DBConnection;
import java.sql.*;

public class ActivityLogDAO {

    public void log(
            int userId,
            String action,
            String description){

        String sql =
                """
                INSERT INTO activity_logs
                (user_id, action, description)
                VALUES (?, ?, ?)
                """;

        try(Connection conn =
                    DBConnection.connect();

            PreparedStatement ps =
                    conn.prepareStatement(sql)){

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, description);

            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}