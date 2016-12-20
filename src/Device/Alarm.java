package Device;

import Pins.*;
import Logger.*;
import java.sql.*;
import com.pi4j.io.gpio.*;
import com.pi4j.gpio.extension.mcp.*;

public class Alarm implements Runnable {

    private GpioPinDigitalOutput PIN;
    private final int DeviceID;
    private final Connection DB;
    private boolean Busy;
    private boolean DeviceState;
    private int AlarmDuration;
    private int AlarmInterval;
    private Thread AlarmThread;

    // Get Device Information from Database
    public Alarm(int DeviceID, PinsList GateNum, boolean DeviceState, boolean isStatusChanged, int AlarmDuration, int AlarmInterval, Connection DB) {
        this.DeviceID = DeviceID;
        this.DB = DB;
        this.Busy = false;
        this.AlarmThread = new Thread(this);

        // To Get Device Pin From Raspberry PI
        getPin(GateNum);

        // To Make the Change For the First Time
        ChangeState(DeviceState, AlarmDuration, AlarmInterval, isStatusChanged);
    }

    // Get Device Pin From Raspberry PI
    private void getPin(PinsList GateNum) {
        // Provision GPIO pin (# from Database) from MCP23017 as an output pin and turn OFF
        PIN = GateNum.getGPIO().provisionDigitalOutputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinState.HIGH);
    }

    // To Change Device State
    public final void ChangeState(boolean DeviceState, int AlarmDuration, int AlarmInterval, boolean isStatusChanged) {
        // To Check if the Current Device State is not equl The New Device State
        // and to check is it Change from DataBase
        // if it is equl just ignore it
        if (this.DeviceState != DeviceState && isStatusChanged) {
            // To tell Database that java change the Device State 
            try (PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps2.setBoolean(1, false);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();

                // To Check If the Thread is working 
                // If it is Working just Stop the Thread.
                // and Change Device State To OFF
                if (Busy) {
                    this.AlarmThread.stop();
                    this.PIN.high();
                }

                // just to Get the New Date and Start The Thread
                this.DeviceState = DeviceState;
                this.AlarmDuration = AlarmDuration;
                this.AlarmInterval = AlarmInterval;
                this.AlarmThread = new Thread(this);
                this.AlarmThread.start();

            } catch (SQLException ex) {
                // This Catch For DataBase Error
                FileLogger.AddWarning("Alarm " + DeviceID + ", Error In DataBase\n" + ex);
            }
        }
    }

    // Return Device State
    public boolean getDeviceState() {
        return DeviceState;
    }

    // The Thread 
    @Override
    public void run() {
        try {
            // The Tell The Method the Thread is Busy.
            Busy = true;

            // Here to Change the Device state to ON
            if (DeviceState) {
                // the Alarm Duration and Interval is zero 
                // the Alarm will Work For One Minute
                if (AlarmDuration == 0 && AlarmInterval == 0) {
                    // To create alarm sound (beep)
                    for (int i = 0; i < 46; i++) {
                        PIN.low();
                        Thread.sleep(300);
                        PIN.high();
                        Thread.sleep(1000);
                    }
                } else {
                    // the Alarm Duration and Interval is gratter than Zero
                    // the Alarm Will Work For Duration time
                    long end = System.currentTimeMillis() + AlarmDuration * 60000;
                    while (System.currentTimeMillis() < end) {
                        // To create alarm sound (beep)
                        for (int i = 0; i < 46; i++) {
                            PIN.low();
                            Thread.sleep(300);
                            PIN.high();
                            Thread.sleep(1000);
                        }
                        // Sleep For Interval time
                        Thread.sleep(AlarmInterval * 60000);
                    }
                }
            } else { // Here to Change the Device state to OFF
                PIN.high();
            }

            // To Set the New Information in The Database
            // To set the New Time
            try (PreparedStatement ps = DB.prepareStatement("update device set lastStatusChange = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            // To set the New State 
            try (PreparedStatement ps = DB.prepareStatement("update device set DeviceState = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, false);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            // The Tell The Method the Thread is not Busy.
            Busy = false;

            // just To Print the Result
            FileLogger.AddInfo("Alarm " + DeviceID + ", State Change To " + DeviceState);

        } catch (SQLException | InterruptedException ex) {
            // This Catch For DataBase Error
            FileLogger.AddWarning("Alarm " + DeviceID + ", Error In DataBase\n" + ex);
        }
    }
}
