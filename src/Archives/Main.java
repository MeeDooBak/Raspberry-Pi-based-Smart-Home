package Archives;

import Device.Relay;
import com.adventnet.snmp.snmp2.SnmpAPI;
import com.pi4j.io.gpio.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static int num = 0;
    public static Relay command;

    public static void main(String[] args) {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput pin17 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, "PIN 17");// IR Receiver PIN 17

        command = new Relay("192.168.1.21", 161, "private");
        while (true) {
            if (pin17.isHigh()) {
                if (num == 1) {
                    try {
                        System.out.println("Good");
                        num = 0;
                        command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + 5 + ".0", SnmpAPI.INTEGER, "1");
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                if (num == 0) {
                    System.out.println("Bad");
                    num = 1;
                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + 5 + ".0", SnmpAPI.INTEGER, "0");
                }
            }
        }
    }
}
