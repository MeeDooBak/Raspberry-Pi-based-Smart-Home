package Device;

import Pins.*;
import Relay.*;
import Rooms.*;
import java.sql.*;

public class DeviceList {

    private final int DeviceID;
    private final RoomList Room;
    private final String DeviceName;

    private Light Light;
    private AC AC;
    private Motor Motor;
    private Alarm Alarm;
    private SecurityCamera SecurityCamera;
    private WaterPump WaterPump;

    // Get Device Information from Database
    public DeviceList(int DeviceID, RoomList Room, String DeviceName, boolean DeviceState, PinsList GateNum1, PinsList GateNum2, PinsList GateNum3, PinsList GateNum4,
            boolean isStatusChanged, int StepperMotorMoves, int AlarmDuration, int AlarmInterval, Connection DB, Relay RelayQueue, int MaxValue, Timestamp lastStatusChange) {

        this.DeviceID = DeviceID;
        this.DeviceName = DeviceName;
        this.Room = Room;
        // add This Device For Given Room
        Room.getDeviceList().add(DeviceID);

        // Create Device Class According to its kind
        switch (DeviceName) {
            case "Roof Lamp":
                Light = new Light(DeviceID, GateNum1, DeviceState, isStatusChanged, DB, RelayQueue);
                break;
            case "AC":
                AC = new AC(DeviceID, GateNum1, DeviceState, isStatusChanged, lastStatusChange, DB, RelayQueue);
                break;
            case "Alarm":
                Alarm = new Alarm(DeviceID, GateNum1, DeviceState, isStatusChanged, AlarmDuration, AlarmInterval, DB);
                break;
            case "Curtains":
                Motor = new Motor(DeviceID, DeviceName, GateNum1, GateNum2, GateNum3, GateNum4, MaxValue, DeviceState, isStatusChanged, StepperMotorMoves, DB);
                break;
            case "Garage Door":
                Motor = new Motor(DeviceID, DeviceName, GateNum1, GateNum2, GateNum3, GateNum4, MaxValue, DeviceState, isStatusChanged, StepperMotorMoves, DB);
                break;
            case "Security Camera":
                SecurityCamera = new SecurityCamera(DeviceID, DB);
                break;
            case "Water Pump":
                WaterPump = new WaterPump(DeviceID, GateNum1, DeviceState, isStatusChanged, DB);
                break;
            default:
                break;
        }
    }

    // Get Device Class According to its kind
    public <T> T GetDevice() {
        switch (DeviceName) {
            case "Roof Lamp":
                return (T) Light;
            case "AC":
                return (T) AC;
            case "Curtains":
                return (T) Motor;
            case "Alarm":
                return (T) Alarm;
            case "Garage Door":
                return (T) Motor;
            case "Security Camera":
                return (T) SecurityCamera;
            case "Water Pump":
                return (T) WaterPump;
            default:
                return null;
        }
    }

    // Get Device ID
    public int getDeviceID() {
        return DeviceID;
    }

    // Get Device Room
    public RoomList getRoom() {
        return Room;
    }

    // Get Device Name
    public String getDeviceName() {
        return DeviceName;
    }
}
