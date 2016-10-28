package Archives;

import SmartHome.Relay;
import com.adventnet.snmp.snmp2.*;
import java.util.logging.*;

public class TestRelay {

    public static Relay command;
    private static String[] Ports;

    public static void main(String[] args) {
        Ports = new String[17];

        for (int i = 1; i < 9; i++) {
            Ports[i] = ".1.3.6.1.4.1.19865.1.2.1." + i + ".0";
        }

        for (int i = 9; i < 17; i++) {
            Ports[i] = ".1.3.6.1.4.1.19865.1.2.2." + (i - 8) + ".0";
        }

        command = new Relay("192.168.1.2", 161, "private");

        for (int i = 1; i < 17; i++) {
            String result = command.SNMP_SET(Ports[i], SnmpAPI.INTEGER, "1");
            System.out.println(result);
        }

        for (int i = 1; i < 17; i++) {
            String result = command.SNMP_GET(Ports[i]);
            System.out.println(result);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestRelay.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 1; i < 17; i++) {
            String result = command.SNMP_SET(Ports[i], SnmpAPI.INTEGER, "0");
            System.out.println(result);
        }

        for (int i = 1; i < 17; i++) {
            String result = command.SNMP_GET(Ports[i]);
            System.out.println(result);
        }
    }
}
