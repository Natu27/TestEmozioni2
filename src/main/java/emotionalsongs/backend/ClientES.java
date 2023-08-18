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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Questa classe rappresenta un client per l'accesso ai servizi offerti dal sistema.
 * È implementata l'interfaccia Servizi per fornire metodi di accesso ai dati e alle funzionalità.
 * Utilizza una connessione al database gestita dalla classe DatabaseConnection.
 * @see emotionalsongs.backend.Servizi
 */
public class ClientES implements Servizi {
    private final DatabaseConnection dbConn= new DatabaseConnection();
    private static ClientES instance;

    // Costruttore privato per garantire il pattern Singleton
    private ClientES() {}

    /**
     * Ottiene un'istanza unica del client per l'accesso ai servizi.
     * @return Un'istanza di ClientES.
     */
    public static synchronized ClientES getInstance() {
        if (instance == null) {
            instance = new ClientES();
        }
        return instance;
    }

    /**
     * Chiude le risorse della connessione al database in modo sicuro.
     *
     * @param conn La connessione al database da chiudere.
     * @param stmt Lo statement SQL da chiudere.
     * @param rs   Il result set da chiudere.
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permette di cercare le canzoni all'interno del database.
     *
     * @param titoloDaCercare Il titolo della canzone da cercare.
     * @param autoreDaCercare L'autore della canzone da cercare.
     * @param year L'anno di pubblicazione della canzone da cercare.
     * @return Una lista di oggetti Canzone corrispondenti ai criteri di ricerca.
     * @throws NessunaCanzoneTrovata Se nessuna canzone corrisponde ai criteri di ricerca.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     * @see emotionalsongs.backend.entities.Canzone
     * @see emotionalsongs.backend.exceptions.NessunaCanzoneTrovata
     */
    @Override
    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year) throws NessunaCanzoneTrovata, SQLException {
        List<Canzone> result = new ArrayList<>();
        //if (titoloDaCercare.equals("") && autoreDaCercare.equals("") && year == null) throw new NessunaCanzoneTrovata();
        String query = "SELECT * FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER(CONCAT( '%',?,'%')) AND LOWER(autore) LIKE LOWER(CONCAT( '%',?,'%'))";
        if (year != null) {
            query = query + " AND anno = ?";
        }
        //if (titoloDaCercare == "" && autoreDaCercare == "" && year == null)
        query = query + " LIMIT 300";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, titoloDaCercare);
            stmt.setString(2, autoreDaCercare);
            if (year != null)
                stmt.setInt(3, year);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"),
                        rs.getString("autore"), rs.getString("titolo"),
                        rs.getString("codice"));
                result.add(canzone);
            }
            if (result.isEmpty()) throw new NessunaCanzoneTrovata();
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return result;
    }


    @Override
    public List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer year, ArrayList<Canzone> braniDaEscludere) throws NessunaCanzoneTrovata, SQLException {
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

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {conn = this.dbConn.getConnection(); stmt = conn.prepareStatement(query.toString()); rs = stmt.executeQuery();
            while (rs.next()) {
                Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                //System.out.println(canzone.getId());
                result.add(canzone);
            }
            if (result.isEmpty()) throw new NessunaCanzoneTrovata();
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return result;
    }

    /**
     * Permette la registrazione di un nuovo utente all'applicazione.
     *
     * @param nome Il nome dell'utente.
     * @param cognome Il cognome dell'utente.
     * @param indirizzo L'indirizzo dell'utente.
     * @param codiceFiscale Il codice fiscale dell'utente.
     * @param email L'indirizzo email dell'utente.
     * @param username L'username scelto dall'utente per l'accesso.
     * @param password La password scelta dall'utente per l'accesso.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO public.\"User\" (nome, cognome, username, hashed_password, indirizzo, cf, email) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {conn = this.dbConn.getConnection(); stmt = conn.prepareStatement(query);
            stmt.setString(1, nome);
            stmt.setString(2, cognome);
            stmt.setString(3, username);
            stmt.setString(4, hashedPassword);
            stmt.setString(5, indirizzo);
            stmt.setString(6, codiceFiscale);
            stmt.setString(7, email);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    /**
     * Ottiene una lista di nomi utente registrati nel sistema.
     *
     * @return Una lista di nomi utente registrati nel sistema.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public List<String> getUsernames() throws SQLException {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM public.\"User\"";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return usernames;
    }

    /**
     * Ottiene la lista degli anni di pubblicazione delle canzoni corrispondenti ai parametri di ricerca.
     *
     * @param titoloDaCercare Il titolo della canzone da cercare.
     * @param autoreDaCercare L'autore della canzone da cercare.
     * @return Una lista di anni di pubblicazione delle canzoni.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws SQLException {

        List<Integer> result = new ArrayList<>();
        String query = "SELECT distinct anno FROM public.\"Canzoni\" WHERE LOWER(titolo) LIKE LOWER(CONCAT( '%',?,'%')) AND LOWER(autore) LIKE LOWER(CONCAT( '%',?,'%')) order by anno asc;";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, titoloDaCercare);
            stmt.setString(2, autoreDaCercare);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("anno"));
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return result;
    }

    /**
     * Permette di effettuare il login all'applicazione.
     *
     * @param userid L'username dell'utente.
     * @param password La password dell'utente.
     * @return L'oggetto Utente che rappresenta l'utente autenticato.
     * @throws PasswordErrata Se la password fornita non è corretta.
     * @throws UsernameErrato Se lo username fornito non è corretto.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     * @see emotionalsongs.backend.entities.Utente
     * @see emotionalsongs.backend.exceptions.utente.PasswordErrata
     * @see emotionalsongs.backend.exceptions.utente.UsernameErrato
     */
    @Override
    public Utente login(String userid, String password) throws PasswordErrata, UsernameErrato, SQLException {
        String query = "SELECT * FROM public.\"User\" WHERE username = ?;";
        Utente result = null;
        int userId;
        String username = "", hashed_pass = "", nome;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userid);
            rs = stmt.executeQuery();
            while (rs.next()) {
                userId = rs.getInt("user_id");
                username = rs.getString("username");
                hashed_pass = rs.getString("hashed_password");
                nome = rs.getString("nome");
                result = new Utente(userId, username, hashed_pass, nome);
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
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

    /**
     * Permette all'utente di aggiunge una nuova playlist.
     *
     * @param titolo Il titolo della playlist da aggiungere.
     * @param userId L'ID dell'utente a cui associare la playlist.
     * @return L'ID della playlist appena aggiunta.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public int addPlaylist(String titolo, int userId) throws SQLException {
        int playlistCreate;
        String query = "INSERT INTO public.\"Playlist\" (titolo, user_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {conn = this.dbConn.getConnection(); stmt = conn.prepareStatement(query);
            stmt.setString(1, titolo);
            stmt.setInt(2, userId);

            playlistCreate = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
        return playlistCreate;
    }

    /**
     * Restituisce la lista delle playlist associate all'utente specificato.
     *
     * @param userId L'ID dell'utente di cui ottenere le playlist.
     * @return Una lista di oggetti Playlist associate all'utente.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see emotionalsongs.backend.entities.Playlist
     * @see Servizi
     */
    @Override
    public List<Playlist> myPlaylist(int userId) throws SQLException {
        List<Playlist> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"Playlist\" WHERE user_id = ?;";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Playlist playlist = new Playlist(rs.getInt("playlist_id"), rs.getString("titolo"), rs.getInt("user_id"));
                result.add(playlist);
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return result;
    }

    /**
     * Permette di rimuove una playlist dell'utente specificato.
     *
     * @param userId L'ID dell'utente a cui appartiene la playlist da rimuovere.
     * @param titolo Il titolo della playlist da rimuovere.
     * @return Il numero di playlist rimosse.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public int removePlaylist(int userId, String titolo) throws SQLException {
        int playlistEliminata;
        String query = "DELETE FROM public.\"Playlist\" WHERE user_id = ? AND titolo = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, titolo);
            playlistEliminata = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
        return playlistEliminata;
    }

    /**
     * Permette di rinominare una playlist dell'utente specificato.
     *
     * @param userId L'ID dell'utente a cui appartiene la playlist da rimuovere.
     * @param nuovoTitolo Il nuovo titolo da assegnare alla playlist.
     * @param playlistId L'ID della playlist da modificare.
     * @return Il numero di playlist modificate.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public int renamePlaylist(int userId, String nuovoTitolo, int playlistId) throws SQLException {
        int playlistModificata;
        String query = "UPDATE public.\"Playlist\" SET titolo = ? WHERE user_id = ? AND playlist_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {conn = this.dbConn.getConnection(); stmt = conn.prepareStatement(query);

            stmt.setString(1, nuovoTitolo);
            stmt.setInt(2, userId);
            stmt.setInt(3, playlistId);

            playlistModificata = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
        return playlistModificata;
    }

    /**
     * Permette di aggiungere brani alla playliste dell'utente.
     *
     * @param playlistId L'ID della playlist a cui aggiungere i brani.
     * @param braniSelezionati I brani da aggiungere alla playlist.
     * @throws NessunaCanzoneTrovata Se nessuna canzone corrisponde ai criteri di ricerca.
     * @throws SQLException In caso di errore durante l'interazione con il database
     * @see Servizi
     * @see emotionalsongs.backend.entities.Canzone
     * @see emotionalsongs.backend.exceptions.NessunaCanzoneTrovata
     */
    @Override
    public void addBraniPlaylist(int playlistId, ArrayList<Canzone> braniSelezionati) throws SQLException, NessunaCanzoneTrovata {
        Connection conn = null;
        PreparedStatement stmt = null;

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
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    /**
     * Permette di visualizzare le canzoni pressenti nella playlist selezionata.
     *
     * @param playlistId L'ID della playlist a cui aggiungere i brani.
     * @throws SQLException In caso di errore durante l'interazione con il database
     * @return Una lista contenente le canzoni presenti nella playlist.
     * @see Servizi
     * @see emotionalsongs.backend.entities.Canzone
     */
    @Override
    public ArrayList<Canzone> showCanzoniPlaylist(int playlistId) throws SQLException {
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

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {conn = this.dbConn.getConnection(); stmt = conn.prepareStatement(query.toString()); rs = stmt.executeQuery();
                while (rs.next()) {
                    Canzone canzone = new Canzone(rs.getInt("Canzoni_id"), rs.getInt("anno"), rs.getString("autore"), rs.getString("titolo"), rs.getString("codice"));
                    result.add(canzone);
                }
            } catch (SQLException e) {
                throw new SQLException();
            } finally {
                closeResources(conn, stmt, rs);
            }
        }
        return result;
    }


    private ArrayList<Integer> getIdSongPlaylist(int playlistId) throws SQLException {
        ArrayList<Integer> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"CanzoniPlaylist\" WHERE playlist_id = ?;";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, playlistId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("canzone_id"));
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return result;
    }


    /**
     * Permette di inserire le emozioni per il brano selezionato.
     *
     * @param playlistId L'ID della playlist in cui è presente il brano da valutare.
     * @param songId L'ID della canzone da valutare.
     * @param emozioni La lista delle emozioni valutate.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public void insEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException {
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
            closeResources(conn, stmt, null);
        }
    }

    /**
     * Permette di modificare le emozioni per il brano selezionato.
     *
     * @param playlistId L'ID della playlist in cui è presente il brano da valutare.
     * @param songId L'ID della canzone da valutare.
     * @param emozioni La lista delle emozioni con le nuove valutazioni.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public void updateEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException {
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
            closeResources(conn, stmt, null);
        }
    }

    /**
     * Permette di visualizzare la media delle emozioni associate alla canzone.
     *
     * @param songId L'ID della canzone di cui si vogliono visualizzare i commenti.
     * @throws NoVotazioni In caso non siano presenti votazioni per la canzone selezionata.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @return Una lista contente le votazioni medie per ciascuna emozione.
     * @see Servizi
     * @see emotionalsongs.backend.exceptions.emozioni.NoVotazioni
     */
    @Override
    public List<Emozione> getVotazioniMedie(int songId) throws NoVotazioni, SQLException {
        List<Emozione> result = new ArrayList<>();
        String query = "SELECT avg(amazement),avg(solemnity),avg(tenderness),avg(nostalgia),avg(calmness),avg(ppower),avg(joy),avg(tension),avg(sadness) FROM public.\"Emozioni\" WHERE canzone_id =?;";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, songId);
            rs = stmt.executeQuery();

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
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
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

    /**
     * Permette di visualizzare i commenti associati alla canzone.
     *
     * @param songId L'ID della canzone di cui si vogliono visualizzare i commenti.
     * @throws NoCommenti In caso non siano presenti commenti per la canzone selezionata.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @return Una lista contenente i commenti per tutte le emozioni.
     * @see Servizi
     * @see emotionalsongs.backend.exceptions.emozioni.NoCommenti
     */
    @Override
    public List<Emozione> getCommenti(int songId) throws NoCommenti, SQLException {
        List<Emozione> result = new ArrayList<>();
        String query = "SELECT amazement,solemnity,tenderness,nostalgia,calmness,ppower,joy,tension,sadness,commamazement,commsolemnity,commtenderness,commnostalgia,commcalmness,commpower,commjoy,commtension,commsadness FROM public.\"Emozioni\" WHERE canzone_id =?;";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, songId);
            rs = stmt.executeQuery();
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
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
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

    /**
     * Permette di visualizzare i dati del proprio account.
     *
     * @param userId L'ID dell'utente collegato all'account.
     * @return Una lista contente tutti i dati dell'account.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public List<String> myAccount(int userId) throws SQLException {
        List<String> result = new ArrayList<>();
        String query = "SELECT * FROM public.\"User\" WHERE user_id = ?;";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
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
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return result;
    }

    /**
     * Permette di rimuovere un brano presente nella playlist.
     *
     * @param playlistId L'ID della playlist in cui è presente il brano.
     * @param canzoneId L'ID della canzone da rimuovere.
     * @return il numero delle canzoni romosse (0 o 1)
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public int removePlaylistSong(int playlistId, int canzoneId) throws SQLException {
        int canzoneRimossa;
        String query1 = "DELETE FROM public.\"Emozioni\" WHERE playlist_id = ? AND canzone_id = ?";
        String query2 = "DELETE FROM public.\"CanzoniPlaylist\" WHERE playlist_id = ? AND canzone_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query1);
            stmt.setInt(1, playlistId);
            stmt.setInt(2, canzoneId);
            canzoneRimossa = stmt.executeUpdate();

            stmt = conn.prepareStatement(query2);
            stmt.setInt(1, playlistId);
            stmt.setInt(2, canzoneId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
        return canzoneRimossa;
    }

    /**
     * Permette di eliminare il proprio account.
     *
     * @param userId L'ID dell'utente collegato all'account da eliminare.
     * @return Il numero di account eliminati (0 o 1)
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public int eliminaAccount(int userId) throws SQLException {
        int accountEliminato;
        String query = "DELETE FROM public.\"User\" WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.dbConn.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            accountEliminato = stmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
        return accountEliminato;
    }

    /**
     * Permette di modificare alcuni dati del proprio account.
     *
     * @param userId L'ID dell'utente collegato all'account da eliminare.
     * @param  residenza La nuova residenza dell'utente.
     * @param email La nuova email dell'utente.
     * @param password La nuova password dell'utente.
     * @return Il nomero di dati modificati.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see Servizi
     */
    @Override
    public int modifcaDati(int userId, String residenza, String email, String password) throws SQLException {
        int modifiche;
        String query = "";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            if (password.length() == 0) {
                query = "UPDATE public.\"User\" SET indirizzo = ? , email = ? WHERE user_id = ?";
                conn = this.dbConn.getConnection();
                stmt = conn.prepareStatement(query);
                stmt.setString(1, residenza);
                stmt.setString(2,email);
                stmt.setInt(3,userId);
                modifiche = stmt.executeUpdate();
            } else {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                query = "UPDATE public.\"User\" SET indirizzo = ?, email = ?, hashed_password = ? WHERE user_id = ?";
                conn = this.dbConn.getConnection();
                stmt = conn.prepareStatement(query);
                stmt.setString(1, residenza);
                stmt.setString(2,email);
                stmt.setString(3,hashedPassword);
                stmt.setInt(4,userId);
                modifiche = stmt.executeUpdate();
            }
        }catch (SQLException e) {
            throw new SQLException();
        } finally {
            closeResources(conn, stmt, null);
        }
        return modifiche;
    }
}

