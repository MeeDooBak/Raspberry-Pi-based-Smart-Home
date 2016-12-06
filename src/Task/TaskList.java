package Task;

import Device.*;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.Map;

public final class TaskList {

    private final int TaskID;
    private final SensorList Sensor;
    private final int SelectedSensorValue;

    private ActionOnDetectionTask ActionOnDetection;
    private ActionAfterNoDetectionTask ActionAfterNoDetection;
    private SmokeTask SmokeDetector;
    private TemperatureTask Temperature;
    private TimingTask Timing;
    private LightTask Light;
    private ActionOnWaterLevelTask ActionOnWaterLevel;

    public TaskList(int TaskID, String TaskName, UserList User, RoomList Room, SensorList SmokeSensor, boolean isDisabled, boolean repeatDaily,
            int AlarmDuration, int AlarmInterval, SensorList Sensor, Map<DeviceList, Boolean> List, int SelectedSensorValue, boolean NotifyByEmail,
            Date ActionDate, Time ActionTime, Time EnableTaskOnTime, Time DisableTaskOnTime, Connection DB) {

        this.TaskID = TaskID;
        this.Sensor = Sensor;
        this.SelectedSensorValue = SelectedSensorValue;

        switch (Sensor.getSensorName()) {
            case "Motion Sensor":
                if (SelectedSensorValue == 0) {
                    ActionOnDetection = new ActionOnDetectionTask(TaskID, TaskName, User, Room, SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval,
                            Sensor, List, NotifyByEmail, ActionDate, EnableTaskOnTime, DisableTaskOnTime, DB);
                } else if (SelectedSensorValue > 0) {
                    ActionAfterNoDetection = new ActionAfterNoDetectionTask(TaskID, TaskName, User, Room, SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval,
                            Sensor, List, SelectedSensorValue, NotifyByEmail, ActionDate, EnableTaskOnTime, DisableTaskOnTime, DB);
                }
                break;
            case "Smoke Detector":
                SmokeDetector = new SmokeTask(TaskID, TaskName, User, Room, isDisabled, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List, NotifyByEmail, ActionDate,
                        EnableTaskOnTime, DisableTaskOnTime, DB);
                break;
            case "Temperature Sensor":
                Temperature = new TemperatureTask(TaskID, TaskName, User, Room, SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List,
                        SelectedSensorValue, NotifyByEmail, ActionDate, EnableTaskOnTime, DisableTaskOnTime, DB);
                break;
            case "Light Sensor":
                Light = new LightTask(TaskID, TaskName, User, Room, SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List, SelectedSensorValue,
                        NotifyByEmail, ActionDate, EnableTaskOnTime, DisableTaskOnTime, DB);
                break;
            case "Ultrasonic":
                ActionOnWaterLevel = new ActionOnWaterLevelTask(TaskID, TaskName, User, Room, SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval, Sensor, List,
                        SelectedSensorValue, NotifyByEmail, ActionDate, EnableTaskOnTime, DisableTaskOnTime, DB);
                break;
            case "Clock":
                Timing = new TimingTask(TaskID, TaskName, User, Room, SmokeSensor, isDisabled, repeatDaily, AlarmDuration, AlarmInterval, ActionTime, Sensor, List, NotifyByEmail,
                        ActionDate, EnableTaskOnTime, DisableTaskOnTime, DB);
                break;
            default:
                break;
        }
    }

    public boolean DeleteTask() {
        switch (Sensor.getSensorName()) {
            case "Motion Sensor":
                if (SelectedSensorValue == 0) {
                    return ActionOnDetection.setisDisabled(true);
                } else if (SelectedSensorValue > 0) {
                    return ActionAfterNoDetection.setisDisabled(true);
                } else {
                    return false;
                }
            case "Smoke Detector":
                return SmokeDetector.setisDisabled(true);
            case "Temperature Sensor":
                return Temperature.setisDisabled(true);
            case "Light Sensor":
                return Light.setisDisabled(true);
            case "Ultrasonic":
                return ActionOnWaterLevel.setisDisabled(true);
            case "Clock":
                return Timing.setisDisabled(true);
            default:
                return false;
        }
    }

    public int getTaskID() {
        return TaskID;
    }
}
