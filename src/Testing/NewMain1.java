package Testing;

import java.sql.*;
import java.util.logging.*;

public class NewMain1 {

    public static void main(String[] args) {
        try {
            System.out.println("Start");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");

            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from sensor")) {

                Result.beforeFirst();
                while (Result.next()) {
                    int SensorID = Result.getInt("SensorID");

                    try (PreparedStatement ps = DB.prepareStatement("update sensor set SenesorState = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                        ps.setBoolean(1, true);
                        ps.setInt(2, SensorID);
                        ps.executeUpdate();
                        System.out.println("OK");
                    }
                    try (PreparedStatement ps = DB.prepareStatement("update sensor set SensorValue = ? where SensorID = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                        ps.setInt(1, 1);
                        ps.setInt(2, SensorID);
                        ps.executeUpdate();
                        System.out.println("OK");
                    }

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(NewMain1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
