package Users;

import Rooms.*;
import java.util.*;

public class UserList {

    private final int UserID;
    private String UserName;
    private String UserEmail;
    private String Description;
    private String Password;
    private boolean isAdmin;
    private boolean isDisabled;
    private ArrayList<RoomList> RoomList;

    // Set User Information from Database
    public UserList(int UserID, String UserName, String UserEmail, String Description, String Password, boolean isAdmin, boolean isDisabled, ArrayList<RoomList> RoomList) {
        this.UserID = UserID;
        this.UserName = UserName;
        this.UserEmail = UserEmail;
        this.Description = Description;
        this.Password = Password;
        this.isAdmin = isAdmin;
        this.isDisabled = isDisabled;
        this.RoomList = RoomList;
    }

    // Get User ID
    public int getUserID() {
        return UserID;
    }

    // Get User Name
    public String getUserName() {
        return UserName;
    }

    // Set User Name
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    // Get User Email
    public String getUserEmail() {
        return UserEmail;
    }

    // Set User Email
    public void setUserEmail(String UserEmail) {
        this.UserEmail = UserEmail;
    }

    // Get User Description
    public String getDescription() {
        return Description;
    }

    // Set User Description
    public void setDescription(String Description) {
        this.Description = Description;
    }

    // Get User Password
    public String getPassword() {
        return Password;
    }

    // Set User Password
    public void setPassword(String Password) {
        this.Password = Password;
    }

    // Return is User Admin
    public boolean isIsAdmin() {
        return isAdmin;
    }

    // Set is User Admin
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    // Return is User Disabled
    public boolean isIsDisabled() {
        return isDisabled;
    }

    // Set is User Disabled
    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    // Get Uset Room Access List
    public ArrayList<RoomList> getRoomList() {
        return RoomList;
    }

    // Set Uset Room Access List
    public void setRoomList(ArrayList<RoomList> RoomList) {
        this.RoomList = RoomList;
    }
}
