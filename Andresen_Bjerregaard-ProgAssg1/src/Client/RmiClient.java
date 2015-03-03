package Client;

import Server.RmiServer;
import Shared.ServerConfig;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiClient {
    private static RmiServer server;
    private static Scanner scan = new Scanner(System.in);
    private static String updaterDelay = "not set";
    private static String updaterPeriod = "not set";
    private static TimeUnit updaterTimeUnit;
    private static HashMap<String, Boolean> updaterSettings;
    static
    {
        updaterSettings = new HashMap<>();
        updaterSettings.put("delay", false);
        updaterSettings.put("period", false);
        updaterSettings.put("timeunit", false);
    }

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
                currFrom = scan.nextLine().trim();
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
                currTo = scan.nextLine().trim();
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
                amount = scan.nextLine().trim();
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
            String input = scan.nextLine().trim();

            try {
                server.checkGenericIntegerInput(input);
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                continue;
            }

            switch (choice) {
                case 0:
                    nextRound = false;
                    System.out.println("Goodbye");
                    System.exit(0);
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

    private static void displayUpdateMenu() {
        System.out.println("Currency converter update settings");
        String print = null;
        int limit = 4;
        for (int i = 1; i <= limit; i++) {
            switch (i) {
                case 1:
                    print = "Initial delay";
                    break;
                case 2:
                    print = "Period";
                    break;
                case 3:
                    print = "Time unit";
                    break;
                case 4:
                    print = "View update settings";
                    break;
            }
            System.out.println(String.format("%2d" + ": " + "%s", i, print));
        }
        System.out.println(String.format("%2d" + ": " + "%s", 0, "Main menu"));
    }

    private static void exitMenu() throws RemoteException {
        displayExitChoice();

        int choice;

        while (scan.hasNextLine()) {
            String input = scan.nextLine().trim();

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
        displayUpdateMenu();

        int choice;

        while (scan.hasNextLine()) {
            String input = scan.nextLine().trim();

            try {
                server.checkGenericIntegerInput(input);
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                continue;
            }

            switch (choice) {
                case 0:
                    mainMenu();
                    break;
                case 1:
                    setUpdateInitialDelay();
                    break;
                case 2:
                    setUpdatePeriod();
                    break;
                case 3:
                    setUpdateTimeUnit();
                    break;
                case 4:
                    viewUpdateSettings();
                    break;
            }
        }
    }

    private static void displayUpdateSettings() {
        String updateTimeUnit = "";

        if (updaterTimeUnit == null)
            updateTimeUnit = "not set";

        System.out.println("View update settings");
        System.out.println(updaterTimeUnit == null ? String.format("%-14s %s", "Time unit:", updateTimeUnit) : String.format("%-14s %s", "Time unit:", updaterTimeUnit.toString().toLowerCase()));
        System.out.println(String.format("%-14s %s", "Initial delay:", updaterDelay));
        System.out.println(String.format("%-14s %s", "Period:", updaterPeriod));
    }

    private static void scheduleUpdater() throws RemoteException {
        if (updaterSettings.values().stream().anyMatch(x -> !x)) {
            displayUpdateSettings();
            updatePolicy();
        }
        else {
            displayUpdateSettings();
            System.out.println("Schedule updater with these settings?");
            System.out.println(String.format("%2d" + ": " + "%s", 1, "Yes"));
            System.out.println(String.format("%2d" + ": " + "%s", 2, "No"));
        }
    }

    private static void viewUpdateSettings() throws RemoteException {
        scheduleUpdater();

        int choice;

        while (scan.hasNextLine()) {
            String input = scan.nextLine().trim();

            try {
                server.checkGenericIntegerInput(input);
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                continue;
            }

            switch (choice) {
                case 1:
                    server.scheduleUpdate(Integer.valueOf(updaterDelay), Integer.valueOf(updaterPeriod), updaterTimeUnit);
                    System.out.println("Updater scheduled to run every " + Integer.valueOf(updaterPeriod) + " " + updaterTimeUnit.toString().toLowerCase());
                    mainMenu();
                    break;
                case 2:
                    mainMenu();
                    break;
            }
        }
    }

    private static void displayUpdateTimeUnitMenu() {
        System.out.println("Set update time unit");
        System.out.println(String.format("%2d" + ": " + "%s", 1, "Set update time units to minutes"));
        System.out.println(String.format("%2d" + ": " + "%s", 2, "Set update time units to hours"));
        System.out.println(String.format("%2d" + ": " + "%s", 3, "Set update time units to days"));
    }

    private static void setUpdateTimeUnit() throws RemoteException {
        displayUpdateTimeUnitMenu();

        int choice;

        while (scan.hasNextLine()) {
            String input = scan.nextLine().trim();

            try {
                server.checkGenericIntegerInput(input);
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
                continue;
            }

            switch (choice) {
                case 1:
                    updaterTimeUnit = TimeUnit.MINUTES;
                    updaterSettings.put("timeunit", true);
                    updatePolicy();
                    break;
                case 2:
                    updaterTimeUnit = TimeUnit.HOURS;
                    updaterSettings.put("timeunit", true);
                    updatePolicy();
                    break;
                case 3:
                    updaterTimeUnit = TimeUnit.DAYS;
                    updaterSettings.put("timeunit", true);
                    updatePolicy();
                    break;
            }
        }
    }

    private static void setUpdatePeriod() throws RemoteException {
        System.out.println("Set update period");

        boolean period = false;
        while (!period) {
            try {
                updaterPeriod = scan.nextLine().trim();
                if (updaterPeriod.trim().equals("0"))
                    System.err.println("'0' is not valid");
                else {
                    period = server.checkGenericIntegerInput(String.valueOf(updaterPeriod));
                    updaterSettings.put("period", true);
                    updatePolicy();
                }
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void setUpdateInitialDelay() throws RemoteException {
        System.out.println("Set updater delay");

        boolean delay = false;
        while (!delay) {
            try {
                updaterDelay = scan.nextLine().trim();
                delay = server.checkGenericIntegerInput(String.valueOf(updaterDelay));
                updaterSettings.put("delay", true);
                updatePolicy();
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
