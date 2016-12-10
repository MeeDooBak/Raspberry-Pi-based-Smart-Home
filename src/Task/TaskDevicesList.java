package Task;

import Device.*;

public class TaskDevicesList {

    private final DeviceList DeviceID;
    private final boolean RequiredDeviceStatus;
    private final int TakeImage;
    private final int TakeVideo;

    public TaskDevicesList(DeviceList DeviceID, boolean RequiredDeviceStatus, int TakeImage, int TakeVideo) {
        this.DeviceID = DeviceID;
        this.RequiredDeviceStatus = RequiredDeviceStatus;
        this.TakeImage = TakeImage;
        this.TakeVideo = TakeVideo;
    }

    public DeviceList getDeviceID() {
        return DeviceID;
    }

    public boolean getRequiredDeviceStatus() {
        return RequiredDeviceStatus;
    }

    public int getTakeImage() {
        return TakeImage;
    }

    public int getTakeVideo() {
        return TakeVideo;
    }
}
