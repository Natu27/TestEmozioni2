package backend;

import com.vaadin.flow.data.provider.ListDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CanzoneService {
    @Autowired
    CanzoneRepository songRepository;

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    private final String tabella = "\"Canzoni\"";
    public ListDataProvider<Canzone> findAll() {
        try {
            ListDataProvider<Canzone> dataProvider = new ListDataProvider<>(new ArrayList<>());
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM public." + tabella;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int anno = rs.getInt("anno");
                String autore = rs.getString("autore");
                String codice = rs.getString("codice");
                String titolo = rs.getString("titolo");
                Canzone canzone = new Canzone(titolo, autore, anno);
                dataProvider.getItems().add(canzone);
                return dataProvider;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public void save(Canzone song) {
        songRepository.save(song);
    }

    public void delete(Canzone song) {
        songRepository.delete(song);
    }
}
