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

    public int getUserID() {
        return UserID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String UserEmail) {
        this.UserEmail = UserEmail;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public ArrayList<RoomList> getRoomList() {
        return RoomList;
    }

    public void setRoomList(ArrayList<RoomList> RoomList) {
        this.RoomList = RoomList;
    }
}
