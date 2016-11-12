package Device;

import Pins.Pins;
import Rooms.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Device extends Thread {

    private final Relay command;
    private final Connection DB;
    private final ArrayList<DeviceList> DeviceList;
    private final Room Rooms;
    private final Pins Pins;

    public Device(Connection DB, ArrayList<DeviceList> DeviceList, Room Rooms, Pins Pins, Relay command) {
        this.DB = DB;
        this.command = command;
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
            while (true) {
                Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select * from device");

                Result.beforeFirst();
                while (Result.next()) {

                    int DeviceID = Result.getInt("DeviceID");
                    int RoomID = Result.getInt("RoomID");
                    String DeviceName = Result.getString("DeviceName");
                    boolean DeviceState = Result.getBoolean("DeviceState");
                    int GateNum = Result.getInt("GateNum");
                    boolean isStatusChanged = Result.getBoolean("isStatusChanged");

                    int index = indexof(DeviceID);
                    if (index > -1) {
                        if (isStatusChanged) {
                            if (DeviceName.equals("Roof Lamp") || DeviceName.equals("AC") || DeviceName.equals("Alarm")) {
                                DeviceList.get(index).setDeviceState(DeviceState);
                                DeviceList.get(index).setIsStatusChanged(isStatusChanged);
                                DeviceList.get(index).Start();
                                System.out.println(DeviceName + " State Change to " + DeviceState);

                            } else if (DeviceName.equals("Curtains") || DeviceName.equals("Garage Door")) {
                                DeviceList.get(index).setDeviceState(DeviceState);
                                DeviceList.get(index).setIsStatusChanged(isStatusChanged);

                                Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID);
                                Result2.next();

                                int StepperMotorMoves = Result2.getInt("StepperMotorMoves");
                                if (DeviceList.get(index).getStepperMotorMoves() != StepperMotorMoves) {
                                    DeviceList.get(index).setStepperMotorMoves(StepperMotorMoves);
                                }

                                Result2.close();
                                Statement2.close();

                                DeviceList.get(index).Start();
                                System.out.println(DeviceName + " State Change to " + DeviceState);
                            }
                        }
                    } else {
                        if (DeviceName.equals("Roof Lamp") || DeviceName.equals("AC")) {
                            DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(GateNum), null, null, null, isStatusChanged, -1,
                                    -1, -1, DB, command));
                            System.out.println("Add Device : " + DeviceID + " " + DeviceName + " With State " + DeviceState);

                        } else if (DeviceName.equals("Curtains") || DeviceName.equals("Garage Door")) {
                            Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID);
                            Result2.next();

                            int GateNum1 = Result2.getInt("GateNum1");
                            int GateNum2 = Result2.getInt("GateNum2");
                            int GateNum3 = Result2.getInt("GateNum3");
                            int GateNum4 = Result2.getInt("GateNum4");
                            int StepperMotorMoves = Result2.getInt("StepperMotorMoves");
                            Result2.close();
                            Statement2.close();

                            DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(GateNum1), Pins.Get(GateNum2),
                                    Pins.Get(GateNum3), Pins.Get(GateNum4), isStatusChanged, StepperMotorMoves, -1, -1, DB, command));
                            System.out.println("Add Device : " + DeviceID + " " + DeviceName + " With State " + DeviceState);

                        } else if (DeviceName.equals("Alarm")) {
                            DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, Pins.Get(GateNum), null, null, null, isStatusChanged, -1,
                                    0, 0, DB, command));
                            System.out.println("Add Device : " + DeviceID + " " + DeviceName + " With State " + DeviceState);
                        }
                    }
                }
                Result.close();
                Statement.close();
                Thread.sleep(1000);
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
