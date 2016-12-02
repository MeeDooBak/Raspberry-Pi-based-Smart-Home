package Relay;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Relay implements Runnable {

    private final MYSNMP Relay;
    private final Queue<RelayQueue> RelayQueueList;

    public Relay(String URL, int Port, byte dataType, String Community) {
        Relay = new MYSNMP(URL, Port, dataType, Community);
        RelayQueueList = new LinkedList();
        new Thread(this).start();
    }

    public void Add(String ID, String Value) {
        RelayQueueList.add(new RelayQueue(ID, Value));
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!RelayQueueList.isEmpty()) {
                    RelayQueue RelayQueue = RelayQueueList.poll();
                    Relay.SNMP_SET(RelayQueue.getID(), RelayQueue.getValue());
                }
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Relay.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
