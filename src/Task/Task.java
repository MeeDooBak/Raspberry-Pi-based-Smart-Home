package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class Task {

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

    public void Start() {
        try {
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from task")) {

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
                    boolean NotifyByEmail = Result.getBoolean("NotifyByEmail");
                    Time EnableTaskOnTime = Result.getTime("EnableTaskOnTime");
                    Time DisableTaskOnTime = Result.getTime("DisableTaskOnTime");

                    int index = indexof(TaskID);
                    if (index > -1) {
                        if (isChange(index, TaskID, SensorID, isDisabled, TaskName, ActionTime, repeatDaily, ActionDate, AlarmDuration, AlarmInterval,
                                SelectedSensorValue, NotifyByEmail, EnableTaskOnTime, DisableTaskOnTime)) {
                            TaskList.get(index).setIsDisabled(isDisabled);
                            System.out.println("Task ID :" + TaskID + " Changed ");
                        }
                    } else {
                        Map<DeviceList, Boolean> List = getDevices(TaskID);
                        SensorList Sensor = Sensors.Get(SensorID);

                        TaskList.add(new TaskList(TaskID, UserID, RoomID, isDisabled, TaskName, ActionTime, repeatDaily, ActionDate, AlarmDuration,
                                AlarmInterval, SelectedSensorValue, Sensor, List, DB, NotifyByEmail, EnableTaskOnTime, DisableTaskOnTime));
                        System.out.println("Add Task " + TaskID + " ");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isChange(int index, int TaskID, int SensorID, boolean isDisabled, String TaskName, Time ActionTime, boolean repeatDaily, Date ActionDate,
            int AlarmDuration, int AlarmInterval, int SelectedSensorValue, boolean NotifyByEmail, Time EnableTaskOnTime, Time DisableTaskOnTime) {

        boolean isChange = false;

        if (!TaskList.get(index).getTaskName().equals(TaskName)) {
            TaskList.get(index).setTaskName(TaskName);
            isChange = true;
        }

        if (Sensors.Get(SensorID).getSensorName().equals("Clock")) {
            if (!TaskList.get(index).getActionTime().equals(ActionTime)) {
                TaskList.get(index).setActionTime(ActionTime);
                isChange = true;
            }
        }

        if (TaskList.get(index).isRepeatDaily() != repeatDaily) {
            TaskList.get(index).setRepeatDaily(repeatDaily);
            isChange = true;
        }

        if (!repeatDaily) {
            if (TaskList.get(index).getActionDate() == null) {
                TaskList.get(index).setActionDate(ActionDate);
                isChange = true;
            } else if (!TaskList.get(index).getActionDate().equals(ActionDate)) {
                TaskList.get(index).setActionDate(ActionDate);
                isChange = true;
            }
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

        if (TaskList.get(index).isNotifyByEmail() != NotifyByEmail) {
            TaskList.get(index).setNotifyByEmail(NotifyByEmail);
            isChange = true;
        }

        if (TaskList.get(index).getEnableTaskOnTime() != null && EnableTaskOnTime == null) {
            TaskList.get(index).setEnableTaskOnTime(EnableTaskOnTime);
            isChange = true;
        } else if (TaskList.get(index).getEnableTaskOnTime() == null && EnableTaskOnTime != null) {
            TaskList.get(index).setEnableTaskOnTime(EnableTaskOnTime);
            isChange = true;
        } else if (TaskList.get(index).getEnableTaskOnTime() != null && EnableTaskOnTime != null) {
            if (!TaskList.get(index).getEnableTaskOnTime().equals(EnableTaskOnTime)) {
                TaskList.get(index).setEnableTaskOnTime(EnableTaskOnTime);
                isChange = true;
            }
        }

        if (TaskList.get(index).getDisableTaskOnTime() != null && DisableTaskOnTime == null) {
            TaskList.get(index).setDisableTaskOnTime(DisableTaskOnTime);
            isChange = true;
        } else if (TaskList.get(index).getDisableTaskOnTime() == null && DisableTaskOnTime != null) {
            TaskList.get(index).setDisableTaskOnTime(DisableTaskOnTime);
            isChange = true;
        } else if (TaskList.get(index).getDisableTaskOnTime() != null && DisableTaskOnTime != null) {
            if (!TaskList.get(index).getDisableTaskOnTime().equals(DisableTaskOnTime)) {
                TaskList.get(index).setDisableTaskOnTime(DisableTaskOnTime);
                isChange = true;
            }
        }

        Map<DeviceList, Boolean> NewList = getDevices(TaskID);
        Map<DeviceList, Boolean> OldList = TaskList.get(index).getList();
        NewList.equals(OldList);

        boolean Found = false;
        for (Map.Entry<DeviceList, Boolean> NewDevice : NewList.entrySet()) {
            Found = false;
            for (Map.Entry<DeviceList, Boolean> OldDevice : OldList.entrySet()) {
                if ((NewDevice.getKey().getDeviceID() == OldDevice.getKey().getDeviceID()) && NewDevice.getValue().booleanValue() == OldDevice.getValue().booleanValue()) {
                    Found = true;
                    break;
                }
            }
            if (!Found) {
                break;
            }
        }

        if (!Found) {
            TaskList.get(index).setList(NewList);
            isChange = true;
        }

        if (TaskList.get(index).getSensor().getSensorID() != Sensors.Get(SensorID).getSensorID()) {
            TaskList.get(index).setSensor(Sensors.Get(SensorID));
            isChange = true;
        }

        if (TaskList.get(index).isIsDisabled() != isDisabled) {
            isChange = true;
        }
        return isChange;
    }

    public Map<DeviceList, Boolean> getDevices(int TaskID) {
        Map<DeviceList, Boolean> List = null;
        try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result2 = Statement2.executeQuery("select * from task_devices where TaskID = " + TaskID)) {
            List = new HashMap();
            while (Result2.next()) {
                if (Result2.getInt("RequiredDeviceStatus") == 1) {
                    List.put(Devices.Get(Result2.getInt("DeviceID")), true);
                } else if (Result2.getInt("RequiredDeviceStatus") == 0) {
                    List.put(Devices.Get(Result2.getInt("DeviceID")), false);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
        }
        return List;
    }
}
