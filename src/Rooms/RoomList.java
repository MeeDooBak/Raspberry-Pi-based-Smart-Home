package Rooms;

import Device.DeviceList;
import java.util.ArrayList;

public class RoomList {

    private final int RoomID;
    private final String RoomName;
    private ArrayList<Integer> DeviceList;

    public RoomList(int RoomID, String RoomName, ArrayList<Integer> DeviceList) {

        this.RoomID = RoomID;
        this.RoomName = RoomName;
        this.DeviceList = DeviceList;
    }

    public int getRoomID() {
        return RoomID;
    }

    public String getRoomName() {
        return RoomName;
    }

    public ArrayList<Integer> getDeviceList() {
        return DeviceList;
    }

    public void setDeviceList(ArrayList<Integer> DeviceList) {
        this.DeviceList = DeviceList;
    }
}
