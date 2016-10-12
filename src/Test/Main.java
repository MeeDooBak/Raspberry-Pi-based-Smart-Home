package Test;

import Relay.Relay;
import com.pi4j.io.gpio.*;

public class Main {

    public static Relay command;

    public static void main(String[] args) {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalInput pin27 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, "PIN 27");// IR Receiver PIN 27
        GpioPinDigitalOutput pin4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "PIN 4");// IR LED PIN 7
        GpioPinDigitalOutput pin18 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "PIN 18");// LED PIN 18

        command = new Relay("192.168.1.2", 161, "private");

        while (true) {
            pin4.high();
            if (pin27.isHigh()) {
                pin18.high();
                System.out.println("Good");
                //command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + 1 + ".0", SnmpAPI.INTEGER, "0");

            } else {
                //command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + 1 + ".0", SnmpAPI.INTEGER, "1");
                pin18.low();
                System.out.println("Bad");
            }
            pin4.low();
        }
    }
}
