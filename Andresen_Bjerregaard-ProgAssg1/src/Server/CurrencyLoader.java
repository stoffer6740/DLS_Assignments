package Server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by prep on 24-02-2015.
 */
public enum CurrencyLoader {
    INSTANCE;

    private List<String> currencyList;

    CurrencyLoader() {
        Charset defaultCharset = Charset.defaultCharset();
        // Path to the file containing the different currencies
        Path currencyPath = Paths.get("Currency");

        // Handle exception in a different way
        try {
            currencyList = Files.readAllLines(currencyPath, defaultCharset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<String> getCurrencyList() {
        return currencyList;
    }
}
