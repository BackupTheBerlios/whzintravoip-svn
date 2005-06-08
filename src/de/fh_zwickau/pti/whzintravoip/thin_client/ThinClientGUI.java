package de.fh_zwickau.pti.whzintravoip.thin_client;
import java.awt.event.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;

import com.borland.jbcl.layout.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.sip_comm.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeSelectionModel;
import java.awt.Dimension;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.GridBagLayout;

/**
 * <p>�berschrift: ThinClientGUI</p>
 *
 * <p>Beschreibung: Oberfl�che f�r den Client</p>
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

    private DefaultMutableTreeNode root = null;
    private DefaultTreeModel treeModel = null;
    private TreePath m_currentTreePath = null;
    private JEditorPane Output = new JEditorPane();
    private JTree treeView = new JTree();

    public ThinClientGUI() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setSize(500, 700);
//        Dimension minimumSize = new Dimension(540, 350);
//        this.setMinimumSize(minimumSize);
        this.setLocation(764, 2);
        jTextFieldMyIP.setText(getOwnIP());
        methodCaller = new SOAPMethodCaller(
            this,
            "http://" + soapServerIP + ":8080/soap/servlet/rpcrouter",
            "urn:sip_server:soapserver:appscope");
//        toggleOutputWindow();
        setStatusLogin();
        initTreeView();
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

    public void setDenyButtonTrue(){
        jButtonDeny.setEnabled(true);
    }

    public void setDenyButtonFalse(){
        jButtonDeny.setEnabled(false);
    }

    public void setEndCallButtonTrue(){
        jButtonEndCall.setEnabled(true);
    }

    public void setEndCallButtonFalse(){
        jButtonEndCall.setEnabled(false);
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
        this.setMinimumSize(new Dimension(540, 350));
        this.getContentPane().setLayout(gridBagLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        jTextFieldMyIP.setMinimumSize(new Dimension(50, 21));
        //        jButtonStartReceiver.setBorder(titledBorder3);
//        jButtonStartReceiver.setMargin(new Insets(2, 2, 2, 2));
//        jButtonStartReceiver.setText("Wait for Call");
//        jButtonStartReceiver.addActionListener(new
//                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        jTextFieldMyIP.setText("127.0.0.1");
        jLabel1.setText("My IP");
        this.addWindowListener(new ThinClientGUI_this_windowAdapter(this));
        jButtonAccept.setEnabled(false);
        jButtonAccept.setText("Annehmen");
        jButtonAccept.addActionListener(new
                ThinClientGUI_jButtonAccept_actionAdapter(this));
        jButtonDeny.setEnabled(false);
        jButtonDeny.setText("Ablehnen");
        jButtonDeny.addActionListener(new
                                      ThinClientGUI_jButtonDeny_actionAdapter(this));
        jButtonEndCall.setEnabled(false);
        jButtonEndCall.setText("Beenden");
        jButtonEndCall.addActionListener(new ThinClientGUI_jButtonBye_actionAdapter(this));
        jButtonToggleOutputWindow.setText("Ausgabefenster �ffnen");
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
        jScrollPane1.setPreferredSize(new Dimension(40, 40));
        jSplitPane1.add(jScrollPane1, JSplitPane.RIGHT);
        jScrollPane1.getViewport().add(Output);
        jSplitPane1.add(treeViewScrollPane, JSplitPane.LEFT);
        this.getContentPane().add(jLabel1,
                                  new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 0, 0, 0), 10, 3));
        this.getContentPane().add(jButtonEndCall,
                                  new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonDeny,
                                  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonAccept,
                                  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonMakeCall,
                                  new GridBagConstraints(3, 3, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonInitCall,
                                  new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jSplitPane1,
                                  new GridBagConstraints(0, 1, 5, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonToggleOutputWindow,
                                  new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonStartReceiver,
                                  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jTextFieldMyIP,
                                  new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 50, 10));
        root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
    }

    /**
     * init the scrollpane and the JTree and set the valueChanged Listener
     */
    public void initTreeView()
    {
      //jTree1 = new JTree();
      TreeSelectionModel tsm = new DefaultTreeSelectionModel();
      tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeViewScrollPane.setPreferredSize(new Dimension(300, 150));
    }

    public void fillOutputPage()
    {
        switch (m_currentTreePath.getPathCount()) {
        case 1:
            // The selected Index ist the Root
//            Output.setText((String) m_vTemplates.get(0));
            Output.setText("bla 1");
            break;
        case 2:
            // The selected Index is a manufacturer
//            this.parseManufacturersPage();
            Output.setText("bla 1");
            break;
        case 3:
            // The selected Index is a SerialID Group
            Output.setText("bla 1");
            break;
        case 4:
            // The selected Index is a leaf, which means it is a mobile device
//            this.parseOutputPage();
            Output.setText("bla 1");
            break;
        }
        Output.setText("bla 1");
    }

    JTextField jTextFieldMyIP = new JTextField();
    JLabel jLabel1 = new JLabel();
    TitledBorder titledBorder1 = new TitledBorder("");
    TitledBorder titledBorder2 = new TitledBorder("");
    public JButton jButtonAccept = new JButton();
    public JButton jButtonDeny = new JButton();
    public JButton jButtonEndCall = new JButton();
    TitledBorder titledBorder3 = new TitledBorder("");
    JButton jButtonToggleOutputWindow = new JButton();
    JButton jButtonInitCall = new JButton();
    JButton jButtonMakeCall = new JButton();
    JButton jButtonStartReceiver = new JButton(); //    JTree treeView = new JTree();
    JScrollPane treeViewScrollPane = new JScrollPane(null);
    JSplitPane jSplitPane1 = new JSplitPane();
    JScrollPane jScrollPane1 = new JScrollPane();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

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
        acceptCall();
    }

    public void jButtonDeny_actionPerformed(ActionEvent e) {
        denyCall();
    }

    public void jButtonEndCall_actionPerformed(ActionEvent e) {
        endCall();
    }

    public void acceptCall() {
        try{
            methodCaller.callSOAPServer("acceptCall", getOwnIP(), null);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        setAcceptButtonFalse();
        setDenyButtonFalse();
        setEndCallButtonTrue();
        setStatusTALKING();
    }

    public void denyCall(){
        try{
            methodCaller.callSOAPServer("denyCall", getOwnIP(), null);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        setAcceptButtonFalse();
        setDenyButtonFalse();
    }

    public void endCall(){
        try{
            methodCaller.callSOAPServer("endCall", getOwnIP(), null);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        setEndCallButtonFalse();
        setStatusPICKUP();
    }

    public void callEstablished(){
        setAcceptButtonFalse();
        setDenyButtonFalse();
        setEndCallButtonTrue();
        setStatusTALKING();
    }

    public void jButtonToggleOutputWindow_actionPerformed(ActionEvent e) {
        toggleOutputWindow();
    }

    public void toggleOutputWindow() {
        if(outputWindow == null){
            outputWindow = new Output(this);
            outputWindow.setSize(530, 600);
            outputWindow.setVisible(true);
            jButtonToggleOutputWindow.setText("Ausgabefenster schlie�en");
        } else if (outputWindow.isVisible() == false) {
            outputWindow.setVisible(true);
            jButtonToggleOutputWindow.setText("Ausgabefenster schlie�en");
        } else {
            outputWindow.setVisible(false);
            jButtonToggleOutputWindow.setText("Ausgabefenster �ffnen");
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
        adaptee.jButtonEndCall_actionPerformed(e);
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

