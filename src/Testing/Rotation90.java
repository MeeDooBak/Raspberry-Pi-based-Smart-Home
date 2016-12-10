package Testing;

import java.awt.image.*;
import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import com.github.sarxos.webcam.util.jh.*;
import java.net.*;
import java.util.logging.*;

public class Rotation90 implements WebcamImageTransformer {

    private final BufferedImageOp filter = new JHFlipFilter(JHFlipFilter.FLIP_90CCW);

    private Webcam webcam;
    private String Name;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public Rotation90(String Name, String IP) {
        try {
            this.Name = Name;
            IpCamDeviceRegistry.register(new IpCamDevice(Name, "http://admin:@" + IP + "/videostream.cgi", IpCamMode.PUSH));
        } catch (MalformedURLException ex) {
            Logger.getLogger(Rotation90.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Start() {
        java.util.List<Webcam> wCam = Webcam.getWebcams();
        for (int i = 0; i < wCam.size(); i++) {
            if (wCam.get(i).getName().equals(Name)) {
                webcam = wCam.get(i);
                break;
            }
        }
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.setImageTransformer(this);
    }

    public WebcamPanel getPanel() {
        return new WebcamPanel(webcam);
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Rotation90.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filter.filter(image, null);
    }
}
