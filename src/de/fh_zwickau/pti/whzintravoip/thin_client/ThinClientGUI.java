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

import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.rtp_comm.*;

public class ThinClientGUI extends JFrame{

    private String m_sSOAPServerIP = "141.32.28.183";
    private String m_sMyIP         = "127.0.0.1";
    private String m_sMySIPAddress = "My SIP Address";
    private String m_sMySIPName    = "SWF";
    private String m_sMyScreenName = "StarWarsFan";
    private String m_sLoginName    = null;

    private VoIP_RTP_Interface m_InterfaceRTP = new VoIP_RTP_Interface();
    private SIPStack m_ThinClientSIPStack = null;
    private Output m_OutputWindow = null;
    private SOAPMethodCaller m_MethodCaller = null;
    private UserTreeGenerator m_UserTreeGenerator = null;
    private PlayTunes m_PlayTunes = null;
    private User m_Myself = null;
    private Vector m_UserVector = null;
    private String m_sOpponentIP = null;
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

    public ThinClientGUI() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setStatusLogin();

        // Login-Namen auslesen
        m_sLoginName = System.getProperty("user.name");
        m_sLoginName = "ys";

        // einige Settings zum Programmfenster
        this.setSize(500, 700);
        this.setLocation(764, 2);
        m_sMyIP = extractOwnIP();
        jTextFieldMyIP.setText(m_sMyIP);

        // SOAP-Caller initialisieren
        m_MethodCaller = new SOAPMethodCaller(
            this,
            "http://" + m_sSOAPServerIP + ":8080/soap/servlet/rpcrouter",
            "urn:sip_server:soapserver:appscope",
            "de.fh_zwickau.pti.whzintravoip.sip_server.user.User");

        // am Server anmelden
//        createMyIdentity();
//        registerMe();

        // User-Tree bauen
        createDummyUsers();
        m_UserTreeGenerator = new UserTreeGenerator(m_UserVector, this);
        m_UserTreeGenerator.initTreeView();

