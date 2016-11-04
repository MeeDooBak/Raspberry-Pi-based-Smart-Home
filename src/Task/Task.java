package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class Task extends Thread {

    private final Connection DB;
    private final ArrayList<TaskList> TaskList;
    private final Sensor Sensors;
    private final Device Devices;

    public Task(Connection DB, ArrayList<TaskList> TaskList, Sensor Sensors, Device Devices) {
        this.DB = DB;
        this.TaskList = TaskList;
        this.Sensors = Sensors;
        this.Devices = Devices;
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
            while (true) {
                Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select * from task");

                Result.beforeFirst();
                while (Result.next()) {

                    int TaskID = Result.getInt("TaskID");
                    int UserID = Result.getInt("UserID");
                    int RoomID = Result.getInt("RoomID");
                    int SensorID = Result.getInt("SensorID");
                    boolean isDisabled = Result.getBoolean("isDisabled");
                    String TaskName = Result.getString("TaskName");
                    Time ActionTime = Result.getTime("ActionTime");
                    boolean repeatDaily = Result.getBoolean("repeatDaily");
                    Date ActionDate = Result.getDate("ActionDate");
                    int AlarmDuration = Result.getInt("AlarmDuration");
                    int AlarmInterval = Result.getInt("AlarmInterval");
                    int SelectedSensorValue = Result.getInt("SelectedSensorValue");

                    int index = indexof(TaskID);
                    if (index > -1) {
                        boolean isChange = false;

                        if (!TaskList.get(index).getTaskName().equals(TaskName)) {
                            TaskList.get(index).setTaskName(TaskName);
                            isChange = true;
                        }

                        if (!TaskList.get(index).getActionTime().equals(ActionTime)) {
                            TaskList.get(index).setActionTime(ActionTime);
                            isChange = true;
                        }

                        if (TaskList.get(index).isRepeatDaily() != repeatDaily) {
                            TaskList.get(index).setRepeatDaily(repeatDaily);
                            isChange = true;
                        }

                        if (!TaskList.get(index).getActionDate().equals(ActionDate)) {
                            TaskList.get(index).setActionDate(ActionDate);
                            isChange = true;
                        }

                        if (TaskList.get(index).getAlarmDuration() != AlarmDuration) {
                            TaskList.get(index).setAlarmDuration(AlarmDuration);
                            isChange = true;
                        }

                        if (TaskList.get(index).getAlarmInterval() != AlarmInterval) {
                            TaskList.get(index).setAlarmInterval(AlarmInterval);
                            isChange = true;
                        }

                        if (TaskList.get(index).getSelectedSensorValue() != SelectedSensorValue) {
                            TaskList.get(index).setSelectedSensorValue(SelectedSensorValue);
                            isChange = true;
                        }

                        Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result2 = Statement2.executeQuery("select * from task_devices where TaskID = " + TaskID);
                        Result2.next();

                        Map<DeviceList, Boolean> NewList = new HashMap();
                        while (Result2.next()) {
                            NewList.put(Devices.Get(Result2.getInt("GateNum1")), Result2.getBoolean("DeviceID"));
                        }
                        Result2.close();
                        Statement2.close();

                        Map<DeviceList, Boolean> OldList = TaskList.get(index).getList();

                        boolean Exit = false;
                        for (Map.Entry<DeviceList, Boolean> NewDevice : NewList.entrySet()) {
                            for (Map.Entry<DeviceList, Boolean> OldDevice : OldList.entrySet()) {
                                if (NewDevice.getKey().getDeviceID() != OldDevice.getKey().getDeviceID()) {
                                    TaskList.get(index).setList(NewList);
                                    isChange = true;
                                    Exit = true;
                                    break;
                                }
                            }
                            if (Exit) {
                                break;
                            }
                        }

                        if (TaskList.get(index).getSensor().getSensorID() != Sensors.Get(SensorID).getSensorID()) {
                            TaskList.get(index).setSensor(Sensors.Get(SensorID));
                            isChange = true;
                        }

                        if (TaskList.get(index).isIsDisabled() != isDisabled) {
                            isChange = true;
                        }

                        if (isChange) {
                            TaskList.get(index).setIsDisabled(isDisabled);
                        }
                    } else {
                        Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result2 = Statement2.executeQuery("select * from task_devices where TaskID = " + TaskID);
                        Result2.next();

                        Map<DeviceList, Boolean> List = new HashMap();
                        while (Result2.next()) {
                            List.put(Devices.Get(Result2.getInt("GateNum1")), Result2.getBoolean("DeviceID"));
                        }
                        Result2.close();
                        Statement2.close();

                        SensorList Sensor = Sensors.Get(SensorID);

                        TaskList.add(new TaskList(TaskID, UserID, RoomID, isDisabled, TaskName, ActionTime, repeatDaily, ActionDate, AlarmDuration,
                                AlarmInterval, SelectedSensorValue, Sensor, List, DB));
                    }
                }
                Result.close();
                Statement.close();
                Thread.sleep(1000);
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
