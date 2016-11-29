package Sensor;

import Pins.PinsList;
import java.sql.*;
import java.util.logging.*;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class TemperatureSensor_Thread {

    private boolean SensorState;
    private int SensorValue;
    private float Humidity;
    private float Celsius;
    private float Fahrenheit;
    private int[] Data = {0, 0, 0, 0, 0};

    private final int SensorID;
    private final Connection DB;
    private final int PIN;
    private final int MAXTIMINGS = 85;

    public TemperatureSensor_Thread(int SensorID, boolean SensorState, PinsList GateNum, int SensorValue, Connection DB) {
        this.DB = DB;
        this.SensorID = SensorID;
        this.SensorState = SensorState;
        this.SensorValue = SensorValue;

        PIN = GateNum.getPin();
        GpioUtil.export(PIN, GpioUtil.DIRECTION_OUT);
    }

    public boolean getSensorState() {
        Start();
        return SensorState;
    }

    public int getSensorValue() {
        Start();
        return SensorValue;
    }

    public void Start() {
        try {
            SensorState = true;

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

                PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? and SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ps.setBoolean(1, SensorState);
                ps.setInt(2, SensorValue);
                ps.setInt(3, SensorID);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotionSensor_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
