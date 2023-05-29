package emotionalsongs.backend;

import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;
import emotionalsongs.backend.exceptions.Utente.PasswordErrata;
import emotionalsongs.backend.exceptions.Utente.UsernameErrato;
import emotionalsongs.backend.exceptions.Utente.UsernameNotFound;
import org.mindrot.jbcrypt.BCrypt;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class ServerES implements Servizi {
    private final static int PORT = 10002;

    public static <Servizi extends Remote> void main(String[] args) throws RemoteException {
        ServerES serverES = new ServerES();
        Servizi skel = (Servizi) UnicastRemoteObject.exportObject(serverES, PORT);
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("ServiziES", skel);
        System.out.println("Server RMI OK");
    }

    /**
     * @param titoloDaCercare
     * @param autoreDaCercare
     * @param year
     * @return
     * @throws NessunaCanzoneTrovata
     */
    @Override
    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year) throws NessunaCanzoneTrovata {
        List<Canzone> result = new ArrayList<>();
        if (titoloDaCercare.equals("") && autoreDaCercare.equals("") && year == null)
            throw new NessunaCanzoneTrovata();
        String query = "SELECT * FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER('%" + titoloDaCercare + "%') " +
                "AND LOWER(autore) LIKE LOWER('%" + autoreDaCercare + "%')";
        if (year != null) {
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
            if (result.isEmpty()) throw new NessunaCanzoneTrovata();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param nome
     * @param cognome
     * @param indirizzo
     * @param codiceFiscale
     * @param email
     * @param username
     * @param password
     * @throws RemoteException
     */
    @Override
    public void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws RemoteException {
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

    /**
     *
     */
    @Override
    public List<Integer> getAnni() throws RemoteException {

        List<Integer> result = new ArrayList<>();
        String query = "SELECT distinct anno FROM public.\"Canzoni\" order by anno asc ;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(rs.getInt("anno"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean login(String userid, String password) throws UsernameNotFound, PasswordErrata, UsernameErrato, RemoteException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "SELECT * FROM public.\"User\" WHERE username = '" + userid + "'";
        System.out.println(userid);
        String username = "";
        String pass = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) throw new UsernameNotFound();
            while (rs.next()) {
                username = rs.getString("username");
                System.out.println(username);
                pass = rs.getString("hashed_password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!userid.equals(username)) throw new UsernameErrato();
        if (!pass.equals(hashedPassword)) throw new PasswordErrata();
        return true;
    }
}
