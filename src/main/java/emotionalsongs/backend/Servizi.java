package emotionalsongs.backend;

import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.entities.Playlist;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;
import emotionalsongs.backend.exceptions.utente.PasswordErrata;
import emotionalsongs.backend.exceptions.utente.UsernameErrato;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Servizi extends Remote {
    String login(String userid, String password) throws PasswordErrata, UsernameErrato, RemoteException;
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno) throws NessunaCanzoneTrovata, RemoteException;

    void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws RemoteException;

    // restituisce gli anni per cui ci sono canzoni con il titolo e l'autore selezionato
    // (utilizzata per popolare la tendina di ricerca)
    List<Integer> getAnni(String titoloDaCercare, String autoreDaCercare) throws RemoteException;
    int addPlaylist(String titolo, String username) throws RemoteException;
    List<Playlist> myPlaylist(String username) throws RemoteException;
    int removePlaylist(String username, String titolo) throws RemoteException;
    //int renamePlaylist(String username, String nuovoTitolo, String vecchioTitolo) throws RemoteException;



}