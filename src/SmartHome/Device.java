package SmartHome;

import com.adventnet.snmp.snmp2.*;
import com.pi4j.io.gpio.*;
import java.sql.*;
import java.util.logging.*;

public class Device extends Thread {

    private final Statement Statement;
    private final GpioController GPIO;
    public final Relay command;

    public Device(Statement Statement) {
        this.Statement = Statement;
        GPIO = GpioFactory.getInstance();
        command = new Relay("192.168.1.2", 161, "private");
    }

    @Override
    public void run() {
        try {
            while (true) {
                ResultSet Result = Statement.executeQuery("select * from device");
                Result.beforeFirst();

                while (Result.next()) {

                    String DeviceName = Result.getString("DeviceName");
                    int DeviceState = Result.getInt("DeviceState");
                    int GateNum = Result.getInt("GateNum");
                    int isStatusChanged = Result.getInt("isStatusChanged");

                    if (DeviceName.equals("Roof Lamp") || DeviceName.equals("AC")) {
                        if (isStatusChanged == 1) {

                            if (DeviceState == 1) {
                                command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "1");
                            } else {
                                command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "0");
                            }

                            isStatusChanged = 0;
                            Result.updateInt("isStatusChanged", isStatusChanged);
                            Result.updateRow();
                        }
                    } else if (DeviceName.equals("Curtains") || DeviceName.equals("Garage Door")) {
                    }
                }
                Thread.sleep(400);
            }
        } catch (SQLException | InterruptedException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
