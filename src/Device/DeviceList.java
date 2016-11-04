package Device;

import Rooms.*;
import java.sql.*;

public class DeviceList {

    private boolean DeviceState;
    private boolean isStatusChanged;
    private int StepperMotorMoves;

    private int AlarmDuration;
    private int AlarmInterval;

    private final int DeviceID;
    private final RoomList Room;
    private final String DeviceName;
    private final int GateNum1;
    private final int GateNum2;
    private final int GateNum3;
    private final int GateNum4;

    private final Connection DB;
    private final Relay command;

    private DeviceThread DeviceThread;
    private MotorThread MotorThread;
    private AlarmThread AlarmThread;

    public DeviceList(int DeviceID, RoomList Room, String DeviceName, boolean DeviceState, int GateNum1, int GateNum2, int GateNum3, int GateNum4,
            boolean isStatusChanged, int StepperMotorMoves, int AlarmDuration, int AlarmInterval, Connection DB, String IP) {

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
        this.command = new Relay(IP, 161, "private");

        if (DeviceName.equals("Roof Lamp") || DeviceName.equals("AC")) {
            DeviceThread = new DeviceThread(DeviceID, DeviceState, GateNum1, isStatusChanged, DB, command);

            MotorThread = null;
            AlarmThread = null;

            DeviceThread.start();

        } else if (DeviceName.equals("Curtains") || DeviceName.equals("Garage Door")) {
            MotorThread = new MotorThread(DeviceID, DeviceState, GateNum1, GateNum2, GateNum3, GateNum4, isStatusChanged, DB, StepperMotorMoves);

            DeviceThread = null;
            AlarmThread = null;

            MotorThread.start();

        } else if (DeviceName.equals("Alarm")) {
            AlarmThread = new AlarmThread(DeviceID, DeviceState, GateNum1, isStatusChanged, AlarmDuration, AlarmInterval, DB, command);

            DeviceThread = null;
            MotorThread = null;

            AlarmThread.start();
        }
    }

    public void Start() {
        if (DeviceThread == null) {
            if (MotorThread == null) {
                AlarmThread = new AlarmThread(DeviceID, DeviceState, GateNum1, isStatusChanged, AlarmDuration, AlarmInterval, DB, command);
                AlarmThread.start();
            } else {
                MotorThread = new MotorThread(DeviceID, DeviceState, GateNum1, GateNum2, GateNum3, GateNum4, isStatusChanged, DB, StepperMotorMoves);
                MotorThread.start();
            }
        } else {
            DeviceThread = new DeviceThread(DeviceID, DeviceState, GateNum1, isStatusChanged, DB, command);
            DeviceThread.start();
        }
    }

    public DeviceThread getDeviceThread() {
        return DeviceThread;
    }

    public MotorThread getMotorThread() {
        return MotorThread;
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

    public int getGateNum1() {
        return GateNum1;
    }

    public int getGateNum2() {
        return GateNum2;
    }

    public int getGateNum3() {
        return GateNum3;
    }

    public int getGateNum4() {
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
}
