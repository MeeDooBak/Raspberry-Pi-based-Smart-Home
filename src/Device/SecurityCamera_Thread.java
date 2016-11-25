package Device;

import Archives.CamCapIP;
import Pins.*;
import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import com.xuggle.mediatool.*;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import java.util.List;
import java.util.logging.*;
import javax.imageio.*;
import javax.swing.*;

public final class SecurityCamera_Thread extends Thread {

    private final boolean isStatusChanged;
    private final boolean DeviceState;

    private final PinsList GateNum;
    private final int DeviceID;
    private final Connection DB;

    private final Dimension ds = new Dimension(640, 480);
    private final Dimension cs = WebcamResolution.VGA.getSize();
    private Webcam WebCam;
    private WebcamPanel WebCamPanel;
    private IMediaWriter writer;
    private long count = 0;
    private Thread Thread;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public SecurityCamera_Thread(int DeviceID, boolean DeviceState, PinsList GateNum, boolean isStatusChanged, Connection DB) {
        this.DB = DB;
        this.DeviceID = DeviceID;
        this.DeviceState = DeviceState;
        this.GateNum = GateNum;
        this.isStatusChanged = isStatusChanged;
        Start();
    }

    public void Start() {
        try {
            IpCamDeviceRegistry.register(new IpCamDevice((DeviceID + ""), "http://admin:@" + GateNum.getIP_Camera() + "/videostream.cgi", IpCamMode.PUSH));

            List<Webcam> wCam = Webcam.getWebcams();
            WebCamPanel = new WebcamPanel(wCam.get(0), ds, false);
            WebCamPanel.setFillArea(true);
            WebCamPanel.setBorder(BorderFactory.createEmptyBorder());

            for (int i = 0; i < wCam.size(); i++) {
                if (wCam.get(i).getName().equals(DeviceID + "")) {
                    WebCam = wCam.get(i);
                    WebCam.setViewSize(cs);
                    break;
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(SecurityCamera_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Capture() {
        try {
            java.util.Date Date = new java.util.Date();
            File Image = new File("C:\\Users\\mkb_2\\Documents\\NetBeansProjects\\RaspberryPITest\\Cameras\\Camera " + DeviceID
                    + " _ " + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(Date) + ".jpg");
            ImageIO.write(WebCam.getImage(), "JPG", Image);

            PreparedStatement ps = DB.prepareStatement("INSERT INTO DBUSER (cameraID, isImage, imgDate, imgPath) VALUES (?, ?, ?, ?)");
            ps.setInt(1, DeviceID);
            ps.setBoolean(2, true);
            ps.setTimestamp(3, new Timestamp(Date.getTime()));
            ps.setString(3, Image.getAbsolutePath());
            ps.executeUpdate();
            ps.close();

        } catch (IOException | SQLException ex) {
            Logger.getLogger(SecurityCamera_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Record(int Minute) {
        java.util.Date Date = new java.util.Date();
        File Video = new File("C:\\Users\\mkb_2\\Documents\\NetBeansProjects\\RaspberryPITest\\Cameras\\Camera " + DeviceID
                + " _ " + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(Date) + ".mp4");

        writer = ToolFactory.makeWriter(Video.getName());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, cs.width, cs.height);

        long start = System.currentTimeMillis();
        long end = start + Minute * 60000;

        Thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (System.currentTimeMillis() <= end) {
                        try {
                            System.out.println("Capture frame " + count);

                            BufferedImage image = ConverterFactory.convertToType(WebCam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
                            IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                            IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
                            frame.setKeyFrame(count == 0);
                            frame.setQuality(0);
                            writer.encodeVideo(0, frame);
                            count++;
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CamCapIP.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    PreparedStatement ps = DB.prepareStatement("INSERT INTO DBUSER (cameraID, isImage, imgDate, imgPath) VALUES (?, ?, ?, ?)");
                    ps.setInt(1, DeviceID);
                    ps.setBoolean(2, false);
                    ps.setTimestamp(3, new Timestamp(Date.getTime()));
                    ps.setString(3, Video.getAbsolutePath());
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(SecurityCamera_Thread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread.start();
    }

    @Override
    public void run() {
        while (true) {
            WebCamPanel.start();
        }
    }
}
