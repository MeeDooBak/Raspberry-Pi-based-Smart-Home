package Sensor;

import Pins.*;
import Logger.*;
import java.sql.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.gpio.extension.mcp.*;

public class InfraredSensor implements Runnable {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private GpioPinDigitalInput PIN1;
    private GpioPinDigitalInput PIN2;
    private GpioPinDigitalInput PIN3;
    private GpioPinDigitalInput PIN4;
    private boolean ChackPIN1;
    private boolean ChackPIN2;
    private boolean ChackPIN3;
    private boolean ChackPIN4;

    // Get Sensor Information from Database
    public InfraredSensor(int SensorID, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

        // To Get Sensor 4 Pins From Raspberry PI
        getPin(GateNum);

        // Create Listeners For Sensors To Get State
        GateNum.getGPIO().addListener(Pin1Listener, PIN1);
        GateNum.getGPIO().addListener(Pin2Listener, PIN2);
        GateNum.getGPIO().addListener(Pin3Listener, PIN3);
        GateNum.getGPIO().addListener(Pin4Listener, PIN4);

        // Start Thread To Check Sensors State
        new Thread(this).start();
    }

    // Get Sensors 4 Pin From Raspberry PI
    private void getPin(PinsList GateNum) {
        // To Check if Pin is " GPIO " or " I2C "
        // Provision GPIO pins (# from Database) to (# from Database) from GPIO as an Input pin
        if (GateNum.getType().equals("GPIO")) {
            PIN1 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber())), PinPullResistance.PULL_UP);
            PIN2 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 1), PinPullResistance.PULL_UP);
            PIN3 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 2), PinPullResistance.PULL_UP);
            PIN4 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 3), PinPullResistance.PULL_UP);
        } else {
            // Check if the I2C Pin is " A " or " B " Part
            // Provision GPIO pins (# from Database) to (# from Database) from MCP23017 as an Input pin
            if (GateNum.getPI4Jnumber().contains("A")) {
                PIN1 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                PIN2 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 1], PinPullResistance.PULL_UP);
                PIN3 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 2], PinPullResistance.PULL_UP);
                PIN4 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 3], PinPullResistance.PULL_UP);
            } else {
                PIN1 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                PIN2 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 1], PinPullResistance.PULL_UP);
                PIN3 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 2], PinPullResistance.PULL_UP);
                PIN4 = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 3], PinPullResistance.PULL_UP);
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

    // The Thread 
    @Override
    public void run() {
        while (true) {
            try {
                // Check Form Listeners if Sensor 1 or 3 is True and Sensor 2 or 4 is True
                if ((ChackPIN1 || ChackPIN3) && (ChackPIN2 || ChackPIN4)) {
                    // Change Sensor State To True
                    SensorState = true;
                    SensorValue = 1;

                    // just To Print the Result
                    FileLogger.AddInfo("InfraredSensor " + SensorID + ", State = true");

                } else { // If Not
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

                // To Sleep For 1 Second
                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                // This Catch For DataBase Error 
                FileLogger.AddWarning("InfraredSensor " + SensorID + ", Error In DataBase\n" + ex);
            }
        }
    }

    // The Listener For Sensor 1 
    private final GpioPinListenerDigital Pin1Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            // Get New State If Sensor State Chenge 
            if (event.getState().isHigh()) {
                ChackPIN1 = true;
            } else {
                ChackPIN1 = false;
            }
        }
    };

    // The Listener For Sensor 2
    private final GpioPinListenerDigital Pin2Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            // Get New State If Sensor State Chenge 
            if (event.getState().isHigh()) {
                ChackPIN2 = true;
            } else {
                ChackPIN2 = false;
            }
        }
    };

    // The Listener For Sensor 3
    private final GpioPinListenerDigital Pin3Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            // Get New State If Sensor State Chenge 
            if (event.getState().isHigh()) {
                ChackPIN3 = true;
            } else {
                ChackPIN3 = false;
            }
        }
    };

    // The Listener For Sensor 4
    private final GpioPinListenerDigital Pin4Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            // Get New State If Sensor State Chenge 
            if (event.getState().isHigh()) {
                ChackPIN4 = true;
            } else {
                ChackPIN4 = false;
            }
        }
    };
}
