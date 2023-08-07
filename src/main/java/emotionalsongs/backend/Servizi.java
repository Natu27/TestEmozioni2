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

public interface Servizi {
    Utente login(String username, String password) throws PasswordErrata, UsernameErrato, SQLException;
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno) throws NessunaCanzoneTrovata, SQLException;
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno, ArrayList<Canzone> braniDaEscludere) throws NessunaCanzoneTrovata, SQLException;
    void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws SQLException;
    List<String> getUsernames() throws SQLException;

    List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws SQLException;

    int addPlaylist(String titolo, int userId) throws SQLException;

    List<Playlist> myPlaylist(int userId) throws SQLException;

    int removePlaylist(int userId, String titolo) throws SQLException;

    int renamePlaylist(int userId, String nuovoTitolo, int playlistId) throws SQLException;

    void addBraniPlaylist(int playlistId, ArrayList<Canzone> brani) throws NessunaCanzoneTrovata, SQLException;

    ArrayList<Canzone> showCanzoniPlaylist(int userId) throws SQLException;

    void insEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException;

    void updateEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException;

    List<Emozione> getVotazioniMedie(int songId) throws NoVotazioni, SQLException;

    List<Emozione> getCommenti(int songId) throws NoCommenti, SQLException;
    List<String> myAccount(int userId) throws SQLException;
    int removePlaylistSong(int playlistId, int canzoneId) throws SQLException;
    //int uploloadProfilePic(int userId, byte[] picture) throws RemoteException;
    //String downloadProfilePic(int userId) throws RemoteException;
}