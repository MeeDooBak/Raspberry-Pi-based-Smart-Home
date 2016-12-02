package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Timing_Task extends Thread {

    private boolean isDisabled;
    private boolean isexecute;

    private final int AlarmDuration;
    private final int AlarmInterval;
    private final java.sql.Date ActionDate;
    private final boolean repeatDaily;
    private final int TaskID;
    private final Connection DB;
    private final SensorList Sensor;
    private final Map<DeviceList, Boolean> List;
    private final boolean NotifyByEmail;
    private final Time EnableTaskOnTime;
    private final Time DisableTaskOnTime;

    public Timing_Task(int TaskID, boolean isDisabled, java.sql.Date ActionDate, Time ActionTime, boolean repeatDaily, int AlarmDuration, int AlarmInterval,
            SensorList Sensor, Map<DeviceList, Boolean> List, Connection DB, boolean NotifyByEmail, Time EnableTaskOnTime, Time DisableTaskOnTime) {

        this.TaskID = TaskID;
        this.isDisabled = isDisabled;
        this.ActionDate = ActionDate;
        this.repeatDaily = repeatDaily;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
        this.Sensor = Sensor;
        this.Sensor.setClock(ActionTime);
        this.List = List;
        this.DB = DB;
        this.NotifyByEmail = NotifyByEmail;
        this.EnableTaskOnTime = EnableTaskOnTime;
        this.DisableTaskOnTime = DisableTaskOnTime;
        this.isexecute = false;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public void execute() {
        if (((Clock_Thread) Sensor.GetSensor()).getSensorState() && !isexecute) {
            for (Map.Entry<DeviceList, Boolean> Device : List.entrySet()) {
                if (Device.getKey().getDeviceName().equals("Alarm")) {
                    Device.getKey().setDeviceState(Device.getValue());
                    Device.getKey().setAlarmDuration(AlarmDuration);
                    Device.getKey().setAlarmInterval(AlarmInterval);
                    Device.getKey().setIsStatusChanged(true);
                    Device.getKey().Start();

                } else if (!Device.getKey().getDeviceState() == Device.getValue()) {
                    Device.getKey().setDeviceState(Device.getValue());
                    Device.getKey().setIsStatusChanged(true);
                    Device.getKey().Start();
                }
            }
            if (NotifyByEmail) {
                System.out.println("Send Email To User");
            }
            isexecute = true;
        }
    }

    @Override
    public void run() {
        while (!isDisabled) {
            try {
                long CurrentTime = new java.util.Date().getTime();
                if ((EnableTaskOnTime == null && DisableTaskOnTime == null) || (EnableTaskOnTime.getTime() <= CurrentTime && CurrentTime <= DisableTaskOnTime.getTime())) {
                    if (repeatDaily) {
                        execute();
                    } else {
                        java.sql.Date CDate = new java.sql.Date(new java.util.Date().getTime());
                        if (("" + CDate).equals("" + ActionDate)) {
                            execute();
                        } else if (CDate.after(ActionDate)) {
                            isDisabled = true;
                            PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                            ps.setBoolean(1, isDisabled);
                            ps.setInt(2, TaskID);
                            ps.executeUpdate();
                        }
                    }
                } else {
                    isDisabled = true;
                    PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                    ps.setBoolean(1, isDisabled);
                    ps.setInt(2, TaskID);
                    ps.executeUpdate();
                }
                Thread.sleep(2000);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(Timing_Task.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
