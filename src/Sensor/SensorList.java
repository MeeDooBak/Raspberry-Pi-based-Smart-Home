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
    private Clock Clock;

    public SensorList(int SensorID, int RoomID, String SensorName, boolean SensorState, PinsList GateNum1, PinsList GateNum2, int SensorValue, Connection DB) {

        this.SensorID = SensorID;
        this.RoomID = RoomID;
        this.SensorName = SensorName;

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
            case "Ultrasonic":
                Ultrasonic = new Ultrasonic(SensorID, GateNum1, GateNum2, SensorValue, DB);
                break;
            case "Clock":
                Clock = new Clock(SensorID, SensorState, SensorValue, DB);
                break;
            case "Infrared Sensor":
                InfraredSensor = new InfraredSensor(SensorID, SensorState, GateNum1, SensorValue, DB);
                break;
            default:
                break;
        }
    }

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
            case "Ultrasonic":
                return (T) Ultrasonic;
            case "Clock":
                return (T) Clock;
            case "Infrared Sensor":
                return (T) InfraredSensor;
            default:
                return null;
        }
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
}
