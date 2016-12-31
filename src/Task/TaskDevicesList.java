package Task;

import Device.*;

public class TaskDevicesList {

    private final DeviceInterface Device;
    private final boolean RequiredDeviceStatus;
    private final int TakeImage;
    private final int TakeVideo;

    // Set Device Information For Using it In Task
    public TaskDevicesList(DeviceInterface Device, boolean RequiredDeviceStatus, int TakeImage, int TakeVideo) {
        this.Device = Device;
        this.RequiredDeviceStatus = RequiredDeviceStatus;
        this.TakeImage = TakeImage;
        this.TakeVideo = TakeVideo;
    }

    // Get Device ID
    public DeviceInterface getDevice() {
        return Device;
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
