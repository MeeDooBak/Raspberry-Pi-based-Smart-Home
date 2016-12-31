package Sensor;

import Pins.*;
import Logger.*;
import java.sql.*;
import java.util.*;
import com.pi4j.wiringpi.*;

public class TemperatureSensor implements Runnable, SensorInterface {

    private int SensorValue;
    private final int[] Data = new int[10];
    private final int SensorID;
    private final int RoomID;
    private final String SensorName;
    private final Connection DB;
    private final int PIN;
    private final int MAXTIMINGS = 85;

    // Get Sensor Information from Database
    public TemperatureSensor(int SensorID, int RoomID, String SensorName, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.RoomID = RoomID;
        this.SensorName = SensorName;
        this.SensorValue = SensorValue;

        // Get Pin Number From Pin Class
        PIN = Integer.parseInt(GateNum.getPI4Jnumber());

        // setup wiringPi
        GpioUtil.export(PIN, GpioUtil.DIRECTION_OUT);

        // Start Thread To Get Sensor State
        new Thread(this).start();
    }
    
    // Get Sensor ID
    @Override
    public int getSensorID() {
        return SensorID;
    }

    // Get Sensor Room ID
    @Override
    public int getRoomID() {
        return RoomID;
    }

    // Get Sensor Name
    @Override
    public String getSensorName() {
        return SensorName;
    }

    // Return Sensor State Value
    @Override
    public int getSensorValue() {
        return SensorValue;
    }

    // The Thread
    @Override
    public void run() {
        while (true) {
            try {
                // Get Sensor State
                int laststate = Gpio.HIGH;
                int j = 0;
                // Fill Array With 0
                Arrays.fill(Data, 0);

                // Pull Pin Down For 18 Milliseconds
                Gpio.pinMode(PIN, Gpio.OUTPUT);
                Gpio.digitalWrite(PIN, Gpio.LOW);
                Gpio.delay(18);

                // Then Pull It UP
                Gpio.digitalWrite(PIN, Gpio.HIGH);

                // Prepare To Read The Pin
                Gpio.pinMode(PIN, Gpio.INPUT);

                // Detect Change and Read Data
                for (int i = 0; i < MAXTIMINGS; i++) {
                    int counter = 0;
                    while (Gpio.digitalRead(PIN) == laststate) {
                        counter++;
                        Gpio.delayMicroseconds(1);
                        if (counter == 255) {
                            break;
                        }
                    }
                    laststate = Gpio.digitalRead(PIN);

                    if (counter == 255) {
                        break;
                    }

                    // Ignore First 3 Transitions
                    if ((i >= 4) && (i % 2 == 0)) {
                        Data[j / 8] <<= 1;
                        if (counter > 16) {
                            Data[j / 8] |= 1;
                        }
                        j++;
                    }
                }

                // Check We Read 40 bits (8bit x 5 ) + Verify Checksum In The Last Byte
                // Print It Out If Data is Good
                if ((j >= 40) && (Data[4] == ((Data[0] + Data[1] + Data[2] + Data[3]) & 0xFF))) {
                    SensorValue = (int) Math.ceil(Data[2] + 0.1f * Data[3]);

                    // To Set the New Information in The Database
                    // To set the New State 
                    try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                        ps.setInt(1, SensorValue);
                        ps.setInt(2, SensorID);
                        ps.executeUpdate();
                    }
                }

                // To Sleep For 1 Second
                Thread.sleep(2000);
            } catch (Exception ex) {
                // This Catch For DataBase Error 
                FileLogger.AddWarning("TemperatureSensor " + SensorID + ", Error In DataBase\n" + ex);

                // If There an Error Restart The Thread
                new Thread(this).start();
            }
        }
    }

    @Override
    public int getMaxValue() {
        return 0;
    }

    @Override
    public int getMinValue() {
        return 0;
    }

    @Override
    public boolean getSensorState() {
        return false;
    }
}
