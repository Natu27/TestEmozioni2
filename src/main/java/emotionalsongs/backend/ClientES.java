package emotionalsongs.backend;

import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Emozione;
import emotionalsongs.backend.entities.Playlist;
import emotionalsongs.backend.entities.Utente;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;
import emotionalsongs.backend.exceptions.emozioni.NoCommenti;
import emotionalsongs.backend.exceptions.emozioni.NoVotazioni;
import emotionalsongs.backend.exceptions.utente.PasswordErrata;
import emotionalsongs.backend.exceptions.utente.UsernameErrato;
import org.mindrot.jbcrypt.BCrypt;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientES implements Servizi {
    private final DatabaseConnection dbConn= new DatabaseConnection();
    private static ClientES instance;

    private ClientES() {}

    public static synchronized ClientES getInstance() throws RemoteException {
        if (instance == null) {
            instance = new ClientES();
        }
        return instance;
    }

    @Override
    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year) throws NessunaCanzoneTrovata {
        List<Canzone> result = new ArrayList<>();
        //if (titoloDaCercare.equals("") && autoreDaCercare.equals("") && year == null) throw new NessunaCanzoneTrovata();
        String query = "SELECT * FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER(CONCAT( '%',?,'%')) AND LOWER(autore) LIKE LOWER(CONCAT( '%',?,'%'))";
        if (year != null) {
            query = query + " AND anno = ?";
        }
        //if (titoloDaCercare == "" && autoreDaCercare == "" && year == null)
        query = query + " LIMIT 300";
        Connection conn;
        try {
            conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, titoloDaCercare);
            stmt.setString(2, autoreDaCercare);
            if (year != null)
                stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"),
                        rs.getString("autore"), rs.getString("titolo"),
                        rs.getString("codice"));
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
        //System.out.println(query);
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
        String query = "SELECT distinct anno FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER(CONCAT( '%',?,'%')) AND LOWER(autore) LIKE LOWER(CONCAT( '%',?,'%')) order by anno asc;";

        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, titoloDaCercare);
            stmt.setString(2, autoreDaCercare);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("anno"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Utente login(String userid, String password) throws PasswordErrata, UsernameErrato, RemoteException {
        String query = "SELECT * FROM public.\"User\" WHERE username = ?;";
        Utente result = null;
        //System.out.println(userid);
        int userId;
        String username = "", hashed_pass = "", nome;
        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userId = rs.getInt("user_id");
                username = rs.getString("username");
                hashed_pass = rs.getString("hashed_password");
                nome = rs.getString("nome");
                result = new Utente(userId, username, hashed_pass, nome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (userid.equals(username) && BCrypt.checkpw(password, hashed_pass)) {
            return result;
        } else {
            if (!userid.equals(username))
                throw new UsernameErrato();
            if (!BCrypt.checkpw(password, hashed_pass))
                throw new PasswordErrata();

        }
        return result;
    }

    @Override
    public int addPlaylist(String titolo, int userId) throws RemoteException {
        int playlistCreate = -1;
        //int userId = userId(username);
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
    public List<Playlist> myPlaylist(int userId) throws RemoteException {
        //int userId = userId(username);
        List<Playlist> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"Playlist\" WHERE user_id = ?;";
        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
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
    public int removePlaylist(int userId, String titolo) throws RemoteException {
        int playlistEliminata = -1;
        //int userId = userId(username);
        String query = "DELETE FROM public.\"Playlist\" WHERE user_id = ? AND titolo = ?";
        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, titolo);
            playlistEliminata = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistEliminata;
    }

    @Override
    public int renamePlaylist(int userId, String nuovoTitolo, int playlistId) throws RemoteException {
        int playlistModificata = -1;
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
    public void addBraniPlaylist(int playlistId, ArrayList<Canzone> braniSelezionati) throws RemoteException, NessunaCanzoneTrovata {
        Connection conn = null;
        PreparedStatement stmt = null;
        //int playlistId = playlistId(username, nomePlaylist);
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
    public ArrayList<Canzone> showCanzoniPlaylist(int playlistId) throws RemoteException {
        ArrayList<Integer> idSong = getIdSongPlaylist(playlistId);
        ArrayList<Canzone> result = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM public.\"Canzoni\" ");
        if(!idSong.isEmpty()) {
            query.append("WHERE ");
            for(Integer id : idSong) {
                if(idSong.indexOf(id) == idSong.size()-1)
                    query.append("\"Canzoni\".\"Canzoni_id\" =").append(id).append(";");
                else
                    query.append("\"Canzoni\".\"Canzoni_id\" =").append(id).append(" OR ");
            }
            try (Connection conn = this.dbConn.getConnection(); PreparedStatement stmt = conn.prepareStatement(query.toString()); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                    result.add(canzone);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(query);
        return result;
    }

    private ArrayList<Integer> getIdSongPlaylist(int playlistId) {
        ArrayList<Integer> result = new ArrayList<>();
        //int playlistId = playlistId(username, nomePlaylist);
        String query = "SELECT * FROM public.\"CanzoniPlaylist\" WHERE playlist_id = ?;";
        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();
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
            double score = e.getScore();
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
            double score = e.getScore();
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

    @Override
    public List<Emozione> getVotazioniMedie(int songId) throws NoVotazioni, RemoteException {
        List<Emozione> result = new ArrayList<>();
        String query = "SELECT avg(amazement),avg(solemnity),avg(tenderness),avg(nostalgia),avg(calmness),avg(ppower),avg(joy),avg(tension),avg(sadness) FROM public.\"Emozioni\" WHERE canzone_id =?;";

        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, songId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Emozione amazement = new Emozione("Amazement", rs.getDouble(1));
                result.add(amazement);
                Emozione solemnity = new Emozione("Solemnity", rs.getDouble(2));
                result.add(solemnity);
                Emozione tenderness = new Emozione("Tenderness", rs.getDouble(3));
                result.add(tenderness);
                Emozione nostalgia = new Emozione("Nostalgia", rs.getDouble(4));
                result.add(nostalgia);
                Emozione calmness = new Emozione("Calmness", rs.getDouble(5));
                result.add(calmness);
                Emozione power = new Emozione("Power", rs.getDouble(6));
                result.add(power);
                Emozione joy = new Emozione("Joy", rs.getDouble(7));
                result.add(joy);
                Emozione tension = new Emozione("Tension", rs.getDouble(8));
                result.add(tension);
                Emozione sadness = new Emozione("Sadness", rs.getDouble(9));
                result.add(sadness);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean votazioni = false;
        for(Emozione e: result) {
            if(e.getScore() != 0.0) {
                votazioni = true;
                break;
            }
        }
        if(!votazioni) throw new NoVotazioni();
        return result;
    }

    @Override
    public List<Emozione> getCommenti(int songId) throws NoCommenti, RemoteException {
        List<Emozione> result = new ArrayList<>();
        String query = "SELECT amazement,solemnity,tenderness,nostalgia,calmness,ppower,joy,tension,sadness,commamazement,commsolemnity,commtenderness,commnostalgia,commcalmness,commpower,commjoy,commtension,commsadness FROM public.\"Emozioni\" WHERE canzone_id =?;";

        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, songId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Emozione amazement = new Emozione("Amazement", rs.getDouble(1), rs.getString(10));
                result.add(amazement);
                Emozione solemnity = new Emozione("Solemnity", rs.getDouble(2), rs.getString(11));
                result.add(solemnity);
                Emozione tenderness = new Emozione("Tenderness", rs.getDouble(3), rs.getString(12));
                result.add(tenderness);
                Emozione nostalgia = new Emozione("Nostalgia", rs.getDouble(4), rs.getString(13));
                result.add(nostalgia);
                Emozione calmness = new Emozione("Calmness", rs.getDouble(5), rs.getString(14));
                result.add(calmness);
                Emozione power = new Emozione("Power", rs.getDouble(6),rs.getString(15));
                result.add(power);
                Emozione joy = new Emozione("Joy", rs.getDouble(7),rs.getString(16));
                result.add(joy);
                Emozione tension = new Emozione("Tension", rs.getDouble(8),rs.getString(17));
                result.add(tension);
                Emozione sadness = new Emozione("Sadness", rs.getDouble(9),rs.getString(18));
                result.add(sadness);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean commenti = false;
        for(Emozione e: result) {
            if(!Objects.equals(e.getCommento(), "") && e.getCommento() != null) {
                commenti = true;
                break;
            }
        }
        if(!commenti) throw new NoCommenti();
        return result;
    }

    @Override
    public List<String> myAccount(int userId) throws RemoteException {
        List<String> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"User\" WHERE user_id = ?;";
        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                result.add(nome);
                String cognome = rs.getString("cognome");
                result.add(cognome);
                String username = rs.getString("username");
                result.add(username);
                String indirizzo = rs.getString("indirizzo");
                result.add(indirizzo);
                String cf = rs.getString("cf");
                result.add(cf);
                String email = rs.getString("email");
                result.add(email);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    public int removePlaylistSong(int playlistId, int canzoneId) throws RemoteException {
        int canzoneRimossa = -1;
        String query = "DELETE FROM public.\"CanzoniPlaylist\" WHERE playlist_id = ? AND canzone_id = ?";
        try {
            Connection conn = this.dbConn.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playlistId);
            stmt.setInt(2, canzoneId);
            canzoneRimossa = stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return canzoneRimossa;
    }

}
