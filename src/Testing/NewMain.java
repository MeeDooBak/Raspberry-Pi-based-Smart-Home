package Testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewMain {

    public static void main(String[] args) {

        try {
            System.out.println("Start");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");

            Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet Result = Statement.executeQuery("select * from task");

            Result.beforeFirst();
            while (Result.next()) {

                int TaskID = Result.getInt("TaskID");
                Time ActionTime = Result.getTime("ActionTime");
                java.sql.Date ActionDate = Result.getDate("ActionDate");

                java.sql.Date CDate = new java.sql.Date(new java.util.Date().getTime());

                System.out.println(CDate);
                System.out.println(ActionDate);

                if (ActionTime != null) {
                    if (("" + CDate).equals("" + ActionDate)) {
                        System.out.println("1");

                        System.out.println(new SimpleDateFormat("HH:mm").format(ActionTime));
                        System.out.println(new SimpleDateFormat("HH:mm").format(new java.util.Date()));
                        System.out.println(new SimpleDateFormat("HH:mm").format(LocalDateTime.now()));

                        if (new SimpleDateFormat("HH:mm").format(ActionTime).equals(new SimpleDateFormat("HH:mm").format(new java.util.Date()))) {
                            System.out.println("2");
                        }
                    } else if (CDate.after(ActionDate)) {
                        System.out.println("-1");
                    }
                }
                System.out.println("----------------");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
