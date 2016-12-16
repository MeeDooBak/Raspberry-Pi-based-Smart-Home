package Logger;

import Rooms.*;
import Sensor.*;
import Task.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.*;

public class SLogger implements Runnable {

    private final Connection DB;
    private static Queue<LoggerQueue> LoggerQueueList;

    public SLogger(Connection DB) {
        this.DB = DB;
        SLogger.LoggerQueueList = new LinkedList();

        new Thread(this).start();
    }

    public static void Logger(String MessageType, String TaskName, RoomList Room, SensorList Sensor, ArrayList<TaskDevicesList> List, int SelectedSensorValue) {
        int RecordCategoryID = 0;
        String Description = "";

        switch (MessageType) {
            case "Notification":
                RecordCategoryID = 11;
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                if (Sensor != null) {
                    Description += " Executed with (" + Sensor.getSensorName() + ")";
                } else {
                    Description += " Executed with (Clock)";
                }
                Description += " and Turned ";
                for (int i = 0; i < List.size(); i++) {
                    Description += "( " + List.get(i).getDeviceID().getDeviceName();
                    Description += " [" + List.get(i).getRequiredDeviceStatus() + "] )";
                    if ((i + 1) < List.size()) {
                        Description += ", ";
                    }
                }
                break;

            case "Smoke":
                RecordCategoryID = 12;
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                Description += " The Smoke sensor detected a fire or a gas leak, The System is in Freeze-Mode.";
                break;

            case "Water level":
                RecordCategoryID = 13;
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                Description += " Executed with (" + Sensor.getSensorName() + ")";
                Description += " The Water level in the tank has reached " + SelectedSensorValue + "%.";
                break;

            case "House parameters":
                RecordCategoryID = 14;
                Description += "Task (" + TaskName + ")";
                Description += " in " + Room.getRoomName();
                Description += " Executed with (" + Sensor.getSensorName() + ")";
                Description += " House parameters are breached, ";
                for (int i = 0; i < List.size(); i++) {
                    Description += "The " + List.get(i).getDeviceID().getDeviceName();
                    if (List.get(i).getDeviceID().getDeviceName().equals("Alarm")) {
                        Description += " Turned " + List.get(i).getRequiredDeviceStatus();
                    } else if (List.get(i).getDeviceID().getDeviceName().equals("Security Camera")) {
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
        LoggerQueueList.add(new LoggerQueue(RecordCategoryID, Description));
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (!LoggerQueueList.isEmpty()) {
                    LoggerQueue LoggerQueue = LoggerQueueList.poll();

                    try (PreparedStatement ps = DB.prepareStatement("INSERT INTO log (RecordCategoryID, EntryDate, Description) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, LoggerQueue.getRecordCategoryID());
                        ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
                        ps.setString(3, LoggerQueue.getDescription());
                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            System.out.println("Add Log ID : " + rs.getInt(1));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SLogger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Thread.sleep(100);
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
