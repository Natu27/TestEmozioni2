package emotionalsongs.backend;

import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

public class ClientES {
    private static final int PORT = 10002;
    private static ClientES instance;
    private final Servizi stub;
    private ClientES() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", PORT);
        stub = (Servizi) registry.lookup("ServiziES");
    }
    public static synchronized ClientES getInstance() throws RemoteException, NotBoundException {
        if (instance == null) { instance = new ClientES();
        } return instance;
    }
    public Servizi getStub() {
        return stub;
    }


}
