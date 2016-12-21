package Device;

import Logger.*;
import java.sql.*;

public final class SecurityCamera {

    private final int DeviceID;
    private final Connection DB;

    // Get Device Information from Database
    public SecurityCamera(int DeviceID, Connection DB) {
        this.DB = DB;
        this.DeviceID = DeviceID;
    }

    // For Start Capture Image From The Camera
    public boolean Capture(int TakeImage) {
        // Start insert The Action To The Database To Execute it in PC Camera 
        try (PreparedStatement ps = DB.prepareStatement("INSERT INTO system_settings (cameraID, isImage, Value) VALUES (?, ?, ?)")) {
            // Device ID
            ps.setInt(1, DeviceID);
            // It is An Image
            ps.setBoolean(2, true);
            // Take Image
            ps.setInt(3, TakeImage);
            // Insert
            ps.executeUpdate();

            return true;
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("SendFile Class, Error In DataBase\n" + ex);
            return false;
        }
    }

    // For Start Record From The Camera
    public boolean Record(int Minute) {
        // Start insert The Action To The Database To Execute it in PC Camera 
        try (PreparedStatement ps = DB.prepareStatement("INSERT INTO system_settings (cameraID, isImage, Value) VALUES (?, ?, ?)")) {
            // Device ID
            ps.setInt(1, DeviceID);
            // It is An Image
            ps.setBoolean(2, false);
            // Take Image
            ps.setInt(3, Minute);
            // Insert
            ps.executeUpdate();

            return true;
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("SendFile Class, Error In DataBase\n" + ex);
            return false;
        }
    }
}
