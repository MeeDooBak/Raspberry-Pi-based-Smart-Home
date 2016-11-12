package SmartHome;

import Device.*;
import Pins.*;
import Rooms.*;
import Sensor.*;
import Task.*;
import Users.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Main {

    private static Connection DB;
    private static Relay command;

    private static String RelayIP;
    private static String Camera1IP;
    private static String Camera2IP;

    private static ArrayList<RoomList> RoomList;
    private static ArrayList<UserList> UserList;
    private static ArrayList<SensorList> SensorList;
    private static ArrayList<DeviceList> DeviceList;
    private static ArrayList<TaskList> TaskList;
    private static ArrayList<PinsList> PinsList;

    public static void main(String[] args) {
        try {
            System.out.println("Start");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");

            getIP();
            command = new Relay(RelayIP, 161, "private");

            RoomList = new ArrayList();
            UserList = new ArrayList();
            SensorList = new ArrayList();
            DeviceList = new ArrayList();
            TaskList = new ArrayList();
            PinsList = new ArrayList();

            Pins Pins = new Pins(DB, PinsList);
            Pins.start();

            Room Room = new Room(DB, RoomList);
            Room.start();

            User User = new User(DB, UserList, Room);
            User.start();

            Thread.sleep(1000);

            Device Device = new Device(DB, DeviceList, Room, Pins, command);
            Device.start();

            Thread.sleep(1000);

            Sensor Sensor = new Sensor(DB, SensorList, Pins);
            Sensor.start();

            Thread.sleep(1000);

            Task Task = new Task(DB, TaskList, Sensor, Device);
            Task.start();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | SQLException | InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getIP() {
        try {
            Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet Result = Statement.executeQuery("select * from ip_address");

            Result.beforeFirst();
            while (Result.next()) {
                String DeviceName = Result.getString("DeviceName");
                String IPaddress = Result.getString("IPaddress");
                if (DeviceName.equals("Relay Switch")) {
                    RelayIP = IPaddress;
                    System.out.println("Relay Switch IP : " + RelayIP);
                } else if (DeviceName.equals("Camera 1")) {
                    Camera1IP = IPaddress;
                    System.out.println("Camera 1 IP : " + Camera1IP);
                } else if (DeviceName.equals("Camera 2")) {
                    Camera2IP = IPaddress;
                    System.out.println("Camera 2 IP : " + Camera2IP);
                }
            }
            Result.close();
            Statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
