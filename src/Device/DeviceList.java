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
//    private SecurityCamera SecurityCamera;
    private WaterPump WaterPump;

    public DeviceList(int DeviceID, RoomList Room, String DeviceName, boolean DeviceState, PinsList GateNum1, PinsList GateNum2, PinsList GateNum3, PinsList GateNum4,
            boolean isStatusChanged, int StepperMotorMoves, int AlarmDuration, int AlarmInterval, Connection DB, Relay RelayQueue, int MaxValue, Timestamp lastStatusChange) {

        this.DeviceID = DeviceID;
        this.Room = Room;
        Room.getDeviceList().add(DeviceID);
        this.DeviceName = DeviceName;

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
//                SecurityCamera = new SecurityCamera(DeviceID, GateNum1, DB);
                break;
            case "Water Pump":
                WaterPump = new WaterPump(DeviceID, GateNum1, DeviceState, isStatusChanged, DB, RelayQueue);
                break;
            default:
                break;
        }
    }

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
//                return (T) SecurityCamera;
            case "Water Pump":
                return (T) WaterPump;
            default:
                return null;
        }
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
}
