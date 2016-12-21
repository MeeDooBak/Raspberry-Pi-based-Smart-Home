package Archives;

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
import java.util.logging.*;
import javax.imageio.*;
import javax.swing.*;

public class CamCapIP extends javax.swing.JFrame implements WebcamImageTransformer {

    private final BufferedImageOp FLIP_90 = new JHFlipFilter(JHFlipFilter.FLIP_90CW);
    private final BufferedImageOp FLIP_180 = new JHFlipFilter(JHFlipFilter.FLIP_180);
    private final BufferedImageOp FLIP_270 = new JHFlipFilter(JHFlipFilter.FLIP_90CCW);
    private int RotationBy;

    private final Dimension ds = new Dimension(640, 480);
    private final Dimension cs = WebcamResolution.VGA.getSize();
    private Webcam webcam;
    private final WebcamPanel CamPanel;
    private IMediaWriter writer;
    private int count = 0;
    private Thread Thread;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public CamCapIP() throws MalformedURLException {
        initComponents();

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

        panelCam1.setLayout(new FlowLayout());
        panelCam1.add(CamPanel);
    }

    @Override
    public BufferedImage transform(BufferedImage Image) {
        try {
            Thread.sleep(100);
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
        } catch (InterruptedException ex) {
            Logger.getLogger(CamCapIP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Image;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btCapture = new javax.swing.JButton();
        panelCam1 = new javax.swing.JPanel();
        Record = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1012, 291));
        setResizable(false);
        setSize(new java.awt.Dimension(1012, 291));

        btCapture.setText("Capture");
        btCapture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCaptureActionPerformed(evt);
            }
        });

        panelCam1.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout panelCam1Layout = new javax.swing.GroupLayout(panelCam1);
        panelCam1.setLayout(panelCam1Layout);
        panelCam1Layout.setHorizontalGroup(
            panelCam1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        panelCam1Layout.setVerticalGroup(
            panelCam1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );

        Record.setText("Record");
        Record.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RecordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btCapture)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Record))
                    .addComponent(panelCam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCapture)
                    .addComponent(Record))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btCaptureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCaptureActionPerformed
        try {
            File file = new File(String.format("Capture Cam %d.jpg", System.currentTimeMillis()));
            ImageIO.write(webcam.getImage(), "JPG", file);
            JOptionPane.showMessageDialog(this, "Capture Complete", "CamCap", 1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "There is Error :\n" + e.getMessage(), "CamCap", 0);
        }
    }//GEN-LAST:event_btCaptureActionPerformed

    private void RecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RecordActionPerformed
        Record.setVisible(false);

        Thread = new Thread(Record_Thread);
        Thread.start();
    }//GEN-LAST:event_RecordActionPerformed

    private final Runnable Record_Thread = new Runnable() {
        @Override
        public void run() {
            writer = ToolFactory.makeWriter("Camera\\Camera_1.mp4");
            writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, cs.height, cs.width);

            long start = System.currentTimeMillis();
            long end = start + 1 * 60000;

            while (System.currentTimeMillis() <= end) {
                try {
                    System.out.println("Capture frame " + count);

                    BufferedImage image = ConverterFactory.convertToType(webcam.getImage(), BufferedImage.TYPE_3BYTE_BGR);
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
            System.out.println("Video recorded ");
            writer.close();

            Record.setVisible(true);
        }
    };

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CamCapIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CamCapIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CamCapIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CamCapIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new CamCapIP().setVisible(true);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(CamCapIP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Record;
    private javax.swing.JButton btCapture;
    private javax.swing.JPanel panelCam1;
    // End of variables declaration//GEN-END:variables

}
