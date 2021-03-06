package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by prep on 20-02-2015.
 */
public interface RmiServer extends Remote {
    void scheduleUpdate(int delay, int period, TimeUnit unit) throws RemoteException;

    String exchangeRate(String sourceCurrency, String targetCurrency, Double amount) throws RemoteException;

    String addCorrectEndLetter(String input) throws RemoteException;

    String exchangeRate(String sourceCurrency, String targetCurrency) throws RemoteException;

    List<String> getCurrencies() throws RemoteException;

    boolean checkValidIntegerInput(String input) throws RemoteException;

    boolean checkGenericIntegerInput(String input) throws RemoteException;

    boolean checkValidDoubleInput(String input) throws RemoteException;

    void getClientInfo() throws ServerNotActiveException, RemoteException;
}