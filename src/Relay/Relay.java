package Relay;

import Logger.*;
import java.util.*;

public class Relay implements Runnable {

    private final MYSNMP Relay;
    private final Queue<RelayQueue> RelayQueueList;

    // Get Infromation from Main Class 
    public Relay(String URL, int Port, byte dataType, String Community) {
        // Create MYSNMP Class
        Relay = new MYSNMP(URL, Port, dataType, Community);
        RelayQueueList = new LinkedList();

        // Start Execute Data If the Queue Not Empty
        new Thread(this).start();
    }

    // Get The Information From The Other Classes to add it in the Queue then Execute it 
    public void Add(String ID, String Value) {
        // Add the Data to the Queue
        RelayQueueList.add(new RelayQueue(ID, Value));
    }

    // The Thread
    @Override
    public void run() {
        while (true) {
            try {
                // check the queue not Empty
                while (!RelayQueueList.isEmpty()) {
                    // get the First Data from the Queue
                    RelayQueue RelayQueue = RelayQueueList.poll();
                    // Execute it
                    Relay.SNMP_SET(RelayQueue.getID(), RelayQueue.getValue());
                    Thread.sleep(100);
                }

                // To Sleep For 1 Second
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                // This Catch For Thread Sleep
                FileLogger.AddWarning("Relay Class, Error In Thread Sleep\n" + ex);
            }
        }
    }
}
