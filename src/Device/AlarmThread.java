package Device;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;

public class AlarmThread extends Thread {

    private boolean isStatusChanged;

    private final PinsList GateNum;
    private final boolean DeviceState;
    private final int AlarmDuration;
    private final int AlarmInterval;
    private final int DeviceID;
    private final Connection DB;

    public AlarmThread(int DeviceID, boolean DeviceState, PinsList GateNum, boolean isStatusChanged, int AlarmDuration, int AlarmInterval, Connection DB) {

        this.DB = DB;
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
//                    long currentTime = System.currentTimeMillis();
//                    long end = currentTime + AlarmDuration * 1000;
//                    while (currentTime < end) {
//                        GateNum.getOutputPIN().high();
//                        Thread.sleep(AlarmInterval * 1000);
//                        GateNum.getOutputPIN().low();
//                    }
                    GateNum.getOutputPIN().high();
                } else {
                    GateNum.getOutputPIN().low();
                }
                isStatusChanged = false;

                PreparedStatement ps = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, isStatusChanged);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AlarmThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
