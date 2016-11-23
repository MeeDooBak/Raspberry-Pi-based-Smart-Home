package Archives;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class DHT11_New_1 {

    private final int PIN;
    private final int MAXTIMINGS;
    private int[] dht11_dat = {0, 0, 0, 0, 0};

    public DHT11_New_1(int PIN, int MAXTIMINGS) {
        this.PIN = PIN;
        this.MAXTIMINGS = MAXTIMINGS;
        GpioUtil.export(PIN, GpioUtil.DIRECTION_OUT);
    }

    public void getTemperature() {
        int laststate = Gpio.HIGH;
        int Counter = 0;
        float Humidity;
        float Celsius;
        float Fahrenheit;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

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
                dht11_dat[Counter / 8] <<= 1;
                if (counter > 16) {
                    dht11_dat[Counter / 8] |= 1;
                }
                Counter++;
            }
        }

        if ((Counter >= 40) && checkParity()) {
            Humidity = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;
            if (Humidity > 100) {
                Humidity = dht11_dat[0];
            }

            Celsius = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;
            if (Celsius > 125) {
                Celsius = dht11_dat[2];
            }

            if ((dht11_dat[2] & 0x80) != 0) {
                Celsius = -Celsius;
            }

            Fahrenheit = Celsius * 1.8f + 32;
            System.out.println("Humidity = " + Humidity + " Temperature = " + Celsius + "(" + Fahrenheit + "F)");
        }
    }

    private boolean checkParity() {
        return (dht11_dat[4] == ((dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3]) & 0xFF));
    }

    public static void main(String ars[]) throws Exception {
        DHT11_New_1 DHT11 = new DHT11_New_1(3, 85);

        while (true) {
            Thread.sleep(500);
            DHT11.getTemperature();
        }
    }
}
