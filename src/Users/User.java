package Users;

import Rooms.Room;
import Rooms.RoomList;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class User implements Runnable {

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

    public UserList Get(int UserID) {
        for (int i = 0; i < UserList.size(); i++) {
            if (UserList.get(i).getUserID() == UserID) {
                return UserList.get(i);
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result = Statement.executeQuery("select * from user")) {

                    Result.beforeFirst();
                    while (Result.next()) {

                        int UserID = Result.getInt("UserID");
                        String UserName = Result.getString("UserName");
                        String UserEmail = Result.getString("Email");
                        String Title = Result.getString("Title");
                        String Password = Result.getString("Password");
                        boolean isAdmin = Result.getBoolean("isAdmin");
                        boolean isDisabled = Result.getBoolean("isDisabled");

                        int index = indexof(UserID);
                        if (index > -1) {
                            boolean isChange = false;

                            if (!UserList.get(index).getUserName().equals(UserName)) {
                                UserList.get(index).setUserName(UserName);
                                isChange = true;
                            }

                            if (!UserList.get(index).getUserEmail().equals(UserEmail)) {
                                UserList.get(index).setUserEmail(UserEmail);
                                isChange = true;
                            }

                            if (!UserList.get(index).getDescription().equals(Title)) {
                                UserList.get(index).setDescription(Title);
                                isChange = true;
                            }

                            if (!UserList.get(index).getPassword().equals(Password)) {
                                UserList.get(index).setPassword(Password);
                                isChange = true;
                            }
                            if (!UserList.get(index).isIsAdmin() == isAdmin) {
                                UserList.get(index).setIsAdmin(isAdmin);
                                isChange = true;
                            }
                            if (!UserList.get(index).isIsDisabled() == isDisabled) {
                                UserList.get(index).setIsDisabled(isDisabled);
                                isChange = true;
                            }

                            ArrayList<RoomList> NewUserRoomList;
                            try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    ResultSet Result2 = Statement2.executeQuery("select * from user_authorized_rooms where UserID = " + UserID)) {
                                NewUserRoomList = new ArrayList();
                                while (Result2.next()) {
                                    NewUserRoomList.add(Rooms.Get(Result2.getInt("RoomID")));
                                }
                            }
                            ArrayList<RoomList> OldUserRoomList = UserList.get(index).getRoomList();

                            for (int i = 0; i < NewUserRoomList.size(); i++) {
                                boolean Found = false;
                                for (int j = 0; j < OldUserRoomList.size(); j++) {
                                    if (NewUserRoomList.get(i).getRoomID() == OldUserRoomList.get(j).getRoomID()) {
                                        Found = true;
                                        break;
                                    }
                                }
                                if (!Found) {
                                    isChange = true;
                                    break;
                                }
                            }

                            if (isChange) {
                                UserList.get(index).setRoomList(NewUserRoomList);
                                System.out.println("User : " + UserID + ", with Name : " + UserName + ", Change His/Her Infromation");
                            }
                        } else {
                            ArrayList<RoomList> UserRoomList;
                            try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    ResultSet Result2 = Statement2.executeQuery("select * from user_authorized_rooms where UserID = " + UserID)) {
                                UserRoomList = new ArrayList();
                                while (Result2.next()) {
                                    UserRoomList.add(Rooms.Get(Result2.getInt("RoomID")));
                                }
                            }
                            System.out.println("Add User : " + UserID + ", with Name : " + UserName);
                            UserList.add(new UserList(UserID, UserName, UserEmail, Title, Password, isAdmin, isDisabled, UserRoomList));
                        }
                    }
                }
                Thread.sleep(5000);
            }
        } catch (SQLException | InterruptedException ex) {
            System.out.println("User Class , Error In DataBase");
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
