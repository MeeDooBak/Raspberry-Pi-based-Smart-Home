package Task;

import Device.*;
import Email.*;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class SmokeTask implements Runnable {

    private boolean isDisabled;
    private boolean isBusy;

    private final int TaskID;
    private final String TaskName;
    private final UserList User;
    private final RoomList Room;
    private final Mail Mail;
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

    public SmokeTask(int TaskID, String TaskName, UserList User, RoomList Room, Mail Mail, boolean isDisabled, boolean repeatDaily, int AlarmDuration, int AlarmInterval,
            SensorList Sensor, ArrayList<TaskDevicesList> List, boolean NotifyByEmail, java.sql.Date ActionDate, Time EnableTaskOnTime, Time DisableTaskOnTime, Connection DB) {

        this.TaskID = TaskID;
        this.TaskName = TaskName;
        this.User = User;
        this.Room = Room;
        this.Mail = Mail;
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

        this.isBusy = false;

        this.Thread = new Thread(this);
        this.Thread.start();
    }

    public boolean setisDisabled(boolean isDisabled) {
        if (Thread.isAlive()) {
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
        isBusy = true;
        if (((SmokeSensor) Sensor.GetSensor()).getSensorState()) {
            for (int i = 0; i < List.size(); i++) {
                switch (List.get(i).getDeviceID().getDeviceName()) {
                    case "Roof Lamp":
                        ((Light) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), true);
                        break;
                    case "AC":
                        ((AC) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), true);
                        break;
                    case "Curtains":
                        try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet Result = Statement.executeQuery("select * from device_stepper_motor where DeviceID = " + List.get(i).getDeviceID().getDeviceID())) {
                            Result.next();
                            ((Motor) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), Result.getInt("StepperMotorMoves"), true);
                        } catch (SQLException ex) {
                            Logger.getLogger(ActionOnDetectionTask.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case "Alarm":
                        ((Alarm) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), AlarmDuration, AlarmInterval, true);
                        break;
                    case "Garage Door":
                        try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet Result = Statement.executeQuery("select * from device_stepper_motor where DeviceID = " + List.get(i).getDeviceID().getDeviceID())) {
                            Result.next();
                            ((Motor) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), Result.getInt("StepperMotorMoves"), true);
                        } catch (SQLException ex) {
                            Logger.getLogger(ActionOnDetectionTask.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    default:
                        break;
                }
            }
            if (NotifyByEmail) {
//                    Mail.SendMail("Warning", TaskName, User, Room, List);
            }
        }
        isBusy = false;
    }

    @Override
    public void run() {
        while (!isDisabled) {
            try {
                long CurrentTime = new java.util.Date().getTime();
                if ((EnableTaskOnTime == null && DisableTaskOnTime == null) || (EnableTaskOnTime.getTime() <= CurrentTime && CurrentTime <= DisableTaskOnTime.getTime())) {
                    if (repeatDaily && !isBusy) {
                        Execute();
                    } else {
                        java.sql.Date CDate = new java.sql.Date(new java.util.Date().getTime());
                        if (("" + CDate).equals("" + ActionDate) && !isBusy) {
                            Execute();
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
                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(SmokeTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
