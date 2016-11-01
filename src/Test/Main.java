package Test;

import Device.Relay;
import com.adventnet.snmp.snmp2.SnmpAPI;
import com.pi4j.io.gpio.*;

public class Main {

    public static int num = 0;
    public static boolean change;
    public static Relay command;
    public static GpioPinDigitalOutput pin18;

    public static void main(String[] args) {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput pin17 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "PIN 17");// IR Receiver PIN 17
        GpioPinDigitalOutput pin18 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "PIN 18");// LED PIN 18

        command = new Relay("192.168.1.15", 161, "private");
        while (true) {
            if (pin17.isLow()) {
                if (num == 1) {
                    System.out.println("Good");
                    pin18.low();
                    num = 0;
                    change = true;
                    new Thread(Change).start();
                }
            } else {
                if (num == 0) {
                    System.out.println("Bad");
                    pin18.high();
                    num = 1;
                    change = false;
                    new Thread(Change).start();
                }
            }
        }
    }
    private static final Runnable Change = new Runnable() {
        @Override
        public void run() {
            if (change) {
                command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + 1 + ".0", SnmpAPI.INTEGER, "0");
            } else {
                command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + 1 + ".0", SnmpAPI.INTEGER, "1");
            }
        }
    };
}
