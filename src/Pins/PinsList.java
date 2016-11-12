package Pins;

import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.*;
import java.util.logging.*;

public class PinsList {

    private final int PinID;

    private int PinIndex = 0;
    private GpioPinDigitalInput PIN_IN;
    private GpioPinDigitalOutput PIN_OUT;
    private String PIN_OUTRelay;

    public PinsList(int PinID, boolean isPinInput, String Type, String PinNumber, String PI4Jnumber, String MCP23017, String DeviceName) {
        this.PinID = PinID;

        GpioController GPIO = GpioFactory.getInstance();

        if (Type.equals("GPIO")) {
            if (isPinInput) {
                PIN_IN = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(PI4Jnumber)));
            } else {
                PIN_OUT = GPIO.provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(PI4Jnumber)));
            }
        } else if (Type.equals("I2C")) {
            try {
                Pin[] Pin = MCP23017Pin.ALL;
                for (int i = 0; i < Pin.length; i++) {
                    if (Pin[i].getName().contains(PI4Jnumber)) {
                        PinIndex = i;
                        break;
                    }
                }
                MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, Integer.parseInt(MCP23017));

                if (isPinInput) {
                    PIN_IN = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL[PinIndex], PinPullResistance.PULL_UP);
                } else {
                    PIN_OUT = GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL[PinIndex], PinState.LOW);
                }
            } catch (IOException ex) {
                Logger.getLogger(PinsList.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (Type.equals("Relay")) {
            if (!isPinInput) {
                PIN_OUTRelay = PI4Jnumber;
            }
        }
    }

    public GpioPinDigitalInput getInputPIN() {
        return PIN_IN;
    }

    public GpioPinDigitalOutput getOutputPIN() {
        return PIN_OUT;
    }

    public String getOutputPINRelay() {
        return PIN_OUTRelay;
    }

    public int getPinID() {
        return PinID;
    }
}
