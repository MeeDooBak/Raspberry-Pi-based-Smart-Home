package Sensor;

import Logger.*;
import Pins.*;
import java.sql.*;
import java.util.*;

public class Sensor {

    private final Connection DB;
    private final ArrayList<SensorInterface> SensorList;
    private final Pins Pins;

    // Get Infromation from Main Class 
    public Sensor(Connection DB, Pins Pins) {
        this.DB = DB;
        this.SensorList = new ArrayList();
        this.Pins = Pins;
    }

    // Start Get Sensor Information From The Database
    public void Start() {
        // Start Get Sensor Information From The Database
        try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select * from sensor")) {

            Result.beforeFirst();
            // While Loop For All Row in DataBase
            while (Result.next()) {

                // Get Sensor ID
                int SensorID = Result.getInt("SensorID");
                // Get Sensor Room ID
                int RoomID = Result.getInt("RoomID");
                // Get Sensor Type
                int SensorTypeID = Result.getInt("SensorTypeID");
                // Get Sensor State
                boolean SenesorState = Result.getBoolean("SenesorState");
                // Get Sensor State Vlaue
                int SensorValue = Result.getInt("SensorValue");
                // Get Sensor Gate Number
                int GateNum = Result.getInt("GateNum");

                // Get Sensor Name Form " Sensor Type " 
                String SensorName;
                try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result2 = Statement2.executeQuery("select * from sensor_type where SensorTypeID = " + SensorTypeID)) {
                    Result2.next();
                    // Get Sensor Name
                    SensorName = Result2.getString("SensorName");
                }

                // Chek if the Sensor Sensor Name To Create a Class
                switch (SensorName) {
                    case "Clock":
                        // Create and add To the ArrayList the Clock Class
                        SensorList.add(new Clock(SensorID, RoomID, SensorName));
                        break;
                    case "Motion Sensor":
                        // Create and add To the ArrayList the Sensor Class
                        SensorList.add(new MotionSensor(SensorID, RoomID, SensorName, SenesorState, Pins.Get(GateNum), SensorValue, DB));
                        break;
                    case "Smoke Detector":
                        // Create and add To the ArrayList the Sensor Class
                        SensorList.add(new SmokeSensor(SensorID, RoomID, SensorName, SenesorState, Pins.Get(GateNum), SensorValue, DB));
                        break;
                    case "Temperature Sensor":
                        // Create and add To the ArrayList the Sensor Class
                        SensorList.add(new TemperatureSensor(SensorID, RoomID, SensorName, Pins.Get(GateNum), SensorValue, DB));
                        break;
                    case "Light Sensor":
                        // Create and add To the ArrayList the Sensor Class
                        SensorList.add(new LightSensor(SensorID, RoomID, SensorName, SenesorState, Pins.Get(GateNum), SensorValue, DB));
                        break;
                    case "Ultrasonic Sensor":
                        int GateNum1;
                        int GateNum2;
                        int MaxValue;
                        int MinValue;
                        try (Statement Statement3 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet Result3 = Statement3.executeQuery("select * from sensor_multi_gate where SensorID = " + SensorID)) {
                            Result3.next();

                            // Get Ultrasonic Sensor Gate Number 1
                            GateNum1 = Result3.getInt("GateNum1");
                            // Get Ultrasonic Sensor Gate Number 2
                            GateNum2 = Result3.getInt("GateNum2");
                            // Get Ultrasonic Sensor Max Value
                            MaxValue = Result3.getInt("MaxValue");
                            // Get Ultrasonic Sensor Min Value
                            MinValue = Result3.getInt("MinValue");
                        }

                        // Create and add To the ArrayList the Sensor Class
                        SensorList.add(new Ultrasonic(SensorID, RoomID, SensorName, Pins.Get(GateNum1), Pins.Get(GateNum2), SensorValue, MaxValue, MinValue, DB));
                        break;
                    case "Infrared Sensor":
                        // Create and add To the ArrayList the Sensor Class
                        SensorList.add(new InfraredSensor(SensorID, RoomID, SensorName, SenesorState, Pins.Get(GateNum), SensorValue, DB));
                        break;
                    default:
                        break;
                }

                // just To Print the Result
                FileLogger.AddInfo("Add Sensor " + SensorID + ", with Name : " + SensorName);
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Sensor Class, Error In DataBase\n" + ex);
        }
    }

    // Search and return ArrayList index if the specific Sensor exists by ID
    public int indexof(int SensorID) {
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorID() == SensorID) {
                return i;
            }
        }
        return -1;
    }

    // Search and return Sensor Class if the specific Sensor exists by ID
    public SensorInterface Get(int SensorID) {
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorID() == SensorID) {
                return SensorList.get(i);
            }
        }
        return null;
    }

    // Search and return Device Class if the specific Sensor exists by Name
    public SensorInterface Get(String SensorName) {
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorName().equals(SensorName)) {
                return SensorList.get(i);
            }
        }
        return null;
    }

    // Search and return Ultrasonic Sensor Class
    public ArrayList<SensorInterface> GetUltrasonic() {
        ArrayList<SensorInterface> List = new ArrayList();
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorName().equals("Ultrasonic Sensor")) {
                List.add(SensorList.get(i));
            }
        }
        return List;
    }
}
