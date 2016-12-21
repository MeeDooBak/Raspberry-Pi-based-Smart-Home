package SmartHome_Camera_WIN.SendFile;

public class SendFileQueue {

    private final int DeviceID;
    private final String FileName;
    private final boolean isImage;
    private final long TakenDate;

    public SendFileQueue(int DeviceID, String FileName, boolean isImage, long TakenDate) {

        this.DeviceID = DeviceID;
        this.FileName = FileName;
        this.isImage = isImage;
        this.TakenDate = TakenDate;
    }

    public int getDeviceID() {
        return DeviceID;
    }

    public String getFileName() {
        return FileName;
    }

    public boolean IsImage() {
        return isImage;
    }

    public long getTakenDate() {
        return TakenDate;
    }
}
