package Sensor;

import Pins.*;
import Logger.*;
import java.sql.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.gpio.extension.mcp.*;

public class SmokeSensor implements GpioPinListenerDigital, SensorInterface {

    private boolean SensorState;
    private int SensorValue;
    private final int SensorID;
    private final int RoomID;
    private final String SensorName;
    private final Connection DB;
    private GpioPinDigitalInput PIN;

    // Get Sensor Information from Database
    public SmokeSensor(int SensorID, int RoomID, String SensorName, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.RoomID = RoomID;
        this.SensorName = SensorName;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

        // To Get Sensor Pin From Raspberry PI
        getPin(GateNum);

        // Create Listener For Sensor To Get State
        GateNum.getGPIO().addListener(this, PIN);
    }

    // Get Sensor Pin From Raspberry PI
    private void getPin(PinsList GateNum) {
        // To Check if Pin is " GPIO " or " I2C "
        // Provision GPIO pin (# from Database) from GPIO as an Input pin
        if (GateNum.getType().equals("GPIO")) {
            PIN = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber())), PinPullResistance.PULL_UP);
        } else {
            // Check if the I2C Pin is " A " or " B " Part
            // Provision GPIO pin (# from Database) from MCP23017 as an Input pin
            if (GateNum.getPI4Jnumber().contains("A")) {
                PIN = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
            } else {
                PIN = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
            }
        }
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

    // Return Sensor State if True Or False
    @Override
    public boolean getSensorState() {
        return SensorState;
    }

    // Return Sensor State if Has Value
    @Override
    public int getSensorValue() {
        return SensorValue;
    }

    // The Listener
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        try {
            // Get New State If Sensor State Chenge
            if (event.getState().isLow()) {
                // Change Sensor State To True
                SensorState = true;
                SensorValue = 1;

                // just To Print the Result
                FileLogger.AddInfo("SmokeSensor " + SensorID + ", State = true");

            } else {
                // Change Sensor State To False
                SensorState = false;
                SensorValue = 0;
            }

            // To Set the New Information in The Database
            // To set the New State 
            try (PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, SensorState);
                ps.setInt(2, SensorID);
                ps.executeUpdate();
            }

            // To set the New State Value
            try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setInt(1, SensorValue);
                ps.setInt(2, SensorID);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("SmokeSensor " + SensorID + ", Error In DataBase\n" + ex);
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
}
