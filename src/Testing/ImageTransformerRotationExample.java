package Testing;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamImageTransformer;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import com.github.sarxos.webcam.util.jh.JHFlipFilter;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageTransformerRotationExample implements WebcamImageTransformer {

    private final BufferedImageOp filter = new JHFlipFilter(JHFlipFilter.FLIP_90CCW);

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public ImageTransformerRotationExample() {

        try {
            IpCamDeviceRegistry.register(new IpCamDevice("IP-Cam-1", "http://admin:@192.168.1.100/videostream.cgi", IpCamMode.PUSH));
            
            Dimension size = WebcamResolution.VGA.getSize();
            
            Webcam webcam = Webcam.getDefault();
            webcam.setViewSize(size);
            webcam.setImageTransformer(this);
            webcam.open();
            
            JFrame window = new JFrame("Test Rotation");
            
            WebcamPanel panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setFitArea(true);
            
            window.add(panel);
            window.pack();
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ImageTransformerRotationExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public BufferedImage transform(BufferedImage image) {
        return filter.filter(image, null);
    }

    public static void main(String[] args) {
        new ImageTransformerRotationExample();
    }
}
