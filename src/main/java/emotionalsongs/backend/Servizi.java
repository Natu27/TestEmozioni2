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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Acquati Luca
 * @author Jamil Muhammad Qasim
 * @author Naturale Lorenzo
 * @author Volonterio Luca
 * <p></p>
 * Interfaccia che definisce i servizi disponibili per l'interazione con l'applicazione.
 * @see emotionalsongs.backend
 * @see ClientES Per l'implementazione dei metodi.
 * @version 1.0
 */
public interface Servizi {

    /**
     * Permette di effettuare il login all'applicazione.
     * @param username L'username dell'utente.
     * @param password La password dell'utente.
     * @throws PasswordErrata Se la password fornita non è corretta.
     * @throws UsernameErrato Se lo username fornito non è corretto.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     * @see emotionalsongs.backend.entities.Utente
     * @see emotionalsongs.backend.exceptions.utente.PasswordErrata
     * @see emotionalsongs.backend.exceptions.utente.UsernameErrato
     */
    Utente login(String username, String password) throws PasswordErrata, UsernameErrato, SQLException;

    /**
     * Permette di cercare le canzoni all'interno del database.
     * @param titoloDaCercare Il titolo della canzone da cercare.
     * @param autoreDaCercare L'autore della canzone da cercare.
     * @param anno L'anno di pubblicazione della canzone da cercare.
     * @throws NessunaCanzoneTrovata Se nessuna canzone corrisponde ai criteri di ricerca.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     * @see emotionalsongs.backend.entities.Canzone
     * @see emotionalsongs.backend.exceptions.NessunaCanzoneTrovata
     */
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno) throws NessunaCanzoneTrovata, SQLException;

    /**
     * Metodo utilizzato per ricercare le canzoni da aggiungere alla playlist.
     * Non vengono mostrate le canzoni già aggiunte alla playlist oppure già selezionate per essere aggiunte ad una playlist.
     * @param titoloDaCercare Il titolo della canzone da cercare.
     * @param autoreDaCercare L'autore della canzone da cercare.
     * @param anno L'anno di pubblicazione della canzone da cercare.
     * @param braniDaEscludere Un' {@code ArrayList} contenente i brani che devoneo essere esclusi dalla ricerca dei brani da aggiungere alla playlist.
     * @throws NessunaCanzoneTrovata Se nessuna canzone corrisponde ai criteri di ricerca.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     * @see emotionalsongs.backend.entities.Canzone
     * @see emotionalsongs.backend.exceptions.NessunaCanzoneTrovata
     */
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno, ArrayList<Canzone> braniDaEscludere) throws NessunaCanzoneTrovata, SQLException;

    /**
     * Permette la registrazione di un nuovo utente all'applicazione.
     * @param nome Il nome dell'utente.
     * @param cognome Il cognome dell'utente.
     * @param indirizzo L'indirizzo dell'utente.
     * @param codiceFiscale Il codice fiscale dell'utente.
     * @param email L'indirizzo email dell'utente.
     * @param username L'username scelto dall'utente per l'accesso.
     * @param password La password scelta dall'utente per l'accesso.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws SQLException;

    /**
     * Ottiene una lista di nomi utente registrati nel sistema.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    List<String> getUsernames() throws SQLException;

    /**
     * Ottiene la lista degli anni di pubblicazione delle canzoni corrispondenti ai parametri di ricerca.
     * @param titoloDaCercare Il titolo della canzone da cercare.
     * @param autoreDaCercare L'autore della canzone da cercare.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws SQLException;

    /**
     * Permette all'utente di aggiunge una nuova playlist.
     * @param titolo Il titolo della playlist da aggiungere.
     * @param userId L'ID dell'utente a cui associare la playlist.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    int addPlaylist(String titolo, int userId) throws SQLException;

    /**
     * Restituisce la lista delle playlist associate all'utente specificato.
     * @param userId L'ID dell'utente di cui ottenere le playlist.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     * @see emotionalsongs.backend.entities.Playlist
     */
    List<Playlist> myPlaylist(int userId) throws SQLException;

    /**
     * Permette di rimuove una playlist dell'utente specificato.
     * @param userId L'ID dell'utente a cui appartiene la playlist da rimuovere.
     * @param titolo Il titolo della playlist da rimuovere.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    int removePlaylist(int userId, String titolo) throws SQLException;

    /**
     * Permette di rinominare una playlist dell'utente specificato.
     * @param userId L'ID dell'utente a cui appartiene la playlist da rimuovere.
     * @param nuovoTitolo Il nuovo titolo da assegnare alla playlist.
     * @param playlistId L'ID della playlist da modificare.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    int renamePlaylist(int userId, String nuovoTitolo, int playlistId) throws SQLException;

    /**
     * Permette di aggiungere brani alla playliste dell'utente.
     * @param playlistId L'ID della playlist a cui aggiungere i brani.
     * @param brani I brani da aggiungere alla playlist.
     * @throws NessunaCanzoneTrovata Se nessuna canzone corrisponde ai criteri di ricerca.
     * @throws SQLException In caso di errore durante l'interazione con il database
     * @see ClientES
     * @see emotionalsongs.backend.entities.Canzone
     * @see emotionalsongs.backend.exceptions.NessunaCanzoneTrovata
     */
    void addBraniPlaylist(int playlistId, ArrayList<Canzone> brani) throws NessunaCanzoneTrovata, SQLException;

    /**
     * Permette di visualizzare le canzoni pressenti nella playlist selezionata.
     * @param playlistId L'ID della playlist a cui aggiungere i brani.
     * @throws SQLException In caso di errore durante l'interazione con il database
     * @see ClientES
     * @see emotionalsongs.backend.entities.Canzone
     */
    ArrayList<Canzone> showCanzoniPlaylist(int playlistId) throws SQLException;

    /**
     * Permette di inserire le emozioni per il brano selezionato.
     * @param playlistId L'ID della playlist in cui è presente il brano da valutare.
     * @param songId L'ID della canzone da valutare.
     * @param emozioni La lista delle emozioni valutate.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    void insEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException;

    /**
     * Permette di modificare le emozioni per il brano selezionato.
     * @param playlistId L'ID della playlist in cui è presente il brano da valutare.
     * @param songId L'ID della canzone da valutare.
     * @param emozioni La lista delle emozioni con le nuove valutazioni.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    void updateEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException;

    /**
     * Permette di visualizzare la media delle emozioni associate alla canzone.
     * @param songId L'ID della canzone di cui si vogliono visualizzare i commenti.
     * @throws NoVotazioni In caso non siano presenti votazioni per la canzone selezionata.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     * @see emotionalsongs.backend.exceptions.emozioni.NoVotazioni
     */
    List<Emozione> getVotazioniMedie(int songId) throws NoVotazioni, SQLException;

    /**
     * Permette di visualizzare i commenti associati alla canzone.
     * @param songId L'ID della canzone di cui si vogliono visualizzare i commenti.
     * @throws NoCommenti In caso non siano presenti commenti per la canzone selezionata.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     * @see emotionalsongs.backend.exceptions.emozioni.NoCommenti
     */
    List<Emozione> getCommenti(int songId) throws NoCommenti, SQLException;

    /**
     * Permette di visualizzare i dati del proprio account.
     * @param userId L'ID dell'utente collegato all'account.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    List<String> myAccount(int userId) throws SQLException;

    /**
     * Permette di rimuovere un brano presente nella playlist.
     * @param playlistId L'ID della playlist in cui è presente il brano.
     * @param canzoneId L'ID della canzone da rimuovere.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    int removePlaylistSong(int playlistId, int canzoneId) throws SQLException;

    /**
     * Permette di eliminare il proprio account.
     * @param userId L'ID dell'utente collegato all'account da eliminare.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    int eliminaAccount(int userId) throws SQLException;

    /**
     * Permette di modificare alcuni dati del proprio account.
     *
     * @param userId L'ID dell'utente collegato all'account da eliminare.
     * @param  residenza La nuova residenza dell'utente.
     * @param email La nuova email dell'utente.
     * @param password La nuova password dell'utente.
     * @throws SQLException In caso di errore durante l'interazione con il database.
     * @see ClientES
     */
    int modifcaDati(int userId, String residenza, String email, String password) throws SQLException;

}