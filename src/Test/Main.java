package Test;

import Relay.Relay;
import com.adventnet.snmp.snmp2.SnmpAPI;
import com.pi4j.io.gpio.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static Relay command;

    public static void main(String[] args) {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput pin17 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "PIN 17");// IR Receiver PIN 17
        GpioPinDigitalOutput pin18 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "PIN 18");// LED PIN 18

        command = new Relay("192.168.1.2", 161, "private");
        while (true) {
            if (pin17.isLow()) {
                pin18.low();
                System.out.println("Good");
                command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + 1 + ".0", SnmpAPI.INTEGER, "0");

            } else {
                command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + 1 + ".0", SnmpAPI.INTEGER, "1");
                pin18.high();
                System.out.println("Bad");
            }
        }
    }
}
