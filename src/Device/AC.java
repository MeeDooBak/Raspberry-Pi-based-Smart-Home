package Device;

import Logger.*;
import Pins.*;
import Relay.*;
import java.sql.*;

public class AC implements Runnable {

    private final PinsList GateNum;
    private final int DeviceID;
    private final Connection DB;
    private final Relay RelayQueue;
    private boolean Busy;
    private boolean DeviceState;
    private Timestamp LastStatusChange;

    // Get Device Information from Database
    public AC(int DeviceID, PinsList GateNum, boolean DeviceState, boolean isStatusChanged, Timestamp LastStatusChange, Connection DB, Relay RelayQueue) {
        this.DeviceID = DeviceID;
        this.GateNum = GateNum;
        this.DB = DB;
        this.RelayQueue = RelayQueue;
        this.Busy = false;
        this.LastStatusChange = LastStatusChange;

        // To Make the Change For the First Time 
        ChangeState(DeviceState, isStatusChanged);
    }

    // To Change Device State
    public final void ChangeState(boolean DeviceState, boolean isStatusChanged) {
        // To Check if the Current Device State is not equl The New Device State
        // and to check is it Change from DataBase
        // if it is equl just ignore it
        if (this.DeviceState != DeviceState && isStatusChanged) {
            // To tell Database that java change the Device State 
            try (PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps2.setBoolean(1, false);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();

                // To Check If the Thread is not working 
                // If it is Working just ignore it.
                if (!Busy) {
                    // just to Get the New Date and Start The Thread
                    this.DeviceState = DeviceState;
                    new Thread(this).start();
                }
            } catch (SQLException ex) {
                // This Catch For DataBase Error
                FileLogger.AddWarning("AC " + DeviceID + ", Error In DataBase\n" + ex);
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
                while (true) {
                    // To Check if the Device is Work Before Half Minute 
                    // If it is Work Before Half Minute just Wait Until Be come More Than Half Minute
                    // If Not Just Change the Device State ON
                    if ((LastStatusChange.getTime() + 30000) <= new Timestamp(new java.util.Date().getTime()).getTime()) {
                        LastStatusChange = new Timestamp(new java.util.Date().getTime());

                        // Add The New Change To Relay Queue To Change the State
                        RelayQueue.Add(".1.3.6.1.4.1.19865.1.2." + GateNum.getPI4Jnumber() + ".0", "1");
                        break;
                    } else {
                        FileLogger.AddInfo("AC " + DeviceID + ", has been run shortly before, Wait Half a minute");
                        Thread.sleep(5000);
                    }
                }
            } else { // Here to Change the Device state to OFF

                // Add The New Change To Relay Queue To Change the State
                RelayQueue.Add(".1.3.6.1.4.1.19865.1.2." + GateNum.getPI4Jnumber() + ".0", "0");
            }

            // To Set the New Information in The Database
            // To set the New Time
            try (PreparedStatement ps = DB.prepareStatement("update device set lastStatusChange = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                Timestamp NewStatusChange = new Timestamp(new java.util.Date().getTime());
                ps.setTimestamp(1, NewStatusChange);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            // To set the New State 
            try (PreparedStatement ps = DB.prepareStatement("update device set DeviceState = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, DeviceState);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            // The Tell The Method the Thread is not Busy.
            Busy = false;

            // just To Print the Result
            FileLogger.AddInfo("AC " + DeviceID + ", State Change To " + DeviceState);

        } catch (SQLException | InterruptedException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("AC " + DeviceID + ", Error In DataBase\n" + ex);
        }
    }
}
