import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

    public RmiServerImpl() throws RemoteException {
        super();
    }

    @Override
    public String getMessage() {
        return "Hello world!";
    }
}