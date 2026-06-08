package com.findit.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException

public class DBConnection {

    public static Connection connect() {

        try {

            String url =
                    "jdbc:postgresql://db.nowzolfypepurbxpjbwt.supabase.co:5432/postgres";

            String user =
                    "postgres";

            String password =
                    "vyb5gHtawnyWTGhY";

            Connection conn =
                    DriverManager.getConnection(url, user, password);

            System.out.println("Database Connected Successfully!");
            return conn;

        } catch (Exception e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
}
