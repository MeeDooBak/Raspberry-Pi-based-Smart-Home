package Device;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;
import com.adventnet.snmp.snmp2.*;

public class RoofLamp_Thread extends Thread {

    private boolean isStatusChanged;
    private boolean DeviceState;

    private final PinsList GateNum;
    private final int DeviceID;
    private final Connection DB;
    private final Relay command;
//    private java.util.Date lastStatusChange;

    public RoofLamp_Thread(int DeviceID, boolean DeviceState, PinsList GateNum, boolean isStatusChanged, Connection DB, Relay command) {
        this.DB = DB;
        this.command = command;
        this.DeviceID = DeviceID;
        this.DeviceState = DeviceState;
        this.GateNum = GateNum;
        this.isStatusChanged = isStatusChanged;
//        this.lastStatusChange = new java.util.Date();
//        this.lastStatusChange.setTime(lastStatusChange.getTime() - 180000);
    }

    public void setIsStatusChanged(boolean isStatusChanged) {
        this.isStatusChanged = isStatusChanged;
    }

    public void setDeviceState(boolean DeviceState) {
        this.DeviceState = DeviceState;
    }

    @Override
    public void run() {
        try {
            if (isStatusChanged) {
                if (DeviceState) {
//                    while (true) {
//                        if ((lastStatusChange.getTime() + 180000) <= new java.util.Date().getTime()) {
//                            lastStatusChange = new java.util.Date();

                            command.SNMP_SET(".1.3.6.1.4.1.19865.1.2." + GateNum.getOutputPINRelay() + ".0", SnmpAPI.INTEGER, "1");
//                            break;
//
//                        } else {
//                            setIsStatusChanged(false);
//
//                            PreparedStatement ps = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
//                            ps.setBoolean(1, isStatusChanged);
//                            ps.setInt(2, DeviceID);
//                            ps.executeUpdate();
//                            ps.close();
//
//                            System.out.println("The RoofLamp has been run shortly before, Wait 3 minutes");
//                            Thread.sleep(60000);
//                        }
//                    }
                } else {
                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2." + GateNum.getOutputPINRelay() + ".0", SnmpAPI.INTEGER, "0");
                }
                setIsStatusChanged(false);

                PreparedStatement ps = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, isStatusChanged);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoofLamp_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
