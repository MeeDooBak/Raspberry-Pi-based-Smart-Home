package Sensor;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;

public class SensorThread extends Thread {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private final GpioPinDigitalInput PIN;

    public SensorThread(int SensorID, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

        PIN = GateNum.getInputPIN();
    }

    public boolean getSensorState() {
        return SensorState;
    }

    public int getSensorValue() {
        return SensorValue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (PIN.isHigh()) {
                    SensorState = true;
                    SensorValue = 1;

                    Thread.sleep(5000);
                } else {
                    SensorState = false;
                    SensorValue = 0;
                }

                PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? and SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, SensorState);
                ps.setInt(2, SensorValue);
                ps.setInt(3, SensorID);
                ps.executeUpdate();

                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(SensorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
