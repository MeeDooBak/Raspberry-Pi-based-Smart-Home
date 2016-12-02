package Testing;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ListenMultipleGpioExample {

    public static void main(String args[]) throws InterruptedException {

        System.out.println("<--Pi4J--> GPIO Listen Example ... started.");

        final GpioController gpio = GpioFactory.getInstance();

        GpioPinListenerDigital listener = new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }
        };

        GpioPinDigitalInput[] pins = {
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP),
            gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_UP)};
        gpio.addListener(listener, pins);
        System.out.println(" ... complete the GPIO circuit and see the listener feedback here in the console.");

        while (true) {
            Thread.sleep(500);
        }
    }

}
