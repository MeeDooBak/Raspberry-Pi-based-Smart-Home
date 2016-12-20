package Logger;

public class LoggerQueue {

    private final int RecordCategoryID;
    private final String Description;

    // Set Logger Information
    public LoggerQueue(int RecordCategoryID, String Description) {
        this.RecordCategoryID = RecordCategoryID;
        this.Description = Description;
    }

    // Get Logger Record Category ID 
    public int getRecordCategoryID() {
        return RecordCategoryID;
    }

    // Get Logger Description
    public String getDescription() {
        return Description;
    }
}
