package Users;

import Rooms.*;
import Logger.*;
import java.sql.*;
import java.util.*;

public class User implements Runnable {

    private final Connection DB;
    private final ArrayList<UserList> UserList;
    private final Room Rooms;

    // Get Infromation from Main Class 
    public User(Connection DB, ArrayList<UserList> UserList, Room Rooms) {
        this.DB = DB;
        this.UserList = UserList;
        this.Rooms = Rooms;
    }

    // Search and return ArrayList index if the specific User exists by ID
    public int indexof(int UserID) {
        for (int i = 0; i < UserList.size(); i++) {
            if (UserList.get(i).getUserID() == UserID) {
                return i;
            }
        }
        return -1;
    }

    // Search and return User Class if the specific User exists by ID
    public UserList Get(int UserID) {
        for (int i = 0; i < UserList.size(); i++) {
            if (UserList.get(i).getUserID() == UserID) {
                return UserList.get(i);
            }
        }
        return null;
    }

    // The Thread To Get User Information From The Database
    @Override
    public void run() {
        try {
            while (true) {
                // Start Get User Information From The Database
                try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result = Statement.executeQuery("select * from user")) {

                    Result.beforeFirst();
                    // While Loop For All Row in DataBase
                    while (Result.next()) {

                        // Get User ID
                        int UserID = Result.getInt("UserID");
                        // Get User Name
                        String UserName = Result.getString("UserName");
                        // Get User Email
                        String UserEmail = Result.getString("Email");
                        // Get User Title
                        String Title = Result.getString("Title");
                        // Get User Password
                        String Password = Result.getString("Password");
                        // Get is User Admin
                        boolean isAdmin = Result.getBoolean("isAdmin");
                        // Get is User Disabled
                        boolean isDisabled = Result.getBoolean("isDisabled");

                        // Search if the specific USer exists by ID
                        int index = indexof(UserID);

                        // if the Return Value is Gratter Than -1 
                        // the USer already exists
                        if (index > -1) {

                            // Create Boolean Verbal To Check if There Any Change
                            boolean isChange = false;

                            // If The Old User Name is Not Equl The New User Name
                            if (!UserList.get(index).getUserName().equals(UserName)) {
                                // Add The New User Name
                                UserList.get(index).setUserName(UserName);
                                isChange = true;
                            }

                            // If The Old User Email is Not Equl The New User Email
                            if (!UserList.get(index).getUserEmail().equals(UserEmail)) {
                                // Add The New User Email
                                UserList.get(index).setUserEmail(UserEmail);
                                isChange = true;
                            }

                            // If The Old User Title is Not Equl The New User Title
                            if (!UserList.get(index).getDescription().equals(Title)) {
                                // Add The New User Title
                                UserList.get(index).setDescription(Title);
                                isChange = true;
                            }

                            // If The Old User Password is Not Equl The New User Password
                            if (!UserList.get(index).getPassword().equals(Password)) {
                                // Add The New User Password
                                UserList.get(index).setPassword(Password);
                                isChange = true;
                            }

                            // If The Old User is Admin is Not Equl The New User is Admin
                            if (!UserList.get(index).isIsAdmin() == isAdmin) {
                                // Add The New User is Admin
                                UserList.get(index).setIsAdmin(isAdmin);
                                isChange = true;
                            }

                            // If The Old User is Disabled is Not Equl The New User is Disabled
                            if (!UserList.get(index).isIsDisabled() == isDisabled) {
                                // Add The New User is Disabled
                                UserList.get(index).setIsDisabled(isDisabled);
                                isChange = true;
                            }

                            // Create ArrayList To Get User Access List
                            ArrayList<RoomList> NewUserRoomList;

                            // Get User Access List Information From The Database
                            try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    ResultSet Result2 = Statement2.executeQuery("select * from user_authorized_rooms where UserID = " + UserID)) {

                                NewUserRoomList = new ArrayList();

                                // While Loop For All Row in DataBase Have User ID
                                while (Result2.next()) {
                                    // Add To the ArrayList the Room Class Using Room ID
                                    NewUserRoomList.add(Rooms.Get(Result2.getInt("RoomID")));
                                }
                            }
                            // Get The Old User Access List 
                            ArrayList<RoomList> OldUserRoomList = UserList.get(index).getRoomList();

                            // Check if The Old List is Not Equl New List
                            for (int i = 0; i < NewUserRoomList.size(); i++) {

                                // Create Boolean Found To Check if Room Found in Old List
                                boolean Found = false;

                                for (int j = 0; j < OldUserRoomList.size(); j++) {
                                    // Check if The Old Room is Equl New Room
                                    if (NewUserRoomList.get(i).getRoomID() == OldUserRoomList.get(j).getRoomID()) {
                                        // Set Found as True
                                        Found = true;
                                        break;
                                    }
                                }

                                // Check if Found Boolean is Still False
                                // The New Room Not Found in Old List
                                if (!Found) {
                                    isChange = true;
                                    break;
                                }
                            }

                            // if There is Any Change Just Change the User Class 
                            if (isChange) {
                                // Add The New User Access List 
                                UserList.get(index).setRoomList(NewUserRoomList);

                                // just To Print the Result
                                FileLogger.AddInfo("User : " + UserID + ", with Name : " + UserName + ", Change His/Her Infromation");
                            }

                        } else { // the User not exists
                            // Create ArrayList To Get User Access List
                            ArrayList<RoomList> UserRoomList;

                            // Get User Access List Information From The Database
                            try (Statement Statement2 = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                    ResultSet Result2 = Statement2.executeQuery("select * from user_authorized_rooms where UserID = " + UserID)) {
                                UserRoomList = new ArrayList();

                                // While Loop For All Row in DataBase Have User ID
                                while (Result2.next()) {
                                    // Add To the ArrayList the Room Class Using Room ID
                                    UserRoomList.add(Rooms.Get(Result2.getInt("RoomID")));
                                }
                            }

                            // Create and add To the ArrayList the User Class
                            UserList.add(new UserList(UserID, UserName, UserEmail, Title, Password, isAdmin, isDisabled, UserRoomList));

                            // just To Print the Result
                            FileLogger.AddInfo("Add User : " + UserID + ", with Name : " + UserName);
                        }
                    }
                }

                // To Sleep For 5 Second
                Thread.sleep(5000);
            }
        } catch (SQLException | InterruptedException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("User Class, Error In DataBase\n" + ex);
        }
    }
}
