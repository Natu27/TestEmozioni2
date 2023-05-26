package emotionalsongs.backend;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;

import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class ServerES implements Servizi {
    private final static int PORT = 10002;

    public static <Servizi extends Remote> void main(String[] args) throws RemoteException {
        ServerES serverES = new ServerES();
        Servizi skel = (Servizi) UnicastRemoteObject.exportObject(serverES, PORT);
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("ServiziES", skel);
        System.out.println("Server RMI OK");
    }

    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year) throws NessunaCanzoneTrovata {
        List<Canzone> result = new ArrayList<>();
        if(titoloDaCercare.equals("") && autoreDaCercare.equals("") && year == null)
            throw new NessunaCanzoneTrovata();
        String query = "SELECT * FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER('%" + titoloDaCercare + "%') " +
                "AND LOWER(autore) LIKE LOWER('%" + autoreDaCercare + "%')";
        if(year != null) {
            query = query + " AND anno = " + year;
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
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
        if(result.isEmpty()) throw new NessunaCanzoneTrovata();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    public void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password ){
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO public.\"User\" (nome, cognome, username, hashed_password, indirizzo, cf, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, username);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, indirizzo);
            stmt.setString(6, codiceFiscale);
            stmt.setString(7, email);

            int utentiRegistrati = stmt.executeUpdate();

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
