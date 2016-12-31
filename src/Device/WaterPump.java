package Device;

import Pins.*;
import Logger.*;
import Rooms.RoomList;
import java.sql.*;
import com.pi4j.io.gpio.*;
import com.pi4j.gpio.extension.mcp.*;

public class WaterPump implements Runnable, DeviceInterface {

    private final int DeviceID;
    private final RoomList Room;
    private final String DeviceName;
    private final Connection DB;
    private boolean Busy;
    private boolean DeviceState;
    private GpioPinDigitalOutput PIN;

    // Get Device Information from Database
    public WaterPump(int DeviceID, RoomList Room, String DeviceName, PinsList GateNum, boolean DeviceState, boolean isStatusChanged, Connection DB) {
        this.DeviceID = DeviceID;
        this.DeviceName = DeviceName;
        this.Room = Room;
        // add This Device For Given Room
        Room.getDeviceList().add(DeviceID);
        this.DB = DB;
        this.Busy = false;

        // To Get Device Pin From Raspberry PI
        this.getPin(GateNum);

        // To Make the Change For the First Time
        this.ChangeState(DeviceState, "UP");
    }

    // Get Device Pin From Raspberry PI
    private void getPin(PinsList GateNum) {
        // Provision GPIO pin (# from Database) from MCP23017 as an output pin and turn OFF
        PIN = GateNum.getGPIO().provisionDigitalOutputPin(GateNum.getMCP23017(), MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinState.HIGH);
        // this will ensure that the motor is stopped when the program terminates
        GateNum.getGPIO().setShutdownOptions(true, PinState.HIGH, PIN);
    }

    @Override
    public int getDeviceID() {
        return DeviceID;
    }

    @Override
    public RoomList getRoom() {
        return Room;
    }

    @Override
    public String getDeviceName() {
        return DeviceName;
    }

    // To Change Device State
    @Override
    public final void ChangeState(boolean DeviceState, String UP_DOWN) {
        // To Check if the Current Device State is not equl The New Device State
        // and to check is it Change from DataBase
        // if it is equl just ignore it
        if (this.DeviceState != DeviceState) {
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

                    // just To Print the Information
                    if (DeviceState) {
                        FileLogger.AddInfo("Fill in The Upper Water Tank");
                    } else {
                        if (UP_DOWN.equals("UP")) {
                            FileLogger.AddInfo("The Upper Water Tank is Full");
                        } else if (UP_DOWN.equals("DOWN")) {
                            FileLogger.AddInfo("The Lower Water Tank is Empty");
                        } else {
                            FileLogger.AddInfo("WaterPump No Massege");
                        }
                    }
                    new Thread(this).start();
                }
            } catch (SQLException ex) {
                // This Catch For DataBase Error
                FileLogger.AddWarning("WaterPump " + DeviceID + ", Error In DataBase\n" + ex);
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
                PIN.low();
            } else { // Here to Change the Device state to OFF
                PIN.high();
            }

            // To Set the New Information in The Database
            // To set the New Time
            try (PreparedStatement ps = DB.prepareStatement("update device set lastStatusChange = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
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
            FileLogger.AddInfo("WaterPump " + DeviceID + ", State Change To " + DeviceState);

        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("WaterPump " + DeviceID + ", Error In DataBase\n" + ex);
        }
    }

    @Override
    public void ChangeState(boolean DeviceState) {
    }

    @Override
    public void ChangeState(boolean DeviceState, int StepperMotorMoves) {
    }

    @Override
    public void ChangeState(boolean DeviceState, int AlarmDuration, int AlarmInterval) {
    }

    @Override
    public boolean getDeviceState() {
        return false;
    }

    @Override
    public boolean Capture(int TakeImage) {
        return false;
    }

    @Override
    public boolean Record(int Minute) {
        return false;
    }
}
