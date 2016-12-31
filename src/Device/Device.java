package Device;

import Logger.*;
import Pins.*;
import Relay.*;
import Rooms.*;
import java.sql.*;
import java.util.*;

public class Device implements Runnable {

    private final Relay RelayQueue;
    private final Connection DB;
    private final ArrayList<DeviceInterface> DeviceList;
    private final Room Rooms;
    private final Pins Pins;

    // Get Infromation from Main Class 
    public Device(Connection DB, Room Rooms, Pins Pins, Relay RelayQueue) {
        this.DB = DB;
        this.RelayQueue = RelayQueue;
        this.Rooms = Rooms;
        this.Pins = Pins;
        this.DeviceList = new ArrayList();
    }

    // The Thread
    @Override
    public void run() {
        try {
            // Start Get Information From The Database about the Devices
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from device")) {

                Result.beforeFirst();
                // While Loop For All Row in DataBase
                while (Result.next()) {

                    // Get the Device ID
                    int DeviceID = Result.getInt("DeviceID");
                    // Get the Device Room ID
                    int RoomID = Result.getInt("RoomID");
                    // Get the Device Name
                    String DeviceName = Result.getString("DeviceName");
                    // Get the Device State
                    boolean DeviceState = Result.getBoolean("DeviceState");
                    // Get the Device Gate Number
                    int GateNum = Result.getInt("GateNum");
                    // Get if The State has Been Change
                    boolean isStatusChanged = Result.getBoolean("isStatusChanged");
                    // Get Last Time The Device Has Been Change
                    Timestamp lastStatusChange = Result.getTimestamp("lastStatusChange");

                    // Search if the specific device exists by ID
                    int index = indexof(DeviceID);

                    // if the Return Value is Gratter Than -1 
                    // the Device already exists
                    if (index > -1) {
                        // check if the State has Been Change
                        if (isStatusChanged) {
                            // Change Device State According to its kind
                            switch (DeviceName) {
                                case "Roof Lamp":
                                    DeviceList.get(index).ChangeState(DeviceState);
                                    break;

                                case "AC":
                                    DeviceList.get(index).ChangeState(DeviceState);
                                    break;

                                case "Alarm":
                                    DeviceList.get(index).ChangeState(DeviceState, 0, 0);
                                    break;

                                case "Curtains":
                                    try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                            ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                        Result2.next();

                                        // get Last Motor Move 
                                        DeviceList.get(index).ChangeState(DeviceState, Result2.getInt("StepperMotorMoves"));

                                    } catch (SQLException ex) {
                                        // This Catch For DataBase Error
                                        FileLogger.AddWarning("Motor " + DeviceID + ", Error In DataBase\n" + ex);
                                    }
                                    break;

                                case "Garage Door":
                                    try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                            ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                        Result2.next();

                                        // get Last Motor Move 
                                        DeviceList.get(index).ChangeState(DeviceState, Result2.getInt("StepperMotorMoves"));

                                    } catch (SQLException ex) {
                                        // This Catch For DataBase Error
                                        FileLogger.AddWarning("Motor " + DeviceID + ", Error In DataBase\n" + ex);
                                    }
                                    break;
                            }
                        }
                    } else { // the Device not exists
                        switch (DeviceName) {
                            // Create and add To the ArrayList the Device Class According to its kind
                            case "Roof Lamp":
                                DeviceList.add(new Light(DeviceID, Rooms.Get(RoomID), DeviceName, Pins.Get(GateNum), DeviceState, true, DB, RelayQueue));
                                break;

                            case "AC":
                                DeviceList.add(new AC(DeviceID, Rooms.Get(RoomID), DeviceName, Pins.Get(GateNum), DeviceState, true, lastStatusChange, DB, RelayQueue));
                                break;

                            case "Alarm":
                                DeviceList.add(new Alarm(DeviceID, Rooms.Get(RoomID), DeviceName, Pins.Get(GateNum), DeviceState, true, 0, 0, DB));
                                break;

                            case "Curtains":
                                try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                    Result2.next();

                                    // if the Device is Motor than Get the 4 Gate Number and Max Value and Last Motor Move
                                    DeviceList.add(new Motor(DeviceID, Rooms.Get(RoomID), DeviceName, Pins.Get(Result2.getInt("GateNum1")), Pins.Get(Result2.getInt("GateNum2")),
                                            Pins.Get(Result2.getInt("GateNum3")), Pins.Get(Result2.getInt("GateNum4")), Result2.getInt("MaxValue"), DeviceState, true,
                                            Result2.getInt("StepperMotorMoves"), DB));

                                } catch (SQLException ex) {
                                    // This Catch For DataBase Error
                                    FileLogger.AddWarning("Motor " + DeviceID + ", Error In DataBase\n" + ex);
                                }
                                break;

                            case "Garage Door":
                                try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                    Result2.next();

                                    // if the Device is Motor than Get the 4 Gate Number and Max Value and Last Motor Move
                                    DeviceList.add(new Motor(DeviceID, Rooms.Get(RoomID), DeviceName, Pins.Get(Result2.getInt("GateNum1")), Pins.Get(Result2.getInt("GateNum2")),
                                            Pins.Get(Result2.getInt("GateNum3")), Pins.Get(Result2.getInt("GateNum4")), Result2.getInt("MaxValue"), DeviceState, true,
                                            Result2.getInt("StepperMotorMoves"), DB));

                                } catch (SQLException ex) {
                                    // This Catch For DataBase Error
                                    FileLogger.AddWarning("Motor " + DeviceID + ", Error In DataBase\n" + ex);
                                }
                                break;

                            case "Security Camera":
                                DeviceList.add(new SecurityCamera(DeviceID, Rooms.Get(RoomID), DeviceName, DB));
                                break;

                            case "Water Pump":
                                DeviceList.add(new WaterPump(DeviceID, Rooms.Get(RoomID), DeviceName, Pins.Get(GateNum), DeviceState, true, DB));
                                break;

                            default:
                                break;
                        }
                        // just To Print the Result
                        FileLogger.AddInfo("Add Device : " + DeviceID + ", with Name : " + DeviceName + ", in Room : " + Rooms.Get(RoomID).getRoomName());
                    }
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Device Class, Error In DataBase\n" + ex);
        }
    }

    // Search and return ArrayList index if the specific device exists by ID
    public int indexof(int DeviceID) {
        for (int i = 0; i < DeviceList.size(); i++) {
            if (DeviceList.get(i).getDeviceID() == DeviceID) {
                return i;
            }
        }
        return -1;
    }

    // Search and return Device Class if the specific device exists by ID
    public DeviceInterface Get(int DeviceID) {
        for (int i = 0; i < DeviceList.size(); i++) {
            if (DeviceList.get(i).getDeviceID() == DeviceID) {
                return DeviceList.get(i);
            }
        }
        return null;
    }

    // Search and return Device Class if the specific device exists by Name
    public DeviceInterface Get(String DeviceName) {
        for (int i = 0; i < DeviceList.size(); i++) {
            if (DeviceList.get(i).getDeviceName().equals(DeviceName)) {
                return DeviceList.get(i);
            }
        }
        return null;
    }
}
