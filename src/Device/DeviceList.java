package Device;

import java.sql.*;

public class DeviceList {

    private boolean DeviceState;
    private boolean isStatusChanged;
    private int StepperMotorMoves;

    private final int DeviceID;
    private final int RoomID;
    private final String DeviceName;
    private final int GateNum1;
    private final int GateNum2;
    private final int GateNum3;
    private final int GateNum4;
    private final Connection DB;
    private final Relay command;

    private DeviceThread DeviceThread;
    private MotorThread MotorThread;

    public DeviceList(int DeviceID, int RoomID, String DeviceName, boolean DeviceState, int GateNum1, int GateNum2, int GateNum3, int GateNum4, boolean isStatusChanged, int StepperMotorMoves, Connection DB, String IP) {

        this.DeviceID = DeviceID;
        this.RoomID = RoomID;
        this.DeviceName = DeviceName;
        this.GateNum1 = GateNum1;
        this.GateNum2 = GateNum2;
        this.GateNum3 = GateNum3;
        this.GateNum4 = GateNum4;
        this.DeviceState = DeviceState;
        this.isStatusChanged = isStatusChanged;
        this.StepperMotorMoves = StepperMotorMoves;
        
        this.DB = DB;
        this.command = new Relay(IP, 161, "private");

        if (GateNum2 > -1) {
            MotorThread = new MotorThread(DeviceID, DeviceState, GateNum1, GateNum2, GateNum3, GateNum4, isStatusChanged, DB, StepperMotorMoves);
            DeviceThread = null;
            MotorThread.start();
        } else {
            DeviceThread = new DeviceThread(DeviceID, DeviceState, GateNum1, isStatusChanged, DB, command);
            MotorThread = null;
            DeviceThread.start();
        }
    }

    public void Start() {
        if (DeviceThread == null) {
            MotorThread = new MotorThread(DeviceID, DeviceState, GateNum1, GateNum2, GateNum3, GateNum4, isStatusChanged, DB, StepperMotorMoves);
            MotorThread.start();
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

    public int getRoomID() {
        return RoomID;
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

        if (DeviceThread == null) {
            MotorThread.setDeviceState(DeviceState);
        } else {
            DeviceThread.setDeviceState(DeviceState);
        }
    }

    public boolean getIsStatusChanged() {
        return isStatusChanged;
    }

    public void setIsStatusChanged(boolean isStatusChanged) {
        this.isStatusChanged = isStatusChanged;

        if (DeviceThread == null) {
            MotorThread.setisStatusChanged(isStatusChanged);
        } else {
            DeviceThread.setisStatusChanged(isStatusChanged);
        }
    }

    public int getStepperMotorMoves() {
        return StepperMotorMoves;
    }

    public void setStepperMotorMoves(int StepperMotorMoves) {
        this.StepperMotorMoves = StepperMotorMoves;
        MotorThread.setStepperMotorMoves(StepperMotorMoves);
    }
}
