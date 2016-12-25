package Logger;

import SmartHome.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

public class FileLogger {

    private static Logger FileLog;

    public FileLogger() {
        try {
            FileLog = Logger.getLogger("SmartHome");

            // Get System Location Path and Decode it
            URL SystemLocation = FileLogger.class.getProtectionDomain().getCodeSource().getLocation();
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
            String DirectoryPath = ParentPath + FileSeparator + "JavaLog" + FileSeparator;

            // Get Directory Path File
            File Directory = new File(DirectoryPath);

            // Check if the Directory is nor Exists
            // To Create Directory
            if (!Directory.exists()) {
                Directory.mkdir();
            }

            // Configure the logger with handler and formatter
            FileHandler FileHandler = new FileHandler(DirectoryPath + FileSeparator + "SmartHome_" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date()) + ".log");
            FileLog.addHandler(FileHandler);
            FileHandler.setFormatter(new SimpleFormatter());

            // To remove the console handler
            FileLog.setUseParentHandlers(false);

        } catch (IOException | SecurityException ex) {
            // This Catch For File Handler
            FileLogger.AddWarning("FileLogger Class, Error In File Handler\n" + ex);
        }
    }

    // To Add Information Message To The Log File
    public static void AddInfo(String Info) {
        FileLog.info(Info);
    }

    // To Add Warning Message To The Log File
    public static void AddWarning(String Warning) {
        FileLog.warning(Warning);
    }
}
