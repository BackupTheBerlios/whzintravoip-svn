package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann <ys@fh-zwickau.de>
 * @version 0.1.0
 */

import java.net.*;
import java.util.*;
import javax.swing.*;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.rtp_comm.*;

public class ThinClient extends JFrame {

//    private String m_sSOAPServerIP = "141.32.28.183";
    private String m_sSOAPServerIP = "voipserver.informatik.fh-zwickau.de";
    private String m_sMyIP = "127.0.0.1";
    private String m_sMySIPAddress = "My SIP Address";
    private String m_sMySIPName = "SWF";
    private String m_sMyScreenName = "StarWarsFan";
    private String m_sLoginName = null;

    private ThinClientGUI m_ThinClientGUI = null;
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

    /////////////////
    // this is only for RTP-Testing
    private boolean m_bStartRTP = true;
    /////////////////

    private static final byte LOGIN = 1;
    private static final byte PICKUP = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING = 5;
    private static final byte TALKING = 6;
    private byte m_bStatus = LOGIN; // Login, Pickup, Incoming, MakeCall, Calling, Talking

//    private enum Status {
//        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
//    }

//    private Status status2 = Status.LOGIN;

    public ThinClient() {
        setStatusLOGIN();

        // Login-Namen auslesen
        m_sLoginName = System.getProperty("user.name");
        m_sLoginName = "ys";

        // am Server anmelden
//        createMyIdentity();
//        registerMe();

        // GUI Fenster bauen
        m_ThinClientGUI = new ThinClientGUI(this);

        // User-Tree bauen
        createUserVector();
        m_UserTreeGenerator = new UserTreeGenerator(m_UserVector, this,
                m_ThinClientGUI);
        m_UserTreeGenerator.initTreeView();

        // eigene IP ermitteln
        m_sMyIP = extractOwnIP();
        m_ThinClientGUI.getTextFieldMyIP().setText(m_sMyIP);

        // SOAP-Caller initialisieren
        m_MethodCaller = new SOAPMethodCaller(
                this,
                "http://" + m_sSOAPServerIP + ":8080/soap/servlet/rpcrouter",
                "urn:sip_server:soapserver:appscope",
                "de.fh_zwickau.pti.whzintravoip.sip_server.user.User");

        // Ringtone-Player initialisieren
        m_PlayTunes = new PlayTunes(this);
        m_PlayTunes.initTune("file:///D:/Coding/Java/WHZIntraVoIP/s1.wav",
                             "Ring", 2000);
        m_ThinClientGUI.setVisible(true);
    }

