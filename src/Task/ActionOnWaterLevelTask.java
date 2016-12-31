package Task;

import Email.*;
import Logger.*;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.*;

public class ActionOnWaterLevelTask implements Runnable, TaskInterface {

    private boolean isDisabled;
    private int Percentage;
    private final int TaskID;
    private final String TaskName;
    private final UserList User;
    private final RoomList Room;
    private final SensorInterface SmokeSensor;
    private final boolean repeatDaily;
    private final int AlarmDuration;
    private final int AlarmInterval;
    private final SensorInterface Sensor;
    private final ArrayList<TaskDevicesList> List;
    private final int SelectedSensorValue;
    private final boolean NotifyByEmail;
    private final java.sql.Date ActionDate;
    private final Time EnableTaskOnTime;
    private final Time DisableTaskOnTime;
    private final Connection DB;
    private final Thread Thread;
    private final int MaxValue;
    private final int MinValue;
    private final int[][] Levels;

    // Get Device Information from Database
    public ActionOnWaterLevelTask(int TaskID, String TaskName, UserList User, RoomList Room, SensorInterface SmokeSensor, boolean isDisabled, boolean repeatDaily,
            int AlarmDuration, int AlarmInterval, SensorInterface Sensor, ArrayList<TaskDevicesList> List, int SelectedSensorValue, boolean NotifyByEmail, java.sql.Date ActionDate,
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

        // Get The Maximum Value For The Sensor
        this.MaxValue = Sensor.getMaxValue();
        // Get The Minimum Value For The Sensor
        this.MinValue = Sensor.getMinValue();

        // Set The Percentage Level For The Water
        this.Levels = new int[][]{{this.MinValue, 100}, {7, 90}, {8, 80}, {9, 70}, {10, 60}, {11, 50}, {12, 40}, {13, 30}, {14, 20}, {this.MaxValue, 10}};

        // Get The Value From User Information Percentage
        for (int[] Level1 : Levels) {
            if (Level1[1] == SelectedSensorValue) {
                this.Percentage = Level1[0];
                break;
            }
        }

        // Start Thread To Get Sensor State and Execute it in Devices
        // According to User Information
        this.Thread = new Thread(this);
        this.Thread.start();
    }

    // Get The Task ID
    @Override
    public int getTaskID() {
        return TaskID;
    }

    // Disabled The Thread To Stop it and Deleting The Task To Set the New Information
    @Override
    public boolean setisDisabled(boolean isDisabled) {
        // Check if Thread is Alive To Stop It
        if (Thread.isAlive()) {
            // Stop The Thread
            Thread.stop();
            // Set It Disabled
            this.isDisabled = true;

            // Loop Untll The Thread is Stop And Return True
            for (int i = 0; i < 2000; i++) {
                if (!Thread.isAlive()) {
                    return true;
                }
            }
            return false;
        } else { // If The Thread is Ready Stop
            // Return True
            return true;
        }
    }

