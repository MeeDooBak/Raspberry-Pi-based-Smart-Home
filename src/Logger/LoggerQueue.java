package Logger;

public class LoggerQueue {

    private final int RecordCategoryID;
    private final String Description;

    public LoggerQueue(int RecordCategoryID, String Description) {
        this.RecordCategoryID = RecordCategoryID;
        this.Description = Description;
    }

    public int getRecordCategoryID() {
        return RecordCategoryID;
    }

    public String getDescription() {
        return Description;
    }
}
