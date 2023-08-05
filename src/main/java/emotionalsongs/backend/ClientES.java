package emotionalsongs.backend;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientES {
    private static final int PORT = 10002;
    private final Servizi stub; // Pattern Singleton + o - ad HOC per RMI

    public ClientES() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", PORT);
        stub = (Servizi) registry.lookup("ServiziES");
    }
    public Servizi getStub() {
        return stub;
    }

}
