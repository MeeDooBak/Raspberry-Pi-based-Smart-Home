package Archives;

import Device.Relay;
import com.adventnet.snmp.snmp2.*;
import com.pi4j.io.gpio.*;
import java.util.logging.*;

public class DistanceMonitor {

    private final static GpioController gpio = GpioFactory.getInstance();
    private final GpioPinDigitalInput echoPin;
    private final GpioPinDigitalOutput trigPin;
    public static Relay command;

    private DistanceMonitor(Pin echoPin, Pin trigPin) {
        this.echoPin = gpio.provisionDigitalInputPin(echoPin);
        this.trigPin = gpio.provisionDigitalOutputPin(trigPin);
        this.trigPin.low();
    }

    public float measureDistance() {
        this.triggerSensor();
        this.waitForSignal();
        long duration = this.measureSignal();

        return duration * 340.29f / (2 * 10000);
    }

    private void triggerSensor() {
        try {
            this.trigPin.high();
            Thread.sleep(0, 10000);
            this.trigPin.low();
        } catch (InterruptedException ex) {
            System.err.println("Interrupt during trigger");
        }
    }

    private void waitForSignal() {
        int countdown = 2100;
        while (this.echoPin.isLow() && countdown > 0) {
            countdown--;
        }
    }

    private long measureSignal() {
        int countdown = 2100;
        long start = System.nanoTime();
        while (this.echoPin.isHigh() && countdown > 0) {
            countdown--;
        }
        long end = System.nanoTime();
        return (long) Math.ceil((end - start) / 1000.0);  // Return micro seconds
    }

    public static void main(String[] args) {
        Pin echoPin = RaspiPin.GPIO_28; // PI4J custom numbering (pin 20)
        Pin trigPin = RaspiPin.GPIO_29; // PI4J custom numbering (pin 21)
        DistanceMonitor monitor = new DistanceMonitor(echoPin, trigPin);

        command = new Relay("192.168.1.21", 161, "private");

        while (true) {
            try {
                int Distance = (int) Math.ceil(monitor.measureDistance());
                if (Distance <= 4) {
                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + 8 + ".0", SnmpAPI.INTEGER, "0");
                } else if (Distance >= 15) {
                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + 8 + ".0", SnmpAPI.INTEGER, "1");
                }
                System.out.println(Distance);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + 8 + ".0", SnmpAPI.INTEGER, "0");
                Logger.getLogger(DistanceMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
