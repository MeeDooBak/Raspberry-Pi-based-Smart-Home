package Task;

import Device.*;
import Email.Mail;
import Pins.*;
import Rooms.*;
import Sensor.*;
import Users.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TaskTest {

    private Connection DB;
    private ArrayList<RoomList> RoomList;
    private ArrayList<UserList> UserList;
    private ArrayList<SensorList> SensorList;
    private ArrayList<DeviceList> DeviceList;
    private ArrayList<TaskList> TaskList;
    private ArrayList<PinsList> PinsList;

    private Task Task;

    public TaskTest() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");
            RoomList = new ArrayList();
            UserList = new ArrayList();
            SensorList = new ArrayList();
            DeviceList = new ArrayList();
            TaskList = new ArrayList();
            PinsList = new ArrayList();

            Pins Pins = new Pins(DB, PinsList);
            Pins.Start();

            Room Room = new Room(DB, RoomList);
            Room.Start();

            User User = new User(DB, UserList, Room);
            new Thread(User).start();
            Thread.sleep(1000);

            Device Device = new Device(DB, DeviceList, Room, null, null);
            Thread DeviceThread = new Thread(Device);
            DeviceThread.start();

            while (true) {
                if (!DeviceThread.isAlive()) {
                    SensorList = new ArrayList();
                    Sensor Sensor = new Sensor(DB, SensorList, Pins);
                    Sensor.Start();

                    TaskList = new ArrayList();
                    Task = new Task(DB, TaskList, Sensor, Device, Room, User, null);
                    new Thread(Task).start();

                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | SQLException | InterruptedException ex) {
            Logger.getLogger(TaskTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testIndexof() {
        System.out.println("indexof");
        assertEquals(0, Task.indexof(6));
        assertEquals(1, Task.indexof(18));
        assertEquals(2, Task.indexof(19));
        assertEquals(3, Task.indexof(20));
        assertEquals(4, Task.indexof(21));
        assertEquals(5, Task.indexof(22));
        assertEquals(6, Task.indexof(24));
    }
}
