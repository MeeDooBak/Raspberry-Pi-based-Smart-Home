package Sensor;

import Pins.PinsList;
import java.sql.*;

public class SensorList {

    private final int SensorID;
    private final int RoomID;
    private final String SensorName;
    private final PinsList GateNum1;
    private final PinsList GateNum2;
    private final Connection DB;

    private final MotionSensor_Thread MotionSensor;
    private final Ultrasonic_Thread Ultrasonic;
    private final SmokeDetector_Thread SmokeDetector;
    private final TemperatureSensor_Thread TemperatureSensor;
    private final LightSensor_Thread LightSensor;
    private Clock_Thread Clock;

    public SensorList(int SensorID, int RoomID, String SensorName, boolean SensorState, PinsList GateNum1, PinsList GateNum2, int SensorValue, Connection DB) {

        this.SensorID = SensorID;
        this.RoomID = RoomID;
        this.SensorName = SensorName;
        this.GateNum1 = GateNum1;
        this.GateNum2 = GateNum2;
        this.DB = DB;

        if (SensorName.equals("Motion Sensor")) {
            MotionSensor = new MotionSensor_Thread(SensorID, SensorState, GateNum2, SensorValue, DB);

            Ultrasonic = null;
            SmokeDetector = null;
            TemperatureSensor = null;
            LightSensor = null;
            Clock = null;

        } else if (SensorName.equals("Smoke Detector")) {
            SmokeDetector = new SmokeDetector_Thread(SensorID, SensorState, GateNum2, SensorValue, DB);

            MotionSensor = null;
            Ultrasonic = null;
            TemperatureSensor = null;
            LightSensor = null;
            Clock = null;

        } else if (SensorName.equals("Temperature Sensor")) {
            TemperatureSensor = new TemperatureSensor_Thread(SensorID, SensorState, GateNum2, SensorValue, DB);

            MotionSensor = null;
            Ultrasonic = null;
            SmokeDetector = null;
            LightSensor = null;
            Clock = null;

        } else if (SensorName.equals("Light Sensor")) {
            LightSensor = new LightSensor_Thread(SensorID, SensorState, GateNum2, SensorValue, DB);

            MotionSensor = null;
            Ultrasonic = null;
            SmokeDetector = null;
            TemperatureSensor = null;
            Clock = null;

        } else if (SensorName.equals("Ultrasonic")) {
            Ultrasonic = new Ultrasonic_Thread(SensorID, SensorState, GateNum1, GateNum2, SensorValue, DB);

            MotionSensor = null;
            SmokeDetector = null;
            TemperatureSensor = null;
            LightSensor = null;
            Clock = null;

        } else if (SensorName.equals("Clock")) {
            Clock = null;

            MotionSensor = null;
            Ultrasonic = null;
            SmokeDetector = null;
            TemperatureSensor = null;
            LightSensor = null;
            
        } else {
            Clock = null;
            MotionSensor = null;
            Ultrasonic = null;
            SmokeDetector = null;
            TemperatureSensor = null;
            LightSensor = null;
        }
    }

    public MotionSensor_Thread getMotionSensor() {
        return MotionSensor;
    }

    public Ultrasonic_Thread getUltrasonic() {
        return Ultrasonic;
    }

    public SmokeDetector_Thread getSmokeDetector() {
        return SmokeDetector;
    }

    public TemperatureSensor_Thread getTemperatureSensor() {
        return TemperatureSensor;
    }

    public LightSensor_Thread getLightSensor() {
        return LightSensor;
    }

    public Clock_Thread getClock() {
        return Clock;
    }

    public void setClock(Time ActionTime) {
        Clock = new Clock_Thread(SensorID, true, SensorID, ActionTime, DB);
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

    public PinsList getGateNum1() {
        return GateNum1;
    }

    public PinsList getGateNum2() {
        return GateNum2;
    }
}
