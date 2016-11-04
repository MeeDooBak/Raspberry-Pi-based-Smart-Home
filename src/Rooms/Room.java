package Rooms;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Room extends Thread {

    private final Connection DB;
    private final ArrayList<RoomList> RoomList;

    public Room(Connection DB, ArrayList<RoomList> RoomList) {
        this.DB = DB;
        this.RoomList = RoomList;
    }

    public int indexof(int RoomID) {
        for (int i = 0; i < RoomList.size(); i++) {
            if (RoomList.get(i).getRoomID() == RoomID) {
                return i;
            }
        }
        return -1;
    }

    public RoomList Get(int RoomID) {
        for (int i = 0; i < RoomList.size(); i++) {
            if (RoomList.get(i).getRoomID() == RoomID) {
                return RoomList.get(i);
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet Result = Statement.executeQuery("select * from room");

            Result.beforeFirst();
            while (Result.next()) {
                int RoomID = Result.getInt("RoomID");
                String RoomName = Result.getString("RoomName");
                RoomList.add(new RoomList(RoomID, RoomName, new ArrayList()));
            }
            Result.close();
            Statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Room.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
