package Device;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;

public class Alarm_Thread extends Thread {

    private boolean isStatusChanged;

    private final PinsList GateNum;
    private final boolean DeviceState;
    private final int AlarmDuration;
    private final int AlarmInterval;
    private final int DeviceID;
    private final Connection DB;

    public Alarm_Thread(int DeviceID, boolean DeviceState, PinsList GateNum, boolean isStatusChanged, int AlarmDuration, int AlarmInterval, Connection DB) {
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
                    if (AlarmDuration == 0 && AlarmInterval == 0) {
                        GateNum.getOutputPIN().low();
                    } else {
                        long currentTime = System.currentTimeMillis();
                        long end = currentTime + AlarmDuration * 1000;
                        while (currentTime < end) {
                            GateNum.getOutputPIN().low();
                            Thread.sleep(AlarmInterval * 1000);
                            GateNum.getOutputPIN().high();
                        }
                    }

                } else {
                    GateNum.getOutputPIN().high();
                }
                isStatusChanged = false;

                PreparedStatement ps = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, isStatusChanged);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(Alarm_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
