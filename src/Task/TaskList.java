package Task;

import Device.DeviceList;
import Sensor.SensorList;
import java.sql.*;
import java.util.Map;

public final class TaskList {

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
    private boolean NotifyByEmail;
    private Time EnableTaskOnTime;
    private Time DisableTaskOnTime;

    private final int TaskID;
    private final int UserID;
    private final int RoomID;
    private final Connection DB;

    private ActionOnDetection_Task ActionOnDetection;
    private ActionAfterNoDetection_Task ActionAfterNoDetection;
    private Smoke_Task SmokeDetector;
    private Temperature_Task Temperature;
    private Timing_Task Timing;
    private Light_Task Light;
    private ActionOnWaterLevel_Thread ActionOnWaterLevel;

    public TaskList(int TaskID, int UserID, int RoomID, boolean isDisabled, String TaskName, Time ActionTime, boolean repeatDaily,
            Date ActionDate, int AlarmDuration, int AlarmInterval, int SelectedSensorValue, SensorList Sensor, Map<DeviceList, Boolean> List,
            Connection DB, boolean NotifyByEmail, Time EnableTaskOnTime, Time DisableTaskOnTime) {

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
        this.NotifyByEmail = NotifyByEmail;
        this.EnableTaskOnTime = EnableTaskOnTime;
        this.DisableTaskOnTime = DisableTaskOnTime;
        Start();
    }

    public void Start() {
        if (Sensor.getSensorName().equals("Motion Sensor") && SelectedSensorValue == 0) {
            ActionOnDetection = new ActionOnDetection_Task(TaskID, isDisabled, ActionDate, repeatDaily, AlarmDuration, AlarmInterval,
                    Sensor, List, DB, NotifyByEmail, EnableTaskOnTime, getDisableTaskOnTime());
            ActionOnDetection.start();

        } else if (Sensor.getSensorName().equals("Motion Sensor") && SelectedSensorValue > 0) {
            ActionAfterNoDetection = new ActionAfterNoDetection_Task(TaskID, isDisabled, ActionDate, repeatDaily, AlarmDuration, AlarmInterval,
                    SelectedSensorValue, Sensor, List, DB, NotifyByEmail, EnableTaskOnTime, DisableTaskOnTime);
            ActionAfterNoDetection.start();

        } else if (Sensor.getSensorName().equals("Smoke Detector")) {
            SmokeDetector = new Smoke_Task(TaskID, isDisabled, ActionDate, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List, DB,
                    NotifyByEmail, EnableTaskOnTime, DisableTaskOnTime);
            SmokeDetector.start();

        } else if (Sensor.getSensorName().equals("Temperature Sensor")) {
            Temperature = new Temperature_Task(TaskID, isDisabled, ActionDate, repeatDaily, AlarmDuration, AlarmInterval, SelectedSensorValue,
                    Sensor, List, DB, NotifyByEmail, EnableTaskOnTime, DisableTaskOnTime);
            Temperature.start();

        } else if (Sensor.getSensorName().equals("Light Sensor")) {
            Light = new Light_Task(TaskID, isDisabled, ActionDate, repeatDaily, AlarmDuration, AlarmInterval, SelectedSensorValue, Sensor, List, DB, NotifyByEmail,
                    EnableTaskOnTime, DisableTaskOnTime);
            Light.start();

        } else if (Sensor.getSensorName().equals("Ultrasonic")) {
            ActionOnWaterLevel = null;

        } else if (Sensor.getSensorName().equals("Clock")) {
            Timing = new Timing_Task(TaskID, isDisabled, ActionDate, ActionTime, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List,
                    DB, NotifyByEmail, EnableTaskOnTime, DisableTaskOnTime);
            Timing.start();
        }
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        if (Sensor.getSensorName().equals("Motion Sensor") && SelectedSensorValue == 0) {
            ActionOnDetection.setIsDisabled(isDisabled);

        } else if (Sensor.getSensorName().equals("Motion Sensor") && SelectedSensorValue > 0) {
            ActionAfterNoDetection.setIsDisabled(isDisabled);

        } else if (Sensor.getSensorName().equals("Smoke Detector")) {
            SmokeDetector.setIsDisabled(isDisabled);

        } else if (Sensor.getSensorName().equals("Temperature Sensor")) {
            Temperature.setIsDisabled(isDisabled);

        } else if (Sensor.getSensorName().equals("Light Sensor")) {
            Light.setIsDisabled(isDisabled);

        } else if (Sensor.getSensorName().equals("Ultrasonic")) {
            ActionOnWaterLevel = null;

        } else if (Sensor.getSensorName().equals("Clock")) {
            Timing.setIsDisabled(isDisabled);
        }
        Start();
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

    public boolean isNotifyByEmail() {
        return NotifyByEmail;
    }

    public void setNotifyByEmail(boolean NotifyByEmail) {
        this.NotifyByEmail = NotifyByEmail;
    }

    public Time getEnableTaskOnTime() {
        return EnableTaskOnTime;
    }

    public void setEnableTaskOnTime(Time EnableTaskOnTime) {
        this.EnableTaskOnTime = EnableTaskOnTime;
    }

    public Time getDisableTaskOnTime() {
        return DisableTaskOnTime;
    }

    public void setDisableTaskOnTime(Time DisableTaskOnTime) {
        this.DisableTaskOnTime = DisableTaskOnTime;
    }
}
