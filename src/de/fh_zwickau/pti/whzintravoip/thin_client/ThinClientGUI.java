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
        jButtonStartReceiver.setBorder(titledBorder2);
        jButtonStartReceiver.setMargin(new Insets(2, 2, 2, 2));
        jButtonStartReceiver.setText("Wait for Call");
        jButtonStartReceiver.addActionListener(new
                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        jTextFieldMyIP.setText("127.0.0.1");
        jLabel1.setText("My IP");
        this.addWindowListener(new ThinClientGUI_this_windowAdapter(this));
        xYLayout1.setWidth(491);
        xYLayout1.setHeight(495);
        this.getContentPane().add(jButtonStartReceiver,
                                  new XYConstraints(9, 9, 103, 27));
        jScrollPane1.getViewport().add(jTextArea);
        this.getContentPane().add(jScrollPane1,
                                  new XYConstraints(9, 45, 469, 440));
        this.getContentPane().add(jTextFieldMyIP,
                                  new XYConstraints(354, 10, 123, 26));
        this.getContentPane().add(jLabel1, new XYConstraints(317, 15, 35, 18));
    }

    XYLayout xYLayout1 = new XYLayout();
    JButton jButtonStartReceiver = new JButton();
    JTextArea jTextArea = new JTextArea();
    JTextField jTextFieldMyIP = new JTextField();
    JLabel jLabel1 = new JLabel();
    TitledBorder titledBorder1 = new TitledBorder("");
    TitledBorder titledBorder2 = new TitledBorder("");
    JScrollPane jScrollPane1 = new JScrollPane();

    public void jButtonStartReceiver_actionPerformed(ActionEvent e) {
        receiver = new SIPReceiver(this, jTextFieldMyIP.getText());
    }

    public void this_windowClosing(WindowEvent e) {
        if(receiver != null) receiver.stopAndRemoveSIPStack();
        System.exit(0);
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
