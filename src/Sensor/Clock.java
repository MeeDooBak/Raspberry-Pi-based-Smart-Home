package Sensor;

public class Clock implements SensorInterface {

    private final int SensorID;
    private final int RoomID;
    private final String SensorName;

    // Get Sensor Information from Database
    public Clock(int SensorID, int RoomID, String SensorName) {
        this.SensorID = SensorID;
        this.RoomID = RoomID;
        this.SensorName = SensorName;
    }

    // Get Sensor ID
    @Override
    public int getSensorID() {
        return SensorID;
    }

    // Get Sensor Room ID
    @Override
    public int getRoomID() {
        return RoomID;
    }

    // Get Sensor Name
    @Override
    public String getSensorName() {
        return SensorName;
    }

    @Override
    public boolean getSensorState() {
        return false;
    }

    @Override
    public int getSensorValue() {
        return 0;
    }

    @Override
    public int getMaxValue() {
        return 0;
    }

    @Override
    public int getMinValue() {
        return 0;
    }
}
