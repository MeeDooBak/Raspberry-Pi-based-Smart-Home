package SmartHome;

import Device.*;
import Logger.*;
import Sensor.*;
import java.util.*;

public class WaterLevelUpTask implements Runnable {

    private final ArrayList<SensorInterface> Sensor;
    private final DeviceInterface Device;
    private final int UpperMax;
    private final int UpperMin;
    private final int DownMax;
    private boolean isDownWaterTankEmpty;
    private int UpperDistance;
    private int DownDistance;

    // Get Infromation from Main Class 
    public WaterLevelUpTask(ArrayList<SensorInterface> Sensor, DeviceInterface Device) {
        this.Sensor = Sensor;
        this.Device = Device;

        // Get Last Distance For Down Water Tank
        DownDistance = Sensor.get(0).getSensorValue();
        // Get Maximum Value For Down Water Tank
        DownMax = Sensor.get(0).getMaxValue();

        // Get Last Distance For Upper Water Tank
        UpperDistance = Sensor.get(1).getSensorValue();
        // Get Maximum Value For Upper Water Tank
        UpperMax = Sensor.get(1).getMaxValue();
        // Get Minimum Value For Upper Water Tank
        UpperMin = Sensor.get(1).getMinValue();

        // Start Thread To Check if Down Water Tank is Empty
        new Thread(this).start();

        // Start Thread To Check if Upper Water Tank is Empty for to Fill
        new Thread(UpperWaterTank).start();
    }

    // The Down Water Tank Thread
    @Override
    public void run() {
        while (true) {
            try {
                // Get and Check The Distance For Down Water Tank is Not Equl -1 
                while ((DownDistance = Sensor.get(0).getSensorValue()) == -1) {
                    Thread.sleep(50);
                }

                // Check if the Distance For Down Water Tank Greater Than Maximum Value For Down Water Tank
                // Mean The Down Water Tank is Empty To Stop Fill the Upper Water Tank
                if (DownDistance > DownMax) {
                    // Set the Boolean Value With True To Stop Fill the Upper Water Tank
                    isDownWaterTankEmpty = true;
                    // Stop The Water Pump To Fill the Upper Water Tank
                    Device.ChangeState(false, "DOWN");

                    // This While Loop Wait Until The Down Water Tank Fill
                    while (true) {
                        // Get and Check The Distance For Down Water Tank is Not Equl -1 
                        while ((DownDistance = Sensor.get(0).getSensorValue()) == -1) {
                            Thread.sleep(50);
                        }

                        // Check if the Distance For Down Water Tank Smaller Than Or Equl  Maximum Value Minus 3 For Down Water Tank
                        // Mean The Down Water Tank is Fill To Start Fill the Upper Water Tank
                        if (DownDistance <= DownMax - 3) {
                            // Set the Boolean Value With False To Start Fill the Upper Water Tank
                            isDownWaterTankEmpty = false;
                            // Exit From The Loop
                            break;
                        } else {
                            // Stop The Water Pump To Fill the Upper Water Tank
                            Device.ChangeState(false, "DOWN");

                            // To Sleep For Half a secon
                            Thread.sleep(500);
                        }
                    }
                }

                // To Sleep For Half a secon
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // This Catch For Thread Sleep
                FileLogger.AddWarning("WaterLevelUpTask Class, Error In Thread Sleep\n" + ex);
            }
        }
    }

    // The Upper Water Tank Thread
    private final Runnable UpperWaterTank = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {

                    // Get and Check The Distance For Upper Water Tank is Not Equl -1 
                    while ((UpperDistance = Sensor.get(1).getSensorValue()) == -1) {
                        Thread.sleep(50);
                    }

                    // Check if the Distance For Upper Water Tank Greater Than Or Equl The Maximum Value For Upper Water Tank
                    // Mean The Upper Water Tank is Empty To Start Fill the Upper Water Tank
                    if (UpperDistance >= UpperMax && !isDownWaterTankEmpty) {
                        // Start The Water Pump To Fill the Upper Water Tank
                        Device.ChangeState(true, "UP");
                    }

                    // Check if the Distance For Upper Water Tank Smaller Than Or Equl The Minimum Value For Upper Water Tank
                    // Mean The Upper Water Tank is Full To Stop Fill the Upper Water Tank
                    if (UpperDistance <= UpperMin) {
                        // Stop The Water Pump To Fill the Upper Water Tank
                        Device.ChangeState(false, "UP");
                    }

                    // To Sleep For Half a secon
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    // This Catch For Thread Sleep
                    FileLogger.AddWarning("WaterLevelUpTask Class, Error In Thread Sleep\n" + ex);
                }
            }
        }
    };
}
