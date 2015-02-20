import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by prep on 20-02-2015.
 */
public interface RmiServer extends Remote {
    public String getMessage() throws RemoteException;
}