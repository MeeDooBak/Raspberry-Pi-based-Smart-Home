package SmartHome_Camera_WIN;

import Logger.*;
import java.sql.*;
import java.util.*;
import SmartHome_Camera_WIN.Pins.*;
import SmartHome_Camera_WIN.Camera.*;
import SmartHome_Camera_WIN.SendFile.*;

public class SmartHome {

    private static Connection DB;
    private static Pins Pins;
    private static Camera Camera;
    private static ArrayList<CameraList> CameraList;
    private static ArrayList<PinsList> PinsList;

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
            DB = DriverManager.getConnection("jdbc:mysql://192.168.1.99:3306/smarthome", "root", "root");
            System.out.println("Connection To Database Established.");

            // Create Logger To Save Log To Java File
            SendFile SendFile = new SendFile("192.168.1.99", 21, "root", "mlover054004", DB);
            System.out.println("Class Send File Executed Successfully.");

            // Create ArrayList To Store The Pin Class
            PinsList = new ArrayList();
            // Create Pin Class
            Pins = new Pins(DB, PinsList);
            // Start Getting Infrmation From The Database
            Pins.Start();
            System.out.println("Class Pins Executed Successfully.");

            // Create ArrayList To Store The Device Class
            CameraList = new ArrayList();
            // Create Device Class
            Camera = new Camera(DB, CameraList, Pins);
            // Start The Thread To Getting Infrmation From The Database
            Thread CameraThread = new Thread(Camera);
            CameraThread.start();
            // Wait Until The Thred Finish
            CameraThread.join();
            System.out.println("Class Camera Executed Successfully.");
            System.out.println("The System in Idle Mode, Waiting For Action");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | InterruptedException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Main Class, Error In DataBase\n" + ex);
        }
    }
}