package emotionalsongs.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://emotionalsongs.c6qhwbtxtze6.eu-north-1.rds.amazonaws.com:5432/emotionalsongs?user=lukeluke&password=lukeluke";
    private static final String USER = "lukeluke";
    private static final String PASSWORD = "lukeluke";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}
