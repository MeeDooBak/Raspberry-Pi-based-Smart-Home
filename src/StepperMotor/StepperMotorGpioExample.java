package StepperMotor;

import com.pi4j.component.motor.impl.*;
import com.pi4j.io.gpio.*;
import java.io.*;

public class StepperMotorGpioExample {

    private static BufferedReader client;
    private static GpioStepperMotorComponent motor;
    private static boolean Stop;
    private static int value; // 0 to 6114

    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("<--Pi4J--> GPIO Stepper Motor Example ... started.");

        client = new BufferedReader(new InputStreamReader((Runtime.getRuntime().exec(new String[]{"/usr/bin/irw"})).getInputStream()));

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput[] pins = {
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, PinState.LOW)
        };
        gpio.setShutdownOptions(true, PinState.LOW, pins);
        motor = new GpioStepperMotorComponent(pins);

        motor.setStepInterval(2);
        motor.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
        motor.setStepsPerRevolution(2038);

        Thread lircThread = new Thread() {
            @Override
            public void run() {
                try {
                    scanEvents();
                } catch (IOException e) {
                }
            }
        };
        lircThread.start();
    }

    private static void scanEvents() throws IOException {
        String Line;
        String Previous = "";
        value = -1;

        while ((Line = client.readLine()) != null) {
            if (Line.split(" ")[2].equals(Previous)) {
                Previous = "";
            } else {
                Previous = Line.split(" ")[2];

                if (Line.split(" ")[2].equals("KEY_UP")) {
                    System.out.println(Line.split(" ")[2]);
                    new Thread(open).start();

                } else if (Line.split(" ")[2].equals("KEY_5")) {
                    System.out.println(Line.split(" ")[2]);
                    new Thread(close).start();

                } else if (Line.split(" ")[2].equals("KEY_8")) {
                    System.out.println(Line.split(" ")[2]);
                    Stop = true;
                    System.out.println(value);
                }
            }
        }
        client.close();
    }

    private static final Runnable open = new Runnable() {
        @Override
        public void run() {
            for (int i = value; i > -1; i--) {
                motor.step(1);
                value = i;
                if (Stop) {
                    Stop = false;
                    break;
                }
            }
        }
    };

    private static final Runnable close = new Runnable() {
        @Override
        public void run() {
            for (int i = value; i < 4410; i++) {
                motor.step(-1);
                value = i;
                if (Stop) {
                    Stop = false;
                    break;
                }
            }
        }
    };
}
