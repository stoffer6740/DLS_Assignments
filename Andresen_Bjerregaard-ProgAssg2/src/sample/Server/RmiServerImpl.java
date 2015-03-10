package sample.Server;

import sample.Shared.RegistryConfig;
import sample.Shared.ServerConfig;

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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private CurrencyUpdater updater = new CurrencyUpdater();
    private static CurrencyLoader currencyCache = CurrencyLoader.INSTANCE;
    private static HashMap<String, Double> currencyExchange = new HashMap<>();
    private UpdaterObject updaterSettings;

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

    @Override
    public void scheduleUpdate(UpdaterObject updaterSettings) {
        System.out.println("Updater scheduled to run every " + updaterSettings.getPeriod() + " " + updaterSettings.getTimeUnit().toString().toLowerCase());
        scheduler.scheduleAtFixedRate(updater, updaterSettings.getDelay(), updaterSettings.getPeriod(), updaterSettings.getTimeUnit());
    }

    @Override
    public double exchangeRate(String sourceCurrency, String targetCurrency, Double amount) {
        String appendedCurrency = sourceCurrency + targetCurrency;

        if (currencyExchange.containsKey(appendedCurrency))
            return currencyExchange.get(appendedCurrency) * amount;
        else {
            double value = fetchSingleCurrency(appendedCurrency);
            currencyExchange.put(appendedCurrency, value);
            return value * amount;
        }
    }

    @Override
    public double exchangeRate(String sourceCurrency, String targetCurrency) {
        return exchangeRate(sourceCurrency, targetCurrency, 1d);
    }

    @Override
    public void getClientInfo() throws ServerNotActiveException {
        // log it instead if a logger was implemented
        System.err.println("Connected client: " + RemoteServer.getClientHost());
    }

    @Override
    public UpdaterObject getUpdaterSettings() {
        return this.updaterSettings;
    }

    @Override
    public void setUpdaterSettings(UpdaterObject updaterSettings) {
        this.updaterSettings = updaterSettings;
    }
}