    /**
     * Mainmethode um das Hauptfenster einzublenden
     *
     * @param args String[]
     */
    public static void main(String[] args) {
//        new ThinClientGUI().setVisible(true);
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
    public void showUserInfo(String string) {
        //        jUserInfoField.setText("");
        m_ThinClientGUI.getTextAreaUserInfo().setText(string);
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusLOGIN() {
        m_bStatus = LOGIN;
        stdOutput("Status ist jetzt LOGIN (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusPICKUP() {
        m_bStatus = PICKUP;
        stdOutput("Status ist jetzt PICKUP (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusINCOMING() {
        m_bStatus = INCOMING;
        stdOutput("Status ist jetzt INCOMING (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusMAKECALL() {
        m_bStatus = MAKECALL;
        stdOutput("Status ist jetzt MAKECALL (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusCALLING() {
        m_bStatus = CALLING;
        stdOutput("Status ist jetzt CALLING (" + m_bStatus + ")\n");
    }

    /**
     * Setzt den eigenen Status
     */
    public void setStatusTALKING() {
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
    public byte getStatus() {
        return m_bStatus;
    }

    /**
     * Registriert das eigene User-Objekt via SOAP am Server
     *
     */
    public void signOn() {
        String success = null;
        try {
            success = m_MethodCaller.registerMyselfAtServer("signOn", m_Myself, null);
        } catch (Exception ex) {
            errOutput(ex.toString());
        }
        stdOutput("SignOn: '" + success + "'");
        if (success.equals("OK")) {
            setStatusPICKUP();
        }
    }

    public void signOff() {
        setStatusLOGIN();
        String success = null;
        try {
            success = m_MethodCaller.callSOAPServer("signOff", m_sMyIP, null);
        } catch (Exception ex) {
            errOutput(ex.toString());
        }
        stdOutput("SignOff: '" + success + "'");
        if (success.equals("OK")) {
            setStatusPICKUP();
        }
    }

    public void whoIsOnAtServer() {
        Vector userVector = null;
        try {
            userVector = m_MethodCaller.whoIsOnAtServer();
        } catch (Exception ex) {
            errOutput(ex.toString());
        }
        m_UserTreeGenerator.setNewUserList(userVector);
    }


    /**
     * Legt ein Userobjekt m_Myself für die eigene Identität an
     */
    private void createMyIdentity() {
        Properties myProperties = new Properties();
        myProperties.setProperty("sip_server.user.USER_IP", m_sMyIP);
        myProperties.setProperty("sip_server.user.USER_INITIAL", m_sLoginName);
        myProperties.setProperty("sip_server.user.SIP_ADDRESS", m_sMySIPAddress);
        myProperties.setProperty("sip_server.user.SIP_NAME", m_sMySIPName);
        myProperties.setProperty("sip_server.user.SCREEN_NAME", m_sMyScreenName);
        m_Myself = new User();
        m_Myself.setThinClientProps(myProperties);
    }

    public void processUPDATERequest() {
        Vector userVector = null;
        try {
            userVector = m_MethodCaller.whoIsOnAtServer();
        } catch (Exception ex) {
            errOutput(ex.toString());
        }
        m_UserTreeGenerator.setNewUserList(userVector);

    }

    public void processOPTIONSRequest() {
    }

    public void processACKRequest() {
        setStatusTALKING();
        m_ThinClientGUI.getButtonHandleCall().setText("Gespräch beenden");
        stdOutput("Init RTP Session");
//        m_InterfaceRTP.enableDebugging();
//        m_InterfaceRTP.DebugErrorMessages(true);
        m_InterfaceRTP.initRtpSession(m_UserTreeGenerator.getIPOfChoosenUser(), null);
        stdOutput("RTP Init finished");
        stdOutput("Starting RTP session");
        m_InterfaceRTP.startRtpSession();
        stdOutput("RTP session startet");
    }

    public void processCANCELRequest() {
        String message = "Das Gespräch wurde nicht angenommen";
        String title = "Abgelehnt";
        JOptionPane.showConfirmDialog(this,
                                      message,
                                      title,
                                      JOptionPane.CLOSED_OPTION,
                                      JOptionPane.INFORMATION_MESSAGE);
        setStatusPICKUP();
    }

    public String getOwnIP() {
        return m_sMyIP;
    }

    /**
     * Ermittelt die eigene IP und gibt diese als String zurück.
     *
     * @return String - Die eigene IP
     */
    public String extractOwnIP() {
        String ip = null;
        try {
            InetAddress myIP = InetAddress.getLocalHost();
            ip = myIP.getHostAddress();
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        }
        return ip;
    }

    /**
     * Start playing the choosen Ringtone
     *
     * @param ringTone String
     */
    public void playRingTone(String ringTone) {
        if (ringTone.equals(null)) {
            m_PlayTunes.playTune("Ring");
        } else {
            m_PlayTunes.playTune(ringTone);
        }
    }

    public void stopRingTone(String ringTone) {
        if (ringTone.equals(null)) {
            m_PlayTunes.stopTune("Ring");
        } else {
            m_PlayTunes.stopTune(ringTone);
        }
    }

    /**
     * Startet den Receiver-Stack und setzt den eigenen Status auf PICKUP
     */
    public void startReceiver() {
        m_sMyIP = m_ThinClientGUI.getTextFieldMyIP().getText();
        createMyIdentity();
        m_ThinClientSIPStack = new SIPStack(this, m_sMyIP);
        setStatusLOGIN();
        m_ThinClientGUI.getButtonStartReceiver().setEnabled(false);
        m_ThinClientGUI.getButtonHandleCall().setEnabled(true);
        m_ThinClientGUI.getButtonRegister().setEnabled(true);
        m_ThinClientGUI.getButtonUpdate().setEnabled(true);
        m_ThinClientGUI.getMenuRegister().setEnabled(true);
        m_ThinClientGUI.getMenuUpdate().setEnabled(true);
        m_ThinClientGUI.getTextFieldMyIP().setEnabled(false);
    }

    public void processINVITERequest(String incomingCallIP) {
        setStatusINCOMING();
        stdOutput("Call from this IP: " + incomingCallIP);
        this.m_sOpponentIP = incomingCallIP;
        String callerName = m_UserTreeGenerator.getUserName(incomingCallIP);
        stdOutput(callerName);
        String message = callerName +
                         " ruft Sie an!\n Wollen Sie das Gespräch annehmen?";
        playRingTone("Ring");
        int returnvalue = JOptionPane.showConfirmDialog(m_ThinClientGUI,
                message, "Es klingelt!", JOptionPane.YES_NO_OPTION);
        stopRingTone("Ring");
        stdOutput("Returnvalue of Request:" + returnvalue);
        switch (returnvalue) {
        case 0:
            stdOutput("Gespräch angenommen");
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
        m_ThinClientGUI.getButtonHandleCall().setText("Gespräch beenden");
        try {
            m_MethodCaller.callSOAPServer("acceptCall", m_sMyIP, m_sOpponentIP);
        } catch (Exception ex) {
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        stdOutput("Init RTP Session");
        m_InterfaceRTP.enableDebugging();
        m_InterfaceRTP.DebugErrorMessages(true);
        m_InterfaceRTP.initRtpSession(m_UserTreeGenerator.getIPOfChoosenUser(), null);
        stdOutput("RTP Init finished");
        stdOutput("Starting RTP Session");
        m_InterfaceRTP.startRtpSession();
        stdOutput("RTP Session started");
    }

    /**
     * Ruft am SOAP-Server die Methode zum Ablehnen eines Anrufs auf und setzt
     * dann die Buttons wieder dementsprechend.
     *
     * @param incomingIP String - IP des anrufenden Clienten
     */
    public void denyCall(String incomingIP) {
        this.m_sOpponentIP = incomingIP;
        try {
            m_MethodCaller.callSOAPServer("denyCall", m_sMyIP, m_sOpponentIP);
        } catch (Exception ex) {
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }
        setStatusPICKUP();
    }

    /**
     * Ruft am SOAP-Server die Methode zum Beenden des Anrufs auf und setzt dann
     * den eigenen Status wieder auf PICKUP
     */
    public void endCallByMyself() {
        stdOutput("Stopping RTP Session");
        m_InterfaceRTP.stopRtpSession();
        stdOutput("RTP Session stopped");
        try {
            m_MethodCaller.callSOAPServer("endCall", m_sMyIP, m_sOpponentIP);
        } catch (Exception ex) {
            errOutput("Fehler beim SOAP-Methodenaufruf: " + ex);
        }

        m_ThinClientGUI.getButtonHandleCall().setText("Anrufen");
        setStatusPICKUP();

        stdOutput("Closing RTP Session");
        m_InterfaceRTP.closeRtpSession();
        stdOutput("RTP Session closed");
    }

    /**
     * Ruft am SOAP-Server die Methode zum Beenden des Anrufs auf und setzt dann
     * den eigenen Status wieder auf PICKUP
     */
    public void processBYERequest() {
        stdOutput("Stopping RTP Session");
        m_InterfaceRTP.stopRtpSession();
        stdOutput("RTP Session stopped");

        m_ThinClientGUI.getButtonHandleCall().setText("Anrufen");
        setStatusPICKUP();

        stdOutput("Closing RTP Session");
        m_InterfaceRTP.closeRtpSession();
        stdOutput("RTP Session closed");
    }

    /**
     * Legt eine neue Instanz des Textausgabefensters an und öffnet bzw.
     * schließt es. Dabei wird die Beschriftung des zuständigen Buttons
     * angepasst.
     */
    public void toggleOutputWindow() {
        if (m_OutputWindow == null) {
            m_OutputWindow = new Output(this);
            m_OutputWindow.setSize(530, 600);
            m_OutputWindow.setVisible(true);
            m_OutputWindow.stdOutput("eingeloggt als '" + m_sLoginName + "'");
            m_ThinClientGUI.getButtonToggleOutputWindow().setText(
                    "Ausgabefenster schließen");
        } else if (m_OutputWindow.isVisible() == false) {
            m_OutputWindow.setVisible(true);
            m_ThinClientGUI.getButtonToggleOutputWindow().setText(
                    "Ausgabefenster schließen");
        } else {
            m_OutputWindow.setVisible(false);
            m_ThinClientGUI.getButtonToggleOutputWindow().setText(
                    "Ausgabefenster öffnen");
        }
    }

    /**
     * Setzt die Beschriftung des Buttons für Ein-/Ausblenden des Textausgabe-
     * fensters. Wird benötigt, wenn Textfenster durch eigenen Button
     * ausgeblendet wird.
     * @param string String - der Text für den Button
     */
    public void setToggleWindowButtonName(String string) {
        m_ThinClientGUI.getButtonToggleOutputWindow().setText(string);
    }

    /**
     * Button zum Anrufen wurde gedrückt
     */
    public void handleCall() {
        if (m_bStatus == PICKUP) {
            setStatusMAKECALL();
            try {
                m_MethodCaller.callSOAPServer("processCall",
                                              m_sMyIP,
                                              m_UserTreeGenerator.
                                              getIPOfChoosenUser());
            } catch (Exception ex) {
                setStatusPICKUP();
            }
        } else if (m_bStatus == TALKING) {
            endCallByMyself();
        }
    }

    /**
     * legt für Testzwecke einen Dummy-Vector an
     * und füllt ihn mit einigen User-Objekten
     */
    private void createUserVector() {
        m_UserVector = new Vector();
        /**
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
         */
    }

    /**
     * The pulldown menu "Exit" was choosen...
     */
    public boolean exitClient() {
        stdOutput("Exiting client now\n"
                  + "Status: " + getStatus());
        if (m_bStatus == TALKING) {
            stdOutput("I'm talking, so terminate the call now...");
            endCallByMyself();
        } else if (m_bStatus == PICKUP) {
            stdOutput(
                    "I'm in PICKUP mode, so set me to LOGIN and sign me of now...");
            signOff();
        } else {
            stdOutput("Status is NOT Pickup! Sorry, cant Logout...");
        }
        stdOutput("Status: " + getStatus());
        stdOutput("Closing player...");
        m_PlayTunes.close_Player("Ring");
        if (m_ThinClientSIPStack != null) {
            stdOutput("Closing SIP stack...");
            m_ThinClientSIPStack.stopAndRemoveSIPStack();
        }
        System.exit(0);
        return true;
    }

    public void testButton(String ip) {
        if (m_bStartRTP == true) {
            /**
                         stdOutput("Init RTP Session");
                         m_InterfaceRTP.enableDebugging();
                         m_InterfaceRTP.DebugErrorMessages(true);
                         m_InterfaceRTP.initRtpSession(ip, null);
                         stdOutput("RTP Init finished");
                         stdOutput("Starting RTP Session");
                         m_InterfaceRTP.startRtpSession();
                         stdOutput("RTP Session started");
             */
            m_PlayTunes.playTune("Ring");
            m_bStartRTP = false;
        } else {
            /**
                         stdOutput("Stopping RTP Session");
                         m_InterfaceRTP.stopRtpSession();
                         stdOutput("RTP Session stopped");
                         stdOutput("Closing RTP Session");
                         m_InterfaceRTP.closeRtpSession();
                         stdOutput("RTP Session closed");
             */
            m_PlayTunes.stopTune("Ring");
            m_bStartRTP = true;
        }
    }
}

