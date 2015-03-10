package sample.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.concurrent.TimeUnit;

/**
 * Created by prep on 20-02-2015.
 */
public interface RmiServer extends Remote {
    double exchangeRate(String sourceCurrency, String targetCurrency, Double amount) throws RemoteException;

    double exchangeRate(String sourceCurrency, String targetCurrency) throws RemoteException;

    void getClientInfo() throws ServerNotActiveException, RemoteException;

    UpdaterObject getUpdaterSettings() throws RemoteException;

    void setUpdaterSettings(int delay, int period, TimeUnit timeUnit) throws RemoteException;
}