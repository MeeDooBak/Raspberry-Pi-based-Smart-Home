package SmartHome;

import Device.*;
import java.util.logging.*;
import org.junit.*;

public class SmartHomeTest {

    public SmartHomeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

//    @AfterClass
//    public static void tearDownClass() {
//        try {
//            for (int i = 0; i < SmartHome.DeviceList.size(); i++) {
//                switch (SmartHome.DeviceList.get(i).getDeviceName()) {
//                    case "Roof Lamp":
//                        ((Light) SmartHome.DeviceList.get(i).GetDevice()).ChangeState(true, true);
//                        break;
//
//                    case "AC":
//                        ((AC) SmartHome.DeviceList.get(i).GetDevice()).ChangeState(true, true);
//                        break;
//
//                    case "Alarm":
//                        ((Alarm) SmartHome.DeviceList.get(i).GetDevice()).ChangeState(true, 2, 1, true);
//                        break;
//
//                    case "Security Camera":
//                        ((SecurityCamera) SmartHome.DeviceList.get(i).GetDevice()).Capture(2);
//                        break;
//                    default:
//                        break;
//                }
//                Thread.sleep(100);
//            }
//
//            Thread.sleep(500);
//
//            for (int i = 0; i < SmartHome.DeviceList.size(); i++) {
//                switch (SmartHome.DeviceList.get(i).getDeviceName()) {
//                    case "Roof Lamp":
//                        ((Light) SmartHome.DeviceList.get(i).GetDevice()).ChangeState(false, true);
//                        break;
//
//                    case "AC":
//                        ((AC) SmartHome.DeviceList.get(i).GetDevice()).ChangeState(false, true);
//                        break;
//
//                    case "Alarm":
//                        ((Alarm) SmartHome.DeviceList.get(i).GetDevice()).ChangeState(false, 2, 1, true);
//                        break;
//                    default:
//                        break;
//                }
//                Thread.sleep(100);
//            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(SmartHomeTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    @Test
    public void testMain() {
        System.out.println("main");
        SmartHome.main(null);
    }
}
