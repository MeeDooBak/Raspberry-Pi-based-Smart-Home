package Device;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;
import com.adventnet.snmp.snmp2.*;

public class RoofLamp_Thread extends Thread {

    private boolean isStatusChanged;
    private final PinsList GateNum;
    private final boolean DeviceState;
    private final int DeviceID;
    private final Connection DB;
    private final Relay command;
    private final Timestamp lastStatusChange;

    public RoofLamp_Thread(int DeviceID, boolean DeviceState, PinsList GateNum, boolean isStatusChanged, Connection DB, Relay command, Timestamp lastStatusChange) {
        this.DB = DB;
        this.command = command;
        this.DeviceID = DeviceID;
        this.DeviceState = DeviceState;
        this.GateNum = GateNum;
        this.isStatusChanged = isStatusChanged;
        this.lastStatusChange = lastStatusChange;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Timestamp lastStatusChangeOLD;
                try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result2 = Statement2.executeQuery("select * from device where DeviceID = " + DeviceID)) {
                    Result2.next();
                    lastStatusChangeOLD = Result2.getTimestamp("lastStatusChange");
                }

                if ((lastStatusChangeOLD.getTime() + 180000) <= lastStatusChange.getTime()) {
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
                    break;
                } else {
                    System.out.println("The RoofLamp has been run shortly before, Wait 3 minutes");
                    Thread.sleep(60000);
                }
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(RoofLamp_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
