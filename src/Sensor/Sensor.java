package Sensor;

import Pins.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Sensor {
    
    private final Connection DB;
    private final ArrayList<SensorList> SensorList;
    private final Pins Pins;
    
    public Sensor(Connection DB, ArrayList<SensorList> SensorList, Pins Pins) {
        this.DB = DB;
        this.SensorList = SensorList;
        this.Pins = Pins;
    }
    
    public int indexof(int SensorID) {
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorID() == SensorID) {
                return i;
            }
        }
        return -1;
    }
    
    public SensorList Get(int SensorID) {
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorID() == SensorID) {
                return SensorList.get(i);
            }
        }
        return null;
    }
    
    public SensorList Get(String SensorName) {
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorName().equals(SensorName)) {
                return SensorList.get(i);
            }
        }
        return null;
    }
    
    public void Start() {
        try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select * from sensor")) {
            
            Result.beforeFirst();
            while (Result.next()) {
                
                int SensorID = Result.getInt("SensorID");
                int RoomID = Result.getInt("RoomID");
                int SensorTypeID = Result.getInt("SensorTypeID");
                boolean SenesorState = Result.getBoolean("SenesorState");
                int SensorValue = Result.getInt("SensorValue");
                int GateNum = Result.getInt("GateNum");
                
                String SensorName;
                try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result2 = Statement2.executeQuery("select * from sensor_type where SensorTypeID = " + SensorTypeID)) {
                    Result2.next();
                    SensorName = Result2.getString("SensorName");
                }
                
                if (SensorName.equals("Ultrasonic")) {
                    
                    int GateNum1;
                    int GateNum2;
                    try (Statement Statement3 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            ResultSet Result3 = Statement3.executeQuery("select * from sensor_multi_gate where SensorID = " + SensorID)) {
                        Result3.next();
                        GateNum1 = Result3.getInt("GateNum1");
                        GateNum2 = Result3.getInt("GateNum2");
                    }
                    
                    SensorList.add(new SensorList(SensorID, RoomID, SensorName, SenesorState, Pins.Get(GateNum1), Pins.Get(GateNum2), SensorValue, DB));
                } else {
                    SensorList.add(new SensorList(SensorID, RoomID, SensorName, SenesorState, Pins.Get(GateNum), null, SensorValue, DB));
                }
                System.out.println("Add Sensor " + SensorID + ", with Name : " + SensorName);
            }
        } catch (SQLException ex) {
            System.out.println("Sensor Class, Error In DataBase");
            Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
