package Sensor;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;

public class UltrasonicThread extends Thread {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private final GpioPinDigitalInput EchoPin;
    private final GpioPinDigitalOutput TrigPin;

    public UltrasonicThread(int SensorID, boolean SensorState, PinsList GateNum1, PinsList GateNum2, int SensorValue, Connection DB) {

        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

        this.EchoPin = GateNum1.getInputPIN();
        this.TrigPin = GateNum2.getOutputPIN();
        this.TrigPin.low();
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
                this.TrigPin.high();
                Thread.sleep(0, 10000);
                this.TrigPin.low();

                int countdown = 2100;
                while (this.EchoPin.isLow() && countdown > 0) {
                    countdown--;
                }

                countdown = 2100;
                long start = System.nanoTime();
                while (this.EchoPin.isHigh() && countdown > 0) {
                    countdown--;
                }
                SensorValue = (int) Math.ceil(Math.ceil((System.nanoTime() - start) / 1000.0) * 340.29f / (2 * 10000));
                SensorState = true;

                PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? and SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, SensorState);
                ps.setInt(2, SensorValue);
                ps.setInt(3, SensorID);
                ps.executeUpdate();
                ps.close();

                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(SensorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
