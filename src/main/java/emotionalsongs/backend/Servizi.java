package emotionalsongs.backend;

import java.rmi.Remote;
import java.rmi.RemoteException;
import emotionalsongs.backend.entities.Canzone;
import emotionalsongs.backend.exceptions.NessunaCanzoneTrovata;

import java.util.List;

public interface Servizi extends Remote {
    List<Canzone> searchSong(String titoloDaCercare, String autoreDaCercare, Integer anno) throws NessunaCanzoneTrovata, RemoteException;

}