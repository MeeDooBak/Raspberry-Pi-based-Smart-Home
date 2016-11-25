package SmartHome;

import AutomaticFunctions.*;
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

            WaterLevel WaterLevel = new WaterLevel(Sensor.Get(1003), Device.Get(1005));
            WaterLevel.start();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getIP() {
        try {
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from ip_address")) {

                Result.beforeFirst();
                while (Result.next()) {
                    String DeviceName = Result.getString("DeviceName");
                    String IPaddress = Result.getString("IPaddress");
                    if (DeviceName.equals("Relay Switch")) {
                        RelayIP = IPaddress;
                        System.out.println("Relay Switch IP : " + RelayIP);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