        // Ringtone-Player initialisieren
//        m_PlayTunes = new PlayTunes(this);
//        m_PlayTunes.initTune("file:///D:/Coding/Java/WHZIntraVoIP/s1.wav", "Ring", 0);
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
     * Wenn m_bDebug == true, dann erfolgt die Ausgabe zusätzlich auf die
     * Standardausgabe.
     *
     * @param msg String - der auszugebende Text als String
     */
    public void stdOutput(String msg) {
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
     * Wenn m_bDebug == true, dann erfolgt die Ausgabe zusätzlich auf die
     * Standardausgabe.
     *
     * @param err String - der auszugebende Text als String
     */
    public void errOutput(String err) {
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
     * 1 = Login,
     * 2 = Pickup,
     * 3 = Incoming,
     * 4 = MakeCall,
     * 5 = Calling,
     * 6 = Talking
     *
     * @return byte - Der eigene Status.
     */
    public byte getStatus(){
        return m_bStatus;
    }

    /**
     * Registriert das eigene User-Objekt via SOAP am Server
     *
     */
    private void registerMe(){
        try{
            m_MethodCaller.registerMyselfAtServer("registerUser", m_Myself, null);
        }catch(Exception ex){
            errOutput(ex.toString());
        }
    }

    /**
     * Legt ein Userobjekt m_Myself für die eigene Identität an
     */
    private void createMyIdentity(){
        Properties myProperties = new Properties();
        myProperties.setProperty("sip_server.user.USER_IP", m_sMyIP);
        myProperties.setProperty("sip_server.user.USER_INITIAL", m_sLoginName);
        myProperties.setProperty("sip_server.user.SIP_ADDRESS", m_sMySIPAddress);
        myProperties.setProperty("sip_server.user.SIP_NAME", m_sMySIPName);
        myProperties.setProperty("sip_server.user.SCREEN_NAME", m_sMyScreenName);
        m_Myself = new User();
        m_Myself.setThinClientProps(myProperties);
    }

    public void updateUserList(){
        Vector userVector = null;
        try{
            userVector = m_MethodCaller.whoIsOnAtServer();
        }catch(Exception ex){
            errOutput(ex.toString());
        }
        m_UserTreeGenerator.setNewUserList(userVector);

    }

    public void processOptionsRequest(){
    }

    public void processACKRequest(){
        setStatusTALKING();
        jButtonHandleCall.setText("Gespräch beenden");
        m_InterfaceRTP.startRtpSession();
    }

    public String getOwnIP(){
        return m_sMyIP;
    }

    /**
     * Ermittelt die eigene IP und gibt diese als String zurück.
     *
     * @return String - Die eigene IP
     */
    public String extractOwnIP(){
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
        m_PlayTunes.playTune("Ring");
    }

    public void stopRingTone(){
        m_PlayTunes.stopTune("Ring");
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
     * Ein Button für diverse Tests...
     * @param e ActionEvent
     */
    public void jButtonForTests_actionPerformed(ActionEvent e) {
        registerMe();
    }

    /**
     * Ein zweiter Button für diverse Tests...
     * @param e ActionEvent
     */
    public void jButtonForTestsII_actionPerformed(ActionEvent e) {
        Vector userVector = null;
        try {
            userVector = m_MethodCaller.whoIsOnAtServer();
        } catch (Exception ex) {
            errOutput(ex.toString());
        }
        m_UserTreeGenerator.setNewUserList(userVector);
    }


    /**
     * Startet den Receiver-Stack und setzt den eigenen Status auf PICKUP
     *
     * @param e ActionEvent
     */
    public void jButtonStartReceiver_actionPerformed(ActionEvent e) {
        m_sMyIP = jTextFieldMyIP.getText();
        createMyIdentity();
        m_ThinClientSIPStack = new SIPStack(this, m_sMyIP);
        setStatusPICKUP();
        jButtonStartReceiver.setEnabled(false);
        jButtonHandleCall.setEnabled(true);
        jButtonForTests.setEnabled(true);
        jButtonForTestsII.setEnabled(true);
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

    public void processIncomingCall(String incomingCallIP){
        setStatusINCOMING();
        stdOutput("Call from this IP: " + incomingCallIP);
        this.m_sOpponentIP = incomingCallIP;
        String callerName = m_UserTreeGenerator.getUserName(incomingCallIP);
        stdOutput(callerName);
        String message = callerName + " ruft Sie an!\n Wollen Sie das Gespräch annehmen?";
        int returnvalue = JOptionPane.showConfirmDialog(this, message, "Es klingelt!", JOptionPane.YES_NO_OPTION);
        stdOutput("Returnvalue of Request:" + returnvalue);
        switch (returnvalue) {
        case 0:
            stdOutput("Gespräch angenommen");
            m_InterfaceRTP.enableDebugging();
            m_InterfaceRTP.DebugErrorMessages(true);
            m_InterfaceRTP.initRtpSession(m_sOpponentIP, null);
            acceptCall(m_sOpponentIP);
            break;
        case 1:
            stdOutput("Gespräch abgelehnt");
            denyCall(m_sOpponentIP);
            break;
        }
    }

    /**
     * Ruft am SOAP-Server die Methode zum Annehmen eines Anrufs auf und setzt
     * dann die Buttons dementsprechend.
     *
     * @param incomingIP String - IP des anrufenden Clienten
     */
    public void acceptCall(String incomingIP) {
        this.m_sOpponentIP = incomingIP;
        setStatusTALKING();
        jButtonHandleCall.setText("Gespräch beenden");
        try{
            m_MethodCaller.callSOAPServer("acceptCall", m_sMyIP, m_sOpponentIP);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        m_InterfaceRTP.startRtpSession();
    }

    /**
     * Ruft am SOAP-Server die Methode zum Ablehnen eines Anrufs auf und setzt
     * dann die Buttons wieder dementsprechend.
     *
     * @param incomingIP String - IP des anrufenden Clienten
     */
    public void denyCall(String incomingIP){
        this.m_sOpponentIP = incomingIP;
        setStatusPICKUP();
        try{
            m_MethodCaller.callSOAPServer("denyCall", m_sMyIP, m_sOpponentIP);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
    }

    /**
     * Ruft am SOAP-Server die Methode zum Beenden des Anrufs auf und setzt dann
     * den eigenen Status wieder auf PICKUP
     */
    public void endCallByMyself(){
        m_InterfaceRTP.stopRtpSession();
        try {
            m_MethodCaller.callSOAPServer("endCall", m_sMyIP, m_sOpponentIP);
        } catch (Exception ex) {
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        jButtonHandleCall.setText("Anrufen");
        setStatusPICKUP();
        m_InterfaceRTP.closeRtpSession();
    }

    /**
     * Ruft am SOAP-Server die Methode zum Beenden des Anrufs auf und setzt dann
     * den eigenen Status wieder auf PICKUP
     */
    public void endCallByOtherSide(){
        m_InterfaceRTP.stopRtpSession();
        jButtonHandleCall.setText("Anrufen");
        setStatusPICKUP();
        m_InterfaceRTP.closeRtpSession();
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
     * Button zum Anrufen wurde gedrückt
     *
     * @param e ActionEvent
     */
    public void jButtonHandleCall_actionPerformed(ActionEvent e) {
        if(m_bStatus == PICKUP){
            setStatusMAKECALL();
            try {
                m_MethodCaller.callSOAPServer("processCall",
                                              m_sMyIP,
                                              m_UserTreeGenerator.
                                              getIPOfChoosenUser());
            } catch (Exception ex) {
                setStatusPICKUP();
            }
            m_InterfaceRTP.enableDebugging();
            m_InterfaceRTP.DebugErrorMessages(true);
            m_InterfaceRTP.initRtpSession(m_UserTreeGenerator.getIPOfChoosenUser(), null);
        }else if(m_bStatus == TALKING){
            endCallByMyself();
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
            user.setUserIP("127.0.0." + (i + 1));
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
        jButtonToggleOutputWindow.setText("Ausgabefenster öffnen");
        jButtonToggleOutputWindow.addActionListener(new
                ThinClientGUI_jButtonToggleOutputWindow_actionAdapter(this));
        jButtonHandleCall.setEnabled(false);
        jButtonHandleCall.setText("Anrufen");
        jButtonHandleCall.addActionListener(new
                ThinClientGUI_jButtonMakeCall_actionAdapter(this));
        jButtonStartReceiver.setText("Start Receiver");
        jButtonStartReceiver.addActionListener(new
                ThinClientGUI_jButtonStartReceiver_actionAdapter(this));
        jScrollPane1.setPreferredSize(new Dimension(40, 40));
        jUserInfoField.setEditable(false);
        jUserInfoField.setText("");
        jButtonForTests.setEnabled(false);
        jButtonForTests.setToolTipText("");
        jButtonForTests.setText("Tests...");
        jButtonForTests.addActionListener(new
                ThinClientGUI_jButtonForTests_actionAdapter(this));
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
        jButtonForTestsII.setEnabled(false);
        jButtonForTestsII.setText("Test2 II...");
        jButtonForTestsII.addActionListener(new
                ThinClientGUI_jButtonForTestsII_actionAdapter(this));
        jSplitPane1.add(jScrollPane1, JSplitPane.RIGHT);
        jScrollPane1.getViewport().add(jUserInfoField);
        jSplitPane1.add(treeViewScrollPane, JSplitPane.LEFT);
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenu2);
        jMenu1.add(jMenuExit);
        jMenu2.add(jMenuInfo);
        this.getContentPane().add(jButtonDeleteAllEntries,
                                  new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonHandleCall,
                                  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jSplitPane1,
                                  new GridBagConstraints(0, 1, 4, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonToggleOutputWindow,
                                  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonStartReceiver,
                                  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jTextFieldMyIP,
                                  new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 50, 10));
        this.getContentPane().add(jLabel1,
                                  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonForTests,
                                  new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonAddEntries,
                                  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonForTestsII,
                                  new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

    }

    JTextField jTextFieldMyIP = new JTextField();
    JLabel jLabel1 = new JLabel();
    TitledBorder titledBorder1 = new TitledBorder("");
    TitledBorder titledBorder2 = new TitledBorder("");
    TitledBorder titledBorder3 = new TitledBorder("");
    JButton jButtonToggleOutputWindow = new JButton();
    JButton jButtonHandleCall = new JButton();
    JButton jButtonStartReceiver = new JButton(); //    JTree treeView = new JTree();
    JScrollPane treeViewScrollPane = new JScrollPane(null);
    JSplitPane jSplitPane1 = new JSplitPane();
    JScrollPane jScrollPane1 = new JScrollPane();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JTextArea jUserInfoField = new JTextArea();
    JButton jButtonForTests = new JButton();
    JButton jButtonDeleteAllEntries = new JButton();
    JButton jButtonAddEntries = new JButton();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenu1 = new JMenu();
    JMenu jMenu2 = new JMenu();
    JMenuItem jMenuExit = new JMenuItem();
    JMenuItem jMenuInfo = new JMenuItem();
    JButton jButtonForTestsII = new JButton();
}


class ThinClientGUI_jButtonForTestsII_actionAdapter implements ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonForTestsII_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonForTestsII_actionPerformed(e);
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


class ThinClientGUI_jButtonForTests_actionAdapter implements
        ActionListener {
    private ThinClientGUI adaptee;
    ThinClientGUI_jButtonForTests_actionAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {

        adaptee.jButtonForTests_actionPerformed(e);
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
        adaptee.jButtonHandleCall_actionPerformed(e);
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


class ThinClientGUI_this_windowAdapter extends WindowAdapter {
    private ThinClientGUI adaptee;
    ThinClientGUI_this_windowAdapter(ThinClientGUI adaptee) {
        this.adaptee = adaptee;
    }

    public void windowClosing(WindowEvent e) {
        adaptee.this_windowClosing(e);
    }
}

