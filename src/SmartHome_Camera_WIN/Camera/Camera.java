package SmartHome_Camera_WIN.Camera;

import Logger.*;
import java.sql.*;
import java.util.*;
import SmartHome_Camera_WIN.Pins.*;

public class Camera {

    private final Connection DB;
    private final ArrayList<CameraList> DeviceList;
    private final Pins Pins;

    // Get Infromation from Main Class 
    public Camera(Connection DB, ArrayList<CameraList> DeviceList, Pins Pins) {
        this.DB = DB;
        this.DeviceList = DeviceList;
        this.Pins = Pins;
    }

    // Start Get Information From The Database
    public void Start() {
        try {
            // Start Get Information From The Database about the Camera
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

                    // Check if The Device Name is Equal To Camera
                    switch (DeviceName) {
                        // Create and add To the ArrayList the Device Class (Just Camera)
                        case "Security Camera":
                            DeviceList.add(new CameraList(DeviceID, DeviceName, Pins.Get(GateNum), DB));

                            // just To Print the Result
                            FileLogger.AddInfo("Add Device : " + DeviceID + ", with Name : " + DeviceName);
                            break;
                        default:
                            break;
                    }

                }
            }

            // Loop For All Camera To Start it
            for (int i = 0; i < DeviceList.size(); i++) {
                DeviceList.get(i).Start();
                // just To Print the Result
                FileLogger.AddInfo("SecurityCamera " + DeviceList.get(i).getDeviceID() + ", Is Live Now");
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Device Class, Error In DataBase\n" + ex);
        }
    }
}
