package emotionalsongs.backend;

import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Servizi extends Remote {
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno) throws NessunaCanzoneTrovata, RemoteException;

    public void registrazione(String nome, String cognome, String indirizzo, String codiceFiscale, String email, String username, String password) throws RemoteException;

    // restituisce gli anni per cui ci sono canzoni
    // (utilizzata per popolare la tendina di ricerca)
    public List<Integer> getAnni() throws RemoteException;
}