package SmartHome_Camera_WIN.Pins;

public class PinsList {

    private final int PinID;
    private final String Type;
    private final String PI4Jnumber;

    // Set Pin Information from Database
    public PinsList(int PinID, String Type, String PI4Jnumber) {
        this.PinID = PinID;
        this.Type = Type;
        this.PI4Jnumber = PI4Jnumber;
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
}
