package RemoteControl;

import Device.*;
import Logger.*;
import java.io.*;
import com.pi4j.io.i2c.*;
import com.pi4j.io.gpio.*;
import com.pi4j.gpio.extension.mcp.*;

public class RemoteControl implements Runnable {

    private final Device Devices;
    private GpioPinDigitalOutput PIN;
    private BufferedReader client;
    private String Line;
    private String Previous = "";
    private String DeviceID = "";

    // Get Infromation from Main Class 
    public RemoteControl(Device Devices) {
        this.Devices = Devices;
        try {
            // using Buffer Reader to get Input Read From Remote Control 
            // It is use Command Line on Raspberry Pi To Get Data Input
            this.client = new BufferedReader(new InputStreamReader((Runtime.getRuntime().exec(new String[]{"/usr/bin/irw"})).getInputStream()));

            // Provision GPIO pin (# A6) from MCP23017 as an output pin and turn OFF
            // It is an Alarm To Tell User the Input is Correct
            this.PIN = GpioFactory.getInstance().provisionDigitalOutputPin(new MCP23017GpioProvider(I2CBus.BUS_1, 0x25), MCP23017Pin.GPIO_A6, PinState.HIGH);
            // this will ensure that the motor is stopped when the program terminates
            GpioFactory.getInstance().setShutdownOptions(true, PinState.HIGH, PIN);
        } catch (IOException ex) {
            // This Catch For create custom MCP23017 GPIO provider Error 
            FileLogger.AddWarning("RemoteControl Class, Error In MCP23017 Gpio Provider\n" + ex);
            FileLogger.AddWarning("System has been Shutdown");
            System.exit(0);
        }

        // Start Thread To Get Data From Remote Control 
        new Thread(this).start();
    }

