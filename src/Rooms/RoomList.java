package Rooms;

import java.util.*;

public class RoomList {

    private final int RoomID;
    private final String RoomName;
    private ArrayList<Integer> DeviceList;

    // Set Room Information from Database
    public RoomList(int RoomID, String RoomName, ArrayList<Integer> DeviceList) {
        this.RoomID = RoomID;
        this.RoomName = RoomName;
        this.DeviceList = DeviceList;
    }

    // Get Room ID
    public int getRoomID() {
        return RoomID;
    }

    // Get Room Name
    public String getRoomName() {
        return RoomName;
    }

    // Get Room Device List
    public ArrayList<Integer> getDeviceList() {
        return DeviceList;
    }

    // Set Room Device List 
    public void setDeviceList(ArrayList<Integer> DeviceList) {
        this.DeviceList = DeviceList;
    }
}
