package Device;

import Pins.*;
import Relay.*;
import java.sql.*;
import java.util.logging.*;

public class AC implements Runnable {

    private final PinsList GateNum;
    private final int DeviceID;
    private final Connection DB;
    private final Relay RelayQueue;
    private boolean Busy;
    private boolean DeviceState;
    private Timestamp LastStatusChange;

    public AC(int DeviceID, PinsList GateNum, boolean DeviceState, boolean isStatusChanged, Timestamp LastStatusChange, Connection DB, Relay RelayQueue) {
        this.DeviceID = DeviceID;
        this.GateNum = GateNum;
        this.DB = DB;
        this.RelayQueue = RelayQueue;
        this.Busy = false;
        this.LastStatusChange = LastStatusChange;

        ChangeState(DeviceState, isStatusChanged);
    }

    public final void ChangeState(boolean DeviceState, boolean isStatusChanged) {
        if (this.DeviceState != DeviceState && isStatusChanged) {
            try (PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps2.setBoolean(1, false);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();

                if (!Busy) {
                    this.DeviceState = DeviceState;
                    new Thread(this).start();
                }
            } catch (SQLException ex) {
                System.out.println("AC " + DeviceID + ", Error In DataBase");
                Logger.getLogger(AC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean getDeviceState() {
        return DeviceState;
    }

    @Override
    public void run() {
        try {
            Busy = true;
            if (DeviceState) {
                while (true) {
                    if ((LastStatusChange.getTime() + 30000) <= new Timestamp(new java.util.Date().getTime()).getTime()) {
                        LastStatusChange = new Timestamp(new java.util.Date().getTime());
                        RelayQueue.Add(".1.3.6.1.4.1.19865.1.2." + GateNum.getPI4Jnumber() + ".0", "1");
                        break;
                    } else {
                        System.out.println("The AC has been run shortly before, Wait Half a minute");
                        Thread.sleep(5000);
                    }
                }
            } else {
                RelayQueue.Add(".1.3.6.1.4.1.19865.1.2." + GateNum.getPI4Jnumber() + ".0", "0");
            }

            try (PreparedStatement ps = DB.prepareStatement("update device set lastStatusChange = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                Timestamp NewStatusChange = new Timestamp(new java.util.Date().getTime());
                ps.setTimestamp(1, NewStatusChange);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = DB.prepareStatement("update device set DeviceState = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, DeviceState);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            Busy = false;
            System.out.println("AC " + DeviceID + ", State Change To " + DeviceState);

        } catch (SQLException | InterruptedException ex) {
            System.out.println("AC " + DeviceID + ", Error In DataBase");
            Logger.getLogger(AC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
