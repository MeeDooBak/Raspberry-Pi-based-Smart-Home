package Archives;

import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import com.github.sarxos.webcam.util.jh.JHFlipFilter;
import java.awt.*;
import java.awt.image.*;
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

        } catch (MalformedURLException ex) {
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

    public static void main(String args[]) {
        CamCapIP_WithNoF camCapIP = new CamCapIP_WithNoF();
    }
}
