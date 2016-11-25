package Device;

import Pins.PinsList;
import Rooms.*;
import java.sql.*;

public final class DeviceList {

    private boolean DeviceState;
    private boolean isStatusChanged;
    private int StepperMotorMoves;
    private int AlarmDuration;
    private int AlarmInterval;

    private final int DeviceID;
    private final RoomList Room;
    private final String DeviceName;
    private final PinsList GateNum1;
    private final PinsList GateNum2;
    private final PinsList GateNum3;
    private final PinsList GateNum4;
    private final Connection DB;
    private final Relay command;
    private Timestamp lastStatusChange;
    private final int MaxValue;

    private RoofLamp_Thread RoofLamp;
    private AC_Thread AC;
    private Curtains_Thread Curtains;
    private Alarm_Thread Alarm;
    private GarageDoor_Thread GarageDoor;
    private SecurityCamera_Thread SecurityCamera;
    private WaterPump_Thread WaterPump;

    public DeviceList(int DeviceID, RoomList Room, String DeviceName, boolean DeviceState, PinsList GateNum1, PinsList GateNum2, PinsList GateNum3,
            PinsList GateNum4, boolean isStatusChanged, int StepperMotorMoves, int AlarmDuration, int AlarmInterval, Connection DB, Relay command,
            Timestamp lastStatusChange, int MaxValue) {

        this.DeviceID = DeviceID;
        this.Room = Room;
        Room.getDeviceList().add(DeviceID);
        this.DeviceName = DeviceName;
        this.GateNum1 = GateNum1;
        this.GateNum2 = GateNum2;
        this.GateNum3 = GateNum3;
        this.GateNum4 = GateNum4;
        this.DeviceState = DeviceState;
        this.isStatusChanged = isStatusChanged;
        this.StepperMotorMoves = StepperMotorMoves;
        this.AlarmDuration = AlarmDuration;
        this.AlarmInterval = AlarmInterval;
        this.DB = DB;
        this.command = command;
        this.lastStatusChange = lastStatusChange;
        this.MaxValue = MaxValue;
        Start();
    }

    public void Start() {
        if (DeviceName.equals("Roof Lamp")) {
            RoofLamp = new RoofLamp_Thread(DeviceID, DeviceState, GateNum1, isStatusChanged, DB, command, getLastStatusChange());
            RoofLamp.start();

            AC = null;
            Curtains = null;
            Alarm = null;
            GarageDoor = null;
            SecurityCamera = null;
            WaterPump = null;

        } else if (DeviceName.equals("AC")) {
            AC = new AC_Thread(DeviceID, DeviceState, GateNum1, isStatusChanged, DB, command, getLastStatusChange());
            AC.start();

            RoofLamp = null;
            Curtains = null;
            Alarm = null;
            GarageDoor = null;
            SecurityCamera = null;
            WaterPump = null;

        } else if (DeviceName.equals("Curtains")) {
            Curtains = new Curtains_Thread(DeviceID, DeviceState, GateNum1, GateNum2, GateNum3, GateNum4, isStatusChanged, getMaxValue(), DB, StepperMotorMoves);
            Curtains.start();

            RoofLamp = null;
            AC = null;
            Alarm = null;
            GarageDoor = null;
            SecurityCamera = null;
            WaterPump = null;

        } else if (DeviceName.equals("Alarm")) {
            Alarm = new Alarm_Thread(DeviceID, DeviceState, GateNum1, isStatusChanged, AlarmDuration, AlarmInterval, DB);
            Alarm.start();

            RoofLamp = null;
            AC = null;
            Curtains = null;
            GarageDoor = null;
            SecurityCamera = null;
            WaterPump = null;

        } else if (DeviceName.equals("Garage Door")) {
            GarageDoor = new GarageDoor_Thread(DeviceID, DeviceState, GateNum1, GateNum2, GateNum3, GateNum4, isStatusChanged, getMaxValue(), DB, StepperMotorMoves);
            GarageDoor.start();

            RoofLamp = null;
            AC = null;
            Curtains = null;
            Alarm = null;
            SecurityCamera = null;
            WaterPump = null;

        } else if (DeviceName.equals("Security Camera")) {
            SecurityCamera = new SecurityCamera_Thread(DeviceID, DeviceState, GateNum1, isStatusChanged, DB);

            RoofLamp = null;
            AC = null;
            Curtains = null;
            Alarm = null;
            GarageDoor = null;
            WaterPump = null;

        } else if (DeviceName.equals("Water Pump")) {
            WaterPump = new WaterPump_Thread(DeviceID, DeviceState, GateNum1, isStatusChanged, DB, command);
            WaterPump.start();

            RoofLamp = null;
            AC = null;
            Curtains = null;
            Alarm = null;
            GarageDoor = null;
            SecurityCamera = null;

        } else {
            RoofLamp = null;
            AC = null;
            Curtains = null;
            Alarm = null;
            GarageDoor = null;
            SecurityCamera = null;
            WaterPump = null;
        }
    }

    public RoofLamp_Thread getRoofLamp() {
        return RoofLamp;
    }

    public AC_Thread getAC() {
        return AC;
    }

    public Curtains_Thread getCurtains() {
        return Curtains;
    }

    public Alarm_Thread getAlarm() {
        return Alarm;
    }

    public GarageDoor_Thread getGarageDoor() {
        return GarageDoor;
    }

    public SecurityCamera_Thread getSecurityCamera() {
        return SecurityCamera;
    }

    public WaterPump_Thread getWaterPump() {
        return WaterPump;
    }

    public int getDeviceID() {
        return DeviceID;
    }

    public RoomList getRoom() {
        return Room;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public PinsList getGateNum1() {
        return GateNum1;
    }

    public PinsList getGateNum2() {
        return GateNum2;
    }

    public PinsList getGateNum3() {
        return GateNum3;
    }

    public PinsList getGateNum4() {
        return GateNum4;
    }

    public boolean getDeviceState() {
        return DeviceState;
    }

    public void setDeviceState(boolean DeviceState) {
        this.DeviceState = DeviceState;
    }

    public boolean getIsStatusChanged() {
        return isStatusChanged;
    }

    public void setIsStatusChanged(boolean isStatusChanged) {
        this.isStatusChanged = isStatusChanged;
    }

    public int getStepperMotorMoves() {
        return StepperMotorMoves;
    }

    public void setStepperMotorMoves(int StepperMotorMoves) {
        this.StepperMotorMoves = StepperMotorMoves;
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

    public Timestamp getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(Timestamp lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }

    public int getMaxValue() {
        return MaxValue;
    }
}
