package Sensor;

import Pins.*;
import com.pi4j.gpio.extension.mcp.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;

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

    public InfraredSensor(int SensorID, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

        getPin(GateNum);
        GateNum.getGPIO().addListener(Pin1Listener, PIN1);
        GateNum.getGPIO().addListener(Pin2Listener, PIN2);
        GateNum.getGPIO().addListener(Pin3Listener, PIN3);
        GateNum.getGPIO().addListener(Pin4Listener, PIN4);
        new Thread(this).start();
    }

    private void getPin(PinsList GateNum) {
        if (GateNum.getType().equals("GPIO")) {
            PIN1 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber())), PinPullResistance.PULL_UP);
            PIN2 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 1), PinPullResistance.PULL_UP);
            PIN3 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 2), PinPullResistance.PULL_UP);
            PIN4 = GateNum.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 3), PinPullResistance.PULL_UP);
        } else {
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

    public boolean getSensorState() {
        return SensorState;
    }

    public int getSensorValue() {
        return SensorValue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if ((ChackPIN1 || ChackPIN3) && (ChackPIN2 || ChackPIN4)) {
                    SensorState = true;
                    SensorValue = 1;
                    System.out.println("InfraredSensor " + SensorID + ", State = true");

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

                Thread.sleep(1000);
            } catch (SQLException | InterruptedException ex) {
                System.out.println("InfraredSensor " + SensorID + ", Error In DataBase");
                Logger.getLogger(InfraredSensor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private final GpioPinListenerDigital Pin1Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            if (event.getState().isHigh()) {
                ChackPIN1 = true;
            } else {
                ChackPIN1 = false;
            }
        }
    };

    private final GpioPinListenerDigital Pin2Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            if (event.getState().isHigh()) {
                ChackPIN2 = true;
            } else {
                ChackPIN2 = false;
            }
        }
    };

    private final GpioPinListenerDigital Pin3Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            if (event.getState().isHigh()) {
                ChackPIN3 = true;
            } else {
                ChackPIN3 = false;
            }
        }
    };

    private final GpioPinListenerDigital Pin4Listener = new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            if (event.getState().isHigh()) {
                ChackPIN4 = true;
            } else {
                ChackPIN4 = false;
            }
        }
    };
}
