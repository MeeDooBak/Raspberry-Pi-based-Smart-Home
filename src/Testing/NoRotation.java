package Testing;

import java.awt.image.*;
import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import java.net.*;
import java.util.logging.*;

public class NoRotation implements WebcamImageTransformer {

    private Webcam webcam;
    private WebcamPanel panel;
    private String Name;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public NoRotation(String Name, String IP) {
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

        panel = new WebcamPanel(webcam);
        panel.setFitArea(true);
    }

    public WebcamPanel getPanel() {
        return panel;
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        return image;
    }
}
