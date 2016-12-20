package Email;

public class EmailQueue {

    private final String ToUser;
    private final String Subject;
    private final String Body;

    // Set Email Information
    public EmailQueue(String ToUser, String Subject, String Body) {
        this.ToUser = ToUser;
        this.Subject = Subject;
        this.Body = Body;
    }

    // Get Email User
    public String getToUser() {
        return ToUser;
    }

    // Get Email Subject 
    public String getSubject() {
        return Subject;
    }

    // Get Email Body Massage
    public String getBody() {
        return Body;
    }
}
