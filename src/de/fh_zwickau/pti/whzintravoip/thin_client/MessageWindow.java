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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageWindow extends JFrame {
    private int m_iSizeX = 330;
    private int m_iSizeY = 170;
    private ThinClientGUI m_UserGUI;

    public MessageWindow(ThinClientGUI userGUI) {
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
        this.m_UserGUI = userGUI;
    }

    public void setMessageText(String message){
        jText.setText(message);
    }

    JPanel jPanel1 = new JPanel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JLabel jText = new JLabel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JButton jButtonLinks = new JButton();
    JButton jButtonRechts = new JButton();
    JPanel jPanel2 = new JPanel();

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(gridBagLayout1);
        jPanel1.setLayout(gridBagLayout2);
        jButtonLinks.setText("OK");
        jButtonLinks.addActionListener(new
                                       MessageWindow_jButtonLinks_actionAdapter(this));
        jButtonRechts.setText("Abbrechen");
        jButtonRechts.addActionListener(new
                MessageWindow_jButtonRechts_actionAdapter(this));
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

    public void jButtonLinks_actionPerformed(ActionEvent e) {
        setVisible(false);
//            m_UserGUI.playRingTone();
    }

    public void jButtonRechts_actionPerformed(ActionEvent e) {
        setVisible(false);
//        m_UserGUI.stopRingTone();
    }
}


class MessageWindow_jButtonRechts_actionAdapter implements ActionListener {
    private MessageWindow adaptee;
    MessageWindow_jButtonRechts_actionAdapter(MessageWindow adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRechts_actionPerformed(e);
    }
}


class MessageWindow_jButtonLinks_actionAdapter implements ActionListener {
    private MessageWindow adaptee;
    MessageWindow_jButtonLinks_actionAdapter(MessageWindow adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonLinks_actionPerformed(e);
    }
}
