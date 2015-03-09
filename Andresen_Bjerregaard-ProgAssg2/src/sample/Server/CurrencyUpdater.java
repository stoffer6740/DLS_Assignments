package sample.Server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 01-03-2015.
 */
public class CurrencyUpdater implements Runnable {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private int count = 1;

    @Override
    public void run() {
        Calendar calStart = Calendar.getInstance();

        System.out.println(String.format("%-19s %s", "Update #" + count + " started ", dateFormat.format(calStart.getTime())));
        RmiServerImpl.fillCurrencyCache();

        Calendar calEnd = Calendar.getInstance();
        System.out.println(String.format("%-19s %s", "Update #" + count + " completed ", dateFormat.format(calEnd.getTime())));
        count++;
    }
}