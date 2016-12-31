package SmartHome;

import Pins.*;
import Task.*;
import Users.*;
import Relay.*;
import Rooms.*;
import Email.*;
import Sensor.*;
import Device.*;
import Logger.*;
import java.sql.*;
import RemoteControl.*;
import com.adventnet.snmp.snmp2.*;

public class SmartHome {

    private static Connection DB;
    private static Relay RelayQueue;
    private static Pins Pins;
    private static Room Room;
    private static User User;
    private static Device Device;
    private static Sensor Sensor;
    private static Task Task;

    public static void main(String[] args) {
        try {
            // Start The Application
            System.out.println("Start The Application...");

            // Create Logger To Save Log To Java File
            FileLogger FileLogger = new FileLogger();
            System.out.println("Class Java Logger Executed Successfully.");

            // Set The DataBase Calss
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            System.out.println("Connecting To Database.\n");
            // Start The Connection With The Database
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");
            System.out.println("Connection To Database Established.");

            // Start Get Relay Information From The Database
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from ip_address where ID = " + 3)) {
                Result.next();

                // Create Relay Class
                RelayQueue = new Relay(Result.getString("IPaddress"), 161, SnmpAPI.INTEGER, "private");
                System.out.println("Class Relay Executed Successfully.");

            } catch (SQLException ex) {
                // This Catch For DataBase Error 
                FileLogger.AddWarning("Relay, Error In DataBase\n" + ex);
            }

            // Create Mail Class To Send Email To User
            Mail Mail = new Mail("smart.home.msgs@gmail.com", "PiSmartHome");
            System.out.println("Class Email Executed Successfully.");

            // Create Logger To Save Log To Database
            SLogger SLogger = new SLogger(DB);
            System.out.println("Class Database Logger Executed Successfully.");

            // Create Pin Class
            Pins = new Pins(DB);
            // Start Getting Infrmation From The Database
            Pins.Start();
            System.out.println("Class Pins Executed Successfully.");

            // Create Room Class
            Room = new Room(DB);
            // Start Getting Infrmation From The Database
            Room.Start();
            System.out.println("Class Room Executed Successfully.");

            // Create User Class
            User = new User(DB, Room);
            // Start The Thread To Getting Infrmation From The Database and Update
            Thread UserThread = new Thread(User);
            UserThread.start();

            // To Sleep For 1 Second 
            Thread.sleep(1000);
            System.out.println("Class User Executed Successfully.");

            // Create Device Class
            Device = new Device(DB, Room, Pins, RelayQueue);
            // Start The Thread To Getting Infrmation From The Database
            Thread DeviceThread = new Thread(Device);
            DeviceThread.start();
            // Wait Until The Thred Finish
            DeviceThread.join();
            System.out.println("Class Device Executed Successfully.");

            // Create Sensor Class
            Sensor = new Sensor(DB, Pins);
            // Start Getting Infrmation From The Database
            Sensor.Start();
            System.out.println("Class Sensor Executed Successfully.");

            // Create Task Class
            Task = new Task(DB, Sensor, Device, Room, User, Sensor.Get("Smoke Detector"));
            // Start The Thread To Getting Infrmation From The Database
            Thread TaskThread = new Thread(Task);
            TaskThread.start();
            // Wait Until The Thred Finish
            TaskThread.join();
            System.out.println("Class Task Executed Successfully.");

            // Create Water Level Up Task Class To Fill The Upper Water Tank
            WaterLevelUpTask WaterLevelUpTask = new WaterLevelUpTask(Sensor.GetUltrasonic(), Device.Get("Water Pump"));
            System.out.println("Class Water Level Up Task Executed Successfully.");

            // Create Remote Control To Start Using Remote Control To Control Devices
            RemoteControl RemoteControl = new RemoteControl(Device);
            System.out.println("Class Remote Control Executed Successfully.");

            // Start Update Thread For Devices and Task
            new Thread(Status).start();
            System.out.println("The System in Idle Mode, Waiting For Action");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | InterruptedException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Main Class, Error In DataBase\n" + ex);
        }
    }

    // The Update Thread For Devices and Task
    private static final Runnable Status = new Runnable() {

        @Override
        public void run() {
            // Start Get The Update
            while (true) {
                // Start Get Update Information From The Database
                try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet Result = Statement.executeQuery("select * from table_status")) {

                    Result.beforeFirst();
                    // While Loop For All Row in DataBase
                    while (Result.next()) {
                        // Get Table ID
                        int TableID = Result.getInt("TableID");
                        // Get Table Name
                        String TableName = Result.getString("TableName");
                        // Get is The Table Updated
                        boolean isTableUpdated = Result.getBoolean("isTableUpdated");

                        // Check if The Table Updated
                        if (isTableUpdated) {
                            // Check if the Table Name is Device 
                            if (TableName.equals("Device")) {
                                // Start Device Thread To Get Update
                                new Thread(Device).start();
                                System.out.println("Some Devices Has Changed The state.");
                            } else if (TableName.equals("Task")) { // Check if the Table Name is Task 
                                // Start Task Thread To Get Update
                                new Thread(Task).start();
                                System.out.println("Some Task Has Changed.");
                            }

                            // To set the That Table Has Been Updated in Java
                            try (PreparedStatement ps2 = DB.prepareStatement("update table_status set isTableUpdated = ? where TableID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                                ps2.setBoolean(1, false);
                                ps2.setInt(2, TableID);
                                ps2.executeUpdate();
                            }
                        }
                    }

                    // To Sleep For 1 Second
                    Thread.sleep(1000);
                } catch (SQLException | InterruptedException ex) {
                    // This Catch For DataBase Error 
                    FileLogger.AddWarning("Main Class, Error In DataBase\n" + ex);
                }
            }
        }
    };
}
