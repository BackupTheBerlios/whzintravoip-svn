package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Überschrift: WHZIntraVoIP</p>
 *
 * <p>Beschreibung: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann yves.schumann@fh-zwickau.de
 * @version 0.0.1
 */

import javax.swing.*;
import java.awt.*;

public class MessageWindow extends JFrame {
    private int m_iSizeX = 330;
    private int m_iSizeY = 170;

    public MessageWindow() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setSize(m_iSizeX, m_iSizeY);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int screenX = dimension.width;
        int screenY = dimension.height;
        setLocation((screenX - m_iSizeX) / 2, (screenY - m_iSizeY) / 2);
    }

    public void setMessageText(String message){
        jText.setText(message);
    }

    private ThinClientGUI m_UserGUI;
    JPanel jPanel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel jText = new JLabel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JButton jButtonLinks = new JButton();
    JButton jButtonRechts = new JButton();
    JPanel jPanel2 = new JPanel();
    public MessageWindow(ThinClientGUI userGUI) {
        this.m_UserGUI = userGUI;
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(gridBagLayout1);
        jPanel1.setLayout(gridBagLayout2);
        jButtonLinks.setText("OK");
        jButtonRechts.setText("Abbrechen");
        jPanel1.add(jText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        this.getContentPane().add(jPanel1,
                                  new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 5, 10), 390, 290));
        this.getContentPane().add(jPanel2,
                                  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 0, 0));
        jPanel2.add(jButtonLinks);
        jPanel2.add(jButtonRechts);
        jText.setText("");
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
    }
}
