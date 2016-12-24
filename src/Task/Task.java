package Task;

import Device.*;
import Logger.*;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.ArrayList;

public class Task implements Runnable {

    private final Connection DB;
    private final ArrayList<TaskList> TaskList;
    private final Device Devices;
    private final Sensor Sensors;
    private final Room Room;
    private final User User;
    private final SensorList SmokeSensor;

    // Get Infromation from Main Class 
    public Task(Connection DB, ArrayList<TaskList> TaskList, Sensor Sensors, Device Devices, Room Room, User User, SensorList SmokeSensor) {
        this.DB = DB;
        this.TaskList = TaskList;
        this.Sensors = Sensors;
        this.Devices = Devices;
        this.Room = Room;
        this.User = User;
        this.SmokeSensor = SmokeSensor;
    }

    // Search and return ArrayList index if the specific Task exists by ID
    public int indexof(int TaskID) {
        for (int i = 0; i < TaskList.size(); i++) {
            if (TaskList.get(i).getTaskID() == TaskID) {
                return i;
            }
        }
        return -1;
    }

    // The Thread
    @Override
    public void run() {
        try {
            // Start Get Task Information From The Database
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from task")) {

                // Delete The Task and Loop Until ArrayList To Be Empty
                while (!TaskList.isEmpty()) {
                    // Loop Until The Task Securely Delete
                    while (!TaskList.get(0).DeleteTask()) {
                    }
                    // Delete The Task Class
                    TaskList.remove(0);
                }

                Result.beforeFirst();
                // While Loop For All Row in DataBase
                while (Result.next()) {

                    // Get Task ID
                    int TaskID = Result.getInt("TaskID");
                    // Get Task User ID
                    int UserID = Result.getInt("UserID");
                    // Get Task Room ID
                    int RoomID = Result.getInt("RoomID");
                    // Get Task Sensor ID
                    int SensorID = Result.getInt("SensorID");
                    // Get is Task Deleted To Delete it
                    boolean isDeleted = Result.getBoolean("isDeleted");
                    // Get is Task Disabled To Ignore it
                    boolean isDisabled = Result.getBoolean("isDisabled");
                    // Get Task Name
                    String TaskName = Result.getString("TaskName");
                    // Get Task Action Time
                    Time ActionTime = Result.getTime("ActionTime");
                    // Get is Task Repeat Daily
                    boolean repeatDaily = Result.getBoolean("repeatDaily");
                    // Get Task Action Date
                    Date ActionDate = Result.getDate("ActionDate");
                    // Get Task Alarm Duration
                    int AlarmDuration = Result.getInt("AlarmDuration");
                    // Get Task Alarm Interval
                    int AlarmInterval = Result.getInt("AlarmInterval");
                    // Get Task Selected Sensor Value
                    int SelectedSensorValue = Result.getInt("SelectedSensorValue");
                    // Get is Task Notify By Email
                    boolean NotifyByEmail = Result.getBoolean("NotifyByEmail");
                    // Get Enable Task On Time
                    Time EnableTaskOnTime = Result.getTime("EnableTaskOnTime");
                    // Get Disable Task On Time
                    Time DisableTaskOnTime = Result.getTime("DisableTaskOnTime");

                    // The Task is Delete
                    if (isDeleted) {
                        // Delete The Camera Task From The Table " task_camera "
                        PreparedStatement preparedStmt1 = DB.prepareStatement("delete from task_camera where TaskID = ?");
                        preparedStmt1.setInt(1, TaskID);
                        preparedStmt1.execute();

                        // Delete The Device Task From The Table " task_devices "
                        PreparedStatement preparedStmt2 = DB.prepareStatement("delete from task_devices where TaskID = ?");
                        preparedStmt2.setInt(1, TaskID);
                        preparedStmt2.execute();

                        // Delete The Task From The DataBase
                        Result.deleteRow();

                        // just To Print the Result
                        FileLogger.AddInfo("Delete Task : " + TaskID + ", with Name : " + TaskName);

                    } else { // If The Task Not Delete
                        // Check if THe Task Disabled To Ignore it
                        if (!isDisabled) {

                            UserList NewUserList = User.Get(UserID);
                            if (User.Get(UserID).getDescription().equals("Admin")) {
                                NewUserList = User.Get("Father");
                            }

                            // Create and add To the ArrayList the Task Class
                            TaskList.add(new TaskList(TaskID, TaskName, NewUserList, Room.Get(RoomID), SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval,
                                    Sensors.Get(SensorID), getDevices(TaskID), SelectedSensorValue, NotifyByEmail, ActionDate, ActionTime, EnableTaskOnTime, DisableTaskOnTime, DB));

                            // just To Print the Result
                            FileLogger.AddInfo("Add Task : " + TaskID + ", with Name : " + TaskName);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Task Class, Error In DataBase\n" + ex);
        }
    }

    // This Method To Get The Device For Specific Task
    public ArrayList<TaskDevicesList> getDevices(int TaskID) {
        // Create ArrayList To Store The Devices List
        ArrayList<TaskDevicesList> List = new ArrayList();

        // Start Get Information From The Database about the Devices
        try (Statement Statement1 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result1 = Statement1.executeQuery("select * from task_devices where TaskID = " + TaskID)) {

            // While Loop For All Row in DataBase According To The Task ID
            while (Result1.next()) {

                // Check the User Want The Device State To Be True
                if (Result1.getInt("RequiredDeviceStatus") == 1) {
                    // add To the ArrayList the Task Devices List Class
                    List.add(new TaskDevicesList(Devices.Get(Result1.getInt("DeviceID")), true, -1, -1));

                } else if (Result1.getInt("RequiredDeviceStatus") == 0) { // if the User Want The Device State To Be False
                    // add To the ArrayList the Task Devices List Class
                    List.add(new TaskDevicesList(Devices.Get(Result1.getInt("DeviceID")), false, -1, -1));
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Task Class, Error In DataBase\n" + ex);
        }

        // Start Get Information From The Database about the Camera
        try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result2 = Statement2.executeQuery("select * from task_camera where TaskID = " + TaskID)) {

            // While Loop For All Row in DataBase According To The Task ID
            while (Result2.next()) {

                // Check the User Want The Camera State To Be True
                if (Result2.getInt("RequiredDeviceStatus") == 1) {
                    // add To the ArrayList the Task Devices List Class
                    List.add(new TaskDevicesList(Devices.Get(Result2.getInt("DeviceID")), true, Result2.getInt("TakeImage"), Result2.getInt("TakeVideo")));
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Task Class, Error In DataBase\n" + ex);
        }

        // Return The List
        return List;
    }
}
