package Email;

import Logger.*;
import Task.*;
import Users.*;
import Rooms.*;
import Sensor.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Mail implements Runnable {

    private final Properties props;
    private final String UserName;
    private final String Password;

    private static Queue<EmailQueue> EmailQueueList;

    // Get Infromation from Main Class 
    public Mail(String UserName, String Password) {
        this.UserName = UserName;
        this.Password = Password;
        Mail.EmailQueueList = new LinkedList();

        // Get the session object  
        this.props = System.getProperties();
        this.props.put("mail.smtp.starttls.enable", "true");
        this.props.put("mail.smtp.host", "smtp.gmail.com");
        this.props.put("mail.smtp.user", UserName);
        this.props.put("mail.smtp.password", Password);
        this.props.put("mail.smtp.port", "587");
        this.props.put("mail.smtp.auth", "true");

        // Start Send Email If the Queue Not Empty
        new Thread(this).start();
    }

    // Get The Information From The Other Classes to add it in the Queue then Send it 
    public static void SendMail(String MessageType, String TaskName, UserList User, RoomList Room, SensorInterface Sensor, ArrayList<TaskDevicesList> List, int SelectedSensorValue) {
        String Email = "";
        String Subject = "";
        String Body = "";

        // Compose the Message According to its kind
        switch (MessageType) {
            case "Notification":
                // Get User Email
                Email = User.getUserEmail();

                // Set the Subject
                Subject = "Notification Message For Task (" + TaskName + ")";

                // Set the Body
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                if (Sensor != null) {
                    Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                } else {
                    Body += "Executed with (Clock)\n";
                }

                Body += "and Turned Devices:\n";
                for (int i = 0; i < List.size(); i++) {
                    Body += "Device Name : (" + List.get(i).getDevice().getDeviceName() + ")";
                    Body += ", Changed State to " + List.get(i).getRequiredDeviceStatus();
                    Body += "\n";
                }
                Body += "\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";

                // just To Print the Result
                FileLogger.AddInfo("Notification Message For Task (" + TaskName + "), Send It To " + User.getUserName() + " Email.");
                break;

            case "Smoke":
                // Get User Email
                Email = User.getUserEmail();

                // Set the Subject
                Subject = "Warning Message For Task (" + TaskName + ")";

                // Set the Body
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                Body += "The Smoke sensor detected a fire or a gas leak, The System is in Freeze-Mode, Devices can't be Switched ON/OFF, the Tasks will be Disabled as well.\n\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";

                // just To Print the Result
                FileLogger.AddInfo("Warning Message For Task (" + TaskName + "), Send It To " + User.getUserName() + " Email.");
                break;

            case "Water level":
                // Get User Email
                Email = User.getUserEmail();

                // Set the Subject
                Subject = "Notification Message For Task (" + TaskName + ")";

                // Set the Body
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                Body += "Water level in the tank has reached " + SelectedSensorValue + "%.\n\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";

                // just To Print the Result
                FileLogger.AddInfo("Notification Message For Task (" + TaskName + "), Send It To " + User.getUserName() + " Email.");
                break;

            case "House parameters":
                // Get User Email
                Email = User.getUserEmail();

                // Set the Subject
                Subject = "Warning Message For Task (" + TaskName + ")";

                // Set the Body
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                Body += "and Turned Devices:\n";
                for (int i = 0; i < List.size(); i++) {
                    Body += "Device Name : " + List.get(i).getDevice().getDeviceName();
                    if (List.get(i).getDevice().getDeviceName().equals("Alarm")) {
                        Body += ", Changed State to " + List.get(i).getRequiredDeviceStatus();
                    } else if (List.get(i).getDevice().getDeviceName().equals("Security Camera")) {
                        Body += ", Took " + List.get(i).getTakeImage() + " Imgs. ";
                    }
                    Body += "\n";
                }
                Body += "If this is a mistake, The alarms can be turned off through the website.\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";

                // just To Print the Result
                FileLogger.AddInfo("Warning Message For Task (" + TaskName + "), Send It To " + User.getUserName() + " Email.");
                break;
            default:
                break;
        }
        // Add the Message to the Queue
        EmailQueueList.add(new EmailQueue(Email, Subject, Body));
    }

    // The Thread
    @Override
    public void run() {
        while (true) {
            try {
                // check the queue not Empty
                while (!EmailQueueList.isEmpty()) {
                    // get the First Message from the Queue
                    EmailQueue EmailQueue = EmailQueueList.poll();

                    // Open The Session
                    Session session = Session.getDefaultInstance(props);
                    MimeMessage message = new MimeMessage(session);

                    // Set the infromation
                    message.setFrom(new InternetAddress(UserName));
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(EmailQueue.getToUser()));
                    message.setSubject(EmailQueue.getSubject());
                    message.setText(EmailQueue.getBody());

                    // Send the Message
                    Transport transport = session.getTransport("smtp");
                    transport.connect("smtp.gmail.com", UserName, Password);
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();

                    // To Sleep For 150 MicroSecond
                    Thread.sleep(150);
                }

                // To Sleep For 1 Second
                Thread.sleep(1000);
            } catch (InterruptedException | MessagingException ex) {
                // This Catch For DataBase Error 
                FileLogger.AddWarning("Mail Class, Error In DataBase\n" + ex);
            }
        }
    }
}
