package Client;

import Server.RmiServer;
import Shared.ServerConfig;

import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

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
        List<String> currencies = server.getCurrencies();
        int index = 1;
        for (String currency : currencies) {
            System.out.println(String.format("%2d" + ": " + "%s", index, currency.split("\\$")[1]));
            index++;
        }

        Scanner scan = new Scanner(System.in);
        System.out.println("Choose the currency you want to convert from:");
        int currFrom = scan.nextInt();
        String selectedFromCurrency = currencies.get(currFrom - 1).split("\\$")[0];
        System.out.print("Choose the currency you want to convert to:");
        int currTo = scan.nextInt();
        String selectedToCurrency = currencies.get(currTo - 1).split("\\$")[0];
        System.out.println("The amount you want to convert");
        Double amount = scan.nextDouble();

        System.out.println(server.exchangeRate(selectedFromCurrency, selectedToCurrency, amount));

    }


}