    // This Method To Get The Device ID Number From Remote Control 
    private DeviceInterface getDevice() {
        try {
            // Some Verbal TO Store Device ID From Remote Control 
            DeviceID = "";

            // While Loop Get Key Pressing From User
            while (true) {

                // Get Key Pressing From User
                Line = client.readLine();

                // To Check If the Key Entered Two Time
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    // To Split " 0000000000f40bf0 00 KEY_POWER A " 
                    // To Get Only Key " KEY_POWER " 
                    Line = Line.split(" ")[2];
                    // Put Key in Previous Verbal to Check if it is Redundant
                    Previous = Line;

                    // Add Key Number To Device ID Verbal
                    if (Line.equals("KEY_1")) {
                        DeviceID += "1";
                    } else if (Line.equals("KEY_2")) {
                        DeviceID += "2";
                    } else if (Line.equals("KEY_3")) {
                        DeviceID += "3";
                    } else if (Line.equals("KEY_4")) {
                        DeviceID += "4";
                    } else if (Line.equals("KEY_5")) {
                        DeviceID += "5";
                    } else if (Line.equals("KEY_6")) {
                        DeviceID += "6";
                    } else if (Line.equals("KEY_7")) {
                        DeviceID += "7";
                    } else if (Line.equals("KEY_8")) {
                        DeviceID += "8";
                    } else if (Line.equals("KEY_9")) {
                        DeviceID += "9";
                    } else if (Line.equals("KEY_0")) {
                        DeviceID += "0";
                    } else if (Line.equals("KEY_OK")) { // if Key is OK New To Check If The Device ID is Correct
                        DeviceInterface DeviceClass = Devices.Get(Integer.parseInt(DeviceID));
                        // if the Device is Found 
                        if (DeviceClass != null) {
                            // just To Print the Result
                            FileLogger.AddInfo("RemoteControl, Device ID : " + DeviceID);
                            // Return Device Class
                            return DeviceClass;
                        } else { // if the Device not Found
                            // just To Print the Result
                            FileLogger.AddInfo("RemoteControl, Device ID Not Found.");
                            // Return Null
                            return null;
                        }
                    } else if (Line.equals("KEY_BACKSPACE")) { // if user Want To Delete last Key He/Her Enter
                        // Delete Last Number
                        DeviceID = DeviceID.substring(0, DeviceID.length() - 1);
                    } else if (Line.equals("KEY_BACK")) { // if User Want To Go Back and Enter Other Device ID
                        return null;
                    } else if (Line.equals("KEY_EXIT")) { // if User Want To Exit 
                        return null;
                    }
                }
            }
        } catch (IOException ex) {
            // This Catch For Error In Getting Key From Remote Control 
            FileLogger.AddWarning("RemoteControl Class, Error In Getting Key From Remote Control\n" + ex);
        }
        return null;
    }

    // This Method To Control Device
    private void Execute(DeviceInterface DeviceClass) {
        try {
            // While Loop Get Key Pressing From User
            while (true) {

                // Get Key Pressing From User
                Line = client.readLine();

                // To Check If the Key Entered Two Time
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    // To Split " 0000000000f40bf0 00 KEY_POWER A " 
                    // To Get Only Key " KEY_POWER " 
                    Line = Line.split(" ")[2];
                    // Put Key in Previous Verbal to Check if it is Redundant
                    Previous = Line;

                    // if User Want To Go Back and Enter Other Device ID
                    if (Line.equals("KEY_BACK")) {
                        break;
                    } else if (Line.equals("KEY_EXIT")) {  // if User Want To Exit
                        break;
                    } else {
                        // Or Check The Device According to its kind
                        // if it Use Power Key Or UP / Down Key
                        switch (DeviceClass.getDeviceName()) {
                            case "Roof Lamp":
                                // if it is Light user can Turn On or Off Using Power Key
                                if (Line.equals("KEY_POWER")) {
                                    // Change the Device State 
                                    DeviceClass.ChangeState(!DeviceClass.getDeviceState());
                                }
                                break;
                            case "AC":
                                // if it is AC user can Turn On or Off Using Power Key
                                if (Line.equals("KEY_POWER")) {
                                    // Change the Device State 
                                    DeviceClass.ChangeState(!DeviceClass.getDeviceState());
                                }
                                break;
                            case "Alarm":
                                // if it is Alarm user can Turn On or Off Using Power Key
                                if (Line.equals("KEY_POWER")) {
                                    // Change the Device State 
                                    DeviceClass.ChangeState(!DeviceClass.getDeviceState(), 0, 0);
                                }
                                break;
                            case "Curtains":
                                // if it is Curtains user can Turn On using UP Key or Off Using Down Key
                                if (Line.equals("KEY_UP")) {
                                    // Change the Device State 
                                    DeviceClass.ChangeState(true, 5499);
                                } else if (Line.equals("KEY_DOWN")) {
                                    // Change the Device State 
                                    DeviceClass.ChangeState(false, 0);
                                }
                                break;
                            case "Garage Door":
                                // if it is Curtains user can Turn On using UP Key or Off Using Down Key
                                if (Line.equals("KEY_UP")) {
                                    // Change the Device State 
                                    DeviceClass.ChangeState(true, 0);
                                } else if (Line.equals("KEY_DOWN")) {
                                    // Change the Device State 
                                    DeviceClass.ChangeState(false, 5599);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            // This Catch For Error In Getting Key From Remote Control 
            FileLogger.AddWarning("RemoteControl Class, Error In Getting Key From Remote Control\n" + ex);
        }
    }

    // The Thread
    @Override
    public void run() {
        try {
            // While Loop Get Key Pressing From User
            while (true) {

                // Get Key Pressing From User
                Line = client.readLine();

                // To Check If the Key Entered Two Time
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    // To Split " 0000000000f40bf0 00 KEY_POWER A " 
                    // To Get Only Key " KEY_POWER " 
                    Line = Line.split(" ")[2];
                    // Put Key in Previous Verbal to Check if it is Redundant
                    Previous = Line;

                    // if User Want To Start Enter Device ID Mast Enter Home Key
                    if (Line.equals("KEY_HOME")) {
                        // just To Print the Result
                        FileLogger.AddInfo("RemoteControl, Start Remote Control.");
                        // The Method To Enter Device ID
                        DeviceInterface DeviceClass = getDevice();
                        // Check The Return Date 
                        // if Method Found the Device
                        if (DeviceClass != null) {
                            // Alarm To Tell User the Input is Correct
                            PIN.low();
                            Thread.sleep(100);
                            PIN.high();
                            // the Method To Control Device
                            Execute(DeviceClass);
                        }
                    } else if (Line.equals("KEY_OPTION")) {  // Dirct Access For Garage Door
                        // just To Print the Result
                        FileLogger.AddInfo("RemoteControl, Start Remote Control For Garage Door.");
                        // Get Garage Door class From Devices Class
                        DeviceInterface DeviceClass = Devices.Get("Garage Door");
                        // if Method Found the Device
                        if (DeviceClass != null) {
                            // Alarm To Tell User the Input is Correct
                            PIN.low();
                            Thread.sleep(100);
                            PIN.high();
                            // the Method To Control Device
                            Execute(DeviceClass);
                        }
                    }
                }

                // To Sleep For 2 Second
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException ex) {
            // This Catch For Error In Getting Key From Remote Control 
            FileLogger.AddWarning("RemoteControl Class, Error In Getting Key From Remote Control\n" + ex);
        }
    }
}
