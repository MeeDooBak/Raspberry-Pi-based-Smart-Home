package Archives;


import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.*;

public class MCP23017GpioExample {

    public static void main(String args[]) throws InterruptedException, IOException {
        System.out.println("<--Pi4J--> MCP23017 GPIO Example ... started.");

        final GpioController gpio = GpioFactory.getInstance();
        final MCP23017GpioProvider Provider20 = new MCP23017GpioProvider(I2CBus.BUS_1, 0x20);
        final MCP23017GpioProvider Provider21 = new MCP23017GpioProvider(I2CBus.BUS_1, 0x21);
        final MCP23017GpioProvider Provider22 = new MCP23017GpioProvider(I2CBus.BUS_1, 0x24);
        final MCP23017GpioProvider Provider23 = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);

        GpioPinDigitalOutput myOutput = gpio.provisionDigitalOutputPin(Provider21, MCP23017Pin.GPIO_A0, "MyOutput-A0", PinState.LOW);
        GpioPinDigitalInput myInput = gpio.provisionDigitalInputPin(Provider20, MCP23017Pin.GPIO_A0, "MyInput-A0", PinPullResistance.PULL_UP);

        for (int i = 0; i < 10; i++) {
            myOutput.high();
            Thread.sleep(1000);
            myOutput.low();
            Thread.sleep(1000);
        }
    }
}
