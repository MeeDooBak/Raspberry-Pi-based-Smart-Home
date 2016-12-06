package Device;

import Pins.*;
import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import com.github.sarxos.webcam.util.jh.*;
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

public final class SecurityCamera implements Runnable, WebcamImageTransformer {

    private final BufferedImageOp FLIP_90 = new JHFlipFilter(JHFlipFilter.FLIP_90CW);
    private final BufferedImageOp FLIP_180 = new JHFlipFilter(JHFlipFilter.FLIP_180);
    private final BufferedImageOp FLIP_270 = new JHFlipFilter(JHFlipFilter.FLIP_90CCW);
    private int FLIP;

    private final int DeviceID;
    private final Connection DB;

    private int TakeImage;
    private int Minute;
    private boolean ImageBusy;
    private boolean RecordBusy;

    private final Dimension DS = new Dimension(640, 480);
    private final Dimension CS = WebcamResolution.VGA.getSize();
    private Webcam WebCam;
    private WebcamPanel WebCamPanel;
    private IMediaWriter writer;
    private long count = 0;
    private Thread SecurityCameraThread;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public SecurityCamera(int DeviceID, PinsList GateNum, Connection DB) {
        this.DB = DB;
        this.DeviceID = DeviceID;

        try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select * from ip_address where ID = " + GateNum.getPI4Jnumber())) {
            Result.next();

            IpCamDeviceRegistry.register(new IpCamDevice(("SecurityCamera " + DeviceID), "http://admin:@" + Result.getString("IPaddress") + "/videostream.cgi", IpCamMode.PUSH));
            this.FLIP = Result.getInt("FLIP");

        } catch (SQLException | MalformedURLException ex) {
            Logger.getLogger(SecurityCamera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Start() {

        List<Webcam> wCam = Webcam.getWebcams();
        for (int i = 0; i < wCam.size(); i++) {
            if (wCam.get(i).getName().equals("SecurityCamera " + DeviceID)) {
                WebCam = wCam.get(i);
                break;
            }
        }
        WebCam.setViewSize(CS);
        WebCam.setImageTransformer(this);

        WebCamPanel = new WebcamPanel(WebCam, DS, false);
        WebCamPanel.setFillArea(true);
        WebCamPanel.setBorder(BorderFactory.createEmptyBorder());

        SecurityCameraThread = new Thread(this);
        SecurityCameraThread.setDaemon(true);
        SecurityCameraThread.start();
    }

    @Override
    public BufferedImage transform(BufferedImage Image) {
        switch (FLIP) {
            case 0:
                return Image;
            case 90:
                return FLIP_90.filter(Image, null);
            case 180:
                return FLIP_180.filter(Image, null);
            case 270:
                return FLIP_270.filter(Image, null);
            default:
                return Image;
        }
    }

    @Override
    public void run() {
        WebCamPanel.start();
    }

    public boolean Capture(int TakeImage) {
        if (ImageBusy) {
            return false;
        } else {
            this.TakeImage = TakeImage;
            new Thread(Capture_Thread).start();
            return true;
        }
    }

    private final Runnable Capture_Thread = new Runnable() {
        @Override
        public void run() {
            ImageBusy = true;
            for (int i = 0; i < TakeImage; i++) {
                try {
                    java.util.Date Date = new java.util.Date();
                    File Image = new File("C:\\Users\\mkb_2\\Documents\\NetBeansProjects\\RaspberryPITest\\Cameras\\Camera_" + DeviceID
                            + "_" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(Date) + ".jpg");
                    ImageIO.write(WebCam.getImage(), "JPG", Image);

                    try (PreparedStatement ps = DB.prepareStatement("INSERT INTO DBUSER (cameraID, isImage, imgDate, imgPath) VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, DeviceID);
                        ps.setBoolean(2, true);
                        ps.setTimestamp(3, new Timestamp(Date.getTime()));
                        ps.setString(4, Image.getAbsolutePath());
                        ps.executeUpdate();
                    }

                } catch (IOException | SQLException ex) {
                    Logger.getLogger(SecurityCamera.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ImageBusy = false;
        }
    };

    public boolean Record(int Minute) {
        if (RecordBusy) {
            return false;
        } else {
            this.Minute = Minute;
            new Thread(Record_Thread).start();
            return true;
        }
    }

    private final Runnable Record_Thread = new Runnable() {
        @Override
        public void run() {
            try {
                RecordBusy = true;
                java.util.Date Date = new java.util.Date();
                File Video = new File("C:\\Users\\mkb_2\\Documents\\NetBeansProjects\\RaspberryPITest\\Cameras\\Camera_" + DeviceID
                        + "_" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(Date) + ".mp4");

                writer = ToolFactory.makeWriter(Video.getName());
                writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, CS.width, CS.height);

                long start = System.currentTimeMillis();
                long end = start + Minute * 60000;

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
                        Logger.getLogger(SecurityCamera.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                try (PreparedStatement ps = DB.prepareStatement("INSERT INTO DBUSER (cameraID, isImage, imgDate, imgPath) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, DeviceID);
                    ps.setBoolean(2, false);
                    ps.setTimestamp(3, new Timestamp(Date.getTime()));
                    ps.setString(4, Video.getAbsolutePath());
                    ps.executeUpdate();
                }
                RecordBusy = false;
            } catch (SQLException ex) {
                Logger.getLogger(SecurityCamera.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
}
