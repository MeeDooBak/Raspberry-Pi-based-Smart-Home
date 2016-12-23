package SmartHome_Camera_WIN.SendFile;

public class SendFileQueue {

    private final int DeviceID;
    private final String FileName;
    private final boolean isImage;
    private final long TakenDate;

    // Set File Information
    public SendFileQueue(int DeviceID, String FileName, boolean isImage, long TakenDate) {
        this.DeviceID = DeviceID;
        this.FileName = FileName;
        this.isImage = isImage;
        this.TakenDate = TakenDate;
    }

    // Get Device ID (Camera ID)
    public int getDeviceID() {
        return DeviceID;
    }

    // Get File Name 
    public String getFileName() {
        return FileName;
    }

    // Get Is an Image
    public boolean IsImage() {
        return isImage;
    }

    // Get Tacken Date
    public long getTakenDate() {
        return TakenDate;
    }
}
