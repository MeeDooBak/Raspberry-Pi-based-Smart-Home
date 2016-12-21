package SmartHome_Camera_WIN.SendFile;

import Logger.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.net.ftp.*;

public class SendFile implements Runnable {

    private final String Server;
    private final int Port;
    private final String UserName;
    private final String Password;
    private final Connection DB;
    private static Queue<SendFileQueue> SendFileList;
    private final FTPClient FTPClient;

    public SendFile(String Server, int Port, String UserName, String Password, Connection DB) {
        this.Server = Server;
        this.Port = Port;
        this.UserName = UserName;
        this.Password = Password;
        this.DB = DB;
        this.SendFileList = new LinkedList();
        this.FTPClient = new FTPClient();

        StartConnection();
        new Thread(this).start();
    }

    private void StartConnection() {
        try {
            FTPClient.connect(Server, Port);
            FTPClient.login(UserName, Password);
            FTPClient.enterLocalPassiveMode();
            FTPClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException ex) {
            Logger.getLogger(SendFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Get The Information From The Other Classes to add it in the Queue then Send it 
    public static void SendFile(int DeviceID, String FileName, boolean isImage, long TakenDate) {
        // Add the Message to the Queue
        SendFileList.add(new SendFileQueue(DeviceID, FileName, isImage, TakenDate));
    }

    // The Thread
    @Override
    public void run() {
        while (true) {
            try {
                // check the queue not Empty
                while (!SendFileList.isEmpty()) {
                    // get the First Message from the Queue
                    SendFileQueue SendFileQueue = SendFileList.poll();

                    // Start Uploads files using an InputStream
                    System.out.println("Start Uploading " + SendFileQueue.getFileName() + " File.");
                    boolean successfully;
                    try (InputStream inputStream = new FileInputStream(new File("Camera\\" + SendFileQueue.getFileName()))) {
                        successfully = FTPClient.storeFile(SendFileQueue.getFileName(), inputStream);
                    }

                    if (successfully) {
                        System.out.println("The " + SendFileQueue.getFileName() + " File is Uploaded Successfully.");

                        // Start insert The Date From ArrayList To The Database 
                        try (PreparedStatement ps = DB.prepareStatement("INSERT INTO camera_gallery (cameraID, isImage, imgDate, imgPath) VALUES (?, ?, ?, ?)")) {
                            // Device ID
                            ps.setInt(1, SendFileQueue.getDeviceID());
                            // It is An Image
                            ps.setBoolean(2, SendFileQueue.IsImage());
                            // Time
                            ps.setTimestamp(3, new Timestamp(SendFileQueue.getTakenDate()));
                            // Image Name
                            ps.setString(4, SendFileQueue.getFileName());
                            // Insert
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            // This Catch For DataBase Error 
                            FileLogger.AddWarning("SendFile Class, Error In DataBase\n" + ex);
                        }
                    }

                    // To Sleep For 150 MicroSecond
                    Thread.sleep(150);
                }

                // To Sleep For 1 Second
                Thread.sleep(1000);
            } catch (InterruptedException | IOException ex) {
                // This Catch For DataBase Error 
                FileLogger.AddWarning("SendFile Class, Error In DataBase\n" + ex);
            }
        }
    }
}
