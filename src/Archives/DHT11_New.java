package Archives;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class DHT11_New {

    private static final int MAXTIMINGS = 85;
    private int[] dht11_dat = {0, 0, 0, 0, 0};

    public DHT11_New() {
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
            return;
        }
        GpioUtil.export(8, GpioUtil.DIRECTION_OUT);
    }

    public void getTemperature() {
        int laststate = Gpio.HIGH;
        int j = 0;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

        Gpio.pinMode(10, Gpio.OUTPUT);
        Gpio.digitalWrite(10, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(10, Gpio.HIGH);
        Gpio.pinMode(10, Gpio.INPUT);

        for (int i = 0; i < MAXTIMINGS; i++) {
            int counter = 0;
            while (Gpio.digitalRead(10) == laststate) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            laststate = Gpio.digitalRead(10);

            if (counter == 255) {
                break;
            }

            if ((i >= 4) && (i % 2 == 0)) {
                dht11_dat[j / 8] <<= 1;
                if (counter > 16) {
                    dht11_dat[j / 8] |= 1;
                }
                j++;
            }
        }

        if ((j >= 40) && checkParity()) {
            float h = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;
            if (h > 100) {
                h = dht11_dat[0];
            }
            float c = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;
            if (c > 125) {
                c = dht11_dat[2];
            }
            if ((dht11_dat[2] & 0x80) != 0) {
                c = -c;
            }
            float f = c * 1.8f + 32;
            System.out.println("Humidity = " + h + " Temperature = " + c + "(" + f + "f)");
        }
    }

    private boolean checkParity() {
        return (dht11_dat[4] == ((dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3]) & 0xFF));
    }

    public static void main(String ars[]) throws Exception {

        DHT11_New dht = new DHT11_New();

        while (true) {
            Thread.sleep(2000);
            dht.getTemperature();
        }
    }
}
