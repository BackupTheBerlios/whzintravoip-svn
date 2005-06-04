package de.fh_zwickau.pti.whzintravoip;

import de.fh_zwickau.pti.whzintravoip.sip_connection.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.*;
import de.fh_zwickau.pti.whzintravoip.db_access.*;
import javax.swing.JFrame;
import com.borland.jbcl.layout.XYLayout;
import com.borland.jbcl.layout.*;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 * <p>Überschrift: WHZIntraVoIP</p>
 *
 * <p>Beschreibung: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann
 * @version 0.0.1
 */
public class IntraVoIP_main  extends JFrame{
    public IntraVoIP_main() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setSize(200, 200);
        this.setLocation(612, 2);
    }

    public static void main(String[] args) {
        /**
        SIPConnector dialog = new SIPConnector();
        dialog.main(null);
        ThinClientGUI thinClientGUI = new ThinClientGUI();
        */
        IntraVoIP_main intraVoIP_main = new IntraVoIP_main();
        intraVoIP_main.setVisible(true);
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(xYLayout1);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("VoIP Main Test Window");
        jButtonCaller.setText("Start Caller");
        jButtonCaller.addActionListener(new
                IntraVoIP_main_jButtonCaller_actionAdapter(this));
        jButtonReceiver.addActionListener(new
                IntraVoIP_main_jButtonReceiver_actionAdapter(this));
        this.addWindowListener(new IntraVoIP_main_this_windowAdapter(this));
        jButtonHib.setActionCommand("start Hib");
        jButtonHib.setText("Hibernate Test");
        jButtonHib.addActionListener(new
                                     IntraVoIP_main_jButtonHib_actionAdapter(this));
        xYLayout1.setWidth(271);
        xYLayout1.setHeight(221);
        this.getContentPane().add(jButtonCaller, new XYConstraints(11, 12, 116, 36));
        this.getContentPane().add(jButtonReceiver, new XYConstraints(11, 55, 116, 36));
        this.getContentPane().add(jButtonHib,
                                  new XYConstraints(10, 100, 163, 36));
        jButtonReceiver.setText("Start Receiver");
    }

    XYLayout xYLayout1 = new XYLayout();
    JButton jButtonCaller = new JButton();
    JButton jButtonReceiver = new JButton();
    JButton jButtonHib = new JButton();

    public void jButtonCaller_actionPerformed(ActionEvent e) {
        SIPConnector dialog = new SIPConnector();
        dialog.main(null);
    }

    public void jButtonReceiver_actionPerformed(ActionEvent e) {
        ThinClientGUI thinClientGUI = new ThinClientGUI();
        thinClientGUI.main(null);
    }

    public void this_windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void jButtonHib_actionPerformed(ActionEvent e) {
        db_test test = new db_test();
        test.main(null);
    }
}


class IntraVoIP_main_jButtonHib_actionAdapter implements ActionListener {
    private IntraVoIP_main adaptee;
    IntraVoIP_main_jButtonHib_actionAdapter(IntraVoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonHib_actionPerformed(e);
    }
}


class IntraVoIP_main_this_windowAdapter extends WindowAdapter {
    private IntraVoIP_main adaptee;
    IntraVoIP_main_this_windowAdapter(IntraVoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}


class IntraVoIP_main_jButtonReceiver_actionAdapter implements ActionListener {
    private IntraVoIP_main adaptee;
    IntraVoIP_main_jButtonReceiver_actionAdapter(IntraVoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonReceiver_actionPerformed(e);
    }
}


class IntraVoIP_main_jButtonCaller_actionAdapter implements ActionListener {
    private IntraVoIP_main adaptee;
    IntraVoIP_main_jButtonCaller_actionAdapter(IntraVoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonCaller_actionPerformed(e);
    }
}
