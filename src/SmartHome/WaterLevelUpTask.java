package SmartHome;

import Device.*;
import Sensor.*;
import java.util.*;
import java.util.logging.*;

public class WaterLevelUpTask implements Runnable {

    private final ArrayList<SensorList> Sensor;
    private final DeviceList Device;
    private boolean isUpperWaterTankFull;
    private boolean isDownWaterTankLow;

    public WaterLevelUpTask(ArrayList<SensorList> Sensor, DeviceList Device) {
        this.Sensor = Sensor;
        this.Device = Device;
        this.isUpperWaterTankFull = false;
        this.isDownWaterTankLow = false;
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
                        if (isUpperWaterTankFull) {
                            System.out.println("The Upper Water Tank is Full");
                            isUpperWaterTankFull = false;
                            isDownWaterTankLow = true;
                        }
                    } else if (Distance >= Max) {
                        ((WaterPump) Device.GetDevice()).ChangeState(true, true);
                        if (!isUpperWaterTankFull) {
                            System.out.println("Fill in The Upper Water Tank");
                            isUpperWaterTankFull = true;
                            isDownWaterTankLow = true;
                        }
                    }
                } else {
                    ((WaterPump) Device.GetDevice()).ChangeState(false, true);
                    if (isDownWaterTankLow) {
                        System.out.println("The Down Water Tank is Low");
                        isDownWaterTankLow = false;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WaterLevelUpTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
