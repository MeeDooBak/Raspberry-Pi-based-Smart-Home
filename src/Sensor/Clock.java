package Sensor;

import java.sql.*;
import java.text.*;
import java.util.logging.*;

public class Clock {

    private boolean SensorState;
    private int SensorValue;
    private Time ActionTime;

    private final int SensorID;
    private final Connection DB;

    public Clock(int SensorID, boolean SensorState, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;
    }

    public void setTime(Time ActionTime) {
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

            try (PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? and SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, SensorState);
                ps.setInt(2, SensorValue);
                ps.setInt(3, SensorID);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Clock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
