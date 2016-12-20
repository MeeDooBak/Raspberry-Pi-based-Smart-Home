package Device;

import Pins.*;
import Logger.*;
import java.io.*;
import java.awt.*;
import java.net.*;
import java.sql.*;
import java.text.*;
import javax.swing.*;
import java.util.List;
import javax.imageio.*;
import java.awt.image.*;
import java.util.logging.*;
import Archives.Rotation90;
import java.util.ArrayList;
import com.xuggle.xuggler.*;
import com.xuggle.mediatool.*;
import com.github.sarxos.webcam.*;
import com.xuggle.xuggler.video.*;
import com.github.sarxos.webcam.util.jh.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SecurityCamera implements WebcamImageTransformer {

    private final BufferedImageOp FLIP_90 = new JHFlipFilter(JHFlipFilter.FLIP_90CW);
    private final BufferedImageOp FLIP_180 = new JHFlipFilter(JHFlipFilter.FLIP_180);
    private final BufferedImageOp FLIP_270 = new JHFlipFilter(JHFlipFilter.FLIP_90CCW);
    private int RotationBy;

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

    // register custom IP Camera driver
    static {
        Webcam.setDriver(new IpCamDriver());
    }

    // Get Device Information from Database
    public SecurityCamera(int DeviceID, PinsList GateNum, Connection DB) {
        this.DB = DB;
        this.DeviceID = DeviceID;

        // Get Camera Information From Datebase
        try (Statement Statement = DB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet Result = Statement.executeQuery("select * from ip_address where ID = " + GateNum.getPI4Jnumber())) {
            Result.next();

            // Get Camera IP Address and it Rotation
            String IP = Result.getString("IPaddress");
            this.RotationBy = Result.getInt("RotationBy");

            // register IP camera device
            IpCamDeviceRegistry.register(new IpCamDevice(("SecurityCamera " + DeviceID), "http://admin:@" + IP + "/videostream.cgi", IpCamMode.PUSH));

        } catch (SQLException ex) {
            // This Catch For DataBase Error 
            FileLogger.AddWarning("SecurityCamera " + DeviceID + ", Error In DataBase\n" + ex);
        } catch (MalformedURLException ex) {
            // This Catch For register IP camera device
            FileLogger.AddWarning("SecurityCamera " + DeviceID + ", Error In register IP camera device\n" + ex);
        }
    }

    // Start to Get Camera Information from Webcam Class
    public void Start() {
        // get all Camera registered in Webcam Class
        List<Webcam> wCam = Webcam.getWebcams();
        for (int i = 0; i < wCam.size(); i++) {
            // Search for a specific camera
            if (wCam.get(i).getName().equals("SecurityCamera " + DeviceID)) {
                // save this camera in this class
                WebCam = wCam.get(i);
                break;
            }
        }
        // set the Size for the Camera 
        WebCam.setViewSize(CS);

        // set the Rotation for the camera 
        WebCam.setImageTransformer((WebcamImageTransformer) this);

        // create camera Panel With Size and start get image for the Camera
        WebCamPanel = new WebcamPanel(WebCam, DS, false);
        WebCamPanel.setFillArea(true);
        WebCamPanel.setBorder(BorderFactory.createEmptyBorder());
        WebCamPanel.start();
    }

    // The Rotation Thread 
    @Override
    public BufferedImage transform(BufferedImage Image) {
        try {
            Thread.sleep(150);
        } catch (InterruptedException ex) {
            // This Catch For Thread Sleep
            FileLogger.AddWarning("SecurityCamera " + DeviceID + ", Error In Thread Sleep\n" + ex);
        }

        // check if Rotation is eqle 0, 90, 180 or 270
        // just Rotate image and send it to Camera Panel
        switch (RotationBy) {
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

    // For Start Capture Image From The Camera
    public boolean Capture(int TakeImage) {
        // To Check If the Camera is not working 
        // If it is Working just ignore it.
        if (ImageBusy) {
            return false;
        } else {
            // just to Get the New Date and Start The Capture Thread
            this.TakeImage = TakeImage;
            new Thread(Capture_Thread).start();
            return true;
        }
    }

    // The Capture Thread
    private final Runnable Capture_Thread = new Runnable() {
        @Override
        public void run() {
            // The Time When Image is Taken
            java.util.Date TakenDate = new java.util.Date();

            // The Tell The Method the Thread is Busy.
            ImageBusy = true;

            // Create 2 ArrayList to Store The Name and The Time
            ArrayList<String> ImageList = new ArrayList();
            ArrayList<java.util.Date> ImageTime = new ArrayList();

            // Begin to capture the image depending on the user's request
            for (int i = 0; i < TakeImage; i++) {
                try {
                    // Create JPG File 
                    java.util.Date Date = new java.util.Date();
                    File Image = new File("/var/www/html/Camera_Gallery/Camera_" + DeviceID + "_" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss-SS").format(Date) + ".jpg");

                    // save image to JPG file
                    ImageIO.write(WebCam.getImage(), "JPG", Image);

                    // Add the Name and time To the ArrayList
                    ImageList.add(Image.getName());
                    ImageTime.add(Date);

                    // just To Print the Result
                    FileLogger.AddInfo("SecurityCamera " + DeviceID + ", Capture : " + Image.getName());

                    Thread.sleep(200);
                } catch (IOException | InterruptedException ex) {
                    // This Catch For save image to JPG file
                    FileLogger.AddWarning("SecurityCamera " + DeviceID + ", Error In save image to JPG file\n" + ex);
                }
            }

            // Start insert The Date From ArrayList To The Database 
            for (int i = 0; i < ImageList.size(); i++) {
                try (PreparedStatement ps = DB.prepareStatement("INSERT INTO camera_gallery (cameraID, isImage, imgDate, imgPath) VALUES (?, ?, ?, ?)")) {
                    // Device ID
                    ps.setInt(1, DeviceID);
                    // It is An Image
                    ps.setBoolean(2, true);
                    // Time
                    ps.setTimestamp(3, new Timestamp(TakenDate.getTime()));
                    // Image Name
                    ps.setString(4, ImageList.get(i));
                    // Insert
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    // This Catch For DataBase Error 
                    FileLogger.AddWarning("SecurityCamera " + DeviceID + ", Error In DataBase\n" + ex);
                }
            }
            // The Tell The Method the Thread is not Busy.
            ImageBusy = false;
        }
    };

    // For Start Record From The Camera
    public boolean Record(int Minute) {
        if (RecordBusy) {
            return false;
        } else {
            this.Minute = Minute;
            new Thread(Record_Thread).start();
            return true;
        }
    }

    // The Record Thread
    private final Runnable Record_Thread = new Runnable() {
        @Override
        public void run() {
            RecordBusy = true;
            try {
                java.util.Date Date = new java.util.Date();
                File Video = new File("Camera_" + DeviceID + "_" + new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(Date) + ".mp4");

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

                try (PreparedStatement ps = DB.prepareStatement("INSERT INTO camera_gallery (cameraID, isImage, imgDate, imgPath) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, DeviceID);
                    ps.setBoolean(2, false);
                    ps.setTimestamp(3, new Timestamp(Date.getTime()));
                    ps.setString(4, Video.getName());
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SecurityCamera.class.getName()).log(Level.SEVERE, null, ex);
                RecordBusy = false;
            }
        }
    };
}
