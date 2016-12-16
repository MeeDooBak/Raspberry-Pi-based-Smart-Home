package Email;

import Rooms.*;
import Sensor.SensorList;
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

    private static Queue<EmailQueue> EmailQueueList;

    public Mail(String UserName, String Password) {
        this.UserName = UserName;
        this.Password = Password;
        Mail.EmailQueueList = new LinkedList();

        this.props = System.getProperties();
        this.props.put("mail.smtp.starttls.enable", "true");
        this.props.put("mail.smtp.host", "smtp.gmail.com");
        this.props.put("mail.smtp.user", UserName);
        this.props.put("mail.smtp.password", Password);
        this.props.put("mail.smtp.port", "587");
        this.props.put("mail.smtp.auth", "true");

        new Thread(this).start();
    }

    public static void SendMail(String MessageType, String TaskName, UserList User, RoomList Room, SensorList Sensor, ArrayList<TaskDevicesList> List, int SelectedSensorValue) {
        String Email = "";
        String Subject = "";
        String Body = "";

        switch (MessageType) {
            case "Notification":
                Email = User.getUserEmail();
                Subject = "Notification Message For Task (" + TaskName + ")";
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                if (Sensor != null) {
                    Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                } else {
                    Body += "Executed with (Clock)\n";
                }

                Body += "and Turned Devices:\n";
                for (int i = 0; i < List.size(); i++) {
                    Body += "Device Name : (" + List.get(i).getDeviceID().getDeviceName() + ")";
                    Body += ", Changed State to " + List.get(i).getRequiredDeviceStatus();
                    Body += "\n";
                }
                Body += "\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";
                System.out.println("Notification Message For Task (" + TaskName + ")");
                System.out.println("Send It To " + User.getUserName() + " Email.");
                break;

            case "Smoke":
                Email = User.getUserEmail();
                Subject = "Warning Message For Task (" + TaskName + ")";
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                Body += "The Smoke sensor detected a fire or a gas leak, The System is in Freeze-Mode, Devices can't be Switched ON/OFF, the Tasks will be Disabled as well.\n\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";
                System.out.println("Warning Message For Task (" + TaskName + ")");
                System.out.println("Send It To " + User.getUserName() + " Email.");
                break;

            case "Water level":
                Email = User.getUserEmail();
                Subject = "Notification Message For Task (" + TaskName + ")";
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                Body += "Water level in the tank has reached " + SelectedSensorValue + "%.\n\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";
                System.out.println("Notification Message For Task (" + TaskName + ")");
                System.out.println("Send It To " + User.getUserName() + " Email.");
                break;

            case "House parameters":
                Email = User.getUserEmail();
                Subject = "Warning Message For Task (" + TaskName + ")";
                Body += "Dear " + User.getUserName() + ",\n";
                Body += "Your Task (" + TaskName + ") in (" + Room.getRoomName() + ")\n\n";
                Body += "Executed with (" + Sensor.getSensorName() + ")\n";
                Body += "and Turned Devices:\n";
                for (int i = 0; i < List.size(); i++) {
                    Body += "Device Name : " + List.get(i).getDeviceID().getDeviceName();
                    if (List.get(i).getDeviceID().getDeviceName().equals("Alarm")) {
                        Body += ", Changed State to " + List.get(i).getRequiredDeviceStatus();
                    } else if (List.get(i).getDeviceID().getDeviceName().equals("Security Camera")) {
                        Body += ", Took " + List.get(i).getTakeImage() + " Imgs. ";
                    }
                    Body += "\n";
                }
                Body += "If this is a mistake, The alarms can be turned off through the website.\n";
                Body += "Best Regards\n";
                Body += "Smart Home\n";
                System.out.println("Notification Message For Task (" + TaskName + ")");
                System.out.println("Send It To " + User.getUserName() + " Email.");
                break;

            default:
                break;
        }
        EmailQueueList.add(new EmailQueue(Email, Subject, Body));
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (!EmailQueueList.isEmpty()) {
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
                    Thread.sleep(100);
                }
                Thread.sleep(1000);
            } catch (InterruptedException | MessagingException ex) {
                Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
