package Testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewMain1 {

    public static void main(String[] args) {
        try {
            System.out.println("Start");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");

            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from gpio_pins")) {

                Result.beforeFirst();
                while (Result.next()) {
                    int PinID = Result.getInt("PinID");
                    int isPinInput = Result.getInt("isPinInput");
                    String Type = Result.getString("Type");

                    System.out.println(PinID + " " + Type + " " + isPinInput);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(NewMain1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
