package Relay;

public class RelayQueue {

    private final String ID;
    private final String Value;

    public RelayQueue(String ID, String Value) {
        this.ID = ID;
        this.Value = Value;
    }

    public String getID() {
        return ID;
    }

    public String getValue() {
        return Value;
    }
}
