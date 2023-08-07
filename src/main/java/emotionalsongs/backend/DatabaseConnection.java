package emotionalsongs.backend;

import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String DB_URL;
    private static String USER;
    private static String PASSWORD;

    private Jdbc3PoolingDataSource connection = null;


    public Connection getConnection() throws SQLException {
        if (connection == null)
            initConnection();
        return connection.getConnection();
    }

    private void initConnection() {
        try {
            setConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection = new Jdbc3PoolingDataSource();
        connection.setDataSourceName("EM. songs");
        connection.setServerName(DB_URL);
        connection.setDatabaseName("emotionalsongs");
        connection.setUser(USER);
        connection.setPassword(PASSWORD);
        //connection.setMaxConnections(10);
    }

    private void setConnection() throws IOException {
        String fname = "application.properties";

    // Ottenere l'InputStream per il file di risorse
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fname);

    // Creare un BufferedReader per leggere il contenuto
        assert is != null;
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

    // Leggere il file riga per riga e cercare gli attributi di connessione
        String line;
        while ((line = buf.readLine()) != null) {
            // Effettuare l'elaborazione delle righe per trovare gli attributi di connessione
            if (line.startsWith("spring.datasource.url")) {
                DB_URL = line.split("=")[1].trim();
                // Utilizza il valore di dbUrl per la connessione al database
                // ...
            } else if (line.startsWith("spring.datasource.username")) {
                USER = line.split("=")[1].trim();
                // Utilizza il valore di username per la connessione al database
                // ...
            } else if (line.startsWith("spring.datasource.password")) {
                PASSWORD = line.split("=")[1].trim();
                // Utilizza il valore di password per la connessione al database
                // ...
            }
        }

    // Chiudere il BufferedReader e l'InputStream
        buf.close();
        is.close();
    }
}