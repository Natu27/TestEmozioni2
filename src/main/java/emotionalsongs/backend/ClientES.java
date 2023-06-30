package emotionalsongs.backend;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientES {
    private static final int PORT = 10002;
    private Servizi stub = null; //Pattern Singleton + o - ad HOC per RMI

    public Servizi getStub() throws Exception {
        if (stub == null) {
            stub = lookUpService();
            return stub;
        }
        return stub;
    }

    private Servizi lookUpService() throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", PORT);
        return (Servizi) registry.lookup("ServiziES");
    }
}
