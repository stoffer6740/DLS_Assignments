package Client;

import Server.RmiServer;
import Shared.ServerConfig;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiClient {
    private static RmiServer server;
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            server = (RmiServer) Naming.lookup(ServerConfig.SERVER_ADDRESS);

            server.getClientInfo();
            mainMenu();

        } catch (NotBoundException | RemoteException | MalformedURLException | ServerNotActiveException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void exchangeConverter() throws RemoteException {
        String splitChar = "\\$";
        List<String> currencies = server.getCurrencies();
        int index = 1;
        for (String currency : currencies) {
            System.out.println(String.format("%2d" + ": " + "%s", index, currency.split(splitChar)[1]));
            index++;
        }

        boolean firstCurrencyValid = false;
        System.out.println("\nChoose the currency you want to convert from:");
        String currFrom = "0";

        while (!firstCurrencyValid) {
            try {
                currFrom = scan.nextLine();
                firstCurrencyValid = server.checkValidIntegerInput(currFrom);
            } catch (InputMismatchException | NumberFormatException e) {
                System.err.println(e.getMessage());
            }
        }

        String sourceCurrencyShortName = currencies.get(Integer.valueOf(currFrom) - 1).split(splitChar)[0];
        String sourceCurrencyLongName = currencies.get(Integer.valueOf(currFrom) - 1).split(splitChar)[1];
        System.out.println("Selected start currency: " + sourceCurrencyLongName);

        boolean secondCurrencyValid = false;
        System.out.println("Choose the currency you want to convert to:");
        String currTo = "0";

        while (!secondCurrencyValid) {
            try {
                currTo = scan.nextLine();
                secondCurrencyValid = server.checkValidIntegerInput(currTo);
            } catch (InputMismatchException | NumberFormatException e) {
                System.err.println(e.getMessage());
            }
        }

        String targetCurrencyShortName = currencies.get(Integer.valueOf(currTo) - 1).split(splitChar)[0];
        String targetCurrencyLongName = currencies.get(Integer.valueOf(currTo) - 1).split(splitChar)[1];
        System.out.println("Selected target currency: " + targetCurrencyLongName);
        System.out.println("Current exchange rate: " + server.exchangeRate(sourceCurrencyShortName, targetCurrencyShortName));

        boolean amountValid = false;
        System.out.println("Input the amount you want to convert");
        String amount = "0";

        while (!amountValid) {
            try {
                amount = scan.nextLine();
                amountValid = server.checkValidDoubleInput(String.valueOf(amount));
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
            }
        }

        String totalAmount = server.exchangeRate(sourceCurrencyShortName, targetCurrencyShortName, Double.valueOf(amount));

        StringBuilder builder = new StringBuilder();
        builder.append(amount).append(" ").append(server.addCorrectEndLetter(sourceCurrencyLongName));
        builder.append(" = ").append(totalAmount).append(" ").append(server.addCorrectEndLetter(targetCurrencyLongName));

        System.out.println(builder);

        exitMenu();
    }

    private static void mainMenu() throws RemoteException {
        boolean nextRound = true;
        int choice;

        displayMainMenu();
        while (nextRound && scan.hasNextLine()) {
            String input = scan.nextLine();

            try {
                server.checkGenericIntegerInput(input);
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                continue;
            }

            switch (choice) {
                case 0:
                    System.out.println("Goodbye");
                    nextRound = false;
                    break;
                case 1:
                    exchangeConverter();
                    break;
                case 2:
                    updatePolicy();
                    break;
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("Currency converter of the future v2.1-a");
        System.out.println(String.format("%2d" + ": " + "%s", 1, "Currency converter"));
        System.out.println(String.format("%2d" + ": " + "%s", 2, "Update policy"));
        System.out.println(String.format("%2d" + ": " + "%s", 0, "Exit"));
    }

    private static void displayExitChoice() {
        System.out.println("Exit the program?");
        System.out.println(String.format("%2d" + ": " + "%s", 1, "Yes"));
        System.out.println(String.format("%2d" + ": " + "%s", 2, "No"));
    }

    private static void exitMenu() throws RemoteException {
        displayExitChoice();

        int choice;

        while (scan.hasNextLine()) {
            String input = scan.nextLine();

            try {
                server.checkGenericIntegerInput(input);
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                continue;
            }

            switch (choice) {
                case 1:
                    System.exit(0);
                    break;
                case 2:
                    mainMenu();
                    break;
            }
        }
    }

    // TODO not complete
    private static void updatePolicy() throws RemoteException {
        System.out.println("\nInput update interval in minutes");

        String minutes = scan.nextLine();
        server.scheduleUpdate(Integer.valueOf(minutes));
    }
}
