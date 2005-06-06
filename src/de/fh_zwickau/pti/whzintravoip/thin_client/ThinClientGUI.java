package de.fh_zwickau.pti.whzintravoip.thin_client;
import java.awt.event.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;

import com.borland.jbcl.layout.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.sip_comm.*;

/**
 * <p>Überschrift: ThinClientGUI</p>
 *
 * <p>Beschreibung: Oberfläche für den Client</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann ys@fh-zwickau.de
 * @version 0.0.1
 */
public class ThinClientGUI extends JFrame{

    private String soapServerIP = "192.168.0.4";
    private String ipToCall =     "192.168.0.2";

    private SIPReceiver receiver = null;
    private Output outputWindow = null;
    private SOAPMethodCaller methodCaller = null;

    private static final byte LOGIN    = 1;
    private static final byte PICKUP   = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING  = 5;
    private static final byte TALKING  = 6;
    private byte status = LOGIN; // Login, Pickup, Incoming, MakeCall, Calling, Talking

/**
    private enum Status {
        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
    }

    private Status status2 = Status.LOGIN;
 */
    public ThinClientGUI() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setSize(500, 700);
        this.setLocation(764, 2);
        jTextFieldMyIP.setText(getOwnIP());
        methodCaller = new SOAPMethodCaller(
            this,
            "http://" + soapServerIP + ":8080/soap/servlet/rpcrouter",
            "urn:sip_server:soapserver:appscope");
//        toggleOutputWindow();
        setStatusLogin();
    }

    public static void main(String[] args) {
        new ThinClientGUI().setVisible(true);
    }

    public void stdOutput(String std) {
//        jTextArea.append("Info: " + std + "\n");
        if(outputWindow != null){
            outputWindow.stdOutput(std);
        }
    }

    public void errOutput(String err) {
//        jTextArea.append("Error: " + err + "\n");
        if(outputWindow != null){
            outputWindow.errOutput(err);
        }
    }

    public void setStatusLogin(){
        status = LOGIN;
        stdOutput("Status ist jetzt LOGIN (" + status + ")\n");
    }

    public void setStatusPICKUP(){
        status = PICKUP;
        stdOutput("Status ist jetzt PICKUP (" + status + ")\n");
    }

    public void setStatusINCOMING(){
        status = INCOMING;
        stdOutput("Status ist jetzt INCOMING (" + status + ")\n");
    }

    public void setStatusMAKECALL(){
        status = MAKECALL;
        stdOutput("Status ist jetzt MAKECALL (" + status + ")\n");
    }

    public void setStatusCALLING(){
        status = CALLING;
        stdOutput("Status ist jetzt CALLING (" + status + ")\n");
    }

    public void setStatusTALKING(){
        status = TALKING;
        stdOutput("Status ist jetzt TALKING (" + status + ")\n");
    }

    public byte getStatus(){
        return status;
    }

    public void setAcceptButtonTrue(){
        jButtonAccept.setEnabled(true);
    }

    public void setAcceptButtonFalse(){
        jButtonAccept.setEnabled(false);
    }

    private String getOwnIP(){
        String ip = null;
        try {
          InetAddress myIP = InetAddress.getLocalHost();
          ip = myIP.getHostAddress();
        }
        catch (UnknownHostException ex) {
          System.err.println(ex);
        }
        return ip;
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(xYLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
//        jButtonStartReceiver.setBorder(titledBorder3);
//        jButtonStartReceiver.setMargin(new Insets(2, 2, 2, 2));
//        jButtonStartReceiver.setText("Wait for Call");
//        jButtonStartReceiver.addActionListener(new
//                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        jTextFieldMyIP.setText("127.0.0.1");
        jLabel1.setText("My IP");
        this.addWindowListener(new ThinClientGUI_this_windowAdapter(this));
        xYLayout1.setWidth(465);
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
        jButtonToggleOutputWindow.setText("Ausgabefenster öffnen");
        jButtonToggleOutputWindow.addActionListener(new
                ThinClientGUI_jButtonToggleOutputWindow_actionAdapter(this));
        jButtonInitCall.setText("Init Call");
        jButtonInitCall.addActionListener(new
                ThinClientGUI_jButtonInitCall_actionAdapter(this));
        jButtonMakeCall.setText("Make Call");
        jButtonMakeCall.addActionListener(new
                ThinClientGUI_jButtonMakeCall_actionAdapter(this));
        jButtonStartReceiver.setText("Start Receiver");
        jButtonStartReceiver.addActionListener(new
                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        this.getContentPane().add(jTextFieldMyIP,
                                  new XYConstraints(354, 10, 123, 26));
        this.getContentPane().add(jLabel1, new XYConstraints(317, 15, 35, 18));
        this.getContentPane().add(jButtonAccept,
                                  new XYConstraints(10, 494, 100, 30));
        this.getContentPane().add(jButtonDeny,
                                  new XYConstraints(113, 494, 100, 30));
        this.getContentPane().add(jButtonBye,
                                  new XYConstraints(216, 494, 100, 30));
        this.getContentPane().add(jButtonMakeCall,
                                  new XYConstraints(327, 494, 100, 30));
        this.getContentPane().add(jButtonInitCall,
                                  new XYConstraints(327, 460, 100, 30));
        this.getContentPane().add(jButtonStartReceiver, new XYConstraints(11, 12, 104, -1));
        this.getContentPane().add(jScrollPane1,
                                  new XYConstraints(41, 74, 226, 216));
        jScrollPane1.getViewport().add(jTree1);
        this.getContentPane().add(jButtonToggleOutputWindow,
                                  new XYConstraints(119, 12, 184, -1));
    }

    XYLayout xYLayout1 = new XYLayout();
    JTextField jTextFieldMyIP = new JTextField();
    JLabel jLabel1 = new JLabel();
    TitledBorder titledBorder1 = new TitledBorder("");
    TitledBorder titledBorder2 = new TitledBorder("");
    public JButton jButtonAccept = new JButton();
    public JButton jButtonDeny = new JButton();
    public JButton jButtonBye = new JButton();
    TitledBorder titledBorder3 = new TitledBorder("");
    JButton jButtonToggleOutputWindow = new JButton();
    JButton jButtonInitCall = new JButton();
    JButton jButtonMakeCall = new JButton();
    JButton jButtonStartReceiver = new JButton();
    JTree jTree1 = new JTree();
    JScrollPane jScrollPane1 = new JScrollPane();

    public void jButtonStartReceiver_actionPerformed(ActionEvent e) {
        receiver = new SIPReceiver(this, jTextFieldMyIP.getText());
        setStatusPICKUP();
        jButtonStartReceiver.setEnabled(false);
    }

    public void this_windowClosing(WindowEvent e) {
        if(receiver != null) receiver.stopAndRemoveSIPStack();
        System.exit(0);
    }

    public void jButtonAccept_actionPerformed(ActionEvent e) {
        try{
            methodCaller.callSOAPServer("acceptCall", getOwnIP(), null);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
    }

    public void jButtonDeny_actionPerformed(ActionEvent e) {
    }

    public void jButtonBye_actionPerformed(ActionEvent e) {
    }

    public void jButtonToggleOutputWindow_actionPerformed(ActionEvent e) {
        toggleOutputWindow();
    }

    public void toggleOutputWindow() {
        if(outputWindow == null){
            outputWindow = new Output(this);
            outputWindow.setSize(530, 600);
            outputWindow.setVisible(true);
            jButtonToggleOutputWindow.setText("Ausgabefenster schließen");
        } else if (outputWindow.isVisible() == false) {
            outputWindow.setVisible(true);
            jButtonToggleOutputWindow.setText("Ausgabefenster schließen");
        } else {
            outputWindow.setVisible(false);
            jButtonToggleOutputWindow.setText("Ausgabefenster öffnen");
        }
    }

    public void setToggleWindowButtonName(String string){
        jButtonToggleOutputWindow.setText(string);
    }

    public void jButtonInitCall_actionPerformed(ActionEvent e) {
        setStatusMAKECALL();
        try{
            methodCaller.callSOAPServer("initCall", getOwnIP(), ipToCall);
        }catch(Exception ex){
            setStatusPICKUP();
        }
    }

    public void jButtonMakeCall_actionPerformed(ActionEvent e) {
        setStatusMAKECALL();
        try{
            methodCaller.callSOAPServer("makeCall", getOwnIP(), null);
            setStatusTALKING();
        }catch(Exception ex){
            setStatusPICKUP();
        }
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


class ThinClientGUI_jButtonMakeCall_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonMakeCall_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonMakeCall_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonInitCall_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonInitCall_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonInitCall_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonToggleOutputWindow_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonToggleOutputWindow_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonToggleOutputWindow_actionPerformed(e);
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

