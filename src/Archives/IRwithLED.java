package Archives;


import com.pi4j.io.gpio.*;
import java.io.*;

public class IRwithLED {

    private final BufferedReader client;
    private final GpioPinDigitalOutput[] GPIOPinsOut;

    public IRwithLED() throws IOException {
        client = new BufferedReader(new InputStreamReader((Runtime.getRuntime().exec(new String[]{"/usr/bin/irw"})).getInputStream()));

        GpioController gpio = GpioFactory.getInstance();

        GPIOPinsOut = new GpioPinDigitalOutput[3];

        GPIOPinsOut[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyLED1");
        GPIOPinsOut[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED2");
        GPIOPinsOut[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED3");

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

    private void scanEvents() throws IOException {
        String Line;
        String Previous = "";
        while ((Line = client.readLine()) != null) {
            if (Line.split(" ")[2].equals(Previous)) {
                Previous = "";
            } else {
                System.out.println(Line.split(" ")[2]);
                Previous = Line.split(" ")[2];

                if (Line.split(" ")[2].equals("KEY_1")) {
                    flipLED(0);
                } else if (Line.split(" ")[2].equals("KEY_2")) {
                    flipLED(1);
                } else if (Line.split(" ")[2].equals("KEY_3")) {
                    flipLED(2);
                }
            }
        }
        client.close();
    }

    public void flipLED(int index) {
        if (GPIOPinsOut[index].isHigh()) {
            GPIOPinsOut[index].low();
        } else {
            GPIOPinsOut[index].high();
        }
    }

    public static void main(String[] args) throws IOException {
        IRwithLED IRwithLED = new IRwithLED();
    }
}
