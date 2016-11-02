package Archives;


import com.pi4j.io.gpio.*;
import java.io.*;
import java.sql.*;
import java.util.logging.*;

public class TestSENSORwithLED {

    private static ResultSet Result;
    private static Statement Statement;

    private static GpioController GPIO;
    private static GpioPinDigitalOutput PIN;

    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Start");

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/raspberrypitest", "root", "");
            Statement = Con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            GPIO = GpioFactory.getInstance();
            PIN = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED", PinState.LOW);

            new Thread(OpenConnection).start();

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TestSENSORwithLED.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final Runnable OpenConnection = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Result = Statement.executeQuery("select * from device");
                    Result.beforeFirst();
                    while (Result.next()) {
                        int State = Result.getInt("State");
                        int Change = Result.getInt("Change");

                        if (Change == 1) {
                            if (State == 1) {
                                PIN.high();
                                System.out.println("The Device is On " + State);
                            } else {
                                PIN.low();
                                System.out.println("The Device is Off " + State);
                            }

                            Change = 0;
                            Result.updateInt("Change", Change);
                            Result.updateRow();
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(TestSENSORwithLED.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };
}
