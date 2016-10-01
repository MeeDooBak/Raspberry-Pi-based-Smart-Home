
import com.pi4j.io.gpio.*;
import java.util.logging.*;

public class CheckIN_OUT {

    public static void main(String[] args) {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        GpioController gpio = GpioFactory.getInstance();

        GpioPinDigitalInput pin17 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "MyLED17");
        GpioPinDigitalOutput pin4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED4");

        while (true) {
            try {
                if (pin17.isHigh()) {
                    System.out.println("--> GPIO 17 state is : ON");
                    System.out.println("--> GPIO 4 state should be: ON");
                    pin4.high();

                } else {
                    System.out.println("--> GPIO 17 state is : OFF");
                    System.out.println("--> GPIO 4 state should be: OFF");
                    pin4.low();

                }
                Thread.sleep(1000);

            } catch (InterruptedException ex) {
                Logger.getLogger(CheckIN_OUT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
