package SmartHome_Camera_WIN.SendFile;

import Logger.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.*;
import java.util.*;
import org.apache.commons.net.ftp.*;

public class SendFile implements Runnable {

    private final String Server;
    private final int Port;
    private final String UserName;
    private final String Password;
    private final Connection DB;
    private static Queue<SendFileQueue> SendFileList;
    private final FTPClient FTPClient;
    private String DirectoryPath;

    // Get Infromation from Main Class 
    public SendFile(String Server, int Port, String UserName, String Password, Connection DB) {
        this.Server = Server;
        this.Port = Port;
        this.UserName = UserName;
        this.Password = Password;
        this.DB = DB;
        this.SendFileList = new LinkedList();
        this.FTPClient = new FTPClient();

        // Start The Connection With The Raspberry Pi
        StartConnection();

        // Start The Thread To Send File If the Queue Not Empty
        new Thread(this).start();
    }

    // This Method To Start Connection With The Raspberry Pi
    private void StartConnection() {
        try {
            // Connecte To Raspberry Pi Useing IP Address and Port Number
            FTPClient.connect(Server, Port);
            // To Authentication
            FTPClient.login(UserName, Password);
            // Set the current data connection mode to PASSIVE_LOCAL_DATA_CONNECTION_MODE
            FTPClient.enterLocalPassiveMode();
            //Sets the file type to be transferred. FTP.BINARY_FILE_TYPE,
            FTPClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Get System Location Path and Decode it
            URL SystemLocation = SendFile.class.getProtectionDomain().getCodeSource().getLocation();
            String Path = URLDecoder.decode(SystemLocation.getFile(), "UTF-8");

            // Get Parent File Path if run Normally or as a JAR.
            String ParentPath;
            if (SystemLocation.toString().contains(".jar")) {
                ParentPath = new File(Path).getParentFile().getPath();
            } else {
                ParentPath = new File(Path).getParentFile().getParentFile().getPath();
            }

            // Get File Separator from the System
            String FileSeparator = System.getProperty("file.separator");

            // Set Directory Path 
            DirectoryPath = ParentPath + FileSeparator + "Camera" + FileSeparator;

            // Get Directory Path File
            File Directory = new File(DirectoryPath);

            // Check if the Directory is nor Exists
            // To Create Directory
            if (!Directory.exists()) {
                Directory.mkdir();
            }
        } catch (IOException ex) {
            // This Catch For FTP Client Connection Error 
            FileLogger.AddWarning("SendFile Class, Error In FTP Client Connection\n" + ex);
        }
    }

    // Get The Information From The Other Classes to add it in the Queue then Send it 
    public static void SendFile(int DeviceID, String FileName, boolean isImage, long TakenDate) {
        // Add the Data to the Queue
        SendFileList.add(new SendFileQueue(DeviceID, FileName, isImage, TakenDate));
    }

    // The Send File Thread
    @Override
    public void run() {
        while (true) {
            try {
                // check the queue not Empty
                while (!SendFileList.isEmpty()) {
                    // get the First Data from the Queue
                    SendFileQueue SendFileQueue = SendFileList.poll();

                    // just To Print the Result
                    FileLogger.AddInfo("Start Uploading " + SendFileQueue.getFileName() + " File.");

                    // Open an Input Stream and Set The File To Prepare To Send it
                    try (InputStream inputStream = new FileInputStream(new File(DirectoryPath + System.getProperty("file.separator") + SendFileQueue.getFileName()))) {

                        // Start Send The File 
                        boolean successfully = FTPClient.storeFile(SendFileQueue.getFileName(), inputStream);

                        // Check if The File Successfully Send it 
                        if (successfully) {

                            // just To Print the Result
                            FileLogger.AddInfo("The " + SendFileQueue.getFileName() + " File is Uploaded Successfully.");
                            System.out.println("The " + SendFileQueue.getFileName() + " File is Uploaded Successfully.");

                            // Start insert The Date To The Database 
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
                    }
                    // To Sleep For 150 Millisecond
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
