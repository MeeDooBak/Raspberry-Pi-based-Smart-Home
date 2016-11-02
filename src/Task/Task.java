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
    private final ArrayList<SensorList> SensorList;
    private final ArrayList<DeviceList> DeviceList;

    public Task(Connection DB, ArrayList<TaskList> TaskList, ArrayList<SensorList> SensorList, ArrayList<DeviceList> DeviceList) {
        this.DB = DB;
        this.TaskList = TaskList;
        this.SensorList = SensorList;
        this.DeviceList = DeviceList;
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

                        ResultSet Result2 = Statement.executeQuery("select * from task_devices where TaskID = " + TaskID);
                        Result2.next();

                        Map<DeviceList, Boolean> NewList = new HashMap();
                        while (Result2.next()) {
                            NewList.put(DeviceList.get(Result2.getInt("GateNum1")), Result2.getBoolean("DeviceID"));
                        }

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

                        if (TaskList.get(index).getSensor().getSensorID() != SensorList.get(SensorID).getSensorID()) {
                            TaskList.get(index).setSensor(SensorList.get(SensorID));
                            isChange = true;
                        }

                        if (TaskList.get(index).isIsDisabled() != isDisabled) {
                            isChange = true;
                        }

                        if (isChange) {
                            TaskList.get(index).setIsDisabled(isDisabled);
                        }

                    } else {
                        ResultSet Result2 = Statement.executeQuery("select * from task_devices where TaskID = " + TaskID);
                        Result2.next();

                        Map<DeviceList, Boolean> List = new HashMap();
                        while (Result2.next()) {
                            List.put(DeviceList.get(Result2.getInt("GateNum1")), Result2.getBoolean("DeviceID"));
                        }
                        SensorList Sensor = SensorList.get(SensorID);

                        TaskList.add(new TaskList(TaskID, UserID, RoomID, isDisabled, TaskName, ActionTime, repeatDaily, ActionDate, AlarmDuration,
                                AlarmInterval, SelectedSensorValue, Sensor, List, DB));
                    }
                }
                Thread.sleep(1000);
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
