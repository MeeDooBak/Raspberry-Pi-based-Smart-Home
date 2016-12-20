package Relay;

public class RelayQueue {

    private final String ID;
    private final String Value;

    // Set Relay Information
    public RelayQueue(String ID, String Value) {
        this.ID = ID;
        this.Value = Value;
    }

    // Get Relay ID
    public String getID() {
        return ID;
    }

    // Get Relay Value
    public String getValue() {
        return Value;
    }
}
