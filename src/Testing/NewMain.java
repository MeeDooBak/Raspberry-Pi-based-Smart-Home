package Testing;

import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.*;

public class NewMain {

    public static void main(String args[]) throws InterruptedException, IOException {
        System.out.println("<--Pi4J--> MCP23017 GPIO Example ... started.");

        final GpioController gpio = GpioFactory.getInstance();

        MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, Integer.decode("0x24"));
        GpioPinDigitalInput Input1 = gpio.provisionDigitalInputPin(Provider, MCP23017Pin.GPIO_A0, PinPullResistance.PULL_UP);
        GpioPinDigitalInput Input2 = gpio.provisionDigitalInputPin(Provider, MCP23017Pin.GPIO_A1, PinPullResistance.PULL_UP);
        GpioPinDigitalInput Input3 = gpio.provisionDigitalInputPin(Provider, MCP23017Pin.GPIO_A2, PinPullResistance.PULL_UP);
        GpioPinDigitalInput Input4 = gpio.provisionDigitalInputPin(Provider, MCP23017Pin.GPIO_A3, PinPullResistance.PULL_UP);

        MCP23017GpioProvider Provider1 = new MCP23017GpioProvider(I2CBus.BUS_1, Integer.decode("0x21"));
        GpioPinDigitalInput Input5 = gpio.provisionDigitalInputPin(Provider1, MCP23017Pin.GPIO_A0, PinPullResistance.PULL_UP);
        GpioPinDigitalInput Input6 = gpio.provisionDigitalInputPin(Provider1, MCP23017Pin.GPIO_A1, PinPullResistance.PULL_UP);
        GpioPinDigitalInput Input7 = gpio.provisionDigitalInputPin(Provider1, MCP23017Pin.GPIO_A2, PinPullResistance.PULL_UP);
        GpioPinDigitalInput Input8 = gpio.provisionDigitalInputPin(Provider1, MCP23017Pin.GPIO_A3, PinPullResistance.PULL_UP);

        while (true) {
            if (Input1.isHigh() && Input2.isHigh() && Input3.isHigh() && Input4.isHigh()) {
                System.out.println("--> There is a Thieves");
            }

            if (Input5.isLow()) {
                System.out.println("--> There is Light in Parent Room");
            }

            if (Input6.isLow()) {
                System.out.println("--> There is Light in Living Room");
            }

            if (Input7.isLow()) {
                System.out.println("--> There is Light in Girl Room");
            }

            if (Input8.isLow()) {
                System.out.println("--> There is Light in Boy Room");
            }
            Thread.sleep(1000);
        }
    }
}
