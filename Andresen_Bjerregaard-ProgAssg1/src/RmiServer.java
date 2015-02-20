import java.rmi.Remote;

/**
 * Created by prep on 20-02-2015.
 */
public interface RmiServer extends Remote {
    public String getMessage();
}