package SmartHome;

import Device.*;
import Sensor.*;
import java.util.*;
import java.util.logging.*;

public class WaterLevelUpTask implements Runnable {

    private final ArrayList<SensorList> Sensor;
    private final DeviceList Device;
    private boolean check;

    public WaterLevelUpTask(ArrayList<SensorList> Sensor, DeviceList Device) {
        this.Sensor = Sensor;
        this.Device = Device;
        this.check = false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (((Ultrasonic) Sensor.get(0).GetSensor()).IsValed()) {

                    int Distance = ((Ultrasonic) Sensor.get(1).GetSensor()).getSensorValue();
                    int Max = ((Ultrasonic) Sensor.get(1).GetSensor()).getMaxValue();
                    int Min = ((Ultrasonic) Sensor.get(1).GetSensor()).getMinValue();

                    if (Distance <= Min) {
                        ((WaterPump) Device.GetDevice()).ChangeState(false, true);
                        if (check) {
                            System.out.println("The Upper Water Tank is Full");
                            check = false;
                        }
                    } else if (Distance >= Max) {
                        ((WaterPump) Device.GetDevice()).ChangeState(true, true);
                        if (!check) {
                            System.out.println("Fill in The Upper Water Tank");
                            check = true;
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WaterLevelUpTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
