package Task;

import Device.*;

public class TaskDevicesList {

    private final DeviceList DeviceID;
    private final boolean RequiredDeviceStatus;
    private final int TakeImage;
    private final int TakeVideo;

    // Set Device Information For Using it In Task
    public TaskDevicesList(DeviceList DeviceID, boolean RequiredDeviceStatus, int TakeImage, int TakeVideo) {
        this.DeviceID = DeviceID;
        this.RequiredDeviceStatus = RequiredDeviceStatus;
        this.TakeImage = TakeImage;
        this.TakeVideo = TakeVideo;
    }

    // Get Device ID
    public DeviceList getDeviceID() {
        return DeviceID;
    }

    // Get Device State User Want
    public boolean getRequiredDeviceStatus() {
        return RequiredDeviceStatus;
    }

    // Get Number Of Image To Cupture
    public int getTakeImage() {
        return TakeImage;
    }

    // Get Time To Record Video
    public int getTakeVideo() {
        return TakeVideo;
    }
}
