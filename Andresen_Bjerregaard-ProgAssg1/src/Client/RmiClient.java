package Client;

import Server.RmiServer;
import Shared.ServerConfig;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiClient {
    private static RmiServer server;

    public static void main(String[] args) {
        try {
            server = (RmiServer) Naming.lookup(ServerConfig.SERVER_ADDRESS);

            exchangeConverter();

        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void exchangeConverter() throws RemoteException {
        int index = 1;
        for (String currency : server.getCurrencies()) {
            System.out.println(String.format("%2d" + ": " + "%s", index, currency.split("\\$")[1]));
            index++;
        }
    }
}