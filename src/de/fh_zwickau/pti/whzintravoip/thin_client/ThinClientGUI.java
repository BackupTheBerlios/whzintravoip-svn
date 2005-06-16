package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>�berschrift: WHZIntraVoIP, ThinClientGUI</p>
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

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

public class ThinClientGUI extends JFrame{

    private String m_sSOAPServerIP = "141.32.28.183";
    private String m_sIPToCall     = "141.32.28.227";
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

    private DefaultMutableTreeNode root = null;
    private DefaultMutableTreeNode child = null;
    private DefaultMutableTreeNode subchild = null;

    public ThinClientGUI() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        m_sLoginName = System.getProperty("user.name");
        this.setSize(500, 700);
        this.setLocation(764, 2);
        jTextFieldMyIP.setText(getOwnIP());

        // SOAP-Caller initialisieren
        m_MethodCaller = new SOAPMethodCaller(
            this,
            "http://" + m_sSOAPServerIP + ":8080/soap/servlet/rpcrouter",
            "urn:sip_server:soapserver:appscope",
            "de.fh_zwickau.pti.whzintravoip.sip_server.user.User");
        setStatusLogin();
//        createAndRegisterMe();

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
     * wenn es bereits einmal ge�ffnet worden ist.
     *
     * @param msg String - der auszugebende Text als String
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
     * wenn es bereits einmal ge�ffnet worden ist.
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
    }

    /**
     * Setzt den entsprechenden Button auf FALSE
     */
    public void setAcceptButtonFalse(){
    }

    /**
     * Setzt den entsprechenden Button auf TRUE
     */
    public void setDenyButtonTrue(){
    }

    /**
     * Setzt den entsprechenden Button auf FALSE
     */
    public void setDenyButtonFalse(){
    }

    /**
     * Setzt den entsprechenden Button auf TRUE
     */
    public void setEndCallButtonTrue(){
    }

    /**
     * Setzt den entsprechenden Button auf FALSE
     */
    public void setEndCallButtonFalse(){
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
     * Legt ein Userobjekt f�r die eigene Identit�t an
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

    public void updateUserList(){

    }

    /**
     * Ermittelt die eigene IP und gibt diese als String zur�ck.
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
        m_PlayTunes.playTune("Ring");
    }

    public void stopRingTone(){
        m_PlayTunes.stopTune("Ring");
    }


    /**
     * Liefert den Scrollpane f�r den UserTree
     *
     * @return JScrollPane - darin wird der UserTree dargestellt
     */
    public JScrollPane getTreeViewScrollPane(){
        return treeViewScrollPane;
    }

    /**
     * Legt das zu zeigende Messagefenster an. Dieses hat ein Textfeld und
     * zwei Buttons
     *
     * @param msg String - Der Messagetext
     * @param buttonL String - Beschriftung linker Button
     * @param buttonR String - Beschriftung rechter Button
     */
    private void createMessage(String msg, String buttonL, String buttonR){
        MessageWindow messageWindow = new MessageWindow(this);
        messageWindow.setMessageText(msg);
        messageWindow.setLeftButtonText(buttonL);
        messageWindow.setRightButtonText(buttonR);
        messageWindow.setVisible(true);
    }

    /**
     * L�scht den momentan aktivierten Eintrag im UserTree
     * @param e ActionEvent
     */
    public void jButtonDeleteTreeEntry_actionPerformed(ActionEvent e) {
        createAndRegisterMe();
    }

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
     * Wenn das Fenster geschlossen wird, wird der SIPStack gel�scht und dann
     * das Programm beendet
     *
     * @param e WindowEvent
     */
    public void this_windowClosing(WindowEvent e) {
        if(m_ThinClientSIPStack != null) m_ThinClientSIPStack.stopAndRemoveSIPStack();
        System.exit(0);
    }

    public void processIncomingCall(String incomingCallIP){
        this.m_sOpponentIP = incomingCallIP;
        String callerName = m_UserTreeGenerator.getUserName(incomingCallIP);
        stdOutput(callerName);
        String message = callerName + " ruft Sie an!\n Wollen Sie das Gespr�ch annehmen?";
        int returnvalue = JOptionPane.showConfirmDialog(this, message, "Es klingelt!", JOptionPane.YES_NO_OPTION);
        stdOutput(returnvalue + "");
        switch (returnvalue) {
        case 0:
            stdOutput("Gespr�ch angenommen");
            acceptCall(m_sOpponentIP);
            break;
        case 1:
            stdOutput("Gespr�ch abgelehnt");
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
        jButtonHandleCall.setText("Gespr�ch beenden");
        try{
            m_MethodCaller.callSOAPServer("acceptCall", getOwnIP(), m_sOpponentIP);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
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
            m_MethodCaller.callSOAPServer("denyCall", getOwnIP(), m_sOpponentIP);
        }catch(Exception ex){
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
    }

    /**
     * Ruft am SOAP-Server die Methode zum Beenden des Anrufs auf und setzt dann
     * den eigenen Status wieder auf PICKUP
     */
    public void endCall(){
        try {
            m_MethodCaller.callSOAPServer("endCall", getOwnIP(), m_sOpponentIP);
        } catch (Exception ex) {
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        jButtonHandleCall.setText("Anrufen");
        setStatusPICKUP();
    }

    /**
     * Button zum Ein- bzw. Ausblenden des Textfensters wurde gedr�ckt
     *
     * @param e ActionEvent
     */
    public void jButtonToggleOutputWindow_actionPerformed(ActionEvent e) {
        toggleOutputWindow();
    }

    /**
     * Legt eine neue Instanz des Textausgabefensters an und �ffnet bzw.
     * schlie�t es. Dabei wird die Beschriftung des zust�ndigen Buttons
     * angepasst.
     */
    public void toggleOutputWindow() {
        if(m_OutputWindow == null){
            m_OutputWindow = new Output(this);
            m_OutputWindow.setSize(530, 600);
            m_OutputWindow.setVisible(true);
            m_OutputWindow.stdOutput("eingeloggt als '" + m_sLoginName + "'");
            jButtonToggleOutputWindow.setText("Ausgabefenster schlie�en");
        } else if (m_OutputWindow.isVisible() == false) {
            m_OutputWindow.setVisible(true);
            jButtonToggleOutputWindow.setText("Ausgabefenster schlie�en");
        } else {
            m_OutputWindow.setVisible(false);
            jButtonToggleOutputWindow.setText("Ausgabefenster �ffnen");
        }
    }

    /**
     * Setzt die Beschriftung des Buttons f�r Ein-/Ausblenden des Textausgabe-
     * fensters. Wird ben�tigt, wenn Textfenster durch eigenen Button
     * ausgeblendet wird.
     * @param string String - der Text f�r den Button
     */
    public void setToggleWindowButtonName(String string){
        jButtonToggleOutputWindow.setText(string);
    }

    /**
     * Button zum Initialisieren eines Calls wurde gedr�ckt
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
     * Button zum Anrufen wurde gedr�ckt
     *
     * @param e ActionEvent
     */
    public void jButtonHandleCall_actionPerformed(ActionEvent e) {
        if(m_bStatus == PICKUP){
            setStatusMAKECALL();
            try {
//                m_MethodCaller.callSOAPServer("processCall", getOwnIP(),
//                                              m_UserTreeGenerator.
//                                              getIPOfChoosenUser());
                m_MethodCaller.callSOAPServer("processCall", getOwnIP(),
                                              m_sIPToCall);
                setStatusTALKING();
            } catch (Exception ex) {
                setStatusPICKUP();
            }
        }else if(m_bStatus == TALKING){
            endCall();
        }
    }

    /**
     * Button zum L�schen der Eintr�ge wurde gedr�ckt
     *
     * @param e ActionEvent
     */
    public void jButtonDeleteAllEntries_actionPerformed(ActionEvent e) {
        m_UserTreeGenerator.removeAllEntries();
    }

    /**
     * Button zum Hinzuf�gen von Eintr�gen wurde gedr�ckt
     *
     * @param e ActionEvent
     */
    public void jButtonAddEntries_actionPerformed(ActionEvent e) {
        m_UserTreeGenerator.addUserTreeEntries(m_UserVector);
    }

    /**
     * legt f�r Testzwecke einen Dummy-Vector an
     * und f�llt ihn mit einigen User-Objekten
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
        jButtonToggleOutputWindow.setText("Ausgabefenster �ffnen");
        jButtonToggleOutputWindow.addActionListener(new
                ThinClientGUI_jButtonToggleOutputWindow_actionAdapter(this));
        jButtonHandleCall.setText("Anrufen");
        jButtonHandleCall.addActionListener(new
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
        jButtonDeleteAllEntries.setText("alles l�schen");
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
        this.getContentPane().add(jButtonDeleteTreeEntry,
                                  new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));
        this.getContentPane().add(jButtonAddEntries,
                                  new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(10, 10, 10, 10), 5, 5));

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
    JButton jButtonDeleteTreeEntry = new JButton();
    JButton jButtonDeleteAllEntries = new JButton();
    JButton jButtonAddEntries = new JButton();
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenu1 = new JMenu();
    JMenu jMenu2 = new JMenu();
    JMenuItem jMenuExit = new JMenuItem();
    JMenuItem jMenuInfo = new JMenuItem();

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

