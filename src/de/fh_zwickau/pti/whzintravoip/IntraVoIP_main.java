package de.fh_zwickau.pti.whzintravoip;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: This is the main class for the different parts of the
 * project. We used it to start parts independently.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann <ys@fh-zwickau.de>
 * @version 0.1.0
 */

import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
import de.fh_zwickau.pti.whzintravoip.ldap_connection.*;
import de.fh_zwickau.pti.whzintravoip.sip_connection.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.*;

public class IntraVoIP_main extends JFrame {
    public IntraVoIP_main() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setSize(200, 250);
        this.setLocation(612, 2);
    }

    public static void main(String[] args) {
        IntraVoIP_main intraVoIP_main = new IntraVoIP_main();
        intraVoIP_main.setVisible(true);
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(xYLayout1);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("VoIP Main Test Window");
        xYLayout1.setWidth(271);
        xYLayout1.setHeight(221);
        jButtonSIPTester.setText("SIP-Tester");
        jButtonSIPTester.setToolTipText("This is the SIP-Tester");
        jButtonSIPTester.addActionListener(new
                                           IntraVoIP_main_jButtonCaller_actionAdapter(this));
        jButtonThinClient.setText("Thin Client");
        jButtonThinClient.setToolTipText("Start the thin client");
        jButtonThinClient.addActionListener(new
                                          IntraVoIP_main_jButtonReceiver_actionAdapter(this));
        jButtonHib.setActionCommand("start Hib");
        jButtonHib.setText("Hibernate Test");
        jButtonHib.setToolTipText("Start the Hibernate test");
        jButtonHib.addActionListener(new
                                     IntraVoIP_main_jButtonHib_actionAdapter(this));
        jButtonLDAPTest.setText("Test LDAP");
        jButtonLDAPTest.setToolTipText("Try a LDAP Test");
        jButtonLDAPTest.addActionListener(new
                                          IntraVoIP_main_jButtonLDAPTest_actionAdapter(this));
        this.addWindowListener(new IntraVoIP_main_this_windowAdapter(this));
        this.getContentPane().add(jButtonSIPTester,
                                  new XYConstraints(11, 12, 116, 36));
        this.getContentPane().add(jButtonThinClient,
                                  new XYConstraints(11, 55, 116, 36));
        this.getContentPane().add(jButtonHib,
                                  new XYConstraints(10, 100, 163, 36));
        this.getContentPane().add(jButtonLDAPTest,
                                  new XYConstraints(11, 145, 116, 36));
    }

    XYLayout xYLayout1 = new XYLayout();
    JButton jButtonSIPTester = new JButton();
    JButton jButtonThinClient = new JButton();
    JButton jButtonHib = new JButton();
    JButton jButtonLDAPTest = new JButton();

    /**
     * This method creates a new SIPConnector and calls "main(null)"
     *
     * @param e ActionEvent
     */
    public void jButtonCaller_actionPerformed(ActionEvent e) {
        SIPConnector dialog = new SIPConnector();
        dialog.main(null);
    }

    /**
     * This method creates a new ThinClient and calls "main(null)"
     *
     * @param e ActionEvent
     */
    public void jButtonReceiver_actionPerformed(ActionEvent e) {
        ThinClient thinClient = new ThinClient();
        thinClient.main(null);
    }

    /**
     * This method calls "System.exit(0)"
     *
     * @param e WindowEvent
     */
    public void this_windowClosing(WindowEvent e) {
        System.exit(0);
    }

    /**
     * This method creates a new db_test and calls "main(null)"
     *
     * @param e ActionEvent
     */
    public void jButtonHib_actionPerformed(ActionEvent e) {
//        db_test test = new db_test();
//        test.main(null);
    }

    /**
     * This method creates a new LDAPConnection and calls "main(null)"
     *
     * @param e ActionEvent
     */
    public void jButtonLDAPTest_actionPerformed(ActionEvent e) {
        LDAPConnectionTest testConnect = new LDAPConnectionTest();
        testConnect.main(null);
    }
}


class IntraVoIP_main_jButtonLDAPTest_actionAdapter implements ActionListener {
    private IntraVoIP_main adaptee;
    IntraVoIP_main_jButtonLDAPTest_actionAdapter(IntraVoIP_main adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonLDAPTest_actionPerformed(e);
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
