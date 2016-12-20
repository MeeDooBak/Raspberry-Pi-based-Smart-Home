package Pins;

import com.pi4j.io.gpio.*;
import com.pi4j.gpio.extension.mcp.*;

public class PinsList {

    private final int PinID;
    private final String Type;
    private final String PI4Jnumber;
    private final GpioController GPIO;
    private final MCP23017GpioProvider MCP23017;

    // Set Pin Information from Database
    public PinsList(int PinID, String Type, String PI4Jnumber, GpioController GPIO, MCP23017GpioProvider MCP23017) {
        this.PinID = PinID;
        this.Type = Type;
        this.PI4Jnumber = PI4Jnumber;
        this.MCP23017 = MCP23017;
        this.GPIO = GPIO;
    }

    // Set Pin ID
    public int getPinID() {
        return PinID;
    }

    // Set Pin Type
    public String getType() {
        return Type;
    }

    // Set Pin PI4J Number
    public String getPI4Jnumber() {
        return PI4Jnumber;
    }

    // Set Pin I2C Type
    public MCP23017GpioProvider getMCP23017() {
        return MCP23017;
    }

    // Get GPIO controller
    public GpioController getGPIO() {
        return GPIO;
    }
}
