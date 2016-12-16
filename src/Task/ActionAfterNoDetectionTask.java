package Task;

import Device.*;
import Email.*;
import Logger.SLogger;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class ActionAfterNoDetectionTask implements Runnable {

    private boolean isDisabled;
    private long CouyntingDate;
    private boolean TimeFinish;

    private final int TaskID;
    private final String TaskName;
    private final UserList User;
    private final RoomList Room;
    private final SensorList SmokeSensor;
    private final boolean repeatDaily;
    private final int AlarmDuration;
    private final int AlarmInterval;
    private final SensorList Sensor;
    private final ArrayList<TaskDevicesList> List;
    private final int SelectedSensorValue;
    private final boolean NotifyByEmail;
    private final java.sql.Date ActionDate;
    private final Time EnableTaskOnTime;
    private final Time DisableTaskOnTime;
    private final Connection DB;
    private final Thread Thread;

    public ActionAfterNoDetectionTask(int TaskID, String TaskName, UserList User, RoomList Room, SensorList SmokeSensor, boolean isDisabled, boolean repeatDaily,
            int AlarmDuration, int AlarmInterval, SensorList Sensor, ArrayList<TaskDevicesList> List, int SelectedSensorValue, boolean NotifyByEmail, java.sql.Date ActionDate,
            Time EnableTaskOnTime, Time DisableTaskOnTime, Connection DB) {

        this.TaskID = TaskID;
        this.TaskName = TaskName;
        this.User = User;
        this.Room = Room;
        this.SmokeSensor = SmokeSensor;
        this.isDisabled = isDisabled;
        this.repeatDaily = repeatDaily;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
        this.Sensor = Sensor;
        this.List = List;
        this.SelectedSensorValue = SelectedSensorValue;
        this.NotifyByEmail = NotifyByEmail;
        this.ActionDate = ActionDate;
        this.EnableTaskOnTime = EnableTaskOnTime;
        this.DisableTaskOnTime = DisableTaskOnTime;
        this.DB = DB;

        this.CouyntingDate = new java.util.Date().getTime() + (SelectedSensorValue * 60000);
        new Thread(Timer).start();

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

    private void Execute() {
        try {
            if (((SmokeSensor) SmokeSensor.GetSensor()).getSensorState()) {
                System.out.println("The Task : " + TaskID + " Has been Deactivate, Because the Gas Sensor is Activated");
            } else {
                boolean Send = false;
                for (int i = 0; i < List.size(); i++) {
                    switch (List.get(i).getDeviceID().getDeviceName()) {
                        case "Roof Lamp":
                            if (((Light) List.get(i).getDeviceID().GetDevice()).getDeviceState() != List.get(i).getRequiredDeviceStatus()) {
                                ((Light) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), true);
                                Send = true;
                            }
                            break;
                        case "AC":
                            if (((AC) List.get(i).getDeviceID().GetDevice()).getDeviceState() != List.get(i).getRequiredDeviceStatus()) {
                                ((AC) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), true);
                                Send = true;
                            }
                            break;
                        case "Curtains":
                            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    ResultSet Result = Statement.executeQuery("select * from device_stepper_motor where DeviceID = " + List.get(i).getDeviceID().getDeviceID())) {
                                Result.next();
                                if (((Motor) List.get(i).getDeviceID().GetDevice()).getDeviceState() != List.get(i).getRequiredDeviceStatus()) {
                                    ((Motor) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), Result.getInt("StepperMotorMoves"), true);
                                    Send = true;
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ActionOnDetectionTask.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        case "Alarm":
                            if (((Alarm) List.get(i).getDeviceID().GetDevice()).getDeviceState() != List.get(i).getRequiredDeviceStatus()) {
                                ((Alarm) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), AlarmDuration, AlarmInterval, true);
                                Send = true;
                            }
                            break;
                        case "Garage Door":
                            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    ResultSet Result = Statement.executeQuery("select * from device_stepper_motor where DeviceID = " + List.get(i).getDeviceID().getDeviceID())) {
                                Result.next();
                                if (((Motor) List.get(i).getDeviceID().GetDevice()).getDeviceState() != List.get(i).getRequiredDeviceStatus()) {
                                    ((Motor) List.get(i).getDeviceID().GetDevice()).ChangeState(List.get(i).getRequiredDeviceStatus(), Result.getInt("StepperMotorMoves"), true);
                                    Send = true;
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(ActionOnDetectionTask.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (NotifyByEmail && Send) {
                    Mail.SendMail("Notification", TaskName, User, Room, Sensor, List, -1);
                }
                if (Send) {
                    SLogger.Logger("Notification", TaskName, Room, Sensor, List, -1);
                }
            }
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ActionAfterNoDetectionTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Check() {
        if (((MotionSensor) Sensor.GetSensor()).getSensorState()) {
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
                            Check();
                            if (TimeFinish) {
                                Execute();
                            }
                        }
                    } else {
                        Check();
                        if (TimeFinish) {
                            Execute();
                        }
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
                                Check();
                                if (TimeFinish) {
                                    Execute();
                                }

                            } else if (System.currentTimeMillis() > DisableDate.getTime()) {
                                isDisabled = true;
                                PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                                ps.setBoolean(1, isDisabled);
                                ps.setInt(2, TaskID);
                                ps.executeUpdate();
                            }
                        } else {
                            Check();
                            if (TimeFinish) {
                                Execute();
                            }
                        }
                    } else if (CDate.after(ActionDate)) {
                        isDisabled = true;
                        PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                        ps.setBoolean(1, isDisabled);
                        ps.setInt(2, TaskID);
                        ps.executeUpdate();
                    }
                }
                Thread.sleep(2000);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(ActionAfterNoDetectionTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private final Runnable Timer = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if (CouyntingDate <= new java.util.Date().getTime()) {
                        TimeFinish = true;
                        break;
                    } else {
                        TimeFinish = false;
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ActionAfterNoDetectionTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
}