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

                ResultSet Result2 = Statement.executeQuery("select * from sensor_type where SensorTypeID = " + SensorTypeID);
                Result2.next();

                String SensorName = Result.getString("SensorName");

                SensorList.add(new SensorList(SensorID, RoomID, SensorName, SenesorState, GateNum, -1, SensorValue, DB));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int indexof(int SensorID) {
        for (int i = 0; i < SensorList.size(); i++) {
            if (SensorList.get(i).getSensorID() == SensorID) {
                return i;
            }
        }
        return -1;
    }
}
