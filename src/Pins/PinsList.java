package Pins;

import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.io.gpio.GpioController;

public class PinsList {

    private final int PinID;
    private final String Type;
    private final String PI4Jnumber;
    private final GpioController GPIO;
    private final MCP23017GpioProvider MCP23017;

    public PinsList(int PinID, String Type, String PI4Jnumber, GpioController GPIO, MCP23017GpioProvider MCP23017) {
        this.PinID = PinID;
        this.Type = Type;
        this.PI4Jnumber = PI4Jnumber;
        this.MCP23017 = MCP23017;
        this.GPIO = GPIO;
    }

    public int getPinID() {
        return PinID;
    }

    public String getType() {
        return Type;
    }

    public String getPI4Jnumber() {
        return PI4Jnumber;
    }

    public MCP23017GpioProvider getMCP23017() {
        return MCP23017;
    }

    public GpioController getGPIO() {
        return GPIO;
    }
}
