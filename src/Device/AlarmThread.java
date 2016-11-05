package Device;

import java.sql.*;
import java.util.logging.*;
import com.adventnet.snmp.snmp2.*;

public class AlarmThread extends Thread {

    private boolean isStatusChanged;

    private final int GateNum;
    private final boolean DeviceState;
    private final int AlarmDuration;
    private final int AlarmInterval;
    private final int DeviceID;
    private final Connection DB;
    private final Relay command;

    public AlarmThread(int DeviceID, boolean DeviceState, int GateNum, boolean isStatusChanged, int AlarmDuration, int AlarmInterval, Connection DB, Relay command) {

        this.DB = DB;
        this.command = command;
        this.DeviceID = DeviceID;
        this.DeviceState = DeviceState;
        this.GateNum = GateNum;
        this.isStatusChanged = isStatusChanged;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
    }

    @Override
    public void run() {
        try {
            if (isStatusChanged) {

                if (DeviceState) {

                    long currentTime = System.currentTimeMillis();
                    long end = currentTime + AlarmDuration * 1000;
                    while (currentTime < end) {
                        if (GateNum < 9) {
                            command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "1");
                        } else {
                            command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + (GateNum - 8) + ".0", SnmpAPI.INTEGER, "1");
                        }
                        Thread.sleep(AlarmInterval * 1000);

                        if (GateNum < 9) {
                            command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "0");
                        } else {
                            command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + (GateNum - 8) + ".0", SnmpAPI.INTEGER, "0");
                        }
                    }
                } else {
                    if (GateNum < 9) {
                        command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "0");
                    } else {
                        command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + (GateNum - 8) + ".0", SnmpAPI.INTEGER, "0");
                    }
                }
                isStatusChanged = false;

                PreparedStatement ps = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, isStatusChanged);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(AlarmThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
