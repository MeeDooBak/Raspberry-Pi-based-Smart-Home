package Pins;

import Logger.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.pi4j.io.i2c.*;
import com.pi4j.io.gpio.*;
import com.pi4j.gpio.extension.mcp.*;

public class Pins {

    private final Connection DB;
    private final ArrayList<PinsList> PinsList;
    private final GpioController GPIO;
    private final MCP23017GpioProvider[] Provider = new MCP23017GpioProvider[4];

    // Get Infromation from Main Class 
    public Pins(Connection DB, ArrayList<PinsList> PinsList) {
        this.DB = DB;
        this.PinsList = PinsList;

        // create gpio controller
        this.GPIO = GpioFactory.getInstance();

        // create custom MCP23017 GPIO provider
        try {
            this.Provider[0] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x20);
            this.Provider[1] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x21);
            this.Provider[2] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x24);
            this.Provider[3] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);

        } catch (IOException ex) {
            // This Catch For create custom MCP23017 GPIO provider Error 
            FileLogger.AddWarning("Pins Class, Error In MCP23017 Gpio Provider\n" + ex);
            FileLogger.AddWarning("System has been Shutdown");
            System.exit(0);
        }
    }

    // Search and return ArrayList index if the specific PIN exists by ID
    public int indexof(int PinID) {
        for (int i = 0; i < PinsList.size(); i++) {
            if (PinsList.get(i).getPinID() == PinID) {
                return i;
            }
        }
        return -1;
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
                while (Result.next()) {
                    // Get the Pin ID
                    int PinID = Result.getInt("PinID");
                    // Get The Pin Type
                    String Type = Result.getString("Type");
                    // Get The Pin I2C Type
                    String MCP23017 = Result.getString("MCP23017");

                    // Create Pin Class According to its kind
                    // if the Pin is I2C
                    if (Type.equals("I2C")) {
                        switch (MCP23017) {
                            case "0x20":
                                PinsList.add(new PinsList(PinID, Type, Result.getString("PI4Jnumber"), GPIO, Provider[0]));
                                break;
                            case "0x21":
                                PinsList.add(new PinsList(PinID, Type, Result.getString("PI4Jnumber"), GPIO, Provider[1]));
                                break;
                            case "0x24":
                                PinsList.add(new PinsList(PinID, Type, Result.getString("PI4Jnumber"), GPIO, Provider[2]));
                                break;
                            case "0x25":
                                PinsList.add(new PinsList(PinID, Type, Result.getString("PI4Jnumber"), GPIO, Provider[3]));
                                break;
                        }
                    } else { // if The Pin is GPIO, Relay or Camera
                        PinsList.add(new PinsList(PinID, Type, Result.getString("PI4Jnumber"), GPIO, null));
                    }

                    // just To Print the Result
                    FileLogger.AddInfo("Add Pin : " + PinID + " " + Type);
                }
            }
        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("Pins Class, Error In DataBase\n" + ex);
        }
    }
}
