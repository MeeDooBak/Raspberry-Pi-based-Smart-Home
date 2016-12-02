//package IR;
//
//import Device.Device;
//import Device.DeviceList;
//import Rooms.*;
//import Users.*;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.logging.*;
//
//public class IR extends Thread {
//
//    private final User Users;
//    private final Device Devices;
//
//    private final BufferedReader client;
//    private String Line;
//    private String Previous = "";
//
//    private String UserID = "";
//    private String UserPass = "";
//    private String RoomID = "";
//    private String DeviceID = "";
//
//    public IR(User Users, Device Devices) throws IOException {
//        this.Users = Users;
//        this.Devices = Devices;
//
//        client = new BufferedReader(new InputStreamReader((Runtime.getRuntime().exec(new String[]{"/usr/bin/irw"})).getInputStream()));
//    }
//
//    private UserList getUserID() {
//        try {
//            while ((Line = client.readLine()) != null) {
//                if (Line.split(" ")[2].equals(Previous)) {
//                    Previous = "";
//                } else {
//                    Line = Line.split(" ")[2];
//                    Previous = Line;
//                    System.out.println(Line);
//
//                    if (Line.equals("KEY_1")) {
//                        UserID += "1";
//                    } else if (Line.equals("KEY_2")) {
//                        UserID += "2";
//                    } else if (Line.equals("KEY_3")) {
//                        UserID += "3";
//                    } else if (Line.equals("KEY_4")) {
//                        UserID += "4";
//                    } else if (Line.equals("KEY_5")) {
//                        UserID += "5";
//                    } else if (Line.equals("KEY_6")) {
//                        UserID += "6";
//                    } else if (Line.equals("KEY_7")) {
//                        UserID += "7";
//                    } else if (Line.equals("KEY_8")) {
//                        UserID += "8";
//                    } else if (Line.equals("KEY_9")) {
//                        UserID += "9";
//                    } else if (Line.equals("KEY_0")) {
//                        UserID += "0";
//                    } else if (Line.equals("KEY_OK")) {
//                        return Users.Get(Integer.parseInt(UserID));
//                    } else if (Line.equals("KEY_BACKSPACE")) {
//                        UserID = UserID.substring(0, UserID.length() - 1);
//                    } else if (Line.equals("KEY_BACK")) {
//                        return null;
//                    } else if (Line.equals("KEY_EXIT")) {
//                        return null;
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(IR.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//
//    private boolean getUserPass(UserList UserClass) {
//        try {
//            while ((Line = client.readLine()) != null) {
//                if (Line.split(" ")[2].equals(Previous)) {
//                    Previous = "";
//                } else {
//                    Line = Line.split(" ")[2];
//                    Previous = Line;
//                    System.out.println(Line);
//
//                    if (Line.equals("KEY_1")) {
//                        UserPass += "1";
//                    } else if (Line.equals("KEY_2")) {
//                        UserPass += "2";
//                    } else if (Line.equals("KEY_3")) {
//                        UserPass += "3";
//                    } else if (Line.equals("KEY_4")) {
//                        UserPass += "4";
//                    } else if (Line.equals("KEY_5")) {
//                        UserPass += "5";
//                    } else if (Line.equals("KEY_6")) {
//                        UserPass += "6";
//                    } else if (Line.equals("KEY_7")) {
//                        UserPass += "7";
//                    } else if (Line.equals("KEY_8")) {
//                        UserPass += "8";
//                    } else if (Line.equals("KEY_9")) {
//                        UserPass += "9";
//                    } else if (Line.equals("KEY_0")) {
//                        UserPass += "0";
//                    } else if (Line.equals("KEY_OK")) {
//                        return UserClass.getPassword().equals(UserPass);
//                    } else if (Line.equals("KEY_BACKSPACE")) {
//                        UserPass = UserPass.substring(0, UserID.length() - 1);
//                    } else if (Line.equals("KEY_BACK")) {
//                        return false;
//                    } else if (Line.equals("KEY_EXIT")) {
//                        return false;
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(IR.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return false;
//    }
//
//    private RoomList getRoom(UserList UserClass) {
//        try {
//            while ((Line = client.readLine()) != null) {
//                if (Line.split(" ")[2].equals(Previous)) {
//                    Previous = "";
//                } else {
//                    Line = Line.split(" ")[2];
//                    Previous = Line;
//                    System.out.println(Line);
//
//                    if (Line.equals("KEY_1")) {
//                        RoomID += "1";
//                    } else if (Line.equals("KEY_2")) {
//                        RoomID += "2";
//                    } else if (Line.equals("KEY_3")) {
//                        RoomID += "3";
//                    } else if (Line.equals("KEY_4")) {
//                        RoomID += "4";
//                    } else if (Line.equals("KEY_5")) {
//                        RoomID += "5";
//                    } else if (Line.equals("KEY_6")) {
//                        RoomID += "6";
//                    } else if (Line.equals("KEY_7")) {
//                        RoomID += "7";
//                    } else if (Line.equals("KEY_8")) {
//                        RoomID += "8";
//                    } else if (Line.equals("KEY_9")) {
//                        RoomID += "9";
//                    } else if (Line.equals("KEY_0")) {
//                        RoomID += "0";
//                    } else if (Line.equals("KEY_OK")) {
//                        ArrayList<RoomList> RoomClass = UserClass.getRoomList();
//                        for (int i = 0; i < RoomClass.size(); i++) {
//                            if (RoomClass.get(i).getRoomID() == Integer.parseInt(RoomID)) {
//                                return RoomClass.get(i);
//                            }
//                        }
//                        return null;
//                    } else if (Line.equals("KEY_BACKSPACE")) {
//                        RoomID = RoomID.substring(0, RoomID.length() - 1);
//                    } else if (Line.equals("KEY_BACK")) {
//                        return null;
//                    } else if (Line.equals("KEY_EXIT")) {
//                        return null;
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(IR.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//
//    private boolean getDevice(RoomList RoomClass) {
//        try {
//            while ((Line = client.readLine()) != null) {
//                if (Line.split(" ")[2].equals(Previous)) {
//                    Previous = "";
//                } else {
//                    Line = Line.split(" ")[2];
//                    Previous = Line;
//                    System.out.println(Line);
//
//                    if (Line.equals("KEY_1")) {
//                        DeviceID += "1";
//                    } else if (Line.equals("KEY_2")) {
//                        DeviceID += "2";
//                    } else if (Line.equals("KEY_3")) {
//                        DeviceID += "3";
//                    } else if (Line.equals("KEY_4")) {
//                        DeviceID += "4";
//                    } else if (Line.equals("KEY_5")) {
//                        DeviceID += "5";
//                    } else if (Line.equals("KEY_6")) {
//                        DeviceID += "6";
//                    } else if (Line.equals("KEY_7")) {
//                        DeviceID += "7";
//                    } else if (Line.equals("KEY_8")) {
//                        DeviceID += "8";
//                    } else if (Line.equals("KEY_9")) {
//                        DeviceID += "9";
//                    } else if (Line.equals("KEY_0")) {
//                        DeviceID += "0";
//                    } else if (Line.equals("KEY_OK")) {
//                        ArrayList<Integer> DeviceClass = RoomClass.getDeviceList();
//                        for (int i = 0; i < DeviceClass.size(); i++) {
//                            if (DeviceClass.get(i) == Integer.parseInt(DeviceID)) {
//                                return true;
//                            }
//                        }
//                        return false;
//                    } else if (Line.equals("KEY_BACKSPACE")) {
//                        DeviceID = DeviceID.substring(0, DeviceID.length() - 1);
//                    } else if (Line.equals("KEY_BACK")) {
//                        return false;
//                    } else if (Line.equals("KEY_EXIT")) {
//                        return false;
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(IR.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return false;
//    }
//
//    private void Execute(int DeviceID) {
//        try {
//            while ((Line = client.readLine()) != null) {
//                if (Line.split(" ")[2].equals(Previous)) {
//                    Previous = "";
//                } else {
//                    Line = Line.split(" ")[2];
//                    Previous = Line;
//                    System.out.println(Line);
//
//                    DeviceList DeviceClass = Devices.Get(DeviceID);
//                    if (DeviceClass != null) {
//                        if (DeviceClass.getDeviceName().equals("Roof Lamp") || DeviceClass.getDeviceName().equals("AC") || DeviceClass.getDeviceName().equals("Alarm")) {
//                            if (Line.equals("KEY_POWER")) {
//                                DeviceClass.setDeviceState(!DeviceClass.getDeviceState());
//                                DeviceClass.setIsStatusChanged(true);
//                                DeviceClass.Start();
//                            }
//                        } else if (DeviceClass.getDeviceName().equals("Curtains") || DeviceClass.getDeviceName().equals("Garage Door")) {
//                            if (Line.equals("KEY_UP")) {
//                                DeviceClass.setDeviceState(!DeviceClass.getDeviceState());
//                                DeviceClass.setIsStatusChanged(true);
//                                DeviceClass.Start();
//                            } else if (Line.equals("KEY_DOWN")) {
//                                DeviceClass.setDeviceState(!DeviceClass.getDeviceState());
//                                DeviceClass.setIsStatusChanged(true);
//                                DeviceClass.Start();
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(IR.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            while ((Line = client.readLine()) != null) {
//                if (Line.split(" ")[2].equals(Previous)) {
//                    Previous = "";
//                } else {
//                    Line = Line.split(" ")[2];
//                    Previous = Line;
//                    System.out.println(Line);
//
//                    if (Line.equals("KEY_HOME")) {
//                        UserList UserClass = getUserID();
//                        if (UserClass != null) {
//                            if (getUserPass(UserClass)) {
//                                while (true) {
//                                    RoomList RoomClass = getRoom(UserClass);
//                                    if (RoomClass != null) {
//                                        while (true) {
//                                            if (getDevice(RoomClass)) {
//                                                Execute(Integer.parseInt(DeviceID));
//                                            } else {
//                                                break;
//                                            }
//                                        }
//                                    } else {
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(IR.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
