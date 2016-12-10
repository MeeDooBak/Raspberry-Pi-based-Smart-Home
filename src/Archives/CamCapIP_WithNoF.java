package Archives;

import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import com.github.sarxos.webcam.util.jh.JHFlipFilter;
import com.xuggle.mediatool.*;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;

public class CamCapIP_WithNoF implements WebcamImageTransformer {

    private final BufferedImageOp FLIP_90 = new JHFlipFilter(JHFlipFilter.FLIP_90CW);
    private final BufferedImageOp FLIP_180 = new JHFlipFilter(JHFlipFilter.FLIP_180);
    private final BufferedImageOp FLIP_270 = new JHFlipFilter(JHFlipFilter.FLIP_90CCW);
    private int RotationBy;

    private final Dimension ds = new Dimension(640, 480);
    private final Dimension cs = WebcamResolution.VGA.getSize();
    private Webcam webcam;
    private WebcamPanel CamPanel;
    private IMediaWriter writer;
    private int count = 0;
    private Thread Thread2;
    private boolean StopRecord;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public CamCapIP_WithNoF() {
        try {
            IpCamDeviceRegistry.register(new IpCamDevice("IP-Cam-1", "http://admin:@192.168.1.100/videostream.cgi", IpCamMode.PUSH));
            RotationBy = 270;

            java.util.List<Webcam> wCam = Webcam.getWebcams();
            for (int i = 0; i < wCam.size(); i++) {
                if (wCam.get(i).getName().equals("IP-Cam-1")) {
                    webcam = wCam.get(i);
                    break;
                }
            }

            webcam.setImageTransformer((WebcamImageTransformer) this);
            webcam.setViewSize(cs);

            CamPanel = new WebcamPanel(webcam, ds, false);
            CamPanel.setFitArea(true);
            CamPanel.start();

            Thread.sleep(1000);
            Record();
        } catch (MalformedURLException | InterruptedException ex) {
            Logger.getLogger(CamCapIP_WithNoF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BufferedImage transform(BufferedImage Image) {
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

    private void Record() {

        File file = new File("output.mp4");

        writer = ToolFactory.makeWriter(file.getName());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, ds.height, ds.width);

        long start = System.currentTimeMillis();
        count = 0;
        StopRecord = true;

        Thread2 = new Thread() {

            @Override
            public void run() {
                while (StopRecord) {
                    try {
                        System.out.println("Capture frame " + count);

                        BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
                        IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

                        IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
                        frame.setKeyFrame(count == 0);
                        frame.setQuality(0);

                        writer.encodeVideo(0, frame);

                        count++;
                        if (count == 100) {
                            writer.close();
                            Thread2.stop();
                        }
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(CamCapIP_WithNoF.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        Thread2.start();
    }

    public static void main(String args[]) {
        CamCapIP_WithNoF camCapIP = new CamCapIP_WithNoF();
    }
}
