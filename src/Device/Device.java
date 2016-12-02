package Device;

import Pins.*;
import Relay.*;
import Rooms.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;

public class Device implements Runnable {

    private final Relay RelayQueue;
    private final Connection DB;
    private final ArrayList<DeviceList> DeviceList;
    private final Room Rooms;
    private final Pins Pins;

    public Device(Connection DB, ArrayList<DeviceList> DeviceList, Room Rooms, Pins Pins, Relay RelayQueue) {
        this.DB = DB;
        this.RelayQueue = RelayQueue;
        this.DeviceList = DeviceList;
        this.Rooms = Rooms;
        this.Pins = Pins;
    }

    public int indexof(int DeviceID) {
        for (int i = 0; i < DeviceList.size(); i++) {
            if (DeviceList.get(i).getDeviceID() == DeviceID) {
                return i;
            }
        }
        return -1;
    }

    public DeviceList Get(int DeviceID) {
        for (int i = 0; i < DeviceList.size(); i++) {
            if (DeviceList.get(i).getDeviceID() == DeviceID) {
                return DeviceList.get(i);
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from device")) {

                Result.beforeFirst();
                while (Result.next()) {

                    int DeviceID = Result.getInt("DeviceID");
                    int RoomID = Result.getInt("RoomID");
                    String DeviceName = Result.getString("DeviceName");
                    boolean DeviceState = Result.getBoolean("DeviceState");
                    int GateNum = Result.getInt("GateNum");
                    boolean isStatusChanged = Result.getBoolean("isStatusChanged");
                    Timestamp lastStatusChange = Result.getTimestamp("lastStatusChange");

                    int index = indexof(DeviceID);
                    if (index > -1) {
                        if (isStatusChanged) {
                            switch (DeviceName) {
                                case "Roof Lamp":
                                    ((Light) DeviceList.get(index).GetDevice()).ChangeState(DeviceState, isStatusChanged);
                                    break;

                                case "AC":
                                    ((AC) DeviceList.get(index).GetDevice()).ChangeState(DeviceState, isStatusChanged);
                                    break;

                                case "Alarm":
                                    ((Alarm) DeviceList.get(index).GetDevice()).ChangeState(DeviceState, 0, 0, isStatusChanged);
                                    break;

                                case "Curtains":
                                    try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                            ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                        Result2.next();
                                        ((Motor) DeviceList.get(index).GetDevice()).ChangeState(DeviceState, Result2.getInt("StepperMotorMoves"), isStatusChanged);

                                    } catch (SQLException ex) {
                                        System.out.println("Motor " + DeviceID + ", Error In DataBase");
                                        Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;

                                case "Garage Door":
                                    try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                            ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                        Result2.next();
                                        ((Motor) DeviceList.get(index).GetDevice()).ChangeState(DeviceState, Result2.getInt("StepperMotorMoves"), isStatusChanged);

                                    } catch (SQLException ex) {
                                        System.out.println("Motor " + DeviceID + ", Error In DataBase");
                                        Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    break;
                            }
                        }
                    } else {
                        switch (DeviceName) {
                            case "Roof Lamp":
                                DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(GateNum),
                                        null, null, null, true, -1, -1, -1, DB, RelayQueue, -1, null));
                                break;

                            case "AC":
                                DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(GateNum),
                                        null, null, null, true, -1, -1, -1, DB, RelayQueue, -1, lastStatusChange));
                                break;

                            case "Alarm":
                                DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(GateNum),
                                        null, null, null, true, -1, 0, 0, DB, null, -1, null));
                                break;

                            case "Curtains":
                                try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                    Result2.next();
                                    DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(Result2.getInt("GateNum1")),
                                            Pins.Get(Result2.getInt("GateNum2")), Pins.Get(Result2.getInt("GateNum3")), Pins.Get(Result2.getInt("GateNum4")),
                                            true, Result2.getInt("StepperMotorMoves"), -1, -1, DB, null, Result2.getInt("MaxValue"), null));
                                } catch (SQLException ex) {
                                    System.out.println("Motor " + DeviceID + ", Error In DataBase");
                                    Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            case "Garage Door":
                                try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                        ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID)) {
                                    Result2.next();
                                    DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(Result2.getInt("GateNum1")),
                                            Pins.Get(Result2.getInt("GateNum2")), Pins.Get(Result2.getInt("GateNum3")), Pins.Get(Result2.getInt("GateNum4")),
                                            true, Result2.getInt("StepperMotorMoves"), -1, -1, DB, null, Result2.getInt("MaxValue"), null));
                                } catch (SQLException ex) {
                                    System.out.println("Motor " + DeviceID + ", Error In DataBase");
                                    Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;

                            case "Security Camera":
                                DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, false, Pins.Get(GateNum),
                                        null, null, null, false, -1, -1, -1, DB, null, -1, null));
                                break;

                            case "Water Pump":
                                DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(GateNum),
                                        null, null, null, false, -1, -1, -1, DB, RelayQueue, -1, null));
                                break;

                            default:
                                break;
                        }
                        System.out.println("Add Device : " + DeviceID + ", with Name : " + DeviceName + ", in Room : " + Rooms.Get(RoomID).getRoomName());
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Device Class, Error In DataBase");
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
