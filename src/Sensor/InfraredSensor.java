package Sensor;

import Pins.*;
import com.pi4j.gpio.extension.mcp.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.io.i2c.*;

import java.io.*;

public class InfraredSensor implements Runnable {

    private boolean SensorState;
    private int SensorValue;

    private final int SensorID;
    private final Connection DB;
    private final GpioController GPIO;
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
        this.GPIO = GpioFactory.getInstance();

        getPin(GateNum);
        GPIO.addListener(Pin1Listener, PIN1);
        GPIO.addListener(Pin2Listener, PIN2);
        GPIO.addListener(Pin3Listener, PIN3);
        GPIO.addListener(Pin4Listener, PIN4);
        new Thread(this).start();
    }

    private void getPin(PinsList GateNum) {
        try {
            if (GateNum.getType().equals("GPIO")) {
                PIN1 = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber())), PinPullResistance.PULL_UP);
                PIN2 = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 1), PinPullResistance.PULL_UP);
                PIN3 = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 2), PinPullResistance.PULL_UP);
                PIN4 = GPIO.provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum.getPI4Jnumber()) + 3), PinPullResistance.PULL_UP);
            } else {
                if (GateNum.getMCP23017().equals("0x21")) {
                    MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x21);
                    if (GateNum.getPI4Jnumber().contains("A")) {
                        PIN1 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                        PIN2 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 1], PinPullResistance.PULL_UP);
                        PIN3 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 2], PinPullResistance.PULL_UP);
                        PIN4 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 3], PinPullResistance.PULL_UP);
                    } else {
                        PIN1 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                        PIN2 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 1], PinPullResistance.PULL_UP);
                        PIN3 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 2], PinPullResistance.PULL_UP);
                        PIN4 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 3], PinPullResistance.PULL_UP);
                    }
                } else {
                    MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x24);
                    if (GateNum.getPI4Jnumber().contains("A")) {
                        PIN1 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                        PIN2 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 1], PinPullResistance.PULL_UP);
                        PIN3 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 2], PinPullResistance.PULL_UP);
                        PIN4 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 3], PinPullResistance.PULL_UP);
                    } else {
                        System.out.println(MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 3].getName());
                        PIN1 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinPullResistance.PULL_UP);
                        PIN2 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 1], PinPullResistance.PULL_UP);
                        PIN3 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 2], PinPullResistance.PULL_UP);
                        PIN4 = GPIO.provisionDigitalInputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1)) + 3], PinPullResistance.PULL_UP);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("InfraredSensor " + SensorID + ", Error In Getting Pin");
            Logger.getLogger(InfraredSensor.class.getName()).log(Level.SEVERE, null, ex);
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
                if ((ChackPIN1 || ChackPIN2) && (ChackPIN3 || ChackPIN4)) {
                    SensorState = true;
                    SensorValue = 1;
                    System.out.println("InfraredSensor " + SensorID + ", State = true");

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
