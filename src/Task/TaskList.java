package Task;

import Device.DeviceList;
import Sensor.SensorList;
import java.sql.*;
import java.util.Map;

public class TaskList {

    private boolean isDisabled;
    private String TaskName;
    private Time ActionTime;
    private boolean repeatDaily;
    private Date ActionDate;
    private int AlarmDuration;
    private int AlarmInterval;
    private int SelectedSensorValue;

    private SensorList Sensor;
    private Map<DeviceList, Boolean> List;

    private final int TaskID;
    private final int UserID;
    private final int RoomID;
    private final Connection DB;

    private ActionOnDetectionThread ActionOnDetectionThread;

    public TaskList(int TaskID, int UserID, int RoomID, boolean isDisabled, String TaskName, Time ActionTime, boolean repeatDaily, Date ActionDate, int AlarmDuration,
            int AlarmInterval, int SelectedSensorValue, SensorList Sensor, Map<DeviceList, Boolean> List, Connection DB) {

        this.TaskID = TaskID;
        this.UserID = UserID;
        this.RoomID = RoomID;
        this.isDisabled = isDisabled;
        this.TaskName = TaskName;
        this.ActionTime = ActionTime;
        this.repeatDaily = repeatDaily;
        this.ActionDate = ActionDate;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
        this.SelectedSensorValue = SelectedSensorValue;
        this.Sensor = Sensor;
        this.List = List;
        this.DB = DB;

        if (ActionTime == null && SelectedSensorValue == 0) {
            ActionOnDetectionThread = new ActionOnDetectionThread(TaskID, isDisabled, ActionDate, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List, DB);
        }
    }

    public int getTaskID() {
        return TaskID;
    }

    public int getUserID() {
        return UserID;
    }

    public int getRoomID() {
        return RoomID;
    }

    public boolean isIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        if (ActionTime == null && SelectedSensorValue == 0) {
            ActionOnDetectionThread.setIsDisabled(false);
            ActionOnDetectionThread = new ActionOnDetectionThread(TaskID, isDisabled, ActionDate, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List, DB);
        }
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String TaskName) {
        this.TaskName = TaskName;
    }

    public Time getActionTime() {
        return ActionTime;
    }

    public void setActionTime(Time ActionTime) {
        this.ActionTime = ActionTime;
    }

    public boolean isRepeatDaily() {
        return repeatDaily;
    }

    public void setRepeatDaily(boolean repeatDaily) {
        this.repeatDaily = repeatDaily;
    }

    public Date getActionDate() {
        return ActionDate;
    }

    public void setActionDate(Date ActionDate) {
        this.ActionDate = ActionDate;
    }

    public int getAlarmDuration() {
        return AlarmDuration;
    }

    public void setAlarmDuration(int AlarmDuration) {
        this.AlarmDuration = AlarmDuration;
    }

    public int getAlarmInterval() {
        return AlarmInterval;
    }

    public void setAlarmInterval(int AlarmInterval) {
        this.AlarmInterval = AlarmInterval;
    }

    public int getSelectedSensorValue() {
        return SelectedSensorValue;
    }

    public void setSelectedSensorValue(int SelectedSensorValue) {
        this.SelectedSensorValue = SelectedSensorValue;
    }

    public SensorList getSensor() {
        return Sensor;
    }

    public void setSensor(SensorList Sensor) {
        this.Sensor = Sensor;
    }

    public Map<DeviceList, Boolean> getList() {
        return List;
    }

    public void setList(Map<DeviceList, Boolean> List) {
        this.List = List;
    }
}
