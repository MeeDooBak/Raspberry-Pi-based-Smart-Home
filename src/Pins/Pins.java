package Pins;

import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class Pins {

    private final Connection DB;
    private final ArrayList<PinsList> PinsList;
    private final GpioController GPIO;
    private final MCP23017GpioProvider[] Provider = new MCP23017GpioProvider[4];

    public Pins(Connection DB, ArrayList<PinsList> PinsList) {
        this.DB = DB;
        this.PinsList = PinsList;
        this.GPIO = GpioFactory.getInstance();

        try {
            this.Provider[0] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x20);
            this.Provider[1] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x21);
            this.Provider[2] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x24);
            this.Provider[3] = new MCP23017GpioProvider(I2CBus.BUS_1, 0x25);
        } catch (IOException ex) {
            System.out.println("Pins Class, Error In MCP23017 Gpio Provider");
            Logger.getLogger(Pins.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void Start() {
        try {
            try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet Result = Statement.executeQuery("select * from gpio_pins")) {

                Result.beforeFirst();
                while (Result.next()) {
                    int PinID = Result.getInt("PinID");
                    String Type = Result.getString("Type");
                    String MCP23017 = Result.getString("MCP23017");
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
                    } else {
                        PinsList.add(new PinsList(PinID, Type, Result.getString("PI4Jnumber"), GPIO, null));
                    }
                    System.out.println("Add Pin : " + PinID + " " + Type);

                }
            }
        } catch (SQLException ex) {
            System.out.println("Pins Class, Error In DataBase");
            Logger.getLogger(Pins.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
