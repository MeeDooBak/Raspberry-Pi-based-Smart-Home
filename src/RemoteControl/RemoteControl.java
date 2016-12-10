package RemoteControl;

import Device.*;
import Rooms.*;
import Users.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class RemoteControl implements Runnable {

    private final User Users;
    private final Room Room;
    private final Device Devices;

    private BufferedReader client;
    private String Line;
    private String Previous = "";

    private String UserID = "";
    private String UserPass = "";
    private String RoomID = "";
    private String DeviceID = "";

    public RemoteControl(User Users, Room Room, Device Devices) {
        this.Users = Users;
        this.Room = Room;
        this.Devices = Devices;

        try {
            client = new BufferedReader(new InputStreamReader((Runtime.getRuntime().exec(new String[]{"/usr/bin/irw"})).getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private UserList getUserID() {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    if (Line.equals("KEY_1")) {
                        UserID += "1";
                    } else if (Line.equals("KEY_2")) {
                        UserID += "2";
                    } else if (Line.equals("KEY_3")) {
                        UserID += "3";
                    } else if (Line.equals("KEY_4")) {
                        UserID += "4";
                    } else if (Line.equals("KEY_5")) {
                        UserID += "5";
                    } else if (Line.equals("KEY_6")) {
                        UserID += "6";
                    } else if (Line.equals("KEY_7")) {
                        UserID += "7";
                    } else if (Line.equals("KEY_8")) {
                        UserID += "8";
                    } else if (Line.equals("KEY_9")) {
                        UserID += "9";
                    } else if (Line.equals("KEY_0")) {
                        UserID += "0";
                    } else if (Line.equals("KEY_OK")) {
                        UserList User = Users.Get(Integer.parseInt(UserID));
                        if (User != null) {
                            System.out.println("RemoteControl, User ID : " + User.getUserID());
                            return User;
                        } else {
                            System.out.println("RemoteControl, User ID Not Found.");
                            return null;
                        }
                    } else if (Line.equals("KEY_BACKSPACE")) {
                        UserID = UserID.substring(0, UserID.length() - 1);
                    } else if (Line.equals("KEY_BACK")) {
                        return null;
                    } else if (Line.equals("KEY_EXIT")) {
                        return null;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean getUserPass(UserList UserClass) {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    if (Line.equals("KEY_1")) {
                        UserPass += "1";
                    } else if (Line.equals("KEY_2")) {
                        UserPass += "2";
                    } else if (Line.equals("KEY_3")) {
                        UserPass += "3";
                    } else if (Line.equals("KEY_4")) {
                        UserPass += "4";
                    } else if (Line.equals("KEY_5")) {
                        UserPass += "5";
                    } else if (Line.equals("KEY_6")) {
                        UserPass += "6";
                    } else if (Line.equals("KEY_7")) {
                        UserPass += "7";
                    } else if (Line.equals("KEY_8")) {
                        UserPass += "8";
                    } else if (Line.equals("KEY_9")) {
                        UserPass += "9";
                    } else if (Line.equals("KEY_0")) {
                        UserPass += "0";
                    } else if (Line.equals("KEY_OK")) {
                        if (UserClass.getPassword().equals(UserPass)) {
                            System.out.println("RemoteControl, User Password True.");
                            return true;
                        } else {
                            System.out.println("RemoteControl, User Password false.");
                            return false;
                        }
                    } else if (Line.equals("KEY_BACKSPACE")) {
                        UserPass = UserPass.substring(0, UserID.length() - 1);
                    } else if (Line.equals("KEY_BACK")) {
                        return false;
                    } else if (Line.equals("KEY_EXIT")) {
                        return false;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private RoomList getRoom(UserList UserClass) {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    if (Line.equals("KEY_1")) {
                        RoomID += "1";
                    } else if (Line.equals("KEY_2")) {
                        RoomID += "2";
                    } else if (Line.equals("KEY_3")) {
                        RoomID += "3";
                    } else if (Line.equals("KEY_4")) {
                        RoomID += "4";
                    } else if (Line.equals("KEY_5")) {
                        RoomID += "5";
                    } else if (Line.equals("KEY_6")) {
                        RoomID += "6";
                    } else if (Line.equals("KEY_7")) {
                        RoomID += "7";
                    } else if (Line.equals("KEY_8")) {
                        RoomID += "8";
                    } else if (Line.equals("KEY_9")) {
                        RoomID += "9";
                    } else if (Line.equals("KEY_0")) {
                        RoomID += "0";
                    } else if (Line.equals("KEY_OK")) {
                        if (UserClass.isIsAdmin()) {
                            RoomList Room2 = Room.Get(Integer.parseInt(RoomID));
                            if (Room2 != null) {
                                System.out.println("RemoteControl, Room ID : " + Room2.getRoomID());
                                return Room2;
                            } else {
                                System.out.println("RemoteControl, Room ID Not Found.");
                                return null;
                            }
                        } else {
                            RoomList Room2 = null;
                            ArrayList<RoomList> RoomClass = UserClass.getRoomList();
                            for (int i = 0; i < RoomClass.size(); i++) {
                                if (RoomClass.get(i).getRoomID() == Integer.parseInt(RoomID)) {
                                    Room2 = RoomClass.get(i);
                                    break;
                                }
                            }

                            if (Room2 != null) {
                                System.out.println("RemoteControl, Room ID : " + Room2.getRoomID());
                                return Room2;
                            } else {
                                System.out.println("RemoteControl, Room ID Not Found.");
                                return null;
                            }
                        }

                    } else if (Line.equals("KEY_BACKSPACE")) {
                        RoomID = RoomID.substring(0, RoomID.length() - 1);
                    } else if (Line.equals("KEY_BACK")) {
                        return null;
                    } else if (Line.equals("KEY_EXIT")) {
                        return null;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean getDevice(RoomList RoomClass) {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    if (Line.equals("KEY_1")) {
                        DeviceID += "1";
                    } else if (Line.equals("KEY_2")) {
                        DeviceID += "2";
                    } else if (Line.equals("KEY_3")) {
                        DeviceID += "3";
                    } else if (Line.equals("KEY_4")) {
                        DeviceID += "4";
                    } else if (Line.equals("KEY_5")) {
                        DeviceID += "5";
                    } else if (Line.equals("KEY_6")) {
                        DeviceID += "6";
                    } else if (Line.equals("KEY_7")) {
                        DeviceID += "7";
                    } else if (Line.equals("KEY_8")) {
                        DeviceID += "8";
                    } else if (Line.equals("KEY_9")) {
                        DeviceID += "9";
                    } else if (Line.equals("KEY_0")) {
                        DeviceID += "0";
                    } else if (Line.equals("KEY_OK")) {
                        boolean Found = false;
                        ArrayList<Integer> DeviceClass = RoomClass.getDeviceList();
                        for (int i = 0; i < DeviceClass.size(); i++) {
                            if (DeviceClass.get(i) == Integer.parseInt(DeviceID)) {
                                Found = true;
                                break;
                            }
                        }
                        if (Found) {
                            System.out.println("RemoteControl, Device ID : " + DeviceID);
                            return true;
                        } else {
                            System.out.println("RemoteControl, Device ID Not Found.");
                            return false;
                        }
                    } else if (Line.equals("KEY_BACKSPACE")) {
                        DeviceID = DeviceID.substring(0, DeviceID.length() - 1);
                    } else if (Line.equals("KEY_BACK")) {
                        return false;
                    } else if (Line.equals("KEY_EXIT")) {
                        return false;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void Execute(int DeviceID) {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    DeviceList DeviceClass = Devices.Get(DeviceID);
                    if (DeviceClass != null) {

                        switch (DeviceClass.getDeviceName()) {
                            case "Roof Lamp":
                                if (Line.equals("KEY_POWER")) {
                                    ((Light) DeviceClass.GetDevice()).ChangeState(!((Light) DeviceClass.GetDevice()).getDeviceState(), true);
                                }
                                break;
                            case "AC":
                                if (Line.equals("KEY_POWER")) {
                                    ((AC) DeviceClass.GetDevice()).ChangeState(!((AC) DeviceClass.GetDevice()).getDeviceState(), true);
                                }
                                break;
                            case "Alarm":
                                if (Line.equals("KEY_POWER")) {
                                    if (Line.equals("KEY_POWER")) {
                                        ((Alarm) DeviceClass.GetDevice()).ChangeState(!((Alarm) DeviceClass.GetDevice()).getDeviceState(), 0, 0, true);
                                    }
                                }
                                break;
                            case "Curtains":
                                if (Line.equals("KEY_UP")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(true, 5499, true);
                                } else if (Line.equals("KEY_DOWN")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(false, 0, true);
                                }
                                break;
                            case "Garage Door":
                                if (Line.equals("KEY_UP")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(true, 0, true);
                                } else if (Line.equals("KEY_DOWN")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(false, 5599, true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    if (Line.equals("KEY_HOME")) {
                        System.out.println("Start Remote Control");

                        UserID = "";
                        UserPass = "";
                        RoomID = "";
                        DeviceID = "";

                        UserList UserClass = getUserID();
                        if (UserClass != null) {
                            if (getUserPass(UserClass)) {
                                while (true) {
                                    RoomList RoomClass = getRoom(UserClass);
                                    if (RoomClass != null) {
                                        while (true) {
                                            if (getDevice(RoomClass)) {
                                                Execute(Integer.parseInt(DeviceID));
                                            } else {
                                                break;
                                            }
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
