package emotionalsongs.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientES {

    public List<Canzone> findAll() {
        List<Canzone> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("Select * from public.\"Canzoni\"");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Canzone canzone = new Canzone(
                        rs.getInt("Canzoni_id"),
                        rs.getInt("anno"),
                        rs.getString("autore"),
                        rs.getString("titolo"),
                        rs.getString("codice")
                        );
                result.add(canzone);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
