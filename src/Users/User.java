package Users;

import Rooms.Room;
import Rooms.RoomList;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class User extends Thread {

    private final Connection DB;
    private final ArrayList<UserList> UserList;
    private final Room Rooms;

    public User(Connection DB, ArrayList<UserList> UserList, Room Rooms) {
        this.DB = DB;
        this.UserList = UserList;
        this.Rooms = Rooms;
    }

    public int indexof(int UserID) {
        for (int i = 0; i < UserList.size(); i++) {
            if (UserList.get(i).getUserID() == UserID) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select * from user");

                Result.beforeFirst();
                while (Result.next()) {

                    int UserID = Result.getInt("UserID");
                    String UserName = Result.getString("UserName");
                    String UserEmail = Result.getString("Email");
                    String Description = Result.getString("Description");
                    String Password = Result.getString("Password");
                    boolean isAdmin = Result.getBoolean("isAdmin");
                    boolean isDisabled = Result.getBoolean("isDisabled");

                    int index = indexof(UserID);
                    if (index > -1) {
                        UserList.get(index).setUserName(UserName);
                        UserList.get(index).setUserEmail(UserEmail);
                        UserList.get(index).setDescription(Description);
                        UserList.get(index).setPassword(Password);
                        UserList.get(index).setIsAdmin(isAdmin);
                        UserList.get(index).setIsDisabled(isDisabled);

                        Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result2 = Statement2.executeQuery("select * from user_authorized_rooms where UserID = " + UserID);

                        ArrayList<RoomList> UserRoomList = new ArrayList();
                        while (Result2.next()) {
                            UserRoomList.add(Rooms.Get(Result2.getInt("RoomID")));
                        }
                        Result2.close();
                        Statement2.close();

                        UserList.get(index).setRoomList(UserRoomList);

                    } else {
                        Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result2 = Statement2.executeQuery("select * from user_authorized_rooms where UserID = " + UserID);

                        ArrayList<RoomList> UserRoomList = new ArrayList();
                        while (Result2.next()) {
                            UserRoomList.add(Rooms.Get(Result2.getInt("RoomID")));
                        }
                        Result2.close();
                        Statement2.close();

                        UserList.add(new UserList(UserID, UserName, UserEmail, Description, Password, isAdmin, isDisabled, UserRoomList));
                    }
                }
                Result.close();
                Statement.close();
                Thread.sleep(1000);
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
