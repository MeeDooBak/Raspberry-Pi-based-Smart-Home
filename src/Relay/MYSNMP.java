package Relay;

import Logger.*;
import com.adventnet.snmp.snmp2.*;

public class MYSNMP {

    private SnmpAPI API;
    private SnmpSession Session;
    private SnmpPDU PDU;
    private SnmpPDU Result;

    private final String URL;
    private final int Port;
    private final byte dataType;
    private final String Community;
    private final int FixedTimeout;

    // Get Infromation from The Queue 
    public MYSNMP(String URL, int Port, byte dataType, String Community) {
        this.URL = URL;
        this.Port = Port;
        this.dataType = dataType;
        this.Community = Community;
        this.FixedTimeout = 2000;
    }

    // Ste The New Data
    public boolean SNMP_SET(String OID, String SetValue) {
        // trying to Do it three times because do not want failure
        for (int i = 0; i < 3; i++) {
            // Send New Data To Set Method
            if (set(OID, SetValue)) {
                return true;
            }
        }
        return false;
    }

    // Set Method To Execute Change
    private boolean set(String OID, String SetValue) {
        try {
            // Open The Session
            API = new SnmpAPI();
            Session = new SnmpSession(API);
            Session.open();
            PDU = new SnmpPDU();
            PDU.setProtocolOptions(new UDPProtocolOptions(URL, Port));
            PDU.setVersion(SnmpAPI.SNMP_VERSION_2C);
            PDU.setCommunity(Community);
            PDU.setTimeout(FixedTimeout);
            PDU.setCommand(SnmpAPI.SET_REQ_MSG);
            // Set the New Value
            PDU.addVariableBinding(new SnmpVarBind(new SnmpOID(OID), SnmpVar.createVariable(SetValue, dataType)));
            Result = Session.syncSend(PDU);

            // if the Result dose not Have Error return true
            if (Result != null && Result.getError().isEmpty()) {
                Session.close();
                API.close();
                return true;
            } else {
                Session.close();
                API.close();
                return false;
            }
        } catch (SnmpException ex) {
            // This Catch For Snmp Error 
            FileLogger.AddWarning("MYSNMP Class, Error In Snmp\n" + ex);
            Session.close();
            API.close();
            return false;
        }
    }
}
