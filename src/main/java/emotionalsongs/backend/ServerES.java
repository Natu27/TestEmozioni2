package emotionalsongs.backend;

import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Emozione;
import emotionalsongs.backend.entities.Playlist;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;
import emotionalsongs.backend.exceptions.utente.PasswordErrata;
import emotionalsongs.backend.exceptions.utente.UsernameErrato;
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


public class ServerES implements Servizi {
    private final static int PORT = 10002;
    DatabaseConnection dbConn;

    public ServerES(DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public static <Servizi extends Remote> void main(String[] ignoredArgs) throws RemoteException {
        DatabaseConnection dbConn = new DatabaseConnection();
        ServerES serverES = new ServerES(dbConn);
        Servizi skel = (Servizi) UnicastRemoteObject.exportObject(serverES, PORT);
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("ServiziES", skel);
        System.out.println("Server RMI OK");
    }


    @Override
    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year) throws NessunaCanzoneTrovata {
        List<Canzone> result = new ArrayList<>();
        //if (titoloDaCercare.equals("") && autoreDaCercare.equals("") && year == null) throw new NessunaCanzoneTrovata();
        String query = "SELECT * FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER('%" + titoloDaCercare + "%') " + "AND LOWER(autore) LIKE LOWER('%" + autoreDaCercare + "%')";
        if (year != null) {
            query = query + " AND anno = " + year;
        }
        query = query + " LIMIT 300";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                //System.out.println(canzone.getId());
                result.add(canzone);
            }
            if (result.isEmpty()) throw new NessunaCanzoneTrovata();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year, ArrayList<Canzone> braniDaEscludere) throws NessunaCanzoneTrovata, RemoteException {
        List<Canzone> result = new ArrayList<>();
        ArrayList<Integer> idNotToFind = new ArrayList<>();
        for (Canzone c : braniDaEscludere) {
            idNotToFind.add(c.getId());
        }

        StringBuilder query = new StringBuilder("SELECT * FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER('%" + titoloDaCercare + "%') " + "AND LOWER(autore) LIKE LOWER('%" + autoreDaCercare + "%')");
        if (year != null) {
            query.append(" AND anno = ").append(year);
        }
        if (!idNotToFind.isEmpty()) {
            query.append("AND \"Canzoni_id\" NOT IN(");
        }
        for (int id : idNotToFind) {
            if (idNotToFind.toArray()[idNotToFind.size() - 1].equals(id))
                query.append(id).append(")");
            else
                query.append(id).append(",");
        }
        if (titoloDaCercare.equals("") && autoreDaCercare.equals(""))
            query.append(" LIMIT 300;");
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query.toString()); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                //System.out.println(canzone.getId());
                result.add(canzone);
            }
            if (result.isEmpty()) throw new NessunaCanzoneTrovata();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws RemoteException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO public.\"User\" (nome, cognome, username, hashed_password, indirizzo, cf, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, username);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, indirizzo);
            stmt.setString(6, codiceFiscale);
            stmt.setString(7, email);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws RemoteException {

        List<Integer> result = new ArrayList<>();
        String query = "SELECT distinct anno FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER('%" + titoloDaCercare + "%') " + "AND LOWER(autore) LIKE LOWER('%" + autoreDaCercare + "%') order by anno asc;";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getInt("anno"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String login(String userid, String password) throws PasswordErrata, UsernameErrato, RemoteException {
        String query = "SELECT * FROM public.\"User\" WHERE username = '" + userid + "'";
        //System.out.println(userid);
        String username = "", hashed_pass = "", nome = "";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                username = rs.getString("username");
                hashed_pass = rs.getString("hashed_password");
                nome = rs.getString("nome");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (userid.equals(username) && BCrypt.checkpw(password, hashed_pass)) {
            return nome;
        } else {
            if (!userid.equals(username))
                throw new UsernameErrato();
            if (!BCrypt.checkpw(password, hashed_pass))
                throw new PasswordErrata();

        }
        return null;
    }

    private int userId(String username) {
        String query = "SELECT user_id FROM public.\"User\" WHERE username = '" + username + "'";
        int userId = -1;
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userId = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;

    }

    private int playlistId(String username, String titolo) {
        int userId = userId(username);
        String query = "SELECT * FROM public.\"Playlist\" WHERE user_id = " + userId + "AND titolo = '" + titolo + "'";
        int playlistId = -1;
        ResultSet rs;
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            rs = stmt.executeQuery();
            while (rs.next()) {
                playlistId = rs.getInt("playlist_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlistId;
    }

    @Override
    public int addPlaylist(String titolo, String username) throws RemoteException {
        int playlistCreate = -1;
        int userId = userId(username);
        String query = "INSERT INTO public.\"Playlist\" (titolo, user_id) VALUES (?, ?)";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, titolo);
            stmt.setInt(2, userId);

            playlistCreate = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistCreate;
    }

    @Override
    public List<Playlist> myPlaylist(String username) throws RemoteException {
        int userId = userId(username);
        List<Playlist> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"Playlist\" WHERE user_id = " + userId;
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Playlist playlist = new Playlist(rs.getInt("playlist_id"), rs.getString("titolo"), rs.getInt("user_id"));
                result.add(playlist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int removePlaylist(String username, String titolo) throws RemoteException {
        int playlistEliminata = -1;
        int userId = userId(username);
        String query = "DELETE FROM public.\"Playlist\" WHERE user_id = " + userId + "AND titolo = '" + titolo + "'";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            playlistEliminata = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistEliminata;
    }

    @Override
    public int renamePlaylist(String username, String nuovoTitolo, String vecchioTitolo) throws RemoteException {
        int playlistModificata = -1;
        int userId = userId(username);
        int playlistId = playlistId(username, vecchioTitolo);
        //System.err.println(playlistId);
        String query = "UPDATE public.\"Playlist\" SET titolo = ? WHERE user_id = ? AND playlist_id = ?";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nuovoTitolo);
            stmt.setInt(2, userId);
            stmt.setInt(3, playlistId);

            playlistModificata = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistModificata;
    }

    @Override
    public void addBraniPlaylist(String nomePlaylist, String username, ArrayList<Canzone> braniSelezionati) throws RemoteException, NessunaCanzoneTrovata {
        Connection conn = null;
        PreparedStatement stmt = null;
        int playlistId = playlistId(username, nomePlaylist);
        StringBuilder query = new StringBuilder("INSERT INTO public.\"CanzoniPlaylist\" VALUES");
        for (Canzone c : braniSelezionati) {
            int canzoneId = c.getId();
            if (braniSelezionati.toArray()[braniSelezionati.size() - 1].equals(c))
                query.append("(").append(playlistId).append(", ").append(canzoneId).append(");");
            else
                query.append("(").append(playlistId).append(", ").append(canzoneId).append("),");
        }
        //System.out.println(query);
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query.toString());

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            if(query.toString().equals("INSERT INTO public.\"CanzoniPlaylist\" VALUES")) throw new NessunaCanzoneTrovata();
            throw new RemoteException();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<Canzone> showCanzoniPlaylist(String nomePlaylist, String username) throws RemoteException {
        ArrayList<Integer> idSong = getIdSongPlaylist(nomePlaylist, username);
        ArrayList<Canzone> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM public.\"Canzoni\" WHERE ");
        if(!idSong.isEmpty()) {
            for(Integer id : idSong) {
                if(idSong.indexOf(id) == idSong.size()-1)
                    query.append("\"Canzoni\".\"Canzoni_id\" =").append(id).append(";");
                else
                    query.append("\"Canzoni\".\"Canzoni_id\" =").append(id).append(" OR ");
            }
        }
        //System.out.println(query);
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query.toString()); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                result.add(canzone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<Integer> getIdSongPlaylist(String nomePlaylist, String username) {
        ArrayList<Integer> result = new ArrayList<>();
        int playlistId = playlistId(username, nomePlaylist);
        String query = "SELECT * FROM public.\"CanzoniPlaylist\" WHERE playlist_id = " + playlistId + ";";
        try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getInt("canzone_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void insEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException, RemoteException {
        StringBuilder query = new StringBuilder("INSERT INTO public.\"Emozioni\" VALUES(" + playlistId + "," + songId + ",");
        for(Emozione e : emozioni) {
            int score = e.getScore();
            String commento = e.getCommento();
            if(emozioni.indexOf(e) != 8) {
                if (score != 0)
                    query.append(score).append(",");
                else
                    query.append("null,");
                if (!commento.equals(""))
                    query.append("'").append(commento).append("',");
                else
                    query.append("null,");
            } else {
                if (score != 0)
                    query.append(score).append(",");
                else
                    query.append("null,");
                if (!commento.equals(""))
                    query.append("'").append(commento).append("';");
                else
                    query.append("null);");
            }
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query.toString());

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws RemoteException {
        StringBuilder query = new StringBuilder("UPDATE public.\"Emozioni\" SET ");
        for (Emozione e : emozioni) {
            int score = e.getScore();
            String commento = e.getCommento();

            // Aggiungi l'istruzione di aggiornamento per il campo "score"
            if(emozioni.indexOf(e) != 5) {
                query.append(e.getName()).append(" = ");
            } else {
                query.append("P").append(e.getName()).append(" = ");
            }

            if (score != 0)
                query.append(score);
            else
                query.append("null");

            if(emozioni.indexOf(e) != 8) {
                // Aggiungi l'istruzione di aggiornamento per il campo "commento"
                query.append(", comm").append(e.getName()).append(" = ");
                if (!commento.equals(""))
                    query.append("'").append(commento).append("', ");
                else
                    query.append("null, ");
            } else {
                query.append(", comm").append(e.getName()).append(" = ");
                if (!commento.equals(""))
                    query.append("'").append(commento).append("' ");
                else
                    query.append("null ");
            }
        }
        // Aggiungi la clausola WHERE per specificare le condizioni di aggiornamento
        query.append(" WHERE playlist_id = ").append(playlistId);
        query.append(" AND canzone_id = ").append(songId).append(";");
        //System.out.println(query);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query.toString());

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO : aggiungere colonne commenti alla query
    @Override
    public List<Emozione> getVotazioni(int songId) throws RemoteException {
        List<Emozione> result = new ArrayList<>();
        String query = "SELECT avg(amazement),avg(solemnity),avg(tenderness),avg(nostalgia),avg(calmness),avg(ppower),avg(joy),avg(tension),avg(sadness) FROM public.\"Emozioni\" WHERE canzone_id =" + songId + ";";

        try (Connection conn = this.dbConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Emozione amazement = new Emozione("Amazement", rs.getInt(1));
                result.add(amazement);
                Emozione solemnity = new Emozione("Solemnity", rs.getInt(2));
                result.add(solemnity);
                Emozione tenderness = new Emozione("Tenderness", rs.getInt(3));
                result.add(tenderness);
                Emozione nostalgia = new Emozione("Nostalgia", rs.getInt(4));
                result.add(nostalgia);
                Emozione calmness = new Emozione("Calmness", rs.getInt(5));
                result.add(calmness);
                Emozione power = new Emozione("Power", rs.getInt(6));
                result.add(power);
                Emozione joy = new Emozione("Joy", rs.getInt(7));
                result.add(joy);
                Emozione tension = new Emozione("Tension", rs.getInt(8));
                result.add(tension);
                Emozione sadness = new Emozione("Sadness", rs.getInt(9));
                result.add(sadness);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
