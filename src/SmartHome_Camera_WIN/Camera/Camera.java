package SmartHome_Camera_WIN.Camera;

import Logger.*;
import SmartHome_Camera_WIN.Pins.*;
import java.sql.*;
import java.util.*;

public class Camera implements Runnable {

    private final Connection DB;
    private final ArrayList<CameraList> DeviceList;
    private final Pins Pins;
    private int CameraCount;

    // Get Infromation from Main Class 
    public Camera(Connection DB, ArrayList<CameraList> DeviceList, Pins Pins) {
        this.DB = DB;
        this.DeviceList = DeviceList;
        this.Pins = Pins;
        this.CameraCount = 0;
    }

    // The Thread
    @Override
    public void run() {
        try {
            // Start Get Information From The Database about the Devices
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from device")) {

                Result.beforeFirst();
                // While Loop For All Row in DataBase
                while (Result.next()) {

                    // Get the Device ID
                    int DeviceID = Result.getInt("DeviceID");
                    // Get the Device Name
                    String DeviceName = Result.getString("DeviceName");
                    // Get the Device Gate Number
                    int GateNum = Result.getInt("GateNum");

                    switch (DeviceName) {
                        // Create and add To the ArrayList the Device Class According to its kind
                        case "Security Camera":
                            DeviceList.add(new CameraList(DeviceID, DeviceName, Pins.Get(GateNum), DB));

                            // To Count How Many new Camera We Have 
                            CameraCount++;

                            // just To Print the Result
                            FileLogger.AddInfo("Add Device : " + DeviceID + ", with Name : " + DeviceName);
                            break;
                        default:
                            break;
                    }

                }
            }

            // To Check if There are New Camera 
            if (CameraCount > 0) {
                // Reset the Counter to Zero
                CameraCount = 0;
                for (int i = 0; i < DeviceList.size(); i++) {
                    // Search and return Security Camera Class and start it to get image for the Camera 
                    if (DeviceList.get(i).getDeviceName().equals("Security Camera")) {
                        DeviceList.get(i).Start();
                        // just To Print the Result
                        FileLogger.AddInfo("SecurityCamera " + DeviceList.get(i).getDeviceID() + ", Is Live Now");
                    }
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Device Class, Error In DataBase\n" + ex);
        }
    }
}
