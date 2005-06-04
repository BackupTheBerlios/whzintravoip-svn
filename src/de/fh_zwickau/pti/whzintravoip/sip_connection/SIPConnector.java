package de.fh_zwickau.pti.whzintravoip.sip_connection;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.borland.jbcl.layout.*;
import de.fh_zwickau.pti.whzintravoip.rtp_comm.*;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Y. Schumann
 * @version 0.0.1
 */
public class SIPConnector extends JFrame {

    private SIPPacketReceiver receiver = null;
    private SIPPacketCaller caller = null;
    private VoIP_main windowForRTP = null;
//    private SIPClient client = null;
    private boolean m_bIAmCaller;
    private String m_sMyIP = "127.0.0.1";
    private String m_sIPToCall = "127.0.0.1";

    public SIPConnector() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setSize(610, 700);
        this.setLocation(0, 2);
    }

    public static void main(String[] args) {
        new SIPConnector().setVisible(true);
        SIPConnector sippacketreceiver_dialog = new SIPConnector();
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        jMainPanel.setLayout(borderLayout2);
        jPanelForButtons.setLayout(xYLayout1);
        jButtonStop.setEnabled(false);
        jButtonStop.setText("Stop");
        jButtonStop.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonStop_actionAdapter(this));
        jButtonInitSIPStack.setEnabled(false);
        jButtonInitSIPStack.setText("Init Stack");
        jButtonInitSIPStack.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonInit_actionAdapter(this));
        jButtonInitFactories.setEnabled(false);
        jButtonInitFactories.setText("Init Factories");
        jButtonInitFactories.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonReceive_actionAdapter(this));
        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonExit_actionAdapter(this));
        jPanel1.setLayout(borderLayout3);
        jTextAreaForOutput.setText("");
        jPanelForButtons2.setLayout(flowLayout2);
        jButtonInvite.setEnabled(false);
        jButtonInvite.setText("Invite");
        jButtonInvite.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonInvite_actionAdapter(this));
        jButtonSendBye.setEnabled(false);
        jButtonSendBye.setText("Send Bye");
        jButtonSendBye.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonSendBye_actionAdapter(this));
        jButtonAcceptCall.setEnabled(false);
        jButtonAcceptCall.setText("Accept");
        jButtonAcceptCall.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonProcessInvite_actionAdapter(this));
        jButtonTalk.setEnabled(false);
        jButtonTalk.setText("Talk");
        jButtonTalk.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonTalk_actionAdapter(this));
        jButtonIamReceiver.setText("Receiver");
        jButtonIamReceiver.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonIamReceiver_actionAdapter(this));
        jButtonIamCaller.setText("Caller");
        jButtonIamCaller.addActionListener(new
                SIPPacketReceiver_Dialog_jButtonIamCaller_actionAdapter(this));
        jScrollPane1.setAutoscrolls(true);
        jTextFieldMyIP.setText("127.0.0.1");
        jLabel1.setText("My IP:");
        jLabel2.setText("IP to call:");
        jTextFieldIPToCall.setText("127.0.0.1");
        jButtonOptions.setEnabled(false);
        jButtonOptions.setText("Options?");
        jButtonOptions.addActionListener(new
                SIPConnector_jButtonOptions_actionAdapter(this));
        this.addWindowListener(new SIPConnector_this_windowAdapter(this));
        this.getContentPane().add(jMainPanel, java.awt.BorderLayout.CENTER);
        jMainPanel.add(jPanel1, java.awt.BorderLayout.CENTER);
        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        jScrollPane1.getViewport().add(jTextAreaForOutput);
        jMainPanel.add(jPanelForButtons2, java.awt.BorderLayout.SOUTH);
        jPanelForButtons2.add(jButtonInvite);
        jPanelForButtons2.add(jButtonAcceptCall);
        jPanelForButtons2.add(jButtonTalk);
        jPanelForButtons2.add(jButtonSendBye);
        jPanelForButtons2.add(jButtonOptions);
        jMainPanel.add(jPanelForButtons, java.awt.BorderLayout.NORTH);
        jPanelForButtons.add(jLabel1, new XYConstraints(12, 9, -1, -1));
        jPanelForButtons.add(jTextFieldMyIP, new XYConstraints(58, 5, 130, -1));
        jPanelForButtons.add(jLabel2, new XYConstraints(224, 9, -1, -1));
        jPanelForButtons.add(jTextFieldIPToCall,
                             new XYConstraints(279, 5, 130, -1));
        jPanelForButtons.add(jButtonInitSIPStack,
                             new XYConstraints(219, 30, 100, -1));
        jPanelForButtons.add(jButtonIamCaller, new XYConstraints(8, 30, 100, -1));
        jPanelForButtons.add(jButtonIamReceiver,
                             new XYConstraints(114, 30, 100, -1));
        jPanelForButtons.add(jButtonInitFactories,
                             new XYConstraints(324, 30, 120, -1));
        jPanelForButtons.add(jButtonStop, new XYConstraints(449, 30, 80, -1));
        jPanelForButtons.add(jButtonExit, new XYConstraints(534, 30, 60, -1));
    }

    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jMainPanel = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel jPanelForButtons = new JPanel();
    JButton jButtonStop = new JButton();
    JButton jButtonInitSIPStack = new JButton();
    JButton jButtonInitFactories = new JButton();
    JButton jButtonExit = new JButton();
    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout3 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextAreaForOutput = new JTextArea();
    JPanel jPanelForButtons2 = new JPanel();
    FlowLayout flowLayout2 = new FlowLayout();
    JButton jButtonInvite = new JButton();
    JButton jButtonSendBye = new JButton();
    JButton jButtonAcceptCall = new JButton();
    JButton jButtonTalk = new JButton();
    JButton jButtonIamReceiver = new JButton();
    JButton jButtonIamCaller = new JButton();
    XYLayout xYLayout1 = new XYLayout();
    JTextField jTextFieldMyIP = new JTextField();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JTextField jTextFieldIPToCall = new JTextField();
    JButton jButtonOptions = new JButton();

    public void jButtonExit_actionPerformed(ActionEvent e) {
        System.out.println("Exitbutton pressed...");
        if(m_bIAmCaller == true){
            if(caller != null) caller.stopAndRemoveSIPStack();
        }else{
            if(receiver != null) receiver.stopAndRemoveSIPStack();
        }
        System.exit(0);
    }

    public void jButtonInit_actionPerformed(ActionEvent e) {
        stdOutput("Initialisierung");
        try {
            if(getCallerOrReceiverStatus() == true){
                caller = new SIPPacketCaller(this, m_sMyIP, m_sIPToCall);
                caller.initCallerSIPStack();
                stdOutput("sipStack = " + caller.sipStackCaller);
            }else{
                receiver = new SIPPacketReceiver(this, m_sMyIP, m_sIPToCall);
                receiver.initReceiverSIPStack();
                stdOutput("sipStack = " + receiver.sipStackReceiver);
            }
        } catch (Exception ex) {
            errOutput("Exception beim Initialisieren abgefangen ");
            ex.printStackTrace();
            errOutput(ex.getMessage());
            if (ex.getCause() != null)
                ex.getCause().printStackTrace();
//            System.exit(0);
        }
        jButtonInitFactories.setEnabled(true);
        jButtonInitSIPStack.setEnabled(false);
    }

    public void stdOutput(String std) {
        jTextAreaForOutput.append("Info: " + std + "\n");
    }

    public void errOutput(String err) {
        jTextAreaForOutput.append("Error: " + err + "\n");
    }

    public void jButtonInitFactories_actionPerformed(ActionEvent e) {
        try {
            if(getCallerOrReceiverStatus() == true){
                caller.initCallerFactories();
                stdOutput("headerFactory = " + caller.sipStackCaller);
            }else{
                receiver.initReceiverFactories();
                stdOutput("headerFactory = " + receiver.sipStackReceiver);
            }
        } catch (Exception ex) {
            errOutput(ex.getMessage());
            ex.printStackTrace();
        }
        jButtonStop.setEnabled(true);
        jButtonInitFactories.setEnabled(false);
        if(getCallerOrReceiverStatus() == true){
            jButtonInvite.setEnabled(true);
            jButtonOptions.setEnabled(true);
        }
    }

    public void jButtonStop_actionPerformed(ActionEvent e) {
       try {
           if(getCallerOrReceiverStatus() == true){
               caller.stopAndRemoveSIPStack();
           }else{
               receiver.stopAndRemoveSIPStack();
           }
       } catch (Exception ex) {
           errOutput(ex.getMessage());
           ex.printStackTrace();
       }
       jButtonStop.setEnabled(false);
       jButtonInitFactories.setEnabled(false);
       jButtonInitSIPStack.setEnabled(false);
       setAllSessionButtonsFalse();
       jButtonIamCaller.setEnabled(true);
       jButtonIamReceiver.setEnabled(true);
    }

    public void setAllSessionButtonsFalse(){
        jButtonInvite.setEnabled(false);
        jButtonAcceptCall.setEnabled(false);
        jButtonTalk.setEnabled(false);
        jButtonSendBye.setEnabled(false);
        jButtonOptions.setEnabled(false);
    }

    public void jButtonInvite_actionPerformed(ActionEvent e) {
        caller.inviteNow();
    }

    public void jButtonProcessInvite_actionPerformed(ActionEvent e) {
        receiver.acceptTheCall();
    }

    public void jButtonTalk_actionPerformed(ActionEvent e) {
        windowForRTP = new VoIP_main();
        windowForRTP.main(null);
    }

    public void jButtonSendBye_actionPerformed(ActionEvent e) {
        if(getCallerOrReceiverStatus() == true){
            caller.sendBye();
        }else{
            receiver.sendBye();
        }
    }

    public void jButtonProcessBye_actionPerformed(ActionEvent e) {
        if(getCallerOrReceiverStatus() == true){
            caller.processBye();
        }else{
//            receiver.processBye();
        }
    }

    public void jButtonIamCaller_actionPerformed(ActionEvent e) {
        initIPs();
        setCallerOrReceiverStatus(true);
        jButtonIamCaller.setEnabled(false);
        jButtonIamReceiver.setEnabled(false);
        jButtonInitSIPStack.setEnabled(true);
    }

    public void jButtonIamReceiver_actionPerformed(ActionEvent e) {
        initIPs();
        setCallerOrReceiverStatus(false);
        jButtonIamCaller.setEnabled(false);
        jButtonIamReceiver.setEnabled(false);
        jButtonInitSIPStack.setEnabled(true);
    }

    public void initIPs(){
        m_sMyIP = jTextFieldMyIP.getText();
        m_sIPToCall = jTextFieldIPToCall.getText();
    }

    public boolean setCallerOrReceiverStatus(boolean status){
        m_bIAmCaller = status;
        return m_bIAmCaller;
    }

    public boolean getCallerOrReceiverStatus(){
        return m_bIAmCaller;
    }

    public void jButtonOptions_actionPerformed(ActionEvent e) {
        try {
            if (getCallerOrReceiverStatus() == true) {
                caller.askForOptions();
            } else {
                receiver.askForOptions();
            }
        } catch (Exception ex) {
            errOutput(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void this_windowClosing(WindowEvent e) {
        System.out.println("Closebutton pressed...");
        if(m_bIAmCaller == true){
            if(caller != null) caller.stopAndRemoveSIPStack();
        }else{
            if(receiver != null) receiver.stopAndRemoveSIPStack();
        }
        System.exit(0);
    }
}


class SIPConnector_this_windowAdapter extends WindowAdapter {
    private SIPConnector adaptee;
    SIPConnector_this_windowAdapter(SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}


class SIPConnector_jButtonOptions_actionAdapter implements ActionListener {
    private SIPConnector adaptee;
    SIPConnector_jButtonOptions_actionAdapter(SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.jButtonOptions_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonIamReceiver_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonIamReceiver_actionAdapter(
            SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonIamReceiver_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonIamCaller_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonIamCaller_actionAdapter(
            SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonIamCaller_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonSendBye_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonSendBye_actionAdapter(
            SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonSendBye_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonTalk_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonTalk_actionAdapter(
            SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonTalk_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonInvite_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonInvite_actionAdapter(
            SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonInvite_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonProcessInvite_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonProcessInvite_actionAdapter(
            SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonProcessInvite_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonStop_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonStop_actionAdapter(SIPConnector
            adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonStop_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonReceive_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonReceive_actionAdapter(
            SIPConnector adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonInitFactories_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonInit_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonInit_actionAdapter(SIPConnector
            adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonInit_actionPerformed(e);
    }
}


class SIPPacketReceiver_Dialog_jButtonExit_actionAdapter implements
        ActionListener {
    private SIPConnector adaptee;
    SIPPacketReceiver_Dialog_jButtonExit_actionAdapter(SIPConnector
            adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonExit_actionPerformed(e);
    }
}
