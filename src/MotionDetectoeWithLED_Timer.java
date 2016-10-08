
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import java.util.*;

public class MotionDetectoeWithLED_Timer {

    private static GpioPinDigitalInput pin12;
    private static GpioPinDigitalOutput pin4;

    private static int counter = 0;
    private static int stop = 0;

    public static void main(String[] args) {
        GpioController gpio = GpioFactory.getInstance();
        pin12 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, PinPullResistance.PULL_UP);
        pin4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED4");

        pin12.setShutdownOptions(true);

        Timer Time = new Timer();
        Time.scheduleAtFixedRate(Task, 1000, 1000);
        pin12.addListener(Sensor);
    }

    private static GpioPinListenerDigital Sensor = new GpioPinListenerDigital() {

        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            if (event.getState().isHigh()) {
                pin4.high();
                stop = 20;
                counter = 0;
                System.out.println("-------------- " + stop);
                System.out.println("WARNING Motion detected!!!");
            }
        }
    };

    private static TimerTask Task = new TimerTask() {
        @Override
        public void run() {
            System.out.println(counter);
            counter++;
            if (stop == counter) {
                pin4.low();
                System.out.println("LED Stop");
            }
        }
    };
}
