package Logger;

import Rooms.*;
import Sensor.*;
import Task.*;
import java.sql.*;
import java.util.*;

public class SLogger implements Runnable {

    private final Connection DB;
    private static Queue<LoggerQueue> LoggerQueueList;

    // Get Infromation from Main Class 
    public SLogger(Connection DB) {
        this.DB = DB;
        SLogger.LoggerQueueList = new LinkedList();

        // Start Insert Log If the Queue Not Empty
        new Thread(this).start();
    }

    // Get The Information From The Other Classes to add it in the Queue then Insert it 
    public static void Logger(String MessageType, String TaskName, RoomList Room, SensorInterface Sensor, ArrayList<TaskDevicesList> List, int SelectedSensorValue) {
        int RecordCategoryID = 0;
        String Description = "";

        // Compose the Message According to its kind
        switch (MessageType) {
            case "Notification":
                // Set Record Category ID
                RecordCategoryID = 11;

                // Set the Description
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                if (Sensor != null) {
                    Description += " Executed with (" + Sensor.getSensorName() + ")";
                } else {
                    Description += " Executed with (Clock)";
                }
                Description += " and Turned ";
                for (int i = 0; i < List.size(); i++) {
                    Description += "( " + List.get(i).getDevice().getDeviceName();
                    Description += " [" + List.get(i).getRequiredDeviceStatus() + "] )";
                    if ((i + 1) < List.size()) {
                        Description += ", ";
                    }
                }
                break;

            case "DisabledTask":
                // Set Record Category ID
                RecordCategoryID = 11;

                // Set the Description
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                if (Sensor != null) {
                    Description += " Executed with (" + Sensor.getSensorName() + ")";
                } else {
                    Description += " Executed with (Clock)";
                }
                if (!List.isEmpty()) {
                    Description += " and Turned ";
                    for (int i = 0; i < List.size(); i++) {
                        Description += "( " + List.get(i).getDevice().getDeviceName();
                        Description += " [" + List.get(i).getRequiredDeviceStatus() + "] )";
                        if ((i + 1) < List.size()) {
                            Description += ", ";
                        }
                    }
                } else {
                    Description += " With No Devices ";
                }
                Description += ", Has Been DISABLED.";
                break;

            case "Smoke":
                // Set Record Category ID
                RecordCategoryID = 12;

                // Set the Description
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                Description += " The Smoke sensor detected a fire or a gas leak, The System is in Freeze-Mode.";
                break;

            case "Water level":
                // Set Record Category ID
                RecordCategoryID = 13;

                // Set the Description
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                Description += " Executed with (" + Sensor.getSensorName() + ")";
                Description += " The Water level in the tank has reached " + SelectedSensorValue + "%.";
                break;

            case "House parameters":
                // Set Record Category ID
                RecordCategoryID = 14;

                // Set the Description
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                Description += " Executed with (" + Sensor.getSensorName() + ")";
                Description += " House parameters are breached, ";
                for (int i = 0; i < List.size(); i++) {
                    Description += "The " + List.get(i).getDevice().getDeviceName();
                    if (List.get(i).getDevice().getDeviceName().equals("Alarm")) {
                        Description += " Turned " + List.get(i).getRequiredDeviceStatus();
                    } else if (List.get(i).getDevice().getDeviceName().equals("Security Camera")) {
                        Description += " Took " + List.get(i).getTakeImage() + " Imgs";
                    }
                    if ((i + 1) < List.size()) {
                        Description += ", ";
                    }
                }
                break;
            default:
                break;
        }

        // Add the Description to the Queue
        LoggerQueueList.add(new LoggerQueue(RecordCategoryID, Description));
    }

    // The Thread
    @Override
    public void run() {
        while (true) {
            try {
                // check the queue not Empty
                while (!LoggerQueueList.isEmpty()) {
                    // get the First Description from the Queue
                    LoggerQueue LoggerQueue = LoggerQueueList.poll();

                    // Start insert The Date From Queue To The Database 
                    try (PreparedStatement ps = DB.prepareStatement("INSERT INTO log (RecordCategoryID, EntryDate, Description) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        // The Record Category ID
                        ps.setInt(1, LoggerQueue.getRecordCategoryID());
                        // The Time
                        ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
                        // The Description
                        ps.setString(3, LoggerQueue.getDescription());
                        // Insert it
                        ps.executeUpdate();

                        // Get The Iogger ID
                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            // just To Print the ID
                            FileLogger.AddInfo("SLogger Add Log ID : " + rs.getInt(1));
                        }
                    } catch (SQLException ex) {
                        // This Catch For DataBase Error 
                        FileLogger.AddWarning("SLogger Class, Error In DataBase\n" + ex);
                    }

                    // To Sleep For 1 Second
                    Thread.sleep(100);
                }

                // To Sleep For 1 Second
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                // This Catch For Thread Sleep
                FileLogger.AddWarning("Logger Class, Error In Thread Sleep\n" + ex);
            }
        }
    }
}
