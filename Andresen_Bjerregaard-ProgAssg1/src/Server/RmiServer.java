package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by prep on 20-02-2015.
 */
public interface RmiServer extends Remote {
    public String exchangeRate(String sourceCurrency, String targetCurrency, Double amount) throws RemoteException;

    public List<String> getCurrencies() throws RemoteException;
}