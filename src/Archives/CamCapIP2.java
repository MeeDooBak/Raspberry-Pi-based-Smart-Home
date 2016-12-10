package Archives;

import java.awt.*;
import java.net.*;
import java.util.logging.*;

public class CamCapIP2 extends javax.swing.JFrame {

    public CamCapIP2() throws MalformedURLException {
        initComponents();

        NoRotation NoRotation = new NoRotation("Cam_1", "192.168.1.101");
        Rotation90 Rotation90 = new Rotation90("Cam_2", "192.168.1.100");

        NoRotation.Start();
        Rotation90.Start();

        panelCam1.setLayout(new FlowLayout());
        panelCam1.add(NoRotation.getPanel());

        panelCam1.setLayout(new FlowLayout());
        panelCam1.add(Rotation90.getPanel());
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelCam1 = new javax.swing.JPanel();
        panelCam2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1012, 291));
        setResizable(false);
        setSize(new java.awt.Dimension(1012, 291));

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panelCam2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panelCam1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CamCapIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            try {
                new CamCapIP2().setVisible(true);
            } catch (MalformedURLException ex) {
                Logger.getLogger(CamCapIP.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private javax.swing.JPanel panelCam1;
    private javax.swing.JPanel panelCam2;
}
