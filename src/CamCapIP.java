
import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.swing.*;

public class CamCapIP extends javax.swing.JFrame {

    private final Dimension ds = new Dimension(450, 360);
    private final Dimension cs = WebcamResolution.VGA.getSize();
    private final Webcam wCam;
    private final WebcamPanel wCamPanel;

    static {
        Webcam.setDriver(new IpCamDriver());
    }

    public CamCapIP() throws MalformedURLException {
        initComponents();

        IpCamDeviceRegistry.register(new IpCamDevice("IP-Cam-1", "http://admin:@192.168.1.99:99/videostream.cgi", IpCamMode.PUSH));

        wCam = Webcam.getDefault();
        wCamPanel = new WebcamPanel(wCam, ds, false);
        

        wCam.setViewSize(cs);
        wCamPanel.setFillArea(true);

        panelCam.setLayout(new FlowLayout());
        panelCam.add(wCamPanel);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btStart = new javax.swing.JButton();
        btCapture = new javax.swing.JButton();
        panelCam = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        panelCam.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout panelCamLayout = new javax.swing.GroupLayout(panelCam);
        panelCam.setLayout(panelCamLayout);
        panelCamLayout.setHorizontalGroup(
            panelCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );
        panelCamLayout.setVerticalGroup(
            panelCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btCapture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btStart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCapture)
                        .addGap(0, 308, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        Thread t = new Thread() {

            @Override
            public void run() {
                wCamPanel.start();
            }
        };
        t.setDaemon(true);
        t.start();
    }//GEN-LAST:event_btStartActionPerformed

    private void btCaptureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCaptureActionPerformed
        try {
            File file = new File(String.format("capture-%d.jpg", System.currentTimeMillis()));
            ImageIO.write(wCam.getImage(), "JPG", file);
            JOptionPane.showMessageDialog(this, "Capture Comblet :\n" + file.getAbsolutePath(), "CamCap", 1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "There is Error :\n" + e.getMessage(), "CamCap", 0);
        }
    }//GEN-LAST:event_btCaptureActionPerformed

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
    private javax.swing.JButton btCapture;
    private javax.swing.JButton btStart;
    private javax.swing.JPanel panelCam;
    // End of variables declaration//GEN-END:variables
}
