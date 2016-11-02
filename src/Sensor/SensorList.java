package Sensor;

import java.sql.*;

public class SensorList {

    private final int SensorID;
    private final int RoomID;
    private final String SensorName;
    private final int GateNum1;
    private final int GateNum2;
    private final SensorThread SensorThread;
    private final UltrasonicThread UltrasonicThread;

    public SensorList(int SensorID, int RoomID, String SensorName, boolean SensorState, int GateNum1, int GateNum2, int SensorValue, Connection DB) {

        this.SensorID = SensorID;
        this.RoomID = RoomID;
        this.SensorName = SensorName;
        this.GateNum1 = GateNum1;
        this.GateNum2 = GateNum2;

        if (GateNum2 > -1) {
            UltrasonicThread = new UltrasonicThread(SensorID, SensorState, GateNum1, GateNum2, SensorValue, DB);
            SensorThread = null;
            UltrasonicThread.start();
        } else {
            SensorThread = new SensorThread(SensorID, SensorState, GateNum1, SensorValue, DB);
            UltrasonicThread = null;
            SensorThread.start();
        }
    }

    public SensorThread getSensorThread() {
        return SensorThread;
    }

    public UltrasonicThread getUltrasonicThread() {
        return UltrasonicThread;
    }

    public int getSensorID() {
        return SensorID;
    }

    public int getRoomID() {
        return RoomID;
    }

    public String getSensorName() {
        return SensorName;
    }

    public int getGateNum1() {
        return GateNum1;
    }

    public int getGateNum2() {
        return GateNum2;
    }
}
