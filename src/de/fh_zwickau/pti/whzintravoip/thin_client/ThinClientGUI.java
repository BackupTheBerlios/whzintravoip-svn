package de.fh_zwickau.pti.whzintravoip.thin_client;
import java.awt.event.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

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

    private String soapServerIP = "141.32.28.226";
    private String ipToCall =     "141.32.28.227";

    private SIPReceiver receiver = null;
    private Output outputWindow = null;
    private SOAPMethodCaller methodCaller = null;
    private UserTreeGenerator userTreeGenerator = null;

    private static final byte LOGIN    = 1;
    private static final byte PICKUP   = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING  = 5;
    private static final byte TALKING  = 6;
    private byte status = LOGIN; // Login, Pickup, Incoming, MakeCall, Calling, Talking

//    private enum Status {
//        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
//    }

//    private Status status2 = Status.LOGIN;

    private JTree jTree = null;
    private DefaultMutableTreeNode root = null;
    private DefaultMutableTreeNode child = null;
    private DefaultMutableTreeNode subchild = null;
    private DefaultTreeModel treeModel = null;
    private TreePath m_currentTreePath = null;
    private JEditorPane userInfoField = new JEditorPane();

    private String loginName = null;

    public ThinClientGUI() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        loginName = System.getProperty("user.name");
        this.setSize(500, 700);
        this.setMinimumSize(new Dimension(540, 350));
        this.setLocation(764, 2);
        jTextFieldMyIP.setText(getOwnIP());
        methodCaller = new SOAPMethodCaller(
            this,
            "http://" + soapServerIP + ":8080/soap/servlet/rpcrouter",
            "urn:sip_server:soapserver:appscope");
        setStatusLogin();
        userTreeGenerator = new UserTreeGenerator(null, this);
        userTreeGenerator.initTreeView();
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

    public void showUserInfo(String string){
//        jUserInfoField.setText("");
        jUserInfoField.setText(string);
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
        this.getContentPane().setLayout(gridBagLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        jTextFieldMyIP.setMinimumSize(new Dimension(50, 21));
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
        jScrollPane1.setPreferredSize(new Dimension(40, 40));
        jUserInfoField.setEditable(false);
        jUserInfoField.setText("");
        jButtonDeleteTreeEntry.setText("löschen");
        jButtonDeleteTreeEntry.addActionListener(new
                ThinClientGUI_jButtonDeleteTreeEntry_actionAdapter(this));
        jButtonDeleteAllEntries.setText("alles löschen");
        jButtonDeleteAllEntries.addActionListener(new
                ThinClientGUI_jButtonDeleteAllEntries_actionAdapter(this));
        jButtonAddEntries.setText("Add Entries");
        jButtonAddEntries.addActionListener(new
                ThinClientGUI_jButtonAddEntries_actionAdapter(this));
        jSplitPane1.add(jScrollPane1, JSplitPane.RIGHT);
        jScrollPane1.getViewport().add(jUserInfoField);
        jSplitPane1.add(treeViewScrollPane, JSplitPane.LEFT);
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
        this.getContentPane().add(jLabel1,
                                  new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonDeleteTreeEntry,
                                  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(jButtonDeleteAllEntries,
                                  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(jButtonAddEntries,
                                  new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

    }

    public JScrollPane getTreeViewScrollPane(){
        return treeViewScrollPane;
    }

    public void jButtonDeleteTreeEntry_actionPerformed(ActionEvent e) {
        userTreeGenerator.removeUserTreeEntry();
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
    JTextArea jUserInfoField = new JTextArea();
    JButton jButtonDeleteTreeEntry = new JButton();
    JButton jButtonDeleteAllEntries = new JButton();
    JButton jButtonAddEntries = new JButton();

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

    public void jButtonDeleteAllEntries_actionPerformed(ActionEvent e) {
        userTreeGenerator.removeAllEntries();
    }

    public void jButtonAddEntries_actionPerformed(ActionEvent e) {
        userTreeGenerator.addUserTreeEntry(null);
    }
}


class ThinClientGUI_jButtonAddEntries_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonAddEntries_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonAddEntries_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonDeleteAllEntries_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonDeleteAllEntries_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonDeleteAllEntries_actionPerformed(e);
    }
}


class ThinClientGUI_jButtonDeleteTreeEntry_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonDeleteTreeEntry_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonDeleteTreeEntry_actionPerformed(e);
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

