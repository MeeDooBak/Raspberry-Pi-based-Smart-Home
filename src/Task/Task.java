package Task;

import Device.*;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;

public class Task implements Runnable {

    private final Connection DB;
    private final ArrayList<TaskList> TaskList;
    private final Device Devices;
    private final Sensor Sensors;
    private final Room Room;
    private final User User;
    private final SensorList SmokeSensor;

    public Task(Connection DB, ArrayList<TaskList> TaskList, Sensor Sensors, Device Devices, Room Room, User User, SensorList SmokeSensor) {
        this.DB = DB;
        this.TaskList = TaskList;
        this.Sensors = Sensors;
        this.Devices = Devices;
        this.Room = Room;
        this.User = User;
        this.SmokeSensor = SmokeSensor;
    }

    public int indexof(int TaskID) {
        for (int i = 0; i < TaskList.size(); i++) {
            if (TaskList.get(i).getTaskID() == TaskID) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void run() {
        try {
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from task")) {

                while (!TaskList.isEmpty()) {
                    while (!TaskList.get(0).DeleteTask()) {
                    }
                    TaskList.remove(0);
                }

                Result.beforeFirst();
                while (Result.next()) {

                    int TaskID = Result.getInt("TaskID");
                    int UserID = Result.getInt("UserID");
                    int RoomID = Result.getInt("RoomID");
                    int SensorID = Result.getInt("SensorID");
                    boolean isDeleted = Result.getBoolean("isDeleted");
                    boolean isDisabled = Result.getBoolean("isDisabled");
                    String TaskName = Result.getString("TaskName");
                    Time ActionTime = Result.getTime("ActionTime");
                    boolean repeatDaily = Result.getBoolean("repeatDaily");
                    Date ActionDate = Result.getDate("ActionDate");
                    int AlarmDuration = Result.getInt("AlarmDuration");
                    int AlarmInterval = Result.getInt("AlarmInterval");
                    int SelectedSensorValue = Result.getInt("SelectedSensorValue");
                    boolean NotifyByEmail = Result.getBoolean("NotifyByEmail");
                    Time EnableTaskOnTime = Result.getTime("EnableTaskOnTime");
                    Time DisableTaskOnTime = Result.getTime("DisableTaskOnTime");

                    if (isDeleted) {
                        PreparedStatement preparedStmt1 = DB.prepareStatement("delete from task_camera where TaskID = ?");
                        preparedStmt1.setInt(1, TaskID);
                        preparedStmt1.execute();

                        PreparedStatement preparedStmt2 = DB.prepareStatement("delete from task_devices where TaskID = ?");
                        preparedStmt2.setInt(1, TaskID);
                        preparedStmt2.execute();

                        Result.deleteRow();
                        System.out.println("Delete Task : " + TaskID + ", with Name : " + TaskName);

                    } else {
                        if (!isDisabled) {
                            TaskList.add(new TaskList(TaskID, TaskName, User.Get(UserID), Room.Get(RoomID), SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval,
                                    Sensors.Get(SensorID), getDevices(TaskID), SelectedSensorValue, NotifyByEmail, ActionDate, ActionTime, EnableTaskOnTime, DisableTaskOnTime, DB));

                            System.out.println("Add Task : " + TaskID + ", with Name : " + TaskName);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<TaskDevicesList> getDevices(int TaskID) {
        ArrayList<TaskDevicesList> List = new ArrayList();
        try (Statement Statement1 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result1 = Statement1.executeQuery("select * from task_devices where TaskID = " + TaskID)) {
            while (Result1.next()) {
                if (Result1.getInt("RequiredDeviceStatus") == 1) {
                    List.add(new TaskDevicesList(Devices.Get(Result1.getInt("DeviceID")), true, -1, -1));
                } else if (Result1.getInt("RequiredDeviceStatus") == 0) {
                    List.add(new TaskDevicesList(Devices.Get(Result1.getInt("DeviceID")), false, -1, -1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result2 = Statement2.executeQuery("select * from task_camera where TaskID = " + TaskID)) {
            while (Result2.next()) {
                if (Result2.getInt("RequiredDeviceStatus") == 1) {
                    List.add(new TaskDevicesList(Devices.Get(Result2.getInt("DeviceID")), true, Result2.getInt("TakeImage"), Result2.getInt("TakeVideo")));
                } else if (Result2.getInt("RequiredDeviceStatus") == 0) {
                    List.add(new TaskDevicesList(Devices.Get(Result2.getInt("DeviceID")), false, Result2.getInt("TakeImage"), Result2.getInt("TakeVideo")));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
        return List;
    }
}
