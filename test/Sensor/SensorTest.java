package Sensor;

import java.sql.*;
import java.util.*;
import java.util.logging.*;
import org.junit.*;
import static org.junit.Assert.*;

public class SensorTest {

    private Connection DB;
    private ArrayList<SensorList> SensorList;
    private Sensor Sensor;

    public SensorTest() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");
            SensorList = new ArrayList();
            Sensor = new Sensor(DB, SensorList);
            Sensor.start();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(SensorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testIndexof() {
        System.out.println("indexof");
        assertEquals(0, Sensor.indexof(100));
        assertEquals(1, Sensor.indexof(101));
        assertEquals(2, Sensor.indexof(102));
        assertEquals(3, Sensor.indexof(103));
        assertEquals(4, Sensor.indexof(200));
        assertEquals(5, Sensor.indexof(201));
        assertEquals(6, Sensor.indexof(202));
    }

    @Test
    public void testGet() {
        System.out.println("Get");
        assertEquals(new SensorList(100, 101, "Clock", true, 1, -1, 0, DB).getSensorID(), Sensor.Get(100).getSensorID());
        assertEquals(new SensorList(101, 101, "Motion Sensor", true, 1, -1, 0, DB).getSensorID(), Sensor.Get(101).getSensorID());
        assertEquals(new SensorList(102, 101, "Temperature Sensor", true, 1, -1, 0, DB).getSensorID(), Sensor.Get(102).getSensorID());
        assertEquals(new SensorList(103, 101, "Light Sensor", true, 1, -1, 0, DB).getSensorID(), Sensor.Get(103).getSensorID());
        assertEquals(new SensorList(200, 102, "Clock", true, 1, -1, 0, DB).getSensorID(), Sensor.Get(200).getSensorID());
    }
}
