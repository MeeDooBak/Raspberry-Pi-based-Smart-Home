package Sensor;

import Pins.*;
import com.pi4j.gpio.extension.mcp.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;

public class LightSensor implements GpioPinListenerDigital {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private GpioPinDigitalInput PIN;

    public LightSensor(int SensorID, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

        getPin(GateNum);
        GateNum.getGPIO().addListener(this, PIN);
    }

    private void getPin(PinsList GateNum) {
        if (GateNum.getType().equals("GPIO")) {
            PIN = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber())), PinPullResistance.PULL_UP);
        } else {
            if (GateNum.getPI4Jnumber().contains("A")) {
                PIN = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
            } else {
                PIN = GateNum.getGPIO().provisionDigitalInputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
            }
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
            if (event.getState().isLow()) {
                SensorState = true;
                SensorValue = 1;
                System.out.println("LightSensor " + SensorID + ", State = true");

            } else {
                SensorState = false;
                SensorValue = 0;
            }

            try (PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, SensorState);
                ps.setInt(2, SensorID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setInt(1, SensorValue);
                ps.setInt(2, SensorID);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println("LightSensor " + SensorID + ", Error In DataBase");
            Logger.getLogger(LightSensor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
