package Testing;

import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.i2c.I2CBus;

public class ADC121C_MQ4 {

    public static void main(String args[]) throws Exception {

        final GpioController gpio = GpioFactory.getInstance();
        final MCP23017GpioProvider Provider20 = new MCP23017GpioProvider(I2CBus.BUS_1, 0x21);

        GpioPinDigitalInput myInput = gpio.provisionDigitalInputPin(Provider20, MCP23017Pin.GPIO_A0, "MyOutput-A0", PinPullResistance.PULL_UP);

        int count = 0;
        while (true) {
            if (myInput.isLow()) {
                System.out.println("Found Gas " + (count++));
            }
        }
    }
}
