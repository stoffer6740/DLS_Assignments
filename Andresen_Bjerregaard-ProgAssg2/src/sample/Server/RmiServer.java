package sample.Server;

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

    double exchangeRate(String sourceCurrency, String targetCurrency, Double amount) throws RemoteException;

    double exchangeRate(String sourceCurrency, String targetCurrency) throws RemoteException;

    void getClientInfo() throws ServerNotActiveException, RemoteException;
}