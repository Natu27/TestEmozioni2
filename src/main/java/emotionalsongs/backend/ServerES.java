package emotionalsongs.backend;

import emotionalsongs.backend.entities.Canzone;
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
import java.util.Set;


/**
 *
 */
public class ServerES implements Servizi {
    private final static int PORT = 10002;
    DatabaseConnection dbConn;

    public ServerES(DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public static <Servizi extends Remote> void main(String[] args) throws RemoteException {
        DatabaseConnection dbConn = new DatabaseConnection();
        ServerES serverES = new ServerES(dbConn);
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
        //if (titoloDaCercare.equals("") && autoreDaCercare.equals("") && year == null) throw new NessunaCanzoneTrovata();
        String query = "SELECT * FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER('%" + titoloDaCercare + "%') " + "AND LOWER(autore) LIKE LOWER('%" + autoreDaCercare + "%')";
        if (year != null) {
            query = query + " AND anno = " + year;
        }
        query = query + " LIMIT 300";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                result.add(canzone);
            }
            if (result.isEmpty()) throw new NessunaCanzoneTrovata();
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
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
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, username);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, indirizzo);
            stmt.setString(6, codiceFiscale);
            stmt.setString(7, email);

            int utentiRegistrati = stmt.executeUpdate();

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
            }
        }
    }

    /**
     *
     */
    @Override
    public List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws RemoteException {

        List<Integer> result = new ArrayList<>();
        String query = "SELECT distinct anno FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER('%" + titoloDaCercare + "%') " + "AND LOWER(autore) LIKE LOWER('%" + autoreDaCercare + "%') order by anno asc;";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("anno"));
            }
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
        }

        return result;
    }

    @Override
    public String login(String userid, String password) throws PasswordErrata, UsernameErrato, RemoteException {
        String query = "SELECT * FROM public.\"User\" WHERE username = '" + userid + "'";
        //System.out.println(userid);
        String username = "", hashed_pass = "", nome = "";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                username = rs.getString("username");
                hashed_pass = rs.getString("hashed_password");
                nome = rs.getString("nome");
            }
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                userId = rs.getInt("user_id");
            }
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
        }

        return userId;

    }

    private int playlistId(String username, String titolo) {
        int userId = userId(username);
        String query = "SELECT * FROM public.\"Playlist\" WHERE user_id = " + userId + "AND titolo = '" + titolo + "'";
        int playlistId = -1;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                playlistId = rs.getInt("playlist_id");
            }
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
            }
        }

        return playlistId;
    }

    @Override
    public int addPlaylist(String titolo, String username) throws RemoteException {
        int playlistCreate = -1;
        int userId = userId(username);
        Connection conn = null;
        PreparedStatement stmt = null;
        String query = "INSERT INTO public.\"Playlist\" (titolo, user_id) VALUES (?, ?)";
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, titolo);
            stmt.setInt(2, userId);

            playlistCreate = stmt.executeUpdate();

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
            }
        }
        return playlistCreate;
    }

    @Override
    public List<Playlist> myPlaylist(String username) throws RemoteException {
        int userId = userId(username);
        List<Playlist> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"Playlist\" WHERE user_id = " + userId;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Playlist playlist = new Playlist(rs.getString("titolo"));
                result.add(playlist);
            }
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
        }
        return result;
    }

    public int removePlaylist(String username, String titolo) throws RemoteException {
        int playlistEliminata = -1;
        int userId = userId(username);
        Connection conn = null;
        PreparedStatement stmt = null;
        String query = "DELETE FROM public.\"Playlist\" WHERE user_id = " + userId + "AND titolo = '" + titolo + "'";
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);

            playlistEliminata = stmt.executeUpdate();

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
            }
        }
        return playlistEliminata;
    }

    public int renamePlaylist(String username, String nuovoTitolo, String vecchioTitolo) throws RemoteException {
        int playlistModificata = -1;
        int userId = userId(username);
        int playlistId = playlistId(username, vecchioTitolo);
        //System.err.println(playlistId);
        Connection conn = null;
        PreparedStatement stmt = null;
        String query = "UPDATE public.\"Playlist\" SET titolo = ? WHERE user_id = ? AND playlist_id = ?";
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);

            stmt.setString(1, nuovoTitolo);
            stmt.setInt(2, userId);
            stmt.setInt(3, playlistId);

            playlistModificata = stmt.executeUpdate();

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
            }
        }
        return playlistModificata;
    }

    @Override
    public void addBraniPlaylist(String nomePlaylist, String username, Set<Canzone> braniSelezionati) throws RemoteException,NessunaCanzoneTrovata {
        Connection conn = null;
        PreparedStatement stmt = null;
        int userId = userId(username);
        int playlistId = playlistId(username, nomePlaylist);
        String query = "INSERT INTO public.\"CanzoniPlaylist\" VALUES";
        for (Canzone c : braniSelezionati) {
            int canzoneId = canzoneId(c.getTitolo(), c.getArtista(), c.getAnno());
            if(braniSelezionati.toArray()[braniSelezionati.size() - 1].equals(c))
                query += "(" + playlistId + ", " + canzoneId + ");";
            else
                query += "(" + playlistId + ", " + canzoneId + "),";
        }
        //System.out.println(query);
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            if(query.equals("INSERT INTO public.\"CanzoniPlaylist\" VALUES")) throw new NessunaCanzoneTrovata();
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
            }
        }
    }

    @Override
    public ArrayList<Canzone> showCanzoniPlaylist(String nomePlaylist, String username) throws RemoteException {
        ArrayList<Integer> idSong = getIdSongPlaylist(nomePlaylist, username);
        ArrayList<Canzone> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"Canzoni\" WHERE ";
        if(!idSong.isEmpty()) {
            for(Integer id : idSong) {
                if(idSong.indexOf(id) == idSong.size()-1)
                    query += "\"Canzoni\".\"Canzoni_id\" =" + id + ";";
                else
                    query += "\"Canzoni\".\"Canzoni_id\" =" + id + " OR ";
            }
        }
        //System.out.println(query);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                result.add(canzone);
            }
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
        }
        return result;
    }

    private ArrayList<Integer> getIdSongPlaylist(String nomePlaylist, String username) {
        ArrayList<Integer> result = new ArrayList<>();
        int userId = userId(username);
        int playlistId = playlistId(username, nomePlaylist);
        String query = "SELECT * FROM public.\"CanzoniPlaylist\" WHERE playlist_id = " + playlistId + ";";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("canzone_id"));
            }
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
        }
        return result;
    }

    private int canzoneId(String titolo, String autore, int anno) {
        int result = -1;
        String query = "SELECT * FROM public.\"Canzoni\" WHERE titolo = '" + titolo
                + "' AND autore = '" + autore + "' AND anno = " + anno + ";";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result = rs.getInt("Canzoni_id");
            }
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
                if (rs != null) {
                    rs.close();
                }

            } catch (SQLException e) {
            }
        }
        return result;
    }
}
