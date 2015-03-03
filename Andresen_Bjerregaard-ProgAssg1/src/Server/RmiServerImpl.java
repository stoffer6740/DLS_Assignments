package Server;

import Shared.RegistryConfig;
import Shared.ServerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private CurrencyUpdater updater = new CurrencyUpdater();
    private static CurrencyLoader currencyCache = CurrencyLoader.INSTANCE;
    private static HashMap<String, Double> currencyExchange;

    public RmiServerImpl() throws RemoteException {
        super(0);
    }

    public static void main(String args[]) throws Exception {
        System.out.println("RMI server started");

        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(RegistryConfig.REGISTRY_PORT);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }

        //Instantiate Server.RmiServer
        RmiServerImpl obj = new RmiServerImpl();

        // Bind this object instance to the name "Server.RmiServer"
        Naming.rebind(ServerConfig.SERVER_ADDRESS, obj);
        System.out.println("PeerServer bound in registry");

        // Fetch currencies from yahoo
        System.out.println("Fetching initial currencies...");
        fillCurrencyCache();
        System.out.println("All currencies are up to date");
    }

    protected static void fillCurrencyCache() {
        String splitChar = "\\$";
        int arrayIndex = 0;
        List<String> currencies = currencyCache.getCurrencyList();
        currencyExchange = new HashMap<>();

        for (String sourceCurrency : currencies) {
            for (String targetCurrency : currencies) {
                String splitSourceCurr = sourceCurrency.split(splitChar)[arrayIndex].trim();
                String splitTargetCurr = targetCurrency.split(splitChar)[arrayIndex].trim();
                String appendedCurrency = splitSourceCurr + splitTargetCurr;

                Double value = fetchSingleCurrency(appendedCurrency);
                currencyExchange.put(appendedCurrency, value);
            }
        }
    }

    private static double fetchSingleCurrency(String currency) {
        String req = "http://quote.yahoo.com/d/quotes.cvs?s=" + currency.toUpperCase() + "=X&f=l1&e=.cvs";
        String exchangeValue = "";

        try {
            URL url = new URL(req);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            exchangeValue = in.readLine();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Double.valueOf(exchangeValue);
    }

    private static boolean isVowel(char c) {
        return "EIUeiu".indexOf(c) != -1;
    }

    @Override
    public void scheduleUpdate(int delay, int period, TimeUnit unit) {
        // could have used Quartz as an alternative
        System.out.println("Updater scheduled to run every " + period + " minute(s)");
        scheduler.scheduleAtFixedRate(updater, delay, period, unit);
    }

    @Override
    public String exchangeRate(String sourceCurrency, String targetCurrency, Double amount) {
        String appendedCurrency = sourceCurrency + targetCurrency;
        DecimalFormat df = new DecimalFormat("###,###.##");

        if (currencyExchange.containsKey(appendedCurrency))
            return df.format(currencyExchange.get(appendedCurrency) * amount);
        else {
            double value = fetchSingleCurrency(appendedCurrency);
            currencyExchange.put(appendedCurrency, value);
            return df.format(value * amount);
        }
    }

    @Override
    public String addCorrectEndLetter(String input) {
        char lastCharacter = input.charAt(input.length() - 1);

        if (isVowel(lastCharacter))
            return input + "r";
        else switch (lastCharacter) {
            case 'n':
                return input;
            default:
                return input + "s";
        }
    }

    @Override
    public String exchangeRate(String sourceCurrency, String targetCurrency) {
        return exchangeRate(sourceCurrency, targetCurrency, 1d);
    }

    @Override
    public List<String> getCurrencies() {
        String splitChar = "\\$";
        int index = 1;

        return currencyCache.getCurrencyList()
                .stream()
                .sorted((s1, s2) -> s1.split(splitChar)[index].compareTo(s2.split(splitChar)[index]))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkValidIntegerInput(String input) {
        int integerLimit = getCurrencies().size();

        try {
            Integer.parseInt(input.trim());
            if (Integer.parseInt(input.trim()) > integerLimit)
                throw new InputMismatchException("Input a valid number between 1 and " + integerLimit);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Input whole numbers only");
        }
        return true;
    }

    @Override
    public boolean checkGenericIntegerInput(String input) {
        try {
            Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("'" + input.trim() + "' is not valid");
        }
        return true;
    }

    @Override
    public boolean checkValidDoubleInput(String input) {
        try {
            Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Numbers only and use '.' to specify double values"); // should use a comma instead if possible
        }
        return true;
    }

    @Override
    public void getClientInfo() throws ServerNotActiveException {
        // log it instead if a logger was implemented
        System.err.println("Connected client: " + RemoteServer.getClientHost());
    }
}