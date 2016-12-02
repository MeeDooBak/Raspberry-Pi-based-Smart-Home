package Sensor;

import Pins.*;
import com.pi4j.gpio.extension.mcp.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.io.i2c.*;
import java.io.*;

public class MotionSensor implements GpioPinListenerDigital {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private final GpioController GPIO;
    private GpioPinDigitalInput PIN;

    public MotionSensor(int SensorID, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;
        this.GPIO = GpioFactory.getInstance();

        getPin(GateNum);
        GPIO.addListener(this, PIN);
    }

    private void getPin(PinsList GateNum) {
        try {
            if (GateNum.getType().equals("GPIO")) {
                PIN = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber())), PinPullResistance.PULL_UP);
            } else {
                if (GateNum.getMCP23017().equals("0x21")) {
                    MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x21);
                    if (GateNum.getPI4Jnumber().contains("A")) {
                        PIN = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                    } else {
                        PIN = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                    }
                } else {
                    MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x24);
                    if (GateNum.getPI4Jnumber().contains("A")) {
                        System.out.println("Gate Number : " + GateNum.getPI4Jnumber() + " " + GateNum.getMCP23017());
                        PIN = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                    } else {
                        PIN = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("MotionSensor " + SensorID + ", Error In Getting Pin");
            Logger.getLogger(MotionSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean getSensorState() {
        return SensorState;
    }

    public int getSensorValue() {
        return SensorValue;
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        try {
            if (event.getState().isHigh()) {
                SensorState = true;
                SensorValue = 1;
                System.out.println("MotionSensor " + SensorID + ", State = true");

            } else {
                SensorState = false;
                SensorValue = 0;
            }

            try (PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? and SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, SensorState);
                ps.setInt(2, SensorValue);
                ps.setInt(3, SensorID);
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            System.out.println("MotionSensor " + SensorID + ", Error In DataBase");
            Logger.getLogger(MotionSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
