package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class ActionAfterNoDetection extends Thread {

    private boolean isDisabled;

    private java.util.Date CouyntingDate;
    private boolean TimeFinish;

    private final int AlarmDuration;
    private final int AlarmInterval;
    private final int SelectedSensorValue;
    private final java.sql.Date ActionDate;
    private final boolean repeatDaily;
    private final int TaskID;
    private final Connection DB;
    private final SensorList Sensor;
    private final Map<DeviceList, Boolean> List;

    public ActionAfterNoDetection(int TaskID, boolean isDisabled, java.sql.Date ActionDate, boolean repeatDaily, int AlarmDuration, int AlarmInterval, int SelectedSensorValue,
            SensorList Sensor, Map<DeviceList, Boolean> List, Connection DB) {

        this.TaskID = TaskID;
        this.isDisabled = isDisabled;
        this.ActionDate = ActionDate;
        this.repeatDaily = repeatDaily;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
        this.SelectedSensorValue = SelectedSensorValue;
        this.Sensor = Sensor;
        this.List = List;
        this.DB = DB;

        this.CouyntingDate = new java.util.Date();
        CouyntingDate.setMinutes(CouyntingDate.getMinutes() + SelectedSensorValue);
        new Thread(Timer).start();
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    private void execute() {
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

    private void Check() {
        if (Sensor.getSensorThread().getSensorState()) {
            java.util.Date NewDate = new java.util.Date();
            NewDate.setMinutes(NewDate.getMinutes() + SelectedSensorValue);
            CouyntingDate = NewDate;

        } else {
            if (TimeFinish) {
                java.util.Date NewDate = new java.util.Date();
                NewDate.setMinutes(NewDate.getMinutes() + SelectedSensorValue);
                CouyntingDate = NewDate;

                new Thread(Timer).start();
            }
        }
    }

    @Override
    public void run() {
        while (!isDisabled) {
            Check();
            if (TimeFinish) {
                try {
                    if (repeatDaily) {
                        execute();
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
                            execute();
                        } else if (startOfToday.getTimeInMillis() > ActionDate.getTime()) {
                            isDisabled = true;

                            PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                            ps.setBoolean(1, isDisabled);
                            ps.setInt(2, TaskID);
                            ps.executeUpdate();
                        }
                    }
                    Thread.sleep(2000);
                } catch (SQLException | InterruptedException ex) {
                    Logger.getLogger(ActionOnDetectionThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private final Thread Timer = new Thread() {
        @Override
        public void run() {
            while (true) {
                if (CouyntingDate.equals(new java.util.Date())) {
                    TimeFinish = true;
                    break;
                } else {
                    TimeFinish = false;
                }
            }
        }
    };
}
