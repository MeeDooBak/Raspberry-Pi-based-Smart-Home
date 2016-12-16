package Device;

import Pins.*;
import Relay.*;
import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CBus;
import java.io.IOException;
import java.sql.*;
import java.util.logging.*;

public class WaterPump implements Runnable {

    private final int DeviceID;
    private final Connection DB;
    private boolean Busy;
    private boolean DeviceState;
    private GpioPinDigitalOutput PIN;

    public WaterPump(int DeviceID, PinsList GateNum, boolean DeviceState, boolean isStatusChanged, Connection DB) {
        this.DeviceID = DeviceID;
        this.DB = DB;
        this.Busy = false;
        this.getPin(GateNum);

        this.ChangeState(DeviceState, isStatusChanged, "UP");
    }

    private void getPin(PinsList GateNum) {
        try {
            GpioController GPIO = GpioFactory.getInstance();
            MCP23017GpioProvider Provider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);
            PIN = GPIO.provisionDigitalOutputPin(Provider, MCP23017Pin.ALL_A_PINS[Integer.parseInt(GateNum.getPI4Jnumber().substring(1))], PinState.HIGH);
        } catch (IOException ex) {
            System.out.println("Alarm " + DeviceID + ", Error In Getting Pin");
            Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void ChangeState(boolean DeviceState, boolean isStatusChanged, String UP_DOWN) {
        if (this.DeviceState != DeviceState && isStatusChanged) {
            try (PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps2.setBoolean(1, false);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();

                if (!Busy) {
                    this.DeviceState = DeviceState;

                    if (DeviceState) {
                        System.out.println("Fill in The Upper Water Tank");
                    } else {
                        if (UP_DOWN.equals("UP")) {
                            System.out.println("The Upper Water Tank is Full");
                        } else if (UP_DOWN.equals("DOWN")) {
                            System.out.println("The Lower Water Tank is Empty");
                        } else {
                            System.out.println("WaterPump No Massege");
                        }
                    }
                    new Thread(this).start();
                } else {
                    System.out.println("WaterPump Busy");
                }
            } catch (SQLException ex) {
                System.out.println("WaterPump " + DeviceID + ", Error In DataBase");
                Logger.getLogger(WaterPump.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run() {
        try {
            Busy = true;
            if (DeviceState) {
                PIN.low();
            } else {
                PIN.high();
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
            System.out.println("WaterPump " + DeviceID + ", State Change To " + DeviceState);
        } catch (SQLException ex) {
            System.out.println("WaterPump " + DeviceID + ", Error In DataBase");
            Logger.getLogger(WaterPump.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
