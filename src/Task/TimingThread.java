package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.text.*;
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

                    Calendar startOfToday = Calendar.getInstance();
                    Calendar endOfToday = Calendar.getInstance();
                    endOfToday.setTime(startOfToday.getTime());

                    startOfToday.set(Calendar.HOUR_OF_DAY, 0);
                    startOfToday.set(Calendar.MINUTE, 0);
                    startOfToday.set(Calendar.SECOND, 0);
                    startOfToday.set(Calendar.MILLISECOND, 0);

                    endOfToday.set(Calendar.HOUR_OF_DAY, 23);
                    endOfToday.set(Calendar.MINUTE, 59);
                    endOfToday.set(Calendar.SECOND, 59);
                    endOfToday.set(Calendar.MILLISECOND, 999);

                    if (startOfToday.getTimeInMillis() <= ActionDate.getTime() && ActionDate.getTime() <= endOfToday.getTimeInMillis()) {
                        if (ActionTime.equals(new SimpleDateFormat("HH:mm").format(new java.util.Date())) && !isexecute) {
                            execute();
                            isexecute = true;
                        }
                    } else if (startOfToday.getTimeInMillis() > ActionDate.getTime()) {
                        isDisabled = true;

                        PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                        ps.setBoolean(1, isDisabled);
                        ps.setInt(2, TaskID);
                        ps.executeUpdate();
                    }
                }
                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(TimingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
