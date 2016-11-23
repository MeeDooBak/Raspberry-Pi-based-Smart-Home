package Device;

import com.adventnet.snmp.snmp2.*;

public class Relay {

    private SnmpAPI api;
    private SnmpSession session;
    private UDPProtocolOptions udp;
    private SnmpPDU pdu;
    private SnmpOID[] oids;
    private SnmpPDU result;
    private String Result;
    private SnmpPDU temp;
    private SnmpVarBind varbind;
    private SnmpVar var;
    private SnmpOID oid;

    private final String IPaddress;
    private final String Community;
    private final int Port;

    public Relay(String IPaddress, int Port, String Community) {
        this.IPaddress = IPaddress;
        this.Community = Community;
        this.Port = Port;
    }

    public String SNMP_SET(String OID, byte dataType, String SetValue) {
        Result = null;
        for (int i = 0; i < 3; i++) {
            Result = set(OID, dataType, SetValue);
            if (Result != null && Result.equalsIgnoreCase("ok")) {
                return Result;
            }
        }
        return Result;
    }

    private String set(String OID, byte dataType, String SetValue) {
        api = new SnmpAPI();
        session = new SnmpSession(api);
        udp = new UDPProtocolOptions(IPaddress, Port);

        session.setTimeout(2000);
        try {
            session.open();
        } catch (SnmpException e) {
            return null;
        }

        pdu = new SnmpPDU();
        pdu.setProtocolOptions(udp);
        pdu.setVersion(SnmpAPI.SNMP_VERSION_1);
        pdu.setCommunity(Community);
        pdu.setTimeout(2000);
        pdu.setCommand(SnmpAPI.SET_REQ_MSG);
        oid = new SnmpOID(OID);
        var = null;
        try {
            var = SnmpVar.createVariable(SetValue, dataType);
        } catch (SnmpException e) {
            return null;
        }
        varbind = new SnmpVarBind(oid, var);
        pdu.addVariableBinding(varbind);
        try {
            result = session.syncSend(pdu);
            if (result == null) {
                session.close();
                api.close();
                return null;
            }
        } catch (SnmpException e) {
            session.close();
            api.close();
            return null;
        }
        session.close();
        api.close();

        if (result.getError().isEmpty()) {
            return "ok";
        } else {
            return result.getError();
        }
    }

    public String SNMP_GET(String OID) {
        Result = null;
        for (int i = 0; i < 3; i++) {
            temp = get(OID);
            if (temp != null && temp.getError().isEmpty()) {
                return temp.printVarBinds().substring(temp.printVarBinds().indexOf(": ", 11) + 2).replace("\n", "");
            } else {
                Result = null;
            }
        }
        return Result;
    }

    private SnmpPDU get(String OID) {
        api = new SnmpAPI();
        session = new SnmpSession(api);
        udp = new UDPProtocolOptions(IPaddress, Port);

        session.setTimeout(2000);
        try {
            session.open();
        } catch (SnmpException e) {
            return null;
        }

        pdu = new SnmpPDU();
        pdu.setProtocolOptions(udp);
        pdu.setVersion(SnmpAPI.SNMP_VERSION_1);
        pdu.setCommunity(Community);
        pdu.setTimeout(2000);
        pdu.setCommand(SnmpAPI.GET_REQ_MSG);

        oids = new SnmpOID[1];
        oids[0] = new SnmpOID(OID);
        for (int i = 0; i < 1; i++) {
            pdu.addNull(oids[i]);
        }

        result = null;
        try {
            result = session.syncSend(pdu);
        } catch (SnmpException ex) {
            session.close();
            api.close();
            return null;
        }

        session.close();
        api.close();

        if (result == null) {
            return null;
        } else {
            return result;
        }
    }
}
