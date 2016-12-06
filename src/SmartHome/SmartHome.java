package SmartHome;

import Device.*;
import Pins.*;
import Relay.*;
import Rooms.*;
import Sensor.*;
import Task.*;
import Users.*;
import com.adventnet.snmp.snmp2.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class SmartHome {

    private static Connection DB;
    private static Relay RelayQueue;

    private static Pins Pins;
    private static Room Room;
    private static User User;
    private static Device Device;
    private static Sensor Sensor;
    private static Task Task;

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

            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from ip_address where ID = " + 3)) {
                Result.next();
                RelayQueue = new Relay(Result.getString("IPaddress"), 161, SnmpAPI.INTEGER, "private");

            } catch (SQLException ex) {
                System.out.println("Relay, Error In DataBase");
                Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
            }

            PinsList = new ArrayList();
            Pins = new Pins(DB, PinsList);
            Pins.Start();

            RoomList = new ArrayList();
            Room = new Room(DB, RoomList);
            Room.Start();

            UserList = new ArrayList();
            User = new User(DB, UserList, Room);
            new Thread(User).start();
            Thread.sleep(1000);

            DeviceList = new ArrayList();
            Device = new Device(DB, DeviceList, Room, Pins, RelayQueue);
            Thread DeviceThread = new Thread(Device);
            DeviceThread.start();

            while (true) {
                if (!DeviceThread.isAlive()) {
                    SensorList = new ArrayList();
                    Sensor = new Sensor(DB, SensorList, Pins);
                    Sensor.Start();

                    TaskList = new ArrayList();
                    Task = new Task(DB, TaskList, Sensor, Device, Room, User, null);
                    Thread TaskThread = new Thread(Task);
                    TaskThread.start();

                    while (true) {
                        if (!TaskThread.isAlive()) {
                            break;
                        }
                    }
                    break;
                }
            }
            new Thread(Status).start();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | InterruptedException ex) {
            Logger.getLogger(SmartHome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final Runnable Status = new Runnable() {

        @Override
        public void run() {
            System.out.println("Start...");
            while (true) {
                try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result = Statement.executeQuery("select * from table_status")) {

                    Result.beforeFirst();
                    while (Result.next()) {
                        int TableID = Result.getInt("TableID");
                        String TableName = Result.getString("TableName");
                        boolean isTableUpdated = Result.getBoolean("isTableUpdated");

                        if (isTableUpdated) {
                            if (TableName.equals("Device")) {
                                new Thread(Device).start();
                            } else if (TableName.equals("Task")) {
                                new Thread(Task).start();
                            }
                            try (PreparedStatement ps2 = DB.prepareStatement("update table_status set isTableUpdated = ? where TableID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                                ps2.setBoolean(1, false);
                                ps2.setInt(2, TableID);
                                ps2.executeUpdate();
                            }
                        }
                    }
                    Thread.sleep(1000);
                } catch (SQLException | InterruptedException ex) {
                    Logger.getLogger(SmartHome.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
}
