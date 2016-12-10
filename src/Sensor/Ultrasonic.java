package Sensor;

import Pins.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;

public class Ultrasonic implements Runnable {

    private int SensorValue;

    private final int SensorID;
    private final int MaxValue;
    private final int MinValue;
    private final Connection DB;

    private GpioPinDigitalInput EchoPin;
    private GpioPinDigitalOutput TrigPin;

    public Ultrasonic(int SensorID, PinsList GateNum1, PinsList GateNum2, int SensorValue, int MaxValue, int MinValue, Connection DB) {

        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorValue = SensorValue;
        this.MaxValue = MaxValue;
        this.MinValue = MinValue;

        getPin(GateNum1, GateNum2);
        new Thread(this).start();
    }

    private void getPin(PinsList GateNum1, PinsList GateNum2) {
        if (GateNum1.getType().equals("GPIO") && GateNum2.getType().equals("GPIO")) {
            EchoPin = GateNum1.getGPIO().provisionDigitalInputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum1.getPI4Jnumber())));
            TrigPin = GateNum2.getGPIO().provisionDigitalOutputPin(RaspiPin.getPinByAddress(Integer.parseInt(GateNum2.getPI4Jnumber())), PinState.LOW);
        }
    }

    public int getSensorValue() {
        return SensorValue;
    }

    public int getMaxValue() {
        return MaxValue;
    }

    public int getMinValue() {
        return MinValue;
    }

    public boolean IsValed() {
        if (MaxValue > SensorValue && SensorValue > MinValue) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.TrigPin.high();
                Thread.sleep(0, 10000);
                this.TrigPin.low();

                int countdown = 2100;
                while (this.EchoPin.isLow() && countdown > 0) {
                    countdown--;
                }

                countdown = 2100;
                long start = System.nanoTime();
                while (this.EchoPin.isHigh() && countdown > 0) {
                    countdown--;
                }
                SensorValue = (int) Math.ceil(Math.ceil((System.nanoTime() - start) / 1000.0) * 340.29f / (2 * 10000));

                try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                    ps.setInt(1, SensorValue);
                    ps.setInt(2, SensorID);
                    ps.executeUpdate();
                }
                Thread.sleep(500);
            } catch (SQLException | InterruptedException ex) {
                System.out.println("Ultrasonic " + SensorID + ", Error In DataBase");
                Logger.getLogger(Ultrasonic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
