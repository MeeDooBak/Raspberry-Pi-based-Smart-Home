package Testing;

import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.*;

public class NewMain {

    public static void main(String args[]) throws InterruptedException, IOException {
        System.out.println("<--Pi4J--> MCP23017 GPIO Example ... started.");

        final GpioController gpio = GpioFactory.getInstance();

        MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, Integer.decode("0x21"));
        GpioPinDigitalInput Input1 = gpio.provisionDigitalInputPin(Provider, MCP23017Pin.GPIO_B1, PinPullResistance.PULL_UP);
        GpioPinDigitalInput Input2 = gpio.provisionDigitalInputPin(Provider, MCP23017Pin.GPIO_B2, PinPullResistance.PULL_UP);

        while (true) {
            if (Input1.isHigh()) {
                System.out.println("--> Input1 state should be: ON");
            } else {
                System.out.println("--> Input1 state should be: OFF");
            }
            if (Input2.isHigh()) {
                System.out.println("--> Input2 state should be: ON");
            } else {
                System.out.println("--> Input2 state should be: OFF");
            }
            Thread.sleep(1000);
        }
    }
}
