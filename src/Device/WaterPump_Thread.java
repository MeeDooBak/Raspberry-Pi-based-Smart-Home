package Device;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;
import com.adventnet.snmp.snmp2.*;

public class WaterPump_Thread extends Thread {

    private boolean isStatusChanged;
    private final PinsList GateNum;
    private final boolean DeviceState;
    private final int DeviceID;
    private final Connection DB;
    private final Relay command;

    public WaterPump_Thread(int DeviceID, boolean DeviceState, PinsList GateNum, boolean isStatusChanged, Connection DB, Relay command) {
        this.DB = DB;
        this.command = command;
        this.DeviceID = DeviceID;
        this.DeviceState = DeviceState;
        this.GateNum = GateNum;
        this.isStatusChanged = isStatusChanged;
    }

    @Override
    public void run() {
        try {
            if (isStatusChanged) {
                if (DeviceState) {
                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2." + GateNum.getOutputPINRelay() + ".0", SnmpAPI.INTEGER, "1");
                } else {
                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2." + GateNum.getOutputPINRelay() + ".0", SnmpAPI.INTEGER, "0");
                }
                isStatusChanged = false;

                PreparedStatement ps = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, isStatusChanged);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(WaterPump_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
