package Device;

import Rooms.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import org.junit.*;
import static org.junit.Assert.*;

public class DeviceTest {

    private Connection DB;
    private static ArrayList<DeviceList> DeviceList;
    private static ArrayList<RoomList> RoomList;
    private Device Device;
    private Room Room;

    public DeviceTest() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");
            DeviceList = new ArrayList();
            RoomList = new ArrayList();
            Room = new Room(DB, RoomList);
            Room.start();
            Device = new Device(DB, DeviceList, Room, null);
            Device.start();
            Thread.sleep(1000);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | InterruptedException ex) {
            Logger.getLogger(DeviceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testIndexof() {
        System.out.println("indexof");
        assertEquals(0, Device.indexof(101));
        assertEquals(1, Device.indexof(102));
        assertEquals(2, Device.indexof(103));
        assertEquals(3, Device.indexof(104));
        assertEquals(4, Device.indexof(201));
        assertEquals(5, Device.indexof(202));
        assertEquals(6, Device.indexof(203));
    }
}
