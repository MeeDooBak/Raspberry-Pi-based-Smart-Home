package Pins;

public class PinsList {

    private final int PinID;
    private final String Type;
    private final String PI4Jnumber;
    private final String MCP23017;

    public PinsList(int PinID, String Type, String PI4Jnumber, String MCP23017) {
        this.PinID = PinID;
        this.Type = Type;
        this.PI4Jnumber = PI4Jnumber;
        this.MCP23017 = MCP23017;
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

    public String getMCP23017() {
        return MCP23017;
    }
}
