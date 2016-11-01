package Device;

import java.sql.*;
import com.pi4j.component.motor.impl.*;
import com.pi4j.io.gpio.*;
import java.util.logging.*;

public class MotorThread extends Thread {

    private boolean DeviceState;
    private boolean isStatusChanged;
    private int StepperMotorMoves;

    private final int DeviceID;
    private final Connection DB;
    private final GpioStepperMotorComponent Motor;

    public MotorThread(int DeviceID, boolean DeviceState, int GateNum1, int GateNum2, int GateNum3, int GateNum4, boolean isStatusChanged, Connection DB, int StepperMotorMoves) {

        this.DB = DB;
        this.DeviceID = DeviceID;
        this.DeviceState = DeviceState;
        this.isStatusChanged = isStatusChanged;
        this.StepperMotorMoves = StepperMotorMoves;

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput[] PINS = {
            gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(GateNum1), PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(GateNum2), PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(GateNum3), PinState.LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(GateNum4), PinState.LOW)
        };
        gpio.setShutdownOptions(true, PinState.LOW, PINS);
        Motor = new GpioStepperMotorComponent(PINS);

        Motor.setStepInterval(2);
        Motor.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
        Motor.setStepsPerRevolution(2038);
    }

    public void setDeviceState(boolean DeviceState) {
        this.DeviceState = DeviceState;
    }

    public void setisStatusChanged(boolean isStatusChanged) {
        this.isStatusChanged = isStatusChanged;
    }

    public void setStepperMotorMoves(int StepperMotorMoves) {
        this.StepperMotorMoves = StepperMotorMoves;
    }

    @Override
    public void run() {
        try {
            if (isStatusChanged) {
                if (DeviceState) {
                    for (int i = StepperMotorMoves; i > -1; i--) {
                        Motor.step(1);
                        StepperMotorMoves = i;
                    }
                } else {
                    for (int i = StepperMotorMoves; i < 4410; i++) {
                        Motor.step(-1);
                        StepperMotorMoves = i;
                    }
                }
                isStatusChanged = false;

                PreparedStatement ps = DB.prepareStatement("SELECT DeviceID, isStatusChanged FROM device WHERE DeviceID=? FOR UPDATE", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setInt(1, DeviceID);
                ResultSet rs = ps.executeQuery();
                rs.next();
                rs.updateBoolean("isStatusChanged", isStatusChanged);
                rs.updateInt("StepperMotorMoves", StepperMotorMoves);
                rs.updateRow();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}