package de.fh_zwickau.pti.whzintravoip.thin_client;
import de.fh_zwickau.pti.whzintravoip.thin_client.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.sip_comm.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import com.borland.jbcl.layout.*;
import javax.swing.border.TitledBorder;

/**
 * <p>Überschrift: ThinClientGUI</p>
 *
 * <p>Beschreibung: Oberfläche für den Client</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann
 * @version 0.0.1
 */
public class ThinClientGUI extends JFrame{

    private SIPReceiver receiver;

    public ThinClientGUI() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setSize(500, 700);
        this.setLocation(764, 2);
    }

    public static void main(String[] args) {
        new ThinClientGUI().setVisible(true);
    }

    public void stdOutput(String std) {
//        jTextArea.append("Info: " + std + "\n");
        jTextArea.append("Info: " + std + "\n");
    }

    public void errOutput(String err) {
//        jTextArea.append("Error: " + err + "\n");
        jTextArea.append("Error: " + err + "\n");
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(xYLayout1);
        jButtonStartReceiver.setBorder(titledBorder3);
        jButtonStartReceiver.setMargin(new Insets(2, 2, 2, 2));
        jButtonStartReceiver.setText("Wait for Call");
        jButtonStartReceiver.addActionListener(new
                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        jTextFieldMyIP.setText("127.0.0.1");
        jLabel1.setText("My IP");
        this.addWindowListener(new ThinClientGUI_this_windowAdapter(this));
        xYLayout1.setWidth(491);
        xYLayout1.setHeight(565);
        jButtonAccept.setEnabled(false);
        jButtonAccept.setText("Annehmen");
        jButtonAccept.addActionListener(new
                ThinClientGUI_jButtonAccept_actionAdapter(this));
        jButtonDeny.setEnabled(false);
        jButtonDeny.setText("Ablehnen");
        jButtonDeny.addActionListener(new
                                      ThinClientGUI_jButtonDeny_actionAdapter(this));
        jButtonBye.setEnabled(false);
        jButtonBye.setText("Beenden");
        jButtonBye.addActionListener(new ThinClientGUI_jButtonBye_actionAdapter(this));
        this.getContentPane().add(jButtonStartReceiver,
                                  new XYConstraints(9, 9, 103, 27));
        jScrollPane1.getViewport().add(jTextArea);
        this.getContentPane().add(jScrollPane1,
                                  new XYConstraints(9, 45, 469, 440));
        this.getContentPane().add(jTextFieldMyIP,
                                  new XYConstraints(354, 10, 123, 26));
        this.getContentPane().add(jLabel1, new XYConstraints(317, 15, 35, 18));
        this.getContentPane().add(jButtonAccept,
                                  new XYConstraints(10, 494, 100, 30));
        this.getContentPane().add(jButtonDeny,
                                  new XYConstraints(113, 494, 100, 30));
        this.getContentPane().add(jButtonBye,
                                  new XYConstraints(216, 494, 100, 30));
    }

    XYLayout xYLayout1 = new XYLayout();
    JButton jButtonStartReceiver = new JButton();
    JTextArea jTextArea = new JTextArea();
    JTextField jTextFieldMyIP = new JTextField();
    JLabel jLabel1 = new JLabel();
    TitledBorder titledBorder1 = new TitledBorder("");
    TitledBorder titledBorder2 = new TitledBorder("");
    JScrollPane jScrollPane1 = new JScrollPane();
    JButton jButtonAccept = new JButton();
    JButton jButtonDeny = new JButton();
    JButton jButtonBye = new JButton();
    TitledBorder titledBorder3 = new TitledBorder("");

    public void jButtonStartReceiver_actionPerformed(ActionEvent e) {
        receiver = new SIPReceiver(this, jTextFieldMyIP.getText());
    }

    public void this_windowClosing(WindowEvent e) {
        if(receiver != null) receiver.stopAndRemoveSIPStack();
        System.exit(0);
    }

    public void jButtonAccept_actionPerformed(ActionEvent e) {

    }

    public void jButtonDeny_actionPerformed(ActionEvent e) {

    }

    public void jButtonBye_actionPerformed(ActionEvent e) {

    }
}


class ThinClientGUI_jButtonBye_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonBye_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonBye_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonDeny_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonDeny_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonDeny_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonAccept_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonAccept_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonAccept_actionPerformed(e);
    }
}


class ThinClientGUI_this_windowAdapter extends WindowAdapter {
    private ThinClientGUI adaptee;
    ThinClientGUI_this_windowAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}

class ThinClientGUI_jButtonStartReceiver_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonStartReceiver_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonStartReceiver_actionPerformed(e);
    }
}
