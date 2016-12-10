package Email;

import Rooms.*;
import Task.*;
import Users.*;
import java.util.*;
import java.util.logging.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Mail implements Runnable {

    private final Properties props;
    private final String UserName;
    private final String Password;

    private final Queue<EmailQueue> EmailQueueList;

    public Mail(String UserName, String Password) {
        this.UserName = UserName;
        this.Password = Password;
        this.EmailQueueList = new LinkedList();

        this.props = System.getProperties();
        this.props.put("mail.smtp.starttls.enable", "true");
        this.props.put("mail.smtp.host", "smtp.gmail.com");
        this.props.put("mail.smtp.user", UserName);
        this.props.put("mail.smtp.password", Password);
        this.props.put("mail.smtp.port", "587");
        this.props.put("mail.smtp.auth", "true");

        new Thread(this).start();
    }

    public void SendMail(String MessageType, String TaskName, UserList User, RoomList Room, ArrayList<TaskDevicesList> List) {
        String Email = "";
        String Subject = "";
        String Body = "";

        if (MessageType.equals("Notification")) {
            Email = User.getUserEmail();
            Subject = "Notification Message For Task " + TaskName;

            Body += "Dear, " + User.getUserName() + "\n";
            Body += "Your Task " + TaskName + " in " + Room.getRoomName() + "\n";
            Body += "Has Been Activated \n";

            for (int i = 0; i < List.size(); i++) {
                Body += "Device ID : " + List.get(i).getDeviceID().getDeviceID();
                Body += ", with Name : " + List.get(i).getDeviceID().getDeviceName();
                Body += ", Shange State to " + List.get(i).getRequiredDeviceStatus();
                Body += "\n";
            }

            Body += "Best Regards, \n";
            Body += "Smart Home \n";

            System.out.println("Notification Message For Task " + TaskName);
            System.out.println("Send Email To " + User.getUserName());

        } else if (MessageType.equals("Warning")) {

        }
        EmailQueueList.add(new EmailQueue(Email, Subject, Body));
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!EmailQueueList.isEmpty()) {
                    EmailQueue RelayQueue = EmailQueueList.poll();

                    Session session = Session.getDefaultInstance(props);
                    MimeMessage message = new MimeMessage(session);

                    message.setFrom(new InternetAddress(UserName));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(RelayQueue.getToUser()));
                    message.setSubject(RelayQueue.getSubject());
                    message.setText(RelayQueue.getBody());

                    Transport transport = session.getTransport("smtp");
                    transport.connect("smtp.gmail.com", UserName, Password);
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();
                }
                Thread.sleep(1000);
            } catch (InterruptedException | MessagingException ex) {
                Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
