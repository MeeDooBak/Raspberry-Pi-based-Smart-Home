package Sensor;

import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;

public class SensorThread extends Thread {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
//    private final GpioController GPIO;
//    private final GpioPinDigitalInput PIN;

    public SensorThread(int SensorID, boolean SensorState, int GateNum, int SensorValue, Connection DB) {

        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

//        GPIO = GpioFactory.getInstance();
//        PIN = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(GateNum));
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
//                if (PIN.isHigh()) {
                if (true) {
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
