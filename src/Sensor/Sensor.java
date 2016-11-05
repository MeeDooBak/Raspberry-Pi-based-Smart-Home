package Sensor;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Sensor {

    private final Connection DB;
    private final ArrayList<SensorList> SensorList;

    public Sensor(Connection DB, ArrayList<SensorList> SensorList) {
        this.DB = DB;
        this.SensorList = SensorList;
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

    public void start() {
        try {
            Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet Result = Statement.executeQuery("select * from sensor");

            Result.beforeFirst();
            while (Result.next()) {

                int SensorID = Result.getInt("SensorID");
                int RoomID = Result.getInt("RoomID");
                int SensorTypeID = Result.getInt("SensorTypeID");
                boolean SenesorState = Result.getBoolean("SenesorState");
                int GateNum = Result.getInt("GateNum");
                int SensorValue = Result.getInt("SensorValue");

                Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result2 = Statement2.executeQuery("select * from sensor_type where SensorTypeID = " + SensorTypeID);
                Result2.next();
                String SensorName = Result2.getString("SensorName");
                Result2.close();
                Statement2.close();

                if (SensorName.equals("Ultrasonic")) {

                    Statement Statement3 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result3 = Statement3.executeQuery("select * from sensor_multi_gate where SensorID = " + SensorID);
                    Result3.next();
                    int GateNum1 = Result3.getInt("GateNum1");
                    int GateNum2 = Result3.getInt("GateNum2");
                    Result3.close();
                    Statement3.close();

                    SensorList.add(new SensorList(SensorID, RoomID, SensorName, SenesorState, GateNum1, GateNum2, SensorValue, DB));
                } else {
                    SensorList.add(new SensorList(SensorID, RoomID, SensorName, SenesorState, GateNum, -1, SensorValue, DB));
                }
                System.out.println("Add " + SensorID + " " + SensorName);
            }
            Result.close();
            Statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
