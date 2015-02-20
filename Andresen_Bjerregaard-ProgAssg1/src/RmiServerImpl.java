import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

    public RmiServerImpl() throws RemoteException {
        super(0);
    }

    public static void main(String args[]) throws Exception {
        System.out.println("RMI server started");

        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }

        //Instantiate RmiServer
        RmiServerImpl obj = new RmiServerImpl();

        // Bind this object instance to the name "RmiServer"
        Naming.rebind(ServerConfig.SERVER_ADDRESS, obj);
        System.out.println("PeerServer bound in registry");
    }

    @Override
    public String getMessage(String name) {
        return "Hello " + name;
    }
}