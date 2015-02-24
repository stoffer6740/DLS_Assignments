package Client;

import Server.RmiServer;
import Shared.ServerConfig;

import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.InputMismatchException;
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

    // Better handling of try catch blocks if possible
    private static void exchangeConverter() throws RemoteException {
        String splitChar = "\\$";
        List<String> currencies = server.getCurrencies();
        int index = 1;
        for (String currency : currencies) {
            System.out.println(String.format("%2d" + ": " + "%s", index, currency.split(splitChar)[1]));
            index++;
        }

        Scanner scan = new Scanner(System.in);

        boolean firstCurrencyValid = false;
        System.out.println("\nChoose the currency you want to convert from:");
        int currFrom = 0;

        while (!firstCurrencyValid) {
            try {
                currFrom = scan.nextInt();
                if (currFrom <= currencies.size())
                    firstCurrencyValid = true;
                else
                    System.err.println("You must pick a valid number from 1 to " + currencies.size());
            } catch (InputMismatchException e) {
                System.err.println("Please input whole numbers only");
                scan.next();
            }
        }
        System.out.println("Selected start currency: " + currencies.get(currFrom - 1).split(splitChar)[1]);
        String selectedFromCurrency = currencies.get(currFrom - 1).split(splitChar)[0];

        boolean secondCurrencyValid = false;
        System.out.println("Choose the currency you want to convert to:");
        int currTo = 0;

        while (!secondCurrencyValid) {
            try {
                currTo = scan.nextInt();
                if (currTo <= currencies.size())
                    secondCurrencyValid = true;
                else
                    System.err.println("You must pick a valid number from 1 to " + currencies.size());
            } catch (InputMismatchException e) {
                System.err.println("Please input whole numbers only");
                scan.next();
            }
        }
        System.out.println("Selected start currency: " + currencies.get(currTo - 1).split(splitChar)[1]);
        String selectedToCurrency = currencies.get(currTo - 1).split(splitChar)[0];
        System.out.println("Current exchange rate: " + server.exchangeRate(selectedFromCurrency, selectedToCurrency));

        boolean amountValid = false;
        System.out.println("Input the amount you want to convert");
        double amount = 0;

        while (!amountValid) {
            try {
                amount = scan.nextDouble();
                amountValid = true;
            } catch (InputMismatchException e) {
                System.err.println("Numbers only and use ',' to specify double values");
                scan.next();
            }
        }
        System.out.println("Total: " + server.exchangeRate(selectedFromCurrency, selectedToCurrency, amount));
    }
}