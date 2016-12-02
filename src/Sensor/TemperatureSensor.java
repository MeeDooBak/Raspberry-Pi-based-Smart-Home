package Sensor;

import Pins.*;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.wiringpi.*;

public class TemperatureSensor implements Runnable {

    private int SensorValue;
    private float Humidity;
    private float Celsius;
    private float Fahrenheit;

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

                Gpio.pinMode(PIN, Gpio.OUTPUT);
                Gpio.digitalWrite(PIN, Gpio.LOW);
                Gpio.delay(18);
                Gpio.digitalWrite(PIN, Gpio.HIGH);
                Gpio.pinMode(PIN, Gpio.INPUT);

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
                    if ((i >= 4) && (i % 2 == 0)) {
                        Data[j / 8] <<= 1;
                        if (counter > 16) {
                            Data[j / 8] |= 1;
                        }
                        j++;
                    }
                }

                if ((j >= 40) && (Data[4] == ((Data[0] + Data[1] + Data[2] + Data[3]) & 0xFF))) {
                    Humidity = (float) ((Data[0] << 8) + Data[1]) / 10;
                    if (Humidity > 100) {
                        Humidity = Data[0];
                    }
                    Celsius = (float) (((Data[2] & 0x7F) << 8) + Data[3]) / 10;
                    if (Celsius > 125) {
                        Celsius = Data[2];
                    }
                    if ((Data[2] & 0x80) != 0) {
                        Celsius = -Celsius;
                    }
                    Fahrenheit = Celsius * 1.8f + 32;

                    SensorValue = (int) Math.ceil(Celsius);

                    try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                        ps.setInt(1, SensorValue);
                        ps.setInt(2, SensorID);
                        ps.executeUpdate();
                    }
                }
                Thread.sleep(500);
            } catch (SQLException | InterruptedException ex) {
                System.out.println("TemperatureSensor " + SensorID + ", Error In DataBase");
                Logger.getLogger(TemperatureSensor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
