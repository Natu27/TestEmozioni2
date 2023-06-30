package emotionalsongs.backend;

import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "emotionalsongs.c6qhwbtxtze6.eu-north-1.rds.amazonaws.com:5432";
    private static final String USER = "lukeluke";
    private static final String PASSWORD = "lukeluke";

    private Jdbc3PoolingDataSource connection = null;


    public Connection getConnection() throws SQLException {
        if (connection == null)
            initConnection();
        return connection.getConnection();
    }

    private void initConnection() {
        connection = new Jdbc3PoolingDataSource();
        connection.setDataSourceName("EM. songs");
        connection.setServerName(DB_URL);
        connection.setDatabaseName("emotionalsongs");
        connection.setUser(USER);
        connection.setPassword(PASSWORD);
        connection.setMaxConnections(10);
    }
}