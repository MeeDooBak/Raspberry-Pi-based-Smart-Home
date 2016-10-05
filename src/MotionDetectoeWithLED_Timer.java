
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import java.util.*;

public class MotionDetectoeWithLED_Timer {

    private static GpioPinDigitalInput pin12;
    private static GpioPinDigitalOutput pin4;

    private static int count = 0;
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
                stop = count + 20;
                System.out.println("-------------- " + count);
                System.out.println("-------------- " + stop);
                System.out.println("WARNING Motion detected!!!");
            }
        }
    };

    private static TimerTask Task = new TimerTask() {
        @Override
        public void run() {
            System.out.println(count);
            count++;
            if (stop == count) {
                pin4.low();
                System.out.println("LED Stop");
            }
        }
    };
}
