
import com.pi4j.io.gpio.*;
import java.util.logging.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("<--Pi4J--> GPIO Control Example ... started.");

        GpioController GPIO = GpioFactory.getInstance();        
        
        GpioPinAnalogInput PIN17 = GPIO.provisionAnalogInputPin(RaspiPin.GPIO_00, "MyLED4");
        
        GpioPinDigitalOutput pin4 = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED4");

        while (true) {
            try {
                
                Double Value = GPIO.getValue(PIN17);
                System.out.println(Value);
                
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CheckIN_OUT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
