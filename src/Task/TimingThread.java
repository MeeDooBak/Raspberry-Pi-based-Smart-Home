package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

public class TimingThread extends Thread {

    private boolean isDisabled;
    private boolean isexecute;

    private final int AlarmDuration;
    private final int AlarmInterval;
    private final java.sql.Date ActionDate;
    private final Time ActionTime;
    private final boolean repeatDaily;
    private final int TaskID;
    private final Connection DB;
    private final SensorList Sensor;
    private final Map<DeviceList, Boolean> List;

    public TimingThread(int TaskID, boolean isDisabled, java.sql.Date ActionDate, Time ActionTime, boolean repeatDaily, int AlarmDuration, int AlarmInterval,
            SensorList Sensor, Map<DeviceList, Boolean> List, Connection DB) {

        this.TaskID = TaskID;
        this.isDisabled = isDisabled;
        this.ActionDate = ActionDate;
        this.ActionTime = ActionTime;
        this.repeatDaily = repeatDaily;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
        this.Sensor = Sensor;
        this.List = List;
        this.DB = DB;

        isexecute = false;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public void execute() {
        if (Sensor.getSensorThread().getSensorState()) {
            for (Map.Entry<DeviceList, Boolean> Device : List.entrySet()) {
                Device.getKey().setDeviceState(Device.getValue());
                Device.getKey().setIsStatusChanged(true);

                if (Device.getKey().getDeviceName().equals("Alarm")) {
                    Device.getKey().setAlarmDuration(AlarmDuration);
                    Device.getKey().setAlarmInterval(AlarmInterval);
                }
                Device.getKey().Start();
            }
        }
    }

    @Override
    public void run() {
        while (!isDisabled) {
            try {
                if (repeatDaily) {
                    if (ActionTime.equals(new SimpleDateFormat("HH:mm").format(new java.util.Date())) && !isexecute) {
                        execute();
                        isexecute = true;
                    }
                } else {
                    if (ActionDate.equals(new java.util.Date())) {
                        if (ActionTime.equals(new SimpleDateFormat("HH:mm").format(new java.util.Date())) && !isexecute) {
                            execute();
                            isexecute = true;
                        }
                    } else if (ActionDate.after(new java.util.Date())) {
                        PreparedStatement ps = DB.prepareStatement("delete from task_devices where TaskID = ?");
                        ps.setInt(1, TaskID);
                        ps.execute();

                        ps = DB.prepareStatement("delete from task where TaskID = ?");
                        ps.setInt(1, TaskID);
                        ps.execute();
                    }
                }
                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(TimingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
