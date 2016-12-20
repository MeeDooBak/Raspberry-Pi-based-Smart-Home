package Device;

import Pins.*;
import Logger.*;
import java.sql.*;
import com.pi4j.io.gpio.*;
import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.component.motor.impl.*;

public class Motor implements Runnable {

    private final int DeviceID;
    private final String Name;
    private final Connection DB;
    private final int MaxValue;

    private GpioStepperMotorComponent Motor;
    private boolean Busy;
    private boolean DeviceState;
    private int StepperMotorMoves;

    // Get Device Information from Database
    public Motor(int DeviceID, String Name, PinsList GateNum1, PinsList GateNum2, PinsList GateNum3, PinsList GateNum4,
            int MaxValue, boolean DeviceState, boolean isStatusChanged, int StepperMotorMoves, Connection DB) {
        this.DeviceID = DeviceID;
        this.Name = Name;
        this.DB = DB;
        this.MaxValue = MaxValue;
        this.Busy = false;

        // To Get Device 4 Pins From Raspberry PI
        getPin(GateNum1, GateNum2, GateNum3, GateNum4);

        // To Make the Change For the First Time
        ChangeState(DeviceState, StepperMotorMoves, isStatusChanged);
    }

    // Get Device 4 Pin From Raspberry PI
    private void getPin(PinsList GateNum1, PinsList GateNum2, PinsList GateNum3, PinsList GateNum4) {
        // Provision GPIO pins (# from Database) to (# from Database) from MCP23017 as an output pin and turn OFF
        GpioPinDigitalOutput[] PINS;
        if (GateNum1.getPI4Jnumber().contains("A")) {
            PINS = new GpioPinDigitalOutput[]{
                GateNum1.getGPIO().provisionDigitalOutputPin(GateNum1.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum1.getPI4Jnumber().substring(1))], PinState.LOW),
                GateNum2.getGPIO().provisionDigitalOutputPin(GateNum2.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum2.getPI4Jnumber().substring(1))], PinState.LOW),
                GateNum3.getGPIO().provisionDigitalOutputPin(GateNum3.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum3.getPI4Jnumber().substring(1))], PinState.LOW),
                GateNum4.getGPIO().provisionDigitalOutputPin(GateNum4.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum4.getPI4Jnumber().substring(1))], PinState.LOW)};
        } else {
            PINS = new GpioPinDigitalOutput[]{
                GateNum1.getGPIO().provisionDigitalOutputPin(GateNum1.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum1.getPI4Jnumber().substring(1))], PinState.LOW),
                GateNum2.getGPIO().provisionDigitalOutputPin(GateNum2.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum2.getPI4Jnumber().substring(1))], PinState.LOW),
                GateNum3.getGPIO().provisionDigitalOutputPin(GateNum3.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum3.getPI4Jnumber().substring(1))], PinState.LOW),
                GateNum4.getGPIO().provisionDigitalOutputPin(GateNum4.getMCP23017(), MCP23017Pin.ALL_B_PINS[Integer.parseInt(GateNum4.getPI4Jnumber().substring(1))], PinState.LOW)};
        }

        // this will ensure that the motor is stopped when the program terminates
        GateNum1.getGPIO().setShutdownOptions(true, PinState.LOW, PINS);

        // create motor component
        Motor = new GpioStepperMotorComponent(PINS);

        // define stepper parameters before attempting to control motor
        Motor.setStepInterval(2);

        // create byte array to demonstrate a single-step sequencing
        Motor.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});

        // There are 32 steps per revolution
        // Gear reduction is actually: = 63.683950617
        // This means is that there are really 32 * 63.683950617 steps per revolution =  2037.88641975 ~ 2038 steps!
        Motor.setStepsPerRevolution(2038);
    }

    // Return Device State
    public boolean getDeviceState() {
        return DeviceState;
    }

    // To Change Device State
    public final void ChangeState(boolean DeviceState, int StepperMotorMoves, boolean isStatusChanged) {
        // To Check if the Current Device State is not equl The New Device State
        // and to check is it Change from DataBase
        // if it is equl just ignore it
        if (this.DeviceState != DeviceState && isStatusChanged) {
            // To tell Database that java change the Device State 
            try (PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps2.setBoolean(1, false);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();

                // To Check If the Thread is not working 
                // If it is Working just ignore it.
                if (!Busy) {
                    // just to Get the New Date and Start The Thread
                    this.DeviceState = DeviceState;
                    this.StepperMotorMoves = StepperMotorMoves;
                    new Thread(this).start();
                }
            } catch (SQLException ex) {
                // This Catch For DataBase Error
                FileLogger.AddWarning("Motor " + DeviceID + ", Error In DataBase\n" + ex);
            }
        }
    }

    // The Thread 
    @Override
    public void run() {
        try {
            // The Tell The Method the Thread is Busy.
            Busy = true;

            // Here to Change the Device state to ON
            if (DeviceState) {
                // If the Motor is Curtains 
                if (Name.equals("Curtains")) {
                    // Start from Old Motor Move until -1 to Open The Curtains
                    for (int i = StepperMotorMoves; i > -1; i--) {
                        Motor.step(1);
                        StepperMotorMoves = i;
                    }
                } else { // If the Motor is Garage Door
                    // Start from Old Motor Move until the Max Value to Open The Garage Door
                    for (int i = StepperMotorMoves; i < MaxValue; i++) {
                        Motor.step(-1);
                        StepperMotorMoves = i;
                    }
                }

            } else { // Here to Change the Device state to OFF
                // If the Motor is Curtains 
                if (Name.equals("Curtains")) {
                    // Start from Old Motor Move until the Max Value to Close The Curtains
                    for (int i = StepperMotorMoves; i < MaxValue; i++) {
                        Motor.step(-1);
                        StepperMotorMoves = i;
                    }
                } else { // If the Motor is Garage Door
                    // Start from Old Motor Move until -1 to Close The Garage Door
                    for (int i = StepperMotorMoves; i > -1; i--) {
                        Motor.step(1);
                        StepperMotorMoves = i;
                    }
                }
            }

            // To Set the New Information in The Database
            // To set the New Motor Moves
            try (PreparedStatement ps = DB.prepareStatement("update device_stepper_motor set StepperMotorMoves = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setInt(1, StepperMotorMoves);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            // To set the New State 
            try (PreparedStatement ps = DB.prepareStatement("update device set DeviceState = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setBoolean(1, DeviceState);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();
            }

            // The Tell The Method the Thread is not Busy.
            Busy = false;

            // just To Print the Result
            FileLogger.AddInfo("Motor " + DeviceID + ", State Change To " + DeviceState);

        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Motor " + DeviceID + ", Error In DataBase\n" + ex);
        }
    }
}
