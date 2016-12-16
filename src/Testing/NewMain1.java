package Testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewMain1 {
    
    private static java.sql.Time ActionTime;
    
    public static void main(String[] args) {
        try {
            System.out.println("Start");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection DB = DriverManager.getConnection("jdbc:mysql://localhost:3306/smarthome", "root", "");
            
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from task")) {
                
                Result.beforeFirst();
                while (Result.next()) {
                    ActionTime = Result.getTime("ActionTime");
                    if (ActionTime != null) {
                        
                        java.util.Date ADate = new java.util.Date(System.currentTimeMillis());
                        ADate.setHours(ActionTime.getHours());
                        ADate.setMinutes(ActionTime.getMinutes());
                        ADate.setSeconds(ActionTime.getSeconds());
                        
                        while (true) {
                            java.util.Date ADate2 = new java.util.Date();
                            if ((ADate2 + "").equals(ADate + "")) {
                                System.out.println("OK");
                                break;
                            }
                            Thread.sleep(1000);
                        }
                        
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
