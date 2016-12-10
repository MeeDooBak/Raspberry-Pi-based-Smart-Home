package Email;

public class EmailQueue {

    private final String ToUser;
    private final String Subject;
    private final String Body;

    public EmailQueue(String ToUser, String Subject, String Body) {
        this.ToUser = ToUser;
        this.Subject = Subject;
        this.Body = Body;
    }

    public String getToUser() {
        return ToUser;
    }

    public String getSubject() {
        return Subject;
    }

    public String getBody() {
        return Body;
    }
}
