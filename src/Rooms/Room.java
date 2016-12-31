package Rooms;

import Logger.*;
import java.sql.*;
import java.util.*;

public class Room {

    private final Connection DB;
    private final ArrayList<RoomList> RoomList;

    // Get Infromation from Main Class 
    public Room(Connection DB) {
        this.DB = DB;
        this.RoomList = new ArrayList();
    }

    // Search and return ArrayList index if the specific Room exists by ID
    public int indexof(int RoomID) {
        for (int i = 0; i < RoomList.size(); i++) {
            if (RoomList.get(i).getRoomID() == RoomID) {
                return i;
            }
        }
        return -1;
    }

    // Search and return Room Class if the specific Room exists by ID
    public RoomList Get(int RoomID) {
        for (int i = 0; i < RoomList.size(); i++) {
            if (RoomList.get(i).getRoomID() == RoomID) {
                return RoomList.get(i);
            }
        }
        return null;
    }

    // Start Get Room Information From The Database
    public void Start() {
        try {
            // Start Get Room Information From The Database
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from room")) {

                Result.beforeFirst();
                // While Loop For All Row in DataBase
                while (Result.next()) {
                    // Get Room ID
                    int RoomID = Result.getInt("RoomID");
                    // Get Room Name 
                    String RoomName = Result.getString("RoomName");
                    // Create and add To the ArrayList the Room Class
                    RoomList.add(new RoomList(RoomID, RoomName, new ArrayList()));

                    // just To Print the Result
                    FileLogger.AddInfo("Add Room : " + RoomID + ", with Name : " + RoomName);
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Room Class, Error In DataBase\n" + ex);
        }
    }
}