    // This Method To Execute Changing in The Device And Send Email To User If He / Her Want
    @Override
    public void Execute() {
        try {
            // Get The Sensor State Value
            int Level = Sensor.getSensorValue();

            // Check if Sensor State Value is Greater Than -1 Or Ignore it
            if (Level != -1) {

                // Check if the Sensor State Value is Equal To User Information Percentage
                if (Level == Percentage) {
                    // Before Execute Changing To Device Let Check The Smoking Sensor
                    // If it is True Do not Execute Changing To Device
                    // For Safety
                    if (SmokeSensor.getSensorState()) {
                        // just To Print the Result of Smoking Sensor State
                        FileLogger.AddWarning("The Task : " + TaskID + " Has been Deactivate, Because the Gas Sensor is Activated");

                    } else { // If The Smoking Sensor Not Activate Execute Changing To Device

                        // Loop For All Device Select From User For This Task
                        for (int i = 0; i < List.size(); i++) {

                            // Get The Device Name To Change it State, According to its kind
                            switch (List.get(i).getDevice().getDeviceName()) {
                                case "Alarm":
                                    // Check if The Device State is Not Equl To User Information State To Change it
                                    if (List.get(i).getDevice().getDeviceState() != List.get(i).getRequiredDeviceStatus()) {
                                        // Change the Device State According to User Information
                                        List.get(i).getDevice().ChangeState(List.get(i).getRequiredDeviceStatus(), AlarmDuration, AlarmInterval);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }

                        // If User Want To Send Email When This Task Execute
                        if (NotifyByEmail) {
                            Mail.SendMail("Water level", TaskName, User, Room, Sensor, List, SelectedSensorValue);
                        }

                        // To Write It In The Log DataBase
                        SLogger.Logger("Water level", TaskName, Room, Sensor, List, SelectedSensorValue);
                    }

                    // Loop Until The Sensor State Value Change
                    while (true) {
                        // Get The Sensor State Value
                        Level = Sensor.getSensorValue();

                        // Check if Sensor State Value is Greater Than -1 and is Not Equal To User Information Percentage
                        // To Break From The Loop
                        if (Level != -1 && Level != Percentage) {
                            break;
                        }

                        // To Sleep For 1 Second
                        Thread.sleep(1000);
                    }
                }
            }
        } catch (InterruptedException ex) {
            // This Catch For Thread Sleep
            FileLogger.AddWarning("ActionOnWaterLevelTask " + TaskID + ", Error In Thread Sleep\n" + ex);
        }
    }

    // The TAsk Thread
    @Override
    public void run() {
        // While Until it Disabled
        while (!isDisabled) {
            try {
                // Check if the Task Repeat Daily
                if (repeatDaily) {
                    // Check if The Task Will Be Work From Time To Time Not 24 Hour
                    if (EnableTaskOnTime != null && DisableTaskOnTime != null) {

                        // Set Starting Time For Start The Task
                        java.util.Date EnableDate = new java.util.Date(System.currentTimeMillis());
                        EnableDate.setHours(EnableTaskOnTime.getHours());
                        EnableDate.setMinutes(EnableTaskOnTime.getMinutes());
                        EnableDate.setSeconds(EnableTaskOnTime.getSeconds());

                        // Set Ending Time For End The Task
                        java.util.Date DisableDate = new java.util.Date(System.currentTimeMillis());
                        DisableDate.setHours(DisableTaskOnTime.getHours());
                        DisableDate.setMinutes(DisableTaskOnTime.getMinutes());
                        DisableDate.setSeconds(DisableTaskOnTime.getSeconds());

                        // Check if The Current Time is Between Starting and Ending Time
                        // To Execute the Changing
                        if (EnableDate.getTime() <= System.currentTimeMillis() && System.currentTimeMillis() <= DisableDate.getTime()) {
                            // Just Execute the Changing
                            Execute();
                        }
                    } else { // if the Task Will Be Work 24 Hour
                        // Just Execute the Changing
                        Execute();
                    }
                } else { // If the Task Not Repeat Daily it is For Specific Day
                    // Set The Task Day
                    java.sql.Date CDate = new java.sql.Date(new java.util.Date().getTime());
                    // Check If The Task Day is Now
                    if ((CDate + "").equals(ActionDate + "")) {

                        // Check if The Task Will Be Work From Time To Time Not 24 Hour
                        if (EnableTaskOnTime != null && DisableTaskOnTime != null) {

                            // Set Starting Time For Start The Task
                            java.util.Date EnableDate = new java.util.Date(System.currentTimeMillis());
                            EnableDate.setHours(EnableTaskOnTime.getHours());
                            EnableDate.setMinutes(EnableTaskOnTime.getMinutes());
                            EnableDate.setSeconds(EnableTaskOnTime.getSeconds());

                            // Set Ending Time For End The Task
                            java.util.Date DisableDate = new java.util.Date(System.currentTimeMillis());
                            DisableDate.setHours(DisableTaskOnTime.getHours());
                            DisableDate.setMinutes(DisableTaskOnTime.getMinutes());
                            DisableDate.setSeconds(DisableTaskOnTime.getSeconds());

                            // Check if The Current Time is Between Starting and Ending Time
                            // To Execute the Changing
                            if (EnableDate.getTime() <= System.currentTimeMillis() && System.currentTimeMillis() <= DisableDate.getTime()) {
                                Execute();

                            } else if (System.currentTimeMillis() > DisableDate.getTime()) { // If The Current Time is Greater Than Ending Time The Task Will Be Disabled
                                // Disable The Task
                                isDisabled = true;

                                // Set In The DataBase This Task Has Been Disabled
                                PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                                ps.setBoolean(1, isDisabled);
                                ps.setInt(2, TaskID);
                                ps.executeUpdate();

                                // Write It In The Log DataBase
                                SLogger.Logger("DisabledTask", TaskName, Room, Sensor, List, -1);
                            }
                        } else { // if the Task Will Be Work 24 Hour
                            // Just Execute the Changing
                            Execute();
                        }
                    } else if (CDate.after(ActionDate)) { // If The Task Day Has Gone The Task Will Be Disabled
                        // Disable The Task
                        isDisabled = true;

                        // Set In The DataBase This Task Has Been Disabled
                        PreparedStatement ps = DB.prepareStatement("update task set isDisabled = ? where TaskID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                        ps.setBoolean(1, isDisabled);
                        ps.setInt(2, TaskID);
                        ps.executeUpdate();

                        // Write It In The Log DataBase
                        SLogger.Logger("DisabledTask", TaskName, Room, Sensor, List, -1);
                    }
                }

                // To Sleep For 1 Second
                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                // This Catch For DataBase Error
                FileLogger.AddWarning("ActionOnWaterLevelTask " + TaskID + ", Error In DataBase\n" + ex);
            }
        }
    }
}
