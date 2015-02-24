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
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {
    private static CurrencyLoader currencyCache = CurrencyLoader.INSTANCE;
    private static LinkedHashMap<String, Double> currencyExchange;

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
        System.out.println("Updating currencies...");
        fillCurrencyCache();
        System.out.println("Done updating currencies");
    }

    private static void fillCurrencyCache() {
        String splitChar = "\\$";
        int arrayIndex = 0;
        List<String> currencies = currencyCache.getCurrencyList();
        currencyExchange = new LinkedHashMap<>();

        for (String sourceCurrency : currencies) {
            for (String targetCurrency : currencies) {
                String splitSourceCurr = sourceCurrency.split(splitChar)[arrayIndex];
                String splitTargetCurr = targetCurrency.split(splitChar)[arrayIndex];
                String appendedCurrency = splitSourceCurr + splitTargetCurr;

                String req = "http://quote.yahoo.com/d/quotes.cvs?s=" + appendedCurrency.toUpperCase() + "=X&f=l1&e=.cvs";
                String exchangeValue = "";

                try {
                    URL url = new URL(req);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    exchangeValue = in.readLine();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                currencyExchange.put(appendedCurrency, Double.valueOf(exchangeValue));
            }
        }
    }

    @Override
    public String exchangeRate(String sourceCurrency, String targetCurrency, Double amount) {
        DecimalFormat df = new DecimalFormat("###,###.##");

        return df.format(currencyExchange.get(sourceCurrency + targetCurrency) * amount);
    }

    @Override
    public List<String> getCurrencies() {
        return currencyCache.getCurrencyList();
    }
}