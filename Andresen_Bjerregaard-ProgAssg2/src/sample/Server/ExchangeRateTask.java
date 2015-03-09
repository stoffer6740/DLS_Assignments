package sample.Server;

import javafx.concurrent.Task;

/**
 * Created by Administrator on 08-03-2015.
 */
public class ExchangeRateTask extends Task<Double> {
    RmiServer server;
    String sourceCurrency;
    String targetCurrency;
    double amount;

    public ExchangeRateTask(RmiServer server, String selectedSourceCurrency, String selectedTargetCurrency, double amount) {
        this.server = server;
        this.sourceCurrency = selectedSourceCurrency;
        this.targetCurrency = selectedTargetCurrency;
        this.amount = amount;
    }

    @Override
    protected Double call() throws Exception {
        return server.exchangeRate(sourceCurrency, targetCurrency, amount);
    }
}
