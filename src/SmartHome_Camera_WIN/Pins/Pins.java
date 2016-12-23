package SmartHome_Camera_WIN.Pins;

import Logger.*;
import java.sql.*;
import java.util.*;

public class Pins {

    private final Connection DB;
    private final ArrayList<PinsList> PinsList;

    // Get Infromation from Main Class 
    public Pins(Connection DB, ArrayList<PinsList> PinsList) {
        this.DB = DB;
        this.PinsList = PinsList;

    }

    // Search and return PIN Class if the specific PIN exists by ID
    public PinsList Get(int PinID) {
        for (int i = 0; i < PinsList.size(); i++) {
            if (PinsList.get(i).getPinID() == PinID) {
                return PinsList.get(i);
            }
        }
        return null;
    }

    // Start Get Information From The Database
    public void Start() {
        try {
            // Start Get Information From The Database about the PINs
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from gpio_pins")) {

                Result.beforeFirst();
                // While Loop For All Row in DataBase
                while (Result.next()) {
                    // Get the Pin ID
                    int PinID = Result.getInt("PinID");
                    // Get The Pin Type
                    String Type = Result.getString("Type");

                    if (Type.equals("Camera")) {
                        // Create Pin Class (Just Camera)
                        PinsList.add(new PinsList(PinID, Type, Result.getString("PI4Jnumber")));

                        // just To Print the Result
                        FileLogger.AddInfo("Add Pin : " + PinID + " " + Type);
                    }
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Pins Class, Error In DataBase\n" + ex);
        }
    }
}
