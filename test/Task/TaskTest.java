package Task;

import Device.*;
import Rooms.*;
import Sensor.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TaskTest {

    private Connection DB;
    private ArrayList<RoomList> RoomList;
    private ArrayList<SensorList> SensorList;
    private ArrayList<DeviceList> DeviceList;
    private ArrayList<TaskList> TaskList;
    private Task Task;

    public TaskTest() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");
            RoomList = new ArrayList();
            SensorList = new ArrayList();
            DeviceList = new ArrayList();
            TaskList = new ArrayList();
            Room Room = new Room(DB, RoomList);
            Room.start();
            Thread.sleep(1000);
            Device Device = new Device(DB, DeviceList, Room, null, null);
            Device.start();
            Thread.sleep(1000);
            Sensor Sensor = new Sensor(DB, SensorList, null);
            Sensor.start();
            Thread.sleep(1000);
            Task = new Task(DB, TaskList, Sensor, Device);
            Task.start();
            Thread.sleep(1000);
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
