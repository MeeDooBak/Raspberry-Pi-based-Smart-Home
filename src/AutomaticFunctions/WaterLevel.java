package AutomaticFunctions;

import Device.DeviceList;
import Sensor.SensorList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaterLevel extends Thread {

    private final SensorList Sensor;
    private final DeviceList Device;
    private boolean check;

    public WaterLevel(SensorList Sensor, DeviceList Device) {
        this.Sensor = Sensor;
        this.Device = Device;
        this.check = false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int Distance = Sensor.getUltrasonicThread().getSensorValue();
                if (Distance <= 4) {
                    Device.setDeviceState(false);
                    Device.setIsStatusChanged(true);
                    Device.Start();
                    if (check) {
                        System.out.println("The Upper Water Tank is Full");
                        check = false;
                    }
                } else if (Distance >= 15) {
                    Device.setDeviceState(true);
                    Device.setIsStatusChanged(true);
                    Device.Start();
                    if (!check) {
                        System.out.println("Fill in The Upper Water Tank");
                        check = true;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WaterLevel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
