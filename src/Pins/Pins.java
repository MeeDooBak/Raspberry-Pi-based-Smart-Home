package Pins;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Pins {
    
    private final Connection DB;
    private final ArrayList<PinsList> PinsList;
    
    public Pins(Connection DB, ArrayList<PinsList> PinsList) {
        this.DB = DB;
        this.PinsList = PinsList;
    }
    
    public int indexof(int PinID) {
        for (int i = 0; i < PinsList.size(); i++) {
            if (PinsList.get(i).getPinID() == PinID) {
                return i;
            }
        }
        return -1;
    }
    
    public PinsList Get(int PinID) {
        for (int i = 0; i < PinsList.size(); i++) {
            if (PinsList.get(i).getPinID() == PinID) {
                return PinsList.get(i);
            }
        }
        return null;
    }
    
    public void start() {
        try {
            Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet Result = Statement.executeQuery("select * from gpio_pins");
            
            Result.beforeFirst();
            while (Result.next()) {
                int PinID = Result.getInt("PinID");
                boolean isPinInput = Result.getBoolean("isPinInput");
                String Type = Result.getString("Type");
                String PinNumber = Result.getString("PinNumber");
                String PI4Jnumber = Result.getString("PI4Jnumber");
                String MCP23017 = Result.getString("MCP23017");
                String DeviceName = Result.getString("DeviceName");
                
                System.out.println("Add Pin : " + PinID + " " + Type);
                PinsList.add(new PinsList(PinID, isPinInput, Type, PinNumber, PI4Jnumber, MCP23017, DeviceName));
            }
            Result.close();
            Statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Pins.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
