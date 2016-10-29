package SmartHome;

import com.adventnet.snmp.snmp2.*;
import com.pi4j.io.gpio.*;
import java.sql.*;
import java.util.logging.*;

public class Device extends Thread {

    private final Connection DB;
    private final GpioController GPIO;
    public final Relay command;

    public Device(Connection DB) {
        this.DB = DB;
        GPIO = GpioFactory.getInstance();
        command = new Relay("192.168.1.14", 161, "private");
    }

    @Override
    public void run() {
        try {
            while (true) {
                Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select DeviceID, DeviceName , DeviceState , GateNum , isStatusChanged  from device");
                Result.beforeFirst();

                while (Result.next()) {

                    int DeviceID = Result.getInt("DeviceID");
                    String DeviceName = Result.getString("DeviceName");
                    boolean DeviceState = Result.getBoolean("DeviceState");
                    int GateNum = Result.getInt("GateNum");
                    boolean isStatusChanged = Result.getBoolean("isStatusChanged");

                    if (DeviceName.equals("Roof Lamp") || DeviceName.equals("AC")) {
                        if (isStatusChanged) {
                            System.out.println(DeviceName);

                            if (DeviceState) {
                                if (GateNum < 9) {
                                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "1");
                                } else {
                                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + (GateNum - 8) + ".0", SnmpAPI.INTEGER, "1");
                                }
                            } else {
                                if (GateNum < 9) {
                                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.1." + GateNum + ".0", SnmpAPI.INTEGER, "0");
                                } else {
                                    command.SNMP_SET(".1.3.6.1.4.1.19865.1.2.2." + (GateNum - 8) + ".0", SnmpAPI.INTEGER, "0");
                                }
                            }

                            isStatusChanged = false;

                            PreparedStatement ps = DB.prepareStatement("SELECT DeviceID, isStatusChanged FROM device WHERE DeviceID=? FOR UPDATE", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                            ps.setInt(1, DeviceID);
                            ResultSet rs = ps.executeQuery();

                            rs.next();
                            rs.updateBoolean("isStatusChanged", isStatusChanged);
                            rs.updateRow();
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
