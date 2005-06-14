package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Überschrift: WHZIntraVoIP, ThinClientGUI</p>
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

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

public class ThinClientGUI extends JFrame{

    private String m_sSOAPServerIP = "192.168.0.7";
    private String m_sIPToCall     = "192.168.0.6";
    private String m_sMySIPAddress = "My SIP Address";
    private String m_sMySIPName    = "SWF";
    private String m_sMyScreenName = "StarWarsFan";
    private String m_sLoginName    = null;

    private SIPStack m_ThinClientSIPStack = null;
    private Output m_OutputWindow = null;
    private SOAPMethodCaller m_MethodCaller = null;
    private UserTreeGenerator m_UserTreeGenerator = null;
    private PlayTunes m_PlayTunes = null;
    private User m_Myself = null;
    private Vector m_UserVector = null;
    private boolean m_bDebug = true;

    private static final byte LOGIN    = 1;
    private static final byte PICKUP   = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING  = 5;
    private static final byte TALKING  = 6;
    private byte m_bStatus = LOGIN; // Login, Pickup, Incoming, MakeCall, Calling, Talking

//    private enum Status {
//        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
//    }

//    private Status status2 = Status.LOGIN;

//    private JTree jTree = null;
    private DefaultMutableTreeNode root = null;
    private DefaultMutableTreeNode child = null;
    private DefaultMutableTreeNode subchild = null;
//    private DefaultTreeModel treeModel = null;
//    private TreePath m_currentTreePath = null;
//    private JEditorPane userInfoField = new JEditorPane();

    public ThinClientGUI() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        m_sLoginName = System.getProperty("user.name");
        this.setSize(500, 700);
        this.setMinimumSize(new Dimension(540, 350));
        this.setLocation(764, 2);
        jTextFieldMyIP.setText(getOwnIP());

        // SOAP-Caller initialisieren
        m_MethodCaller = new SOAPMethodCaller(
            this,
            "http://" + m_sSOAPServerIP + ":8080/soap/servlet/rpcrouter",
            "urn:sip_server:soapserver:appscope",
            "de.fh_zwickau.pti.whzintravoip.sip_server.user.User");
        setStatusLogin();

        // User-Tree bauen
        createDummyUsers();
        m_UserTreeGenerator = new UserTreeGenerator(m_UserVector, this);
        m_UserTreeGenerator.initTreeView();

