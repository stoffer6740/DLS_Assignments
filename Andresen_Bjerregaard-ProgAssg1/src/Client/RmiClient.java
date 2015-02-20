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
    public static void main(String[] args) {
        String name = "Klaus";

        try {
            RmiServer obj = (RmiServer) Naming.lookup(ServerConfig.SERVER_ADDRESS);
            System.out.println(obj.getMessage(name));
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }
}