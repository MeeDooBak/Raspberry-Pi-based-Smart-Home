package Sensor;

import Pins.*;
import Logger.*;
import java.sql.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.gpio.extension.mcp.*;

public class MotionSensor implements GpioPinListenerDigital {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private GpioPinDigitalInput PIN;

    // Get Sensor Information from Database
    public MotionSensor(int SensorID, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
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

    // Return Sensor State if True Or False
    public boolean getSensorState() {
        return SensorState;
    }

    // Return Sensor State if Has Value
    public int getSensorValue() {
        return SensorValue;
    }

    // The Listener
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        try {
            // Get New State If Sensor State Chenge
            if (event.getState().isHigh()) {
                // Change Sensor State To True
                SensorState = true;
                SensorValue = 1;

                // just To Print the Result
                FileLogger.AddInfo("MotionSensor " + SensorID + ", State = true");

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
            FileLogger.AddWarning("MotionSensor " + SensorID + ", Error In DataBase\n" + ex);
        }
    }
}
