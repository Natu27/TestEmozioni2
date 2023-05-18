package emotionalsongs.backend;

import java.rmi.Remote;
import java.rmi.RemoteException;
import emotionalsongs.backend.entities.Canzone;
import java.util.List;

public interface Servizi extends Remote {
    List<Canzone> searchSong(String titoloDaCercare, String auoreDaCercare, Integer anno) throws RemoteException;

}