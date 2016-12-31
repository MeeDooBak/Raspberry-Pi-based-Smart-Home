package Device;

import Logger.*;
import Rooms.RoomList;
import java.sql.*;

public final class SecurityCamera implements DeviceInterface {

    private final int DeviceID;
    private final RoomList Room;
    private final String DeviceName;
    private final Connection DB;

    // Get Device Information from Database
    public SecurityCamera(int DeviceID, RoomList Room, String DeviceName, Connection DB) {
        this.DB = DB;
        this.DeviceID = DeviceID;
        this.DeviceName = DeviceName;
        this.Room = Room;
        // add This Device For Given Room
        Room.getDeviceList().add(DeviceID);
    }

    @Override
    public int getDeviceID() {
        return DeviceID;
    }

    @Override
    public RoomList getRoom() {
        return Room;
    }

    @Override
    public String getDeviceName() {
        return DeviceName;
    }

    // For Start Capture Image From The Camera
    @Override
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
    @Override
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

    @Override
    public void ChangeState(boolean DeviceState) {
    }

    @Override
    public void ChangeState(boolean DeviceState, String UP_DOWN) {
    }

    @Override
    public void ChangeState(boolean DeviceState, int StepperMotorMoves) {
    }

    @Override
    public void ChangeState(boolean DeviceState, int AlarmDuration, int AlarmInterval) {
    }

    @Override
    public boolean getDeviceState() {
        return false;
    }
}
