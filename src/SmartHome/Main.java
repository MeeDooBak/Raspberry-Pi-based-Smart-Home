package SmartHome;

import Device.*;
import Rooms.*;
import Sensor.*;
import Task.*;
import Users.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Main {

    private static Connection DB;

    private static ArrayList<RoomList> RoomList;
    private static ArrayList<UserList> UserList;
    private static ArrayList<SensorList> SensorList;
    private static ArrayList<DeviceList> DeviceList;
    private static ArrayList<TaskList> TaskList;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");

            RoomList = new ArrayList();
            UserList = new ArrayList();
            SensorList = new ArrayList();
            DeviceList = new ArrayList();
            TaskList = new ArrayList();

            Room Room = new Room(DB, RoomList);
            Room.start();

            User User = new User(DB, UserList, Room);
            User.start();

            Device Device = new Device(DB, DeviceList, Room, "192.168.1.102");
            Device.start();

            Sensor Sensor = new Sensor(DB, SensorList);
            Sensor.start();

            Task Task = new Task(DB, TaskList, Sensor, Device);
            Task.start();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
