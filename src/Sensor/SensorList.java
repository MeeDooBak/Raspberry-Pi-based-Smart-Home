package Sensor;

import Pins.*;
import java.sql.*;

public class SensorList {

    private final int SensorID;
    private final int RoomID;
    private final String SensorName;
    private MotionSensor MotionSensor;
    private Ultrasonic Ultrasonic;
    private SmokeSensor SmokeDetector;
    private TemperatureSensor TemperatureSensor;
    private LightSensor LightSensor;
    private InfraredSensor InfraredSensor;

    // Get Device Information from Database
    public SensorList(int SensorID, int RoomID, String SensorName, boolean SensorState, PinsList GateNum1, PinsList GateNum2, int SensorValue, int MaxValue, int MinValue, Connection DB) {

        this.SensorID = SensorID;
        this.RoomID = RoomID;
        this.SensorName = SensorName;

        // Create Sensor Class According to its kind
        switch (SensorName) {
            case "Motion Sensor":
                MotionSensor = new MotionSensor(SensorID, SensorState, GateNum1, SensorValue, DB);
                break;
            case "Smoke Detector":
                SmokeDetector = new SmokeSensor(SensorID, SensorState, GateNum1, SensorValue, DB);
                break;
            case "Temperature Sensor":
                TemperatureSensor = new TemperatureSensor(SensorID, GateNum1, SensorValue, DB);
                break;
            case "Light Sensor":
                LightSensor = new LightSensor(SensorID, SensorState, GateNum1, SensorValue, DB);
                break;
            case "Ultrasonic Sensor":
                Ultrasonic = new Ultrasonic(SensorID, GateNum1, GateNum2, SensorValue, MaxValue, MinValue, DB);
                break;
            case "Infrared Sensor":
                InfraredSensor = new InfraredSensor(SensorID, SensorState, GateNum1, SensorValue, DB);
                break;
            default:
                break;
        }
    }

    // Get Sensor Class According to its kind
    public final <T> T GetSensor() {
        switch (SensorName) {
            case "Motion Sensor":
                return (T) MotionSensor;
            case "Smoke Detector":
                return (T) SmokeDetector;
            case "Temperature Sensor":
                return (T) TemperatureSensor;
            case "Light Sensor":
                return (T) LightSensor;
            case "Ultrasonic Sensor":
                return (T) Ultrasonic;
            case "Infrared Sensor":
                return (T) InfraredSensor;
            default:
                return null;
        }
    }

    // Get Sensor ID
    public int getSensorID() {
        return SensorID;
    }

    // Get Sensor Room ID
    public int getRoomID() {
        return RoomID;
    }

    // Get Sensor Name
    public String getSensorName() {
        return SensorName;
    }
}
