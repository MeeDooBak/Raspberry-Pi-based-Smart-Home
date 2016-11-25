package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class ActionAfterNoDetection_Thread extends Thread {

    private boolean isDisabled;
    private long CouyntingDate;
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
    private final boolean NotifyByEmail;
    private final Time EnableTaskOnTime;
    private final Time DisableTaskOnTime;

    public ActionAfterNoDetection_Thread(int TaskID, boolean isDisabled, java.sql.Date ActionDate, boolean repeatDaily, int AlarmDuration, int AlarmInterval,
            int SelectedSensorValue, SensorList Sensor, Map<DeviceList, Boolean> List, Connection DB, boolean NotifyByEmail, Time EnableTaskOnTime, Time DisableTaskOnTime) {

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
        this.NotifyByEmail = NotifyByEmail;
        this.EnableTaskOnTime = EnableTaskOnTime;
        this.DisableTaskOnTime = DisableTaskOnTime;

        CouyntingDate = new java.util.Date().getTime() + (SelectedSensorValue * 60000);
        new Thread(Timer).start();
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    private void execute() {
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
    }

    private void Check() {
        if (Sensor.getMotionSensor().getSensorState()) {
            CouyntingDate = new java.util.Date().getTime() + (SelectedSensorValue * 60000);
        } else {
            if (TimeFinish) {
                CouyntingDate = new java.util.Date().getTime() + (SelectedSensorValue * 60000);
                new Thread(Timer).start();
            }
        }
    }

    @Override
    public void run() {
        while (!isDisabled) {
            try {
                long CurrentTime = new java.util.Date().getTime();
                if (EnableTaskOnTime.getTime() >= CurrentTime && CurrentTime <= DisableTaskOnTime.getTime()) {
                    Check();
                    if (TimeFinish) {
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
                Logger.getLogger(ActionAfterNoDetection_Thread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private final Thread Timer = new Thread() {
        @Override
        public void run() {
            while (true) {
                if (CouyntingDate == new java.util.Date().getTime()) {
                    TimeFinish = true;
                    break;
                } else {
                    TimeFinish = false;
                }
            }
        }
    };
}
