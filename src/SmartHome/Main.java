package SmartHome;

import Device.*;
import Sensor.*;
import Task.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Main {

    private static Connection DB;

    private static ArrayList<SensorList> SensorList;
    private static ArrayList<DeviceList> DeviceList;
    private static ArrayList<TaskList> TaskList;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");

            Device Device = new Device(DB, DeviceList, "192.168.1.102");
            Device.start();

            Sensor Sensor = new Sensor(DB, SensorList);
            Sensor.start();

            Task Task = new Task(DB, TaskList, SensorList, DeviceList);
            Task.start();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
