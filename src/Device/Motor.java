package Device;

import Pins.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.component.motor.impl.*;
import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.*;

public class Motor implements Runnable {

    private final int DeviceID;
    private final String Name;
    private final Connection DB;
    private final int MaxValue;

    private GpioStepperMotorComponent Motor;
    private boolean Busy;
    private boolean DeviceState;
    private int StepperMotorMoves;

    public Motor(int DeviceID, String Name, PinsList GateNum1, PinsList GateNum2, PinsList GateNum3, PinsList GateNum4,
            int MaxValue, boolean DeviceState, boolean isStatusChanged, int StepperMotorMoves, Connection DB) {
        this.DeviceID = DeviceID;
        this.Name = Name;
        this.DB = DB;
        this.MaxValue = MaxValue;
        this.Busy = false;

        getPin(GateNum1, GateNum2, GateNum3, GateNum4);
        ChangeState(DeviceState, StepperMotorMoves, isStatusChanged);
    }

    private void getPin(PinsList GateNum1, PinsList GateNum2, PinsList GateNum3, PinsList GateNum4) {
        try {
            GpioController GPIO = GpioFactory.getInstance();
            GpioPinDigitalOutput[] PINS;

            if (GateNum1.getMCP23017().equals("0x20")) {
                MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x20);
                if (GateNum1.getPI4Jnumber().contains("A")) {
                    PINS = new GpioPinDigitalOutput[]{
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum1.getPI4Jnumber().substring(1))], PinState.LOW),
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum2.getPI4Jnumber().substring(1))], PinState.LOW),
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum3.getPI4Jnumber().substring(1))], PinState.LOW),
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum4.getPI4Jnumber().substring(1))], PinState.LOW)};
                } else {
                    PINS = new GpioPinDigitalOutput[]{
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum1.getPI4Jnumber().substring(1))], PinState.LOW),
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum2.getPI4Jnumber().substring(1))], PinState.LOW),
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum3.getPI4Jnumber().substring(1))], PinState.LOW),
                        GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum4.getPI4Jnumber().substring(1))], PinState.LOW)};
                }
            } else {
                MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);
                PINS = new GpioPinDigitalOutput[]{
                    GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum1.getPI4Jnumber().substring(1))], PinState.LOW),
                    GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum2.getPI4Jnumber().substring(1))], PinState.LOW),
                    GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum3.getPI4Jnumber().substring(1))], PinState.LOW),
                    GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum4.getPI4Jnumber().substring(1))], PinState.LOW)};
            }
            GPIO.setShutdownOptions(true, PinState.LOW, PINS);

            Motor = new GpioStepperMotorComponent(PINS);
            Motor.setStepInterval(2);
            Motor.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
            Motor.setStepsPerRevolution(2038);

        } catch (IOException ex) {
            System.out.println("Motor " + DeviceID + ", Error In Getting Pin");
            Logger.getLogger(Motor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void ChangeState(boolean DeviceState, int StepperMotorMoves, boolean isStatusChanged) {
        if (isStatusChanged) {
            try (PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps2.setBoolean(1, false);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();

                if (!Busy) {
                    this.DeviceState = DeviceState;
                    this.StepperMotorMoves = StepperMotorMoves;
                    new Thread(this).start();
                }
            } catch (SQLException ex) {
                System.out.println("Motor " + DeviceID + ", Error In DataBase");
                Logger.getLogger(Motor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {
        try {
            Busy = true;
            if (DeviceState) {
                if (Name.equals("Curtains")) {
                    for (int i = StepperMotorMoves; i > -1; i--) {
                        Motor.step(1);
                        StepperMotorMoves = i;
                    }
                } else {
                    for (int i = StepperMotorMoves; i < MaxValue; i++) {
                        Motor.step(-1);
                        StepperMotorMoves = i;
                    }
                }
            } else {
                if (Name.equals("Curtains")) {
                    for (int i = StepperMotorMoves; i < MaxValue; i++) {
                        Motor.step(-1);
                        StepperMotorMoves = i;
                    }
                } else {
                    for (int i = StepperMotorMoves; i > -1; i--) {
                        Motor.step(1);
                        StepperMotorMoves = i;
                    }
                }
            }

            try (PreparedStatement ps = DB.prepareStatement("update device_stepper_motor set StepperMotorMoves = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setInt(1, StepperMotorMoves);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = DB.prepareStatement("update device set lastStatusChange = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                Timestamp NewStatusChange = new Timestamp(new java.util.Date().getTime());
                ps.setTimestamp(1, NewStatusChange);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = DB.prepareStatement("update device set DeviceState = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, DeviceState);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            Busy = false;
            System.out.println("Motor " + DeviceID + ", State Change To " + DeviceState);

        } catch (SQLException ex) {
            System.out.println("Motor " + DeviceID + ", Error In DataBase");
            Logger.getLogger(Motor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
