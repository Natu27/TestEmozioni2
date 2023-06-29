package emotionalsongs.backend;

import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Emozione;
import emotionalsongs.backend.entities.Playlist;
import emotionalsongs.backend.entities.Utente;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;
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
    // restituisce gli anni per cui ci sono canzoni con il titolo e l'autore selezionato (utilizzata per popolare la tendina di ricerca)
    List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws RemoteException;
    int addPlaylist(String titolo, int userId) throws RemoteException;
    List<Playlist> myPlaylist(int userId) throws RemoteException;
    int removePlaylist(int userId, String titolo) throws RemoteException;
    int renamePlaylist(int userId, String nuovoTitolo, int playlistId) throws RemoteException;
    void addBraniPlaylist(int playlistId, ArrayList<Canzone> brani) throws NessunaCanzoneTrovata, RemoteException;
    ArrayList<Canzone> showCanzoniPlaylist(int userId) throws RemoteException;
    void insEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws SQLException, RemoteException;
    void updateEmoBranoPlaylist(int playlistId, int songId, List<Emozione> emozioni) throws RemoteException;
    List<Emozione> getVotazioni(int songId) throws  RemoteException;
}