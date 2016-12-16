package Testing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewMain {

    private static java.sql.Date ActionDate;
    private static java.sql.Time ActionTime;
    private static Time EnableTaskOnTime;
    private static Time DisableTaskOnTime;

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
                    ActionDate = Result.getDate("ActionDate");
                    EnableTaskOnTime = Result.getTime("EnableTaskOnTime");
                    DisableTaskOnTime = Result.getTime("DisableTaskOnTime");

                    long CurrentTime = System.currentTimeMillis();

                    if ((EnableTaskOnTime != null && DisableTaskOnTime != null)) {
                        java.util.Date EDate = new java.util.Date(CurrentTime);
                        EDate.setHours(EnableTaskOnTime.getHours());
                        EDate.setMinutes(EnableTaskOnTime.getMinutes());
                        EDate.setSeconds(EnableTaskOnTime.getSeconds());

                        java.util.Date DDate = new java.util.Date(CurrentTime);
                        DDate.setHours(DisableTaskOnTime.getHours());
                        DDate.setMinutes(DisableTaskOnTime.getMinutes());
                        DDate.setSeconds(DisableTaskOnTime.getSeconds());

                        long Disable = DDate.getTime();
                        long Enable = EDate.getTime();

                        System.out.println(CurrentTime);
                        System.out.println("----" + EDate);
                        System.out.println("----" + Enable);
                        System.out.println("----" + DDate);
                        System.out.println("----" + Disable);

                        while (Enable <= CurrentTime && CurrentTime <= Disable) {
                            java.sql.Date CDate = new java.sql.Date(new java.util.Date().getTime());
                            if (("" + CDate).equals("" + ActionDate)) {

                                java.util.Date ADate = new java.util.Date(CurrentTime);
                                ADate.setHours(ActionTime.getHours());
                                ADate.setMinutes(ActionTime.getMinutes());
                                ADate.setSeconds(ActionTime.getSeconds());

                                CurrentTime = System.currentTimeMillis();
                                if (ADate.getTime() == CurrentTime) {
                                    System.out.println("OK");
                                }
                            } else if (CDate.after(ActionDate)) {
                                System.out.println("End 1");
                            }
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
