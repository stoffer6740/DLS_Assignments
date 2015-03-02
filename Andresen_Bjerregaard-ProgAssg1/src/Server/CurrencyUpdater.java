package Server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 01-03-2015.
 */
public class CurrencyUpdater implements Runnable {
    private int count = 1;
    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void run() {
        System.out.println("Update #" + count + " started " + sdf.format(cal.getTime()));
        RmiServerImpl.fillCurrencyCache();
        System.out.println("Update #" + count + " completed " + sdf.format(cal.getTime()));
        count++;
    }
}
