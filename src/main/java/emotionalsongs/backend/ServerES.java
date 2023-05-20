package emotionalsongs.backend;

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
import emotionalsongs.backend.entities.Canzone;
import java.util.List;

public class ServerES implements Servizi {
    private final static int PORT = 10002;

    public static <Servizi extends Remote> void main(String[] args) throws RemoteException {
        ServerES serverES = new ServerES();
        Servizi skel = (Servizi) UnicastRemoteObject.exportObject(serverES, PORT);
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("ServiziES", skel);
        System.out.println("Server RMI OK");
    }

    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year) {
        List<Canzone> result = new ArrayList<>();
        if(titoloDaCercare.equals("") && autoreDaCercare.equals("") && year == null)
            return new ArrayList<>();
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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
