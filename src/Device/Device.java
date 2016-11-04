package Device;

import Rooms.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Device extends Thread {

    private final String IP;
    private final Connection DB;
    private final ArrayList<DeviceList> DeviceList;
    private final Room Rooms;

    public Device(Connection DB, ArrayList<DeviceList> DeviceList, Room Rooms, String IP) {
        this.DB = DB;
        this.IP = IP;
        this.DeviceList = DeviceList;
        this.Rooms = Rooms;
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
                    int StepperMotorMoves = Result.getInt("StepperMotorMoves");

                    int index = indexof(DeviceID);
                    if (index > -1) {
                        if (DeviceName.equals("Roof Lamp") || DeviceName.equals("AC")) {
                            DeviceList.get(index).setDeviceState(DeviceState);
                            DeviceList.get(index).setIsStatusChanged(isStatusChanged);
                            DeviceList.get(index).Start();

                        } else if (DeviceName.equals("Curtains") || DeviceName.equals("Garage Door")) {
                            DeviceList.get(index).setDeviceState(DeviceState);
                            DeviceList.get(index).setStepperMotorMoves(StepperMotorMoves);
                            DeviceList.get(index).setIsStatusChanged(isStatusChanged);
                            DeviceList.get(index).Start();

                        } else if (DeviceName.equals("Alarm")) {
                            DeviceList.get(index).setDeviceState(DeviceState);
                            DeviceList.get(index).setIsStatusChanged(isStatusChanged);
                            DeviceList.get(index).Start();

                        }
                    } else {
                        if (DeviceName.equals("Roof Lamp") || DeviceName.equals("AC")) {
                            DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, GateNum, -1, -1, -1, isStatusChanged, -1, -1, -1, DB, IP));

                        } else if (DeviceName.equals("Curtains") || DeviceName.equals("Garage Door")) {
                            Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            ResultSet Result2 = Statement2.executeQuery("select * from device_stepper_motor where DeviceID = " + DeviceID);
                            Result2.next();

                            int GateNum1 = Result2.getInt("GateNum1");
                            int GateNum2 = Result2.getInt("GateNum2");
                            int GateNum3 = Result2.getInt("GateNum3");
                            int GateNum4 = Result2.getInt("GateNum4");
                            Result2.close();
                            Statement2.close();

                            DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, GateNum1, GateNum2, GateNum3, GateNum4, isStatusChanged, StepperMotorMoves, -1, -1, DB, IP));

                        } else if (DeviceName.equals("Alarm")) {
                            DeviceList.add(new DeviceList(DeviceID, Rooms.Get(RoomID), DeviceName, DeviceState, GateNum, -1, -1, -1, isStatusChanged, -1, 0, 0, DB, IP));
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
