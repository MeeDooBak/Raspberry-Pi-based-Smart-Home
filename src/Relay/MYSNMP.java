package Relay;

import com.adventnet.snmp.snmp2.*;
import java.util.logging.*;

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

    public MYSNMP(String URL, int Port, byte dataType, String Community) {
        this.URL = URL;
        this.Port = Port;
        this.dataType = dataType;
        this.Community = Community;
        this.FixedTimeout = 2000;
    }

    public boolean SNMP_SET(String OID, String SetValue) {
        for (int i = 0; i < 3; i++) {
            if (set(OID, SetValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean set(String OID, String SetValue) {
        try {
            API = new SnmpAPI();
            Session = new SnmpSession(API);
            Session.open();
            PDU = new SnmpPDU();
            PDU.setProtocolOptions(new UDPProtocolOptions(URL, Port));
            PDU.setVersion(SnmpAPI.SNMP_VERSION_2C);
            PDU.setCommunity(Community);
            PDU.setTimeout(FixedTimeout);
            PDU.setCommand(SnmpAPI.SET_REQ_MSG);
            PDU.addVariableBinding(new SnmpVarBind(new SnmpOID(OID), SnmpVar.createVariable(SetValue, dataType)));
            Result = Session.syncSend(PDU);

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
            Session.close();
            API.close();

            Logger.getLogger(MYSNMP.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
