package RemoteControl;

import Device.*;
import java.io.*;
import com.pi4j.io.i2c.*;
import com.pi4j.io.gpio.*;
import java.util.logging.*;
import com.pi4j.gpio.extension.mcp.*;

public class RemoteControl implements Runnable {

    private final Device Devices;
    private GpioPinDigitalOutput PIN;
    private BufferedReader client;
    private String Line;
    private String Previous = "";
    private String DeviceID = "";

    public RemoteControl(Device Devices) {
        this.Devices = Devices;
        try {
            this.client = new BufferedReader(new InputStreamReader((Runtime.getRuntime().exec(new String[]{"/usr/bin/irw"})).getInputStream()));
            this.PIN = GpioFactory.getInstance().provisionDigitalOutputPin(new MCP23017GpioProvider(I2CBus.BUS_1, 0x25), MCP23017Pin.GPIO_A6, PinState.HIGH);
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        new Thread(this).start();
    }

    private DeviceList getDevice() {
        try {
            DeviceID = "";
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

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
                    } else if (Line.equals("KEY_OK")) {
                        DeviceList DeviceClass = Devices.Get(Integer.parseInt(DeviceID));
                        if (DeviceClass != null) {
                            System.out.println("RemoteControl, Device ID : " + DeviceID);
                            return DeviceClass;
                        } else {
                            System.out.println("RemoteControl, Device ID Not Found.");
                            return null;
                        }
                    } else if (Line.equals("KEY_BACKSPACE")) {
                        DeviceID = DeviceID.substring(0, DeviceID.length() - 1);
                    } else if (Line.equals("KEY_BACK")) {
                        return null;
                    } else if (Line.equals("KEY_EXIT")) {
                        return null;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void Execute(DeviceList DeviceClass) {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    if (Line.equals("KEY_BACK")) {
                        break;
                    } else if (Line.equals("KEY_EXIT")) {
                        break;
                    } else {
                        switch (DeviceClass.getDeviceName()) {
                            case "Roof Lamp":
                                if (Line.equals("KEY_POWER")) {
                                    ((Light) DeviceClass.GetDevice()).ChangeState(!((Light) DeviceClass.GetDevice()).getDeviceState(), true);
                                }
                                break;
                            case "AC":
                                if (Line.equals("KEY_POWER")) {
                                    ((AC) DeviceClass.GetDevice()).ChangeState(!((AC) DeviceClass.GetDevice()).getDeviceState(), true);
                                }
                                break;
                            case "Alarm":
                                if (Line.equals("KEY_POWER")) {
                                    ((Alarm) DeviceClass.GetDevice()).ChangeState(!((Alarm) DeviceClass.GetDevice()).getDeviceState(), 0, 0, true);
                                }
                                break;
                            case "Curtains":
                                if (Line.equals("KEY_UP")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(true, 5499, true);
                                } else if (Line.equals("KEY_DOWN")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(false, 0, true);
                                }
                                break;
                            case "Garage Door":
                                if (Line.equals("KEY_UP")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(true, 0, true);
                                } else if (Line.equals("KEY_DOWN")) {
                                    ((Motor) DeviceClass.GetDevice()).ChangeState(false, 5599, true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while ((Line = client.readLine()) != null) {
                if (Line.split(" ")[2].equals(Previous)) {
                    Previous = "";
                } else {
                    Line = Line.split(" ")[2];
                    Previous = Line;
                    System.out.println(Line);

                    if (Line.equals("KEY_HOME")) {
                        System.out.println("Start Remote Control");
                        DeviceList DeviceClass = getDevice();
                        if (DeviceClass != null) {
                            PIN.low();
                            Thread.sleep(100);
                            PIN.high();
                            Execute(DeviceClass);
                        }
                    } else if (Line.equals("KEY_OPTION")) {
                        System.out.println("Start Remote Control For Garage Door");
                        DeviceList DeviceClass = Devices.Get("Garage Door");
                        if (DeviceClass != null) {
                            PIN.low();
                            Thread.sleep(100);
                            PIN.high();
                            Execute(DeviceClass);
                        }
                    }
                }
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(RemoteControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
