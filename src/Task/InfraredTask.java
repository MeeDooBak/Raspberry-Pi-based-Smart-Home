package Task;

import Device.*;
import Email.*;
import Logger.*;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class InfraredTask implements Runnable {

    private boolean isDisabled;
    private boolean isChange;

    private final int TaskID;
    private final String TaskName;
    private final UserList User;
    private final RoomList Room;
    private final boolean repeatDaily;
    private final int AlarmDuration;
    private final int AlarmInterval;
    private final SensorList Sensor;
    private final ArrayList<TaskDevicesList> List;
    private final boolean NotifyByEmail;
    private final java.sql.Date ActionDate;
    private final Time EnableTaskOnTime;
    private final Time DisableTaskOnTime;
    private final Connection DB;
    private final Thread Thread;

    public InfraredTask(int TaskID, String TaskName, UserList User, RoomList Room, boolean isDisabled, boolean repeatDaily, int AlarmDuration, int AlarmInterval,
            SensorList Sensor, ArrayList<TaskDevicesList> List, boolean NotifyByEmail, java.sql.Date ActionDate, Time EnableTaskOnTime, Time DisableTaskOnTime, Connection DB) {
        this.TaskID = TaskID;
        this.TaskName = TaskName;
        this.User = User;
        this.Room = Room;
        this.isDisabled = isDisabled;
        this.repeatDaily = repeatDaily;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
        this.Sensor = Sensor;
        this.List = List;
        this.NotifyByEmail = NotifyByEmail;
        this.ActionDate = ActionDate;
        this.EnableTaskOnTime = EnableTaskOnTime;
        this.DisableTaskOnTime = DisableTaskOnTime;
        this.DB = DB;

        this.isChange = true;

        this.Thread = new Thread(this);
        this.Thread.start();
    }

    public boolean setisDisabled(boolean isDisabled) {
        if (Thread.isAlive()) {
            Thread.stop();
            this.isDisabled = true;
            for (int i = 0; i < 2000; i++) {
                if (!Thread.isAlive()) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public void Execute() {
        try {
            if (((InfraredSensor) Sensor.GetSensor()).getSensorState() && isChange) {
                boolean Send = false;
                for (int i = 0; i < List.size(); i++) {
                    switch (List.get(i).getDeviceID().getDeviceName()) {
                        case "Alarm":
                            if (((Alarm) List.get(i).getDeviceID().GetDevice()).getDeviceState() != List.get(i).getRequiredDeviceStatus()) {
                                ((Alarm) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), AlarmDuration, AlarmInterval, true);
                                Send = true;
                            }
                            break;
                        case "Security Camera":
                            if (List.get(i).getTakeImage() > 0) {
                                ((SecurityCamera) List.get(i).getDeviceID().GetDevice()).Capture(List.get(i).getTakeImage());
                                Send = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (List.isEmpty()) {
                    Send = true;
                }
                if (NotifyByEmail && Send) {
                    Mail.SendMail("House parameters", TaskName, User, Room, Sensor, List, -1);
                }
                if (Send) {
                    SLogger.Logger("House parameters", TaskName, Room, Sensor, List, -1);
                }
                isChange = false;
                Thread.sleep(1000);
            } else {
                isChange = true;
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(InfraredTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (!isDisabled) {
            try {
                if (repeatDaily) {
                    if (EnableTaskOnTime != null && DisableTaskOnTime != null) {
                        java.util.Date EnableDate = new java.util.Date(System.currentTimeMillis());
                        EnableDate.setHours(EnableTaskOnTime.getHours());
                        EnableDate.setMinutes(EnableTaskOnTime.getMinutes());
                        EnableDate.setSeconds(EnableTaskOnTime.getSeconds());

                        java.util.Date DisableDate = new java.util.Date(System.currentTimeMillis());
                        DisableDate.setHours(DisableTaskOnTime.getHours());
                        DisableDate.setMinutes(DisableTaskOnTime.getMinutes());
                        DisableDate.setSeconds(DisableTaskOnTime.getSeconds());

                        if (EnableDate.getTime() <= System.currentTimeMillis() && System.currentTimeMillis() <= DisableDate.getTime()) {
                            Execute();
                        }
                    } else {
                        Execute();
                    }
                } else {
                    java.sql.Date CDate = new java.sql.Date(new java.util.Date().getTime());
                    if ((CDate + "").equals(ActionDate + "")) {
                        if (EnableTaskOnTime != null && DisableTaskOnTime != null) {
                            java.util.Date EnableDate = new java.util.Date(System.currentTimeMillis());
                            EnableDate.setHours(EnableTaskOnTime.getHours());
                            EnableDate.setMinutes(EnableTaskOnTime.getMinutes());
                            EnableDate.setSeconds(EnableTaskOnTime.getSeconds());

                            java.util.Date DisableDate = new java.util.Date(System.currentTimeMillis());
                            DisableDate.setHours(DisableTaskOnTime.getHours());
                            DisableDate.setMinutes(DisableTaskOnTime.getMinutes());
                            DisableDate.setSeconds(DisableTaskOnTime.getSeconds());

                            if (EnableDate.getTime() <= System.currentTimeMillis() && System.currentTimeMillis() <= DisableDate.getTime()) {
                                Execute();

                            } else if (System.currentTimeMillis() > DisableDate.getTime()) {
                                isDisabled = true;
                                PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                                ps.setBoolean(1, isDisabled);
                                ps.setInt(2, TaskID);
                                ps.executeUpdate();
                            }
                        } else {
                            Execute();
                        }
                    } else if (CDate.after(ActionDate)) {
                        isDisabled = true;
                        PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                        ps.setBoolean(1, isDisabled);
                        ps.setInt(2, TaskID);
                        ps.executeUpdate();
                    }
                }
                Thread.sleep(500);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(InfraredTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
