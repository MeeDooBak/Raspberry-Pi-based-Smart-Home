package Task;

import Device.*;
import Sensor.*;
import java.sql.*;
import java.util.*;

public class ActionOnWaterLevel extends Thread {

    private boolean isDisabled;

    private final java.sql.Date ActionDate;
    private final boolean repeatDaily;
    private final int TaskID;
    private final Connection DB;
    private final SensorList Sensor;
    private final Map<DeviceList, Boolean> List;

    public ActionOnWaterLevel(int TaskID, boolean isDisabled, java.sql.Date ActionDate, boolean repeatDaily,
            SensorList Sensor, Map<DeviceList, Boolean> List, Connection DB) {

        this.TaskID = TaskID;
        this.isDisabled = isDisabled;
        this.ActionDate = ActionDate;
        this.repeatDaily = repeatDaily;
        this.Sensor = Sensor;
        this.List = List;
        this.DB = DB;
    }
}
