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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Servizi extends Remote {
    Utente login(String username, String password) throws PasswordErrata, UsernameErrato, SQLException, RemoteException;
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno) throws NessunaCanzoneTrovata,SQLException, RemoteException;
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno, ArrayList<Canzone> braniDaEscludere) throws NessunaCanzoneTrovata, SQLException, RemoteException;
    void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws SQLException, RemoteException;
    // restituisce gli anni per cui ci sono canzoni con il titolo e l'autore selezionato (utilizzata per popolare la tendina di ricerca)
    List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws SQLException, RemoteException;

    int addPlaylist(String titolo, int userId) throws SQLException, RemoteException;

    List<Playlist> myPlaylist(int userId) throws SQLException, RemoteException;

    int removePlaylist(int userId, String titolo) throws SQLException, RemoteException;

    int renamePlaylist(int userId, String nuovoTitolo, int playlistId) throws SQLException, RemoteException;

    void addBraniPlaylist(int playlistId, ArrayList<Canzone> brani) throws NessunaCanzoneTrovata, SQLException, RemoteException;

    ArrayList<Canzone> showCanzoniPlaylist(int userId) throws SQLException, RemoteException;

    void insEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException, RemoteException;

    void updateEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException, RemoteException;

    List<Emozione> getVotazioniMedie(int songId) throws NoVotazioni, SQLException, RemoteException;

    List<Emozione> getCommenti(int songId) throws NoCommenti, SQLException, RemoteException;
    int removePlaylistSong(int playlistId, int canzoneId) throws SQLException, RemoteException;
    List<String> getUsernames() throws SQLException, RemoteException;
    List<String> myAccount(int userId) throws SQLException, RemoteException;
    int eliminaAccount(int userId) throws SQLException, RemoteException;
    int modifcaDati(int userId, String residenza, String email, String password) throws SQLException, RemoteException;
}