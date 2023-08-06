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
    Utente login(String username, String password) throws PasswordErrata, UsernameErrato, RemoteException;
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno) throws NessunaCanzoneTrovata, RemoteException;
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno, ArrayList<Canzone> braniDaEscludere) throws NessunaCanzoneTrovata, RemoteException;
    void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws RemoteException;
    List<String> getUsernames() throws RemoteException;

    List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws RemoteException;

    int addPlaylist(String titolo, int userId) throws RemoteException;

    List<Playlist> myPlaylist(int userId) throws RemoteException;

    int removePlaylist(int userId, String titolo) throws RemoteException;

    int renamePlaylist(int userId, String nuovoTitolo, int playlistId) throws RemoteException;

    void addBraniPlaylist(int playlistId, ArrayList<Canzone> brani) throws NessunaCanzoneTrovata, RemoteException;

    ArrayList<Canzone> showCanzoniPlaylist(int userId) throws RemoteException;

    void insEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException, RemoteException;

    void updateEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws RemoteException;

    List<Emozione> getVotazioniMedie(int songId) throws NoVotazioni, RemoteException;

    List<Emozione> getCommenti(int songId) throws NoCommenti, RemoteException;
    List<String> myAccount(int userId) throws RemoteException;
    int removePlaylistSong(int playlistId, int canzoneId) throws RemoteException;
    //int uploloadProfilePic(int userId, byte[] picture) throws RemoteException;
    //String downloadProfilePic(int userId) throws RemoteException;
}