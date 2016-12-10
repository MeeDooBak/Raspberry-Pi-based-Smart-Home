package Device;

import Pins.*;
import com.pi4j.gpio.extension.mcp.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.*;
import java.io.IOException;
import java.sql.*;
import java.util.logging.*;

public class Alarm implements Runnable {

    private GpioPinDigitalOutput PIN;
    private final int DeviceID;
    private final Connection DB;
    private boolean Busy;
    private boolean DeviceState;
    private int AlarmDuration;
    private int AlarmInterval;

    public Alarm(int DeviceID, PinsList GateNum, boolean DeviceState, boolean isStatusChanged, int AlarmDuration, int AlarmInterval, Connection DB) {
        this.DeviceID = DeviceID;
        this.DB = DB;
        this.Busy = false;

        getPin(GateNum);
        ChangeState(DeviceState, AlarmDuration, AlarmInterval, isStatusChanged);
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

    public final void ChangeState(boolean DeviceState, int AlarmDuration, int AlarmInterval, boolean isStatusChanged) {
        if (this.DeviceState != DeviceState && isStatusChanged) {
            try (PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                ps2.setBoolean(1, false);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();

                if (!Busy) {
                    this.DeviceState = DeviceState;
                    this.AlarmDuration = AlarmDuration;
                    this.AlarmInterval = AlarmInterval;
                    new Thread(this).start();
                }
            } catch (SQLException ex) {
                System.out.println("Alarm " + DeviceID + ", Error In DataBase");
                Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean getDeviceState() {
        return DeviceState;
    }

    @Override
    public void run() {
        try {
            Busy = true;
            if (DeviceState) {
                if (AlarmDuration == 0 && AlarmInterval == 0) {
                    PIN.low();
                } else {
                    long currentTime = System.currentTimeMillis();
                    long end = currentTime + AlarmDuration * 1000;
                    while (currentTime < end) {
                        try {
                            PIN.low();
                            Thread.sleep(AlarmInterval * 1000);
                            PIN.high();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
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
            System.out.println("Alarm " + DeviceID + ", State Change To " + DeviceState);

        } catch (SQLException ex) {
            System.out.println("Alarm " + DeviceID + ", Error In DataBase");
            Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
