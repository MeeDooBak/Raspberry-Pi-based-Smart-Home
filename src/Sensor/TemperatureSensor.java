package Sensor;

import Pins.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.wiringpi.*;

public class TemperatureSensor implements Runnable {

    private int SensorValue;

    private final int[] Data = {0, 0, 0, 0, 0};
    private final int SensorID;
    private final Connection DB;
    private final int PIN;
    private final int MAXTIMINGS = 85;

    public TemperatureSensor(int SensorID, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorValue = SensorValue;

        PIN = Integer.parseInt(GateNum.getPI4Jnumber());

        // setup wiringPi
        GpioUtil.export(PIN, GpioUtil.DIRECTION_OUT);
        new Thread(this).start();
    }

    public int getSensorValue() {
        return SensorValue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int laststate = Gpio.HIGH;
                int j = 0;
                Data[0] = Data[1] = Data[2] = Data[3] = Data[4] = 0;

                // pull pin down for 18 milliseconds
                Gpio.pinMode(PIN, Gpio.OUTPUT);
                Gpio.digitalWrite(PIN, Gpio.LOW);
                Gpio.delay(18);

                // then pull it up
                Gpio.digitalWrite(PIN, Gpio.HIGH);

                // prepare to read the pin
                Gpio.pinMode(PIN, Gpio.INPUT);

                // detect change and read data
                for (int i = 0; i < MAXTIMINGS; i++) {
                    int counter = 0;
                    while (Gpio.digitalRead(PIN) == laststate) {
                        counter++;
                        Gpio.delayMicroseconds(1);
                        if (counter == 255) {
                            break;
                        }
                    }
                    laststate = Gpio.digitalRead(PIN);

                    if (counter == 255) {
                        break;
                    }

                    // ignore first 3 transitions
                    if ((i >= 4) && (i % 2 == 0)) {
                        Data[j / 8] <<= 1;
                        if (counter > 16) {
                            Data[j / 8] |= 1;
                        }
                        j++;
                    }
                }

                // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
                // print it out if data is good
                if ((j >= 40) && (Data[4] == ((Data[0] + Data[1] + Data[2] + Data[3]) & 0xFF))) {
                    SensorValue = (int) Math.ceil(Data[2] + 0.1f * Data[3]);

                    try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                        ps.setInt(1, SensorValue);
                        ps.setInt(2, SensorID);
                        ps.executeUpdate();
                    }
                }
                Thread.sleep(2000);
            } catch (Exception ex) {
                System.out.println("TemperatureSensor " + SensorID + ", Error In DataBase");
                Logger.getLogger(TemperatureSensor.class.getName()).log(Level.SEVERE, null, ex);
                new Thread(this).start();
            }
        }
    }
}
