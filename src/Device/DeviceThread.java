package Device;

import java.sql.*;
import java.util.logging.*;
import com.adventnet.snmp.snmp2.*;

public class DeviceThread extends Thread {

    private boolean isStatusChanged;
    private final int GateNum;
    private final boolean DeviceState;
    private final int DeviceID;
    private final Connection DB;
    private final Relay command;

    public DeviceThread(int DeviceID, boolean DeviceState, int GateNum, boolean isStatusChanged, Connection DB, Relay command) {

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
                    if (GateNum < 9) {
                        command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "1");
                    } else {
                        command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + (GateNum - 8) + ".0", SnmpAPI.INTEGER, "1");
                    }
                } else {
                    if (GateNum < 9) {
                        command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "0");
                    } else {
                        command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + (GateNum - 8) + ".0", SnmpAPI.INTEGER, "0");
                    }
                }
                isStatusChanged = false;

                PreparedStatement ps = DB.prepareStatement("SELECT DeviceID, isStatusChanged FROM device WHERE DeviceID=? FOR UPDATE", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setInt(1, DeviceID);
                ResultSet rs = ps.executeQuery();
                rs.next();
                rs.updateBoolean("isStatusChanged", isStatusChanged);
                rs.updateRow();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
