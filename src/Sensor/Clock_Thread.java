package Sensor;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.logging.*;

public class Clock_Thread {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private final Time ActionTime;

    public Clock_Thread(int SensorID, boolean SensorState, int SensorValue, Time ActionTime, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;
        this.ActionTime = ActionTime;
    }

    public boolean getSensorState() {
        Start();
        return SensorState;
    }

    public int getSensorValue() {
        Start();
        return SensorValue;
    }

    public void Start() {
        try {
            if (new SimpleDateFormat("HH:mm").format(ActionTime).equals(new SimpleDateFormat("HH:mm").format(new java.util.Date()))) {
                SensorState = true;
                SensorValue = 1;
            } else {
                SensorState = false;
                SensorValue = 0;
            }

            PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? and SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ps.setBoolean(1, SensorState);
            ps.setInt(2, SensorValue);
            ps.setInt(3, SensorID);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException ex) {
            Logger.getLogger(MotionSensor_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
