package SmartHome;

import Device.*;
import Sensor.*;
import java.util.*;
import java.util.logging.*;

public class WaterLevelUpTask implements Runnable {

    private final ArrayList<SensorList> Sensor;
    private final DeviceList Device;
    private final int UpperMax;
    private final int UpperMin;
    private final int DownMax;
    private final int DownMin;

    private boolean isUpperWaterTankFull;
    private boolean isDownWaterTankEmpty;
    private int UpperDistance;
    private int DownDistance;

    public WaterLevelUpTask(ArrayList<SensorList> Sensor, DeviceList Device) {
        this.Sensor = Sensor;
        this.Device = Device;

        DownDistance = ((Ultrasonic) Sensor.get(0).GetSensor()).getSensorValue();
        DownMax = ((Ultrasonic) Sensor.get(0).GetSensor()).getMaxValue();
        DownMin = ((Ultrasonic) Sensor.get(0).GetSensor()).getMinValue();

        UpperDistance = ((Ultrasonic) Sensor.get(1).GetSensor()).getSensorValue();
        UpperMax = ((Ultrasonic) Sensor.get(1).GetSensor()).getMaxValue();
        UpperMin = ((Ultrasonic) Sensor.get(1).GetSensor()).getMinValue();

        this.isUpperWaterTankFull = UpperDistance < UpperMax;

        new Thread(this).start();
        new Thread(UpperWaterTank).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                while ((DownDistance = ((Ultrasonic) Sensor.get(0).GetSensor()).getSensorValue()) == -1) {
                    Thread.sleep(50);
                }
                if (DownDistance > DownMax) {
                    isDownWaterTankEmpty = true;
                    ((WaterPump) Device.GetDevice()).ChangeState(false, true, "DOWN");
                    while (true) {
                        while ((DownDistance = ((Ultrasonic) Sensor.get(0).GetSensor()).getSensorValue()) == -1) {
                            Thread.sleep(50);
                        }
                        if (DownDistance <= DownMax - 3) {
                            isDownWaterTankEmpty = false;
                            break;
                        } else {
                            ((WaterPump) Device.GetDevice()).ChangeState(false, true, "DOWN");
                            Thread.sleep(500);
                        }
                    }
                }
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(WaterLevelUpTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Runnable UpperWaterTank = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    while ((UpperDistance = ((Ultrasonic) Sensor.get(1).GetSensor()).getSensorValue()) == -1) {
                        Thread.sleep(50);
                    }
                    if (UpperDistance >= UpperMax && !isDownWaterTankEmpty) {
                        ((WaterPump) Device.GetDevice()).ChangeState(true, true, "UP");
                    }
                    if (UpperDistance <= UpperMin) {
                        ((WaterPump) Device.GetDevice()).ChangeState(false, true, "UP");
                    }
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WaterLevelUpTask.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
}
