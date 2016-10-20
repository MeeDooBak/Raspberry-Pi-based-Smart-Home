package StepperMotor;

import com.pi4j.component.motor.impl.*;
import com.pi4j.io.gpio.*;
import java.io.*;

public class StepperMotorGpioExample {

    private static BufferedReader client;
    private static GpioStepperMotorComponent motor;
    private static boolean isOpen;
    private static boolean isColse;

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
        isOpen = true;

        while ((Line = client.readLine()) != null) {
            if (Line.split(" ")[2].equals(Previous)) {
                Previous = "";
            } else {
                Previous = Line.split(" ")[2];
                if (Line.split(" ")[2].equals("KEY_UP")) {
                    System.out.println(Line.split(" ")[2]);
                    if (!isOpen) {
                        new Thread(open).start();
                        isOpen = true;
                        isColse = false;
                    }
                } else if (Line.split(" ")[2].equals("KEY_5")) {
                    System.out.println(Line.split(" ")[2]);
                    if (!isColse) {
                        new Thread(close).start();
                        isOpen = false;
                        isColse = true;
                    }
                }
            }
        }
        client.close();
    }

    private static final Runnable open = new Runnable() {
        @Override
        public void run() {
            motor.step(-6114);
        }
    };

    private static final Runnable close = new Runnable() {
        @Override
        public void run() {
            motor.step(6114);
        }
    };
}
