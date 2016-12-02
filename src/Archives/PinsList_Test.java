package Archives;

import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.*;
import java.sql.Connection;
import java.util.logging.*;

public class PinsList_Test {

    private int PinIndex = 0;
    private GpioPinDigitalInput PIN_IN;
    private GpioPinDigitalOutput PIN_OUT;

    public PinsList_Test(int PinID, int isPinInput, String Type, String PI4Jnumber, String MCP23017, String Color, Connection DB) {
        GpioController GPIO = GpioFactory.getInstance();
        if (Type.equals("GPIO")) {
            if (!Color.equals("Blue") && !PI4Jnumber.equals("07") && !Color.equals("Light Purple") && !Color.equals("Dark Purple")) {
                if (isPinInput == 1) {
                    PIN_IN = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(PI4Jnumber)), PinPullResistance.PULL_UP);
                } else {
                    PIN_OUT = GPIO.provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(PI4Jnumber)));
                }
            }
        } else if (Type.equals("I2C")) {
            try {
                Pin[] Pins = MCP23017Pin.ALL;
                for (int i = 0; i < Pins.length; i++) {
                    if (Pins[i].getName().contains(PI4Jnumber)) {
                        PinIndex = i;
                        break;
                    }
                }
                MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, Integer.decode(MCP23017));
                if (isPinInput == 1) {
                    PIN_IN = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL[PinIndex], PinPullResistance.PULL_UP);
                } else {
                    if (MCP23017.equals("0x25") && PI4Jnumber.contains("A")) {
                        PIN_OUT = GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL[PinIndex], PinState.HIGH);
                    } else {
                        PIN_OUT = GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL[PinIndex], PinState.LOW);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(PinsList_Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
