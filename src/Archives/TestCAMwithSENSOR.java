package Archives;


import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.v4l4j.*;
import com.pi4j.io.gpio.*;
import java.io.File;
import java.io.*;
import java.util.logging.*;
import javax.imageio.*;
import javax.swing.*;

public class TestCAMwithSENSOR {

    public static void main(String[] args) {
        Webcam.setDriver(new V4l4jDriver());
        Webcam CAM = Webcam.getDefault();

        GpioController GPIO = GpioFactory.getInstance();
        GpioPinDigitalInput PIN17 = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_00, "MySensor");
        GpioPinDigitalOutput PIN4 = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLed");

        JFrame frame = new JFrame("Hello From RasPi Camera Module");
        frame.add(new WebcamPanel(CAM));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (true) {
            try {
                if (PIN17.isHigh()) {
                    System.out.println("--> GPIO 17 state is : ON");
                    System.out.println("--> GPIO 4 state should be: ON");
                    PIN4.high();

                    File file = new File(String.format("capture-%d.jpg", System.currentTimeMillis()));
                    ImageIO.write(CAM.getImage(), "JPG", file);
                    System.out.println("Capture Complete :" + file.getAbsolutePath());

                    Thread.sleep(5000);

                } else {
                    System.out.println("--> GPIO 17 state is : OFF");
                    System.out.println("--> GPIO 4 state should be: OFF");
                    PIN4.low();
                }
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                Logger.getLogger(TestCAMwithSENSOR.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}
