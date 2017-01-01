package _Testing;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewMain1 {

    public static void main(String[] args) {
        NewMain1 newMain1 = new NewMain1();
    }

    public NewMain1() {
        GpioController GPIO = GpioFactory.getInstance();

        GpioPinDigitalInput PIN1 = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_UP);
        GpioPinDigitalInput PIN2 = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_23, PinPullResistance.PULL_UP);
        GpioPinDigitalInput PIN3 = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_UP);
        GpioPinDigitalInput PIN4 = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_UP);

        GPIO.addListener(Listener1, PIN1);
        GPIO.addListener(Listener2, PIN2);
        GPIO.addListener(Listener3, PIN3);
        GPIO.addListener(Listener4, PIN4);

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NewMain1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    GpioPinListenerDigital Listener1 = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
            if (gpdsce.getState().isLow()) {
                System.out.println("LightSensor in Parent Room is ON");
            } else {
                System.out.println("LightSensor in Parent Room is OFF");
            }
        }
    };
    GpioPinListenerDigital Listener2 = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
            if (gpdsce.getState().isLow()) {
                System.out.println("LightSensor in Living Room is ON");
            } else {
                System.out.println("LightSensor in Living Room is OFF");
            }
        }
    };
    GpioPinListenerDigital Listener3 = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
            if (gpdsce.getState().isLow()) {
                System.out.println("LightSensor in Girl Room is ON");
            } else {
                System.out.println("LightSensor in Girl Room is OFF");
            }
        }
    };
    GpioPinListenerDigital Listener4 = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent gpdsce) {
            if (gpdsce.getState().isLow()) {
                System.out.println("LightSensor in Boy Room is ON");
            } else {
                System.out.println("LightSensor in Boy Room is OFF");
            }
        }
    };
}
