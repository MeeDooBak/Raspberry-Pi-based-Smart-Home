package Logger;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

public class FileLogger {

    private static Logger FileLog;

    public FileLogger() {
        try {
            FileLog = Logger.getLogger("SmartHome");

            // Configure the logger with handler and formatter
            FileHandler FileHandler = new FileHandler("SmartHome_" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date()) + ".log");
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
