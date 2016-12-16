package Sensor;

import Pins.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.io.gpio.*;
import java.util.Arrays;

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

    @Override
    public void run() {
        while (true) {
            try {
                SensorValue = -1;
                int[] Data = new int[5];

                for (int i = 0; i < Data.length; i++) {
                    this.TrigPin.high();
                    Thread.sleep(0, 10000);
                    this.TrigPin.low();

                    int countdown = 2100;
                    while (this.EchoPin.isLow() && countdown > 0) {
                        countdown--;
                    }

                    if (countdown > 0) {

                        countdown = 2100;
                        long start = System.nanoTime();

                        while (this.EchoPin.isHigh() && countdown > 0) {
                            countdown--;
                        }
                        long end = System.nanoTime();

                        if (countdown > 0) {
                            Data[i] = ((int) ((float) Math.ceil((end - start) / 1000.0) * 340.29f / 20000));
                        }
                    }
                    Thread.sleep(50);
                }
                Arrays.sort(Data);
                SensorValue = Data[2];

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
