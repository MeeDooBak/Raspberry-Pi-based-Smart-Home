package Device;

import Pins.PinsList;
import java.sql.*;
import com.pi4j.component.motor.impl.*;
import com.pi4j.io.gpio.*;
import java.util.logging.*;

public class MotorThread extends Thread {

    private boolean isStatusChanged;
    private int StepperMotorMoves;

    private final boolean DeviceState;
    private final int DeviceID;
    private final Connection DB;
    private final GpioStepperMotorComponent Motor;

    public MotorThread(int DeviceID, boolean DeviceState, PinsList GateNum1, PinsList GateNum2, PinsList GateNum3, PinsList GateNum4, boolean isStatusChanged, Connection DB, int StepperMotorMoves) {

        this.DB = DB;
        this.DeviceID = DeviceID;
        this.DeviceState = DeviceState;
        this.isStatusChanged = isStatusChanged;
        this.StepperMotorMoves = StepperMotorMoves;

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput[] PINS = {
            GateNum1.getOutputPIN(),
            GateNum2.getOutputPIN(),
            GateNum3.getOutputPIN(),
            GateNum4.getOutputPIN(),};
        gpio.setShutdownOptions(true, PinState.LOW, PINS);
        Motor = new GpioStepperMotorComponent(PINS);

        Motor.setStepInterval(2);
        Motor.setStepSequence(new byte[]{0b0001, 0b0010, 0b0100, 0b1000});
        Motor.setStepsPerRevolution(2038);
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

                PreparedStatement ps = DB.prepareStatement("update device_stepper_motor set StepperMotorMoves = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setInt(1, StepperMotorMoves);
                ps.setInt(2, DeviceID);
                ps.executeUpdate();

                PreparedStatement ps2 = DB.prepareStatement("update device set isStatusChanged = ? where DeviceID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps2.setBoolean(1, isStatusChanged);
                ps2.setInt(2, DeviceID);
                ps2.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
