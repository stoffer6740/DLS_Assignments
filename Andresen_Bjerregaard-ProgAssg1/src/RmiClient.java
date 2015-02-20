import java.rmi.Naming;

/**
 * Created by prep on 20-02-2015.
 */
public class RmiClient {
    public static void main(String[] args) throws Exception {
        RmiServer obj = (RmiServer) Naming.lookup("//localhost/RmiServer");
        System.out.println(obj.getMessage());
    }
}