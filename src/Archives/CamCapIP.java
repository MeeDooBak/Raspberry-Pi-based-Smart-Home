package Archives;

import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.*;
import com.xuggle.mediatool.*;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.video.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.logging.*;
import javax.imageio.*;
import javax.swing.*;

public class CamCapIP extends javax.swing.JFrame {

    private final Dimension ds = new Dimension(640, 480);
    private final Dimension cs = WebcamResolution.VGA.getSize();
    private final List<Webcam> wCam;
    private final WebcamPanel wCamPanel1;
    private IMediaWriter writer;
    private int count = 0;
    private Thread Thread;
    private boolean StopRecord;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public CamCapIP() throws MalformedURLException {
        initComponents();
        Stop.setVisible(false);

        IpCamDeviceRegistry.register(new IpCamDevice("IP-Cam-1", "http://admin:@192.168.1.101/videostream.cgi", IpCamMode.PUSH));

        wCam = Webcam.getWebcams();
        System.out.println(wCam.size());

        wCamPanel1 = new WebcamPanel(wCam.get(0), ds, false);
        wCamPanel1.setFillArea(true);
        wCamPanel1.setBorder(BorderFactory.createEmptyBorder());

        for (int i = 0; i < wCam.size(); i++) {
            wCam.get(i).setViewSize(cs);
        }

        panelCam1.setLayout(new FlowLayout());
        panelCam1.add(wCamPanel1);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btStart = new javax.swing.JButton();
        btCapture = new javax.swing.JButton();
        panelCam1 = new javax.swing.JPanel();
        panelCam2 = new javax.swing.JPanel();
        Record = new javax.swing.JButton();
        Stop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1012, 291));
        setResizable(false);
        setSize(new java.awt.Dimension(1012, 291));

        btStart.setText("Start");
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });

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
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panelCam2.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout panelCam2Layout = new javax.swing.GroupLayout(panelCam2);
        panelCam2.setLayout(panelCam2Layout);
        panelCam2Layout.setHorizontalGroup(
            panelCam2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 640, Short.MAX_VALUE)
        );
        panelCam2Layout.setVerticalGroup(
            panelCam2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );

        Record.setText("Record");
        Record.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RecordActionPerformed(evt);
            }
        });

        Stop.setText("Stop");
        Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StopActionPerformed(evt);
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
                        .addComponent(btStart, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btCapture)
                        .addGap(18, 18, 18)
                        .addComponent(Record)
                        .addGap(18, 18, 18)
                        .addComponent(Stop))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelCam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelCam2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btStart)
                    .addComponent(btCapture)
                    .addComponent(Record)
                    .addComponent(Stop))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelCam2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCam1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        Thread t = new Thread() {

            @Override
            public void run() {
                wCamPanel1.start();
            }
        };
        t.setDaemon(true);
        t.start();
    }//GEN-LAST:event_btStartActionPerformed

    private void btCaptureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCaptureActionPerformed
        try {
            for (int i = 0; i < wCam.size(); i++) {
                File file = new File(String.format("Capture Cam %d - %d.jpg", i, System.currentTimeMillis()));
                ImageIO.write(wCam.get(i).getImage(), "JPG", file);
            }
            JOptionPane.showMessageDialog(this, "Capture Complete", "CamCap", 1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "There is Error :\n" + e.getMessage(), "CamCap", 0);
        }
    }//GEN-LAST:event_btCaptureActionPerformed

    private void RecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RecordActionPerformed
        Record.setVisible(false);
        Stop.setVisible(true);

        File file = new File("output.mp4");

        writer = ToolFactory.makeWriter(file.getName());
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, cs.width, cs.height);

        long start = System.currentTimeMillis();
        count = 0;
        StopRecord = true;

        Thread = new Thread() {

            @Override
            public void run() {
                while (StopRecord) {
                    try {
                        System.out.println("Capture frame " + count);

                        BufferedImage image = ConverterFactory.convertToType(wCam.get(0).getImage(), BufferedImage.TYPE_3BYTE_BGR);
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
            }
        };
        Thread.setDaemon(true);
        Thread.start();
    }//GEN-LAST:event_RecordActionPerformed

    private void StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StopActionPerformed
        Record.setVisible(true);
        Stop.setVisible(false);
        StopRecord = false;
        Thread.stop();

        writer.close();
        System.out.println("Video recorded ");
    }//GEN-LAST:event_StopActionPerformed

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
    private javax.swing.JButton Stop;
    private javax.swing.JButton btCapture;
    private javax.swing.JButton btStart;
    private javax.swing.JPanel panelCam1;
    private javax.swing.JPanel panelCam2;
    // End of variables declaration//GEN-END:variables
}
