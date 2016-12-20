package Sensor;

import Pins.*;
import Logger.*;
import java.sql.*;
import java.util.*;
import com.pi4j.io.gpio.*;

public class Ultrasonic implements Runnable {

    private int SensorValue;
    private final int SensorID;
    private final int MaxValue;
    private final int MinValue;
    private final Connection DB;
    private GpioPinDigitalInput EchoPin;
    private GpioPinDigitalOutput TrigPin;

    // Get Sensor Information from Database
    public Ultrasonic(int SensorID, PinsList GateNum1, PinsList GateNum2, int SensorValue, int MaxValue, int MinValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorValue = SensorValue;
        this.MaxValue = MaxValue;
        this.MinValue = MinValue;

        // To Get Sensor Pins From Raspberry PI
        getPin(GateNum1, GateNum2);

        // Start Thread To Get Sensor State
        new Thread(this).start();
    }

    // Get Sensor Pin From Raspberry PI
    private void getPin(PinsList GateNum1, PinsList GateNum2) {
        // To Check if Pins is " GPIO " not Other
        if (GateNum1.getType().equals("GPIO") && GateNum2.getType().equals("GPIO")) {
            // Provision GPIO pins (# from Database) from GPIO as an Input pin
            EchoPin = GateNum1.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum1.getPI4Jnumber())));
            // Provision GPIO pins (# from Database) from GPIO as an Output pin and turn OFF
            TrigPin = GateNum2.getGPIO().provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum2.getPI4Jnumber())), PinState.LOW);
        }
    }

    // Return Sensor State Value
    public int getSensorValue() {
        return SensorValue;
    }

    // Get the Maximum Value For The Sensor
    public int getMaxValue() {
        return MaxValue;
    }

    // Get the Minimum Value For The Sensor
    public int getMinValue() {
        return MinValue;
    }

    // The Thread
    @Override
    public void run() {
        while (true) {
            try {
                // Set The Sensor State Value To -1
                SensorValue = -1;

                // Create Array To Set The Value
                int[] Data = new int[10];

                // For Loop For The Size Of Array
                for (int i = 0; i < Data.length; i++) {
                    // Put Trig Pin High for 10000 nanoseconds Than Set Back To Low
                    this.TrigPin.high();
                    Thread.sleep(0, 10000);
                    this.TrigPin.low();

                    // Wait for Echo pin To Be high
                    int countdown = 2100;
                    while (this.EchoPin.isLow() && countdown > 0) {
                        countdown--;
                    }

                    // Check If The are No Error To Continue
                    if (countdown > 0) {

                        // To Get Duration of the signal in micro seconds
                        countdown = 2100;
                        long start = System.nanoTime();
                        while (this.EchoPin.isHigh() && countdown > 0) {
                            countdown--;
                        }
                        long end = System.nanoTime();

                        // Check If The are No Error To Continue
                        if (countdown > 0) {
                            // Save The Sensor Value In The Array
                            Data[i] = ((int) ((float) Math.ceil((end - start) / 1000.0) * 340.29f / 20000));
                        }
                    }
                    // To Sleep For 50 Microsecond
                    Thread.sleep(50);
                }
                // To Sort the Array To Get The Mid Value
                Arrays.sort(Data);
                SensorValue = Data[5];

                // To Set the New Information in The Database
                // To set the New State 
                try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                    ps.setInt(1, SensorValue);
                    ps.setInt(2, SensorID);
                    ps.executeUpdate();
                }

                // To Sleep For 1 Second
                Thread.sleep(500);
            } catch (SQLException | InterruptedException ex) {
                // This Catch For DataBase Error 
                FileLogger.AddWarning("Ultrasonic " + SensorID + ", Error In DataBase\n" + ex);
            }
        }
    }
}