        // Ringtone-Player initialisieren
//        playTunes = new PlayTunes(this);
    }

    /**
     * Mainmethode um das Hauptfenster einzublenden
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        new ThinClientGUI().setVisible(true);
    }

    /**
     * Gibt Text als Infomessage im Ausgabefenster aus. Dies geschieht nur,
     * wenn es bereits einmal geöffnet worden ist.
     *
     * @param std String - der auszugebende Text als String
     */
    public void stdOutput(String msg) {
        //        jTextArea.append("Info: " + std + "\n");
        if (m_bDebug == true) {
            System.out.println("Info: " + msg);
        }
        if (m_OutputWindow != null) {
            m_OutputWindow.stdOutput(msg);
        }
    }

    /**
     * Gibt Text als Fehlermessage im Ausgabefenster aus. Dies geschieht nur,
     * wenn es bereits einmal geöffnet worden ist.
     *
     * @param err String - der auszugebende Text als String
     */
    public void errOutput(String err) {
        //        jTextArea.append("Error: " + err + "\n");
        if (m_bDebug == true) {
            System.out.println("Error: " + err);
        }
        if (m_OutputWindow != null) {
            m_OutputWindow.errOutput(err);
        }
    }

    /**
     * Gibt im Textarea rechts neben dem Tree einen Infotext aus
     *
     * @param string String - auszugebender Infotext
     */
    public void showUserInfo(String string){
    //        jUserInfoField.setText("");
        jUserInfoField.setText(string);
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusLogin(){
        m_bStatus = LOGIN;
        stdOutput("Status ist jetzt LOGIN (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusPICKUP(){
        m_bStatus = PICKUP;
        stdOutput("Status ist jetzt PICKUP (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusINCOMING(){
        m_bStatus = INCOMING;
        stdOutput("Status ist jetzt INCOMING (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusMAKECALL(){
        m_bStatus = MAKECALL;
        stdOutput("Status ist jetzt MAKECALL (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusCALLING(){
        m_bStatus = CALLING;
        stdOutput("Status ist jetzt CALLING (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusTALKING(){
        m_bStatus = TALKING;
        stdOutput("Status ist jetzt TALKING (" + m_bStatus + ")\n");
    }

    /**
     * Liefert den eigenen Status.
     *
     * @return byte - Der eigene Status.
     */
    public byte getStatus(){
        return m_bStatus;
    }

    /**
     * Setzt den entsprechenden Button auf TRUE
     */
    public void setAcceptButtonTrue(){
        jButtonAccept.setEnabled(true);
    }

    /**
     * Setzt den entsprechenden Button auf FALSE
     */
    public void setAcceptButtonFalse(){
        jButtonAccept.setEnabled(false);
    }

    /**
     * Setzt den entsprechenden Button auf TRUE
     */
    public void setDenyButtonTrue(){
        jButtonDeny.setEnabled(true);
    }

    /**
     * Setzt den entsprechenden Button auf FALSE
     */
    public void setDenyButtonFalse(){
        jButtonDeny.setEnabled(false);
    }

    /**
     * Setzt den entsprechenden Button auf TRUE
     */
    public void setEndCallButtonTrue(){
        jButtonEndCall.setEnabled(true);
    }

    /**
     * Setzt den entsprechenden Button auf FALSE
     */
    public void setEndCallButtonFalse(){
        jButtonEndCall.setEnabled(false);
    }

    private void createAndRegisterMe(){
        createMyIdentity();
        try{
            m_MethodCaller.registerMyselfAtServer("registerUser", m_Myself, null);
        }catch(Exception ex){
            errOutput(ex.toString());
        }
    }

    /**
     * Legt ein Userobjekt für die eigene Identität an
     */
    private void createMyIdentity(){
        Properties myProperties = new Properties();
        myProperties.setProperty("sip_server.user.USER_IP", getOwnIP());
        myProperties.setProperty("sip_server.user.USER_INITIAL", m_sLoginName);
        myProperties.setProperty("sip_server.user.SIP_ADDRESS", m_sMySIPAddress);
        myProperties.setProperty("sip_server.user.SIP_NAME", m_sMySIPName);
        myProperties.setProperty("sip_server.user.SCREEN_NAME", m_sMyScreenName);
        m_Myself = new User();
        m_Myself.setThinClientProps(myProperties);
    }

    /**
     * Ermittelt die eigene IP und gibt diese als String zurück.
     *
     * @return String - Die eigene IP
     */
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

    public void playRingTone(){
        m_PlayTunes.playTune(null);
    }

    /**
     * Init-Methode um das Hauptfenster zu etablieren
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(gridBagLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setJMenuBar(jMenuBar1);
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
        jButtonMakeCall.setText("Anrufen");
        jButtonMakeCall.addActionListener(new
                ThinClientGUI_jButtonMakeCall_actionAdapter(this));
        jButtonStartReceiver.setText("Start Receiver");
        jButtonStartReceiver.addActionListener(new
                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        jScrollPane1.setPreferredSize(new Dimension(40, 40));
        jUserInfoField.setEditable(false);
        jUserInfoField.setText("");
        jButtonDeleteTreeEntry.setToolTipText("");
        jButtonDeleteTreeEntry.setText("Tests...");
        jButtonDeleteTreeEntry.addActionListener(new
                ThinClientGUI_jButtonDeleteTreeEntry_actionAdapter(this));
        jButtonDeleteAllEntries.setText("alles löschen");
        jButtonDeleteAllEntries.addActionListener(new
                ThinClientGUI_jButtonDeleteAllEntries_actionAdapter(this));
        jButtonAddEntries.setText("Add Entries");
        jButtonAddEntries.addActionListener(new
                ThinClientGUI_jButtonAddEntries_actionAdapter(this));
        jMenu1.setText("Datei");
        jMenu2.setText("?");
        jMenuExit.setText("Ende");
        jMenuExit.addActionListener(new ThinClientGUI_jMenuExit_actionAdapter(this));
        jMenuInfo.setText("Info");
        jMenuInfo.addActionListener(new ThinClientGUI_jMenuInfo_actionAdapter(this));
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
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenu1.add(jMenuExit);
        jMenu2.add(jMenuInfo);

    }

    /**
     * Liefert den Scrollpane für den UserTree
     *
     * @return JScrollPane - darin wird der UserTree dargestellt
     */
    public JScrollPane getTreeViewScrollPane(){
        return treeViewScrollPane;
    }

    /**
     * Löscht den momentan aktivierten Eintrag im UserTree
     * @param e ActionEvent
     */
    public void jButtonDeleteTreeEntry_actionPerformed(ActionEvent e) {
//        userTreeGenerator.removeUserTreeEntry();
//        playRingTone();
//        createAndRegisterMe();
            MessageWindow messageWindow = new MessageWindow();
            messageWindow.setMessageText("Hurz - es klingelt!");
            messageWindow.setVisible(true);
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
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenu1 = new JMenu();
    JMenu jMenu2 = new JMenu();
    JMenuItem jMenuExit = new JMenuItem();
    JMenuItem jMenuInfo = new JMenuItem();

    /**
     * Startet den Receiver-Stack und setzt den eigenen Status auf PICKUP
     *
     * @param e ActionEvent
     */
    public void jButtonStartReceiver_actionPerformed(ActionEvent e) {
        m_ThinClientSIPStack = new SIPStack(this, jTextFieldMyIP.getText());
        setStatusPICKUP();
        jButtonStartReceiver.setEnabled(false);
    }

    /**
     * Wenn das Fenster geschlossen wird, wird der SIPStack gelöscht und dann
     * das Programm beendet
     *
     * @param e WindowEvent
     */
    public void this_windowClosing(WindowEvent e) {
        if(m_ThinClientSIPStack != null) m_ThinClientSIPStack.stopAndRemoveSIPStack();
        System.exit(0);
    }

    /**
     * Button AcceptCall wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonAccept_actionPerformed(ActionEvent e) {
        acceptCall();
    }

    /**
     * Button DenyCall wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonDeny_actionPerformed(ActionEvent e) {
        denyCall();
    }

    /**
     * Button EndCall wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonEndCall_actionPerformed(ActionEvent e) {
        endCall();
    }

    /**
     * Ruft am SOAP-Server die Methode zum Annehmen eines Anrufs auf und setzt
     * dann die Buttons dementsprechend.
     */
    public void acceptCall() {
        try{
            m_MethodCaller.callSOAPServer("acceptCall", getOwnIP(), null);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        setAcceptButtonFalse();
        setDenyButtonFalse();
        setEndCallButtonTrue();
        setStatusTALKING();
    }

    /**
     * Ruft am SOAP-Server die Methode zum Ablehnen eines Anrufs auf und setzt
     * dann die Buttons wieder dementsprechend.
     */
    public void denyCall(){
        try{
            m_MethodCaller.callSOAPServer("denyCall", getOwnIP(), null);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        setAcceptButtonFalse();
        setDenyButtonFalse();
    }

    /**
     * Ruft am SOAP-Server die Methode zum Beenden des Anrufs auf und setzt dann
     * den eigenen Status wieder auf PICKUP
     */
    public void endCall(){
        try {
            m_MethodCaller.callSOAPServer("endCall", getOwnIP(), null);
        } catch (Exception ex) {
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        setEndCallButtonFalse();
        setStatusPICKUP();
    }
    /**
     * Setzt die entsrechenden Buttons true bzw. false, wenn der Anruf
     * erfolgreich aufgebaut wurde.
     */
    public void callEstablished(){
        setAcceptButtonFalse();
        setDenyButtonFalse();
        setEndCallButtonTrue();
        setStatusTALKING();
    }

    /**
     * Button zum Ein- bzw. Ausblenden des Textfensters wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonToggleOutputWindow_actionPerformed(ActionEvent e) {
        toggleOutputWindow();
    }

    /**
     * Legt eine neue Instanz des Textausgabefensters an und öffnet bzw.
     * schließt es. Dabei wird die Beschriftung des zuständigen Buttons
     * angepasst.
     */
    public void toggleOutputWindow() {
        if(m_OutputWindow == null){
            m_OutputWindow = new Output(this);
            m_OutputWindow.setSize(530, 600);
            m_OutputWindow.setVisible(true);
            m_OutputWindow.stdOutput("eingeloggt als '" + m_sLoginName + "'");
            jButtonToggleOutputWindow.setText("Ausgabefenster schließen");
        } else if (m_OutputWindow.isVisible() == false) {
            m_OutputWindow.setVisible(true);
            jButtonToggleOutputWindow.setText("Ausgabefenster schließen");
        } else {
            m_OutputWindow.setVisible(false);
            jButtonToggleOutputWindow.setText("Ausgabefenster öffnen");
        }
    }

    /**
     * Setzt die Beschriftung des Buttons für Ein-/Ausblenden des Textausgabe-
     * fensters. Wird benötigt, wenn Textfenster durch eigenen Button
     * ausgeblendet wird.
     * @param string String - der Text für den Button
     */
    public void setToggleWindowButtonName(String string){
        jButtonToggleOutputWindow.setText(string);
    }

    /**
     * Button zum Initialisieren eines Calls wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonInitCall_actionPerformed(ActionEvent e) {
        setStatusMAKECALL();
        try{
            m_MethodCaller.callSOAPServer("initCall", getOwnIP(), m_sIPToCall);
        }catch(Exception ex){
            setStatusPICKUP();
        }
    }

    /**
     * Button zum Anrufen wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonMakeCall_actionPerformed(ActionEvent e) {
        setStatusMAKECALL();
        try{
            m_MethodCaller.callSOAPServer("processCall", getOwnIP(), m_UserTreeGenerator.getIPOfChoosenUser());
            setStatusTALKING();
        }catch(Exception ex){
            setStatusPICKUP();
        }
    }

    /**
     * Button zum Löschen der Einträge wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonDeleteAllEntries_actionPerformed(ActionEvent e) {
        m_UserTreeGenerator.removeAllEntries();
    }

    /**
     * Button zum Hinzufügen von Einträgen wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonAddEntries_actionPerformed(ActionEvent e) {
        m_UserTreeGenerator.addUserTreeEntries(m_UserVector);
    }

    /**
     * legt für Testzwecke einen Dummy-Vector an
     * und füllt ihn mit einigen User-Objekten
     */
    private void createDummyUsers(){
        m_UserVector = new Vector();
        for (int i=0; i<=5; ++i) {
            User user = new User();
            user.setIdUser(i);
            user.setUserInitial("Initial-" + i);
            user.setUserMail("Mail-" + i);
            user.setUserCompany("Matrikel-" + i);
            user.setUserFName("FName-" + i);
            user.setUserLName("LName-" + i);
            user.setSipScreenName("SipScreenName-" + i);
            m_UserVector.addElement(user);
        }
    }

    public void jMenuInfo_actionPerformed(ActionEvent e) {
        stdOutput("Info!!!");
    }

    public void jMenuExit_actionPerformed(ActionEvent e) {
        if(m_ThinClientSIPStack != null){
            m_ThinClientSIPStack.stopAndRemoveSIPStack();
        }
        System.exit(0);
    }
}


class ThinClientGUI_jMenuExit_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jMenuExit_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuExit_actionPerformed(e);
    }
}


class ThinClientGUI_jMenuInfo_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jMenuInfo_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jMenuInfo_actionPerformed(e);
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

