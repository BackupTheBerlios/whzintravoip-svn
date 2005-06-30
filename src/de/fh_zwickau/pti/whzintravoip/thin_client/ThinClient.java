package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: The main class of the Thin Client.</p>
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
    private String m_sSOAPServletPath = ":8080/soap/servlet/rpcrouter";
    private String m_sSOAPURN = "urn:sip_server:soapserver:appscope";
    private String m_sUserObjectPath = "de.fh_zwickau.pti.whzintravoip.sip_server.user.User";
    private String m_sMyIP = "127.0.0.1";
    private String m_sMySIPAddress = "My SIP Address";
    private String m_sMySIPName = "SWF";
    private String m_sMyScreenName = "StarWarsFan";
    private String m_sLoginName = null;
    private String m_sRingtonePath = "file:///D:/Coding/Java/WHZIntraVoIP/s1.wav";
    private String m_sRingtoneName = "Ring";
    private int m_iRingtoneDelay = 2000;

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

    // this is used in the test method
    private boolean m_bStartTest = true;

    private static final byte LOGIN = 1;
    private static final byte PICKUP = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING = 5;
    private static final byte TALKING = 6;
    private byte m_bStatus = LOGIN; // Login, Pickup, Incoming, MakeCall, Calling, Talking

    ////////////////////////
    // we used JBuilder 2005 Foundation and JDK 1.5
    // but there are some problems with enum and JBuilder
    // and so we dont use enum for the status of the client
    /**
    private enum Status {
        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
    }
    private Status m_bStatus = Status.LOGIN;
     */

    /**
     * The main class for the WHZIntraVoIP application. This will run all the
     * necessary things...
     */
    public ThinClient() {
        setStatusLOGIN();

        // get Login-Namen
        m_sLoginName = System.getProperty("user.name");
        m_sLoginName = "ys";

        // register at Server
//        createMyIdentity();
//        registerMe();

        // create GUI
        m_ThinClientGUI = new ThinClientGUI(this);

        // create User-Tree
        createUserVector();
        m_UserTreeGenerator = new UserTreeGenerator(m_UserVector, this,
                m_ThinClientGUI);
        m_UserTreeGenerator.initTreeView();

        // get own IP
        m_sMyIP = getOwnIP();
        m_ThinClientGUI.getTextFieldMyIP().setText(m_sMyIP);

        // init the SOAP-Caller
        m_MethodCaller = new SOAPMethodCaller(
                this,
                "http://" + m_sSOAPServerIP + m_sSOAPServletPath,
                m_sSOAPURN,
                m_sUserObjectPath);

        // init Ringtone-Player
        m_PlayTunes = new PlayTunes(this);
        m_PlayTunes.initTune(m_sRingtonePath,
                             m_sRingtoneName,
                             m_iRingtoneDelay);
        m_ThinClientGUI.setVisible(true);
    }

    /**
     * The main method does nothing at the moment
     *
     * @param args String[]
     */
    public static void main(String[] args) {
    }

    /**
     * Writes a Info-Message to the Output-Window. This only happens, if the
     * window was opened before and is going on if you close the output window.
     * If m_bDebug is true, then the message is written to the stdout too.
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
     * Writes a Error-Message to the Output-Window. This only happens, if the
     * window was opened before and is going on if you close the output window.
     * If m_bDebug is true, then the message is written to the stdout too.
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
     * Writes a text in the TextArea on the right of the main window.
     *
     * @param string String - auszugebender Infotext
     */
    public void showUserInfo(String string) {
        m_ThinClientGUI.getTextAreaUserInfo().setText(string);
    }

    /**
     * Set own status
     */
    public void setStatusLOGIN() {
        m_bStatus = LOGIN;
        stdOutput("Status ist jetzt LOGIN (" + m_bStatus + ")\n");
    }

    /**
     * Set own status
     */
    public void setStatusPICKUP() {
        m_bStatus = PICKUP;
        stdOutput("Status ist jetzt PICKUP (" + m_bStatus + ")\n");
    }

    /**
     * Set own status
     */
    public void setStatusINCOMING() {
        m_bStatus = INCOMING;
        stdOutput("Status ist jetzt INCOMING (" + m_bStatus + ")\n");
    }

    /**
     * Set own status
     */
    public void setStatusMAKECALL() {
        m_bStatus = MAKECALL;
        stdOutput("Status ist jetzt MAKECALL (" + m_bStatus + ")\n");
    }

    /**
     * Set own status
     */
    public void setStatusCALLING() {
        m_bStatus = CALLING;
        stdOutput("Status ist jetzt CALLING (" + m_bStatus + ")\n");
    }

    /**
     * Set own status
     */
    public void setStatusTALKING() {
        m_bStatus = TALKING;
        stdOutput("Status ist jetzt TALKING (" + m_bStatus + ")\n");
    }

    /**
     * Returns the own status. <br>
     * 1 = Login,              <br>
     * 2 = Pickup,             <br>
     * 3 = Incoming,           <br>
     * 4 = MakeCall,           <br>
     * 5 = Calling,            <br>
     * 6 = Talking             <br>
     *
     * @return byte - The status.
     */
    public byte getStatus() {
        return m_bStatus;
    }

    /**
     * Register the own user object via SOAP on the server
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

    /**
     * Unregister the own user object via SOAP on the server
     */
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

    /**
     * Asks the server, who is online at the moment and then set the new
     * userlist using the UserTreeGenerator. <br>
     * Actually this method is identical to processUPDATERequest and so
     * it may not be used sometime...
     */
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
     * Creates a user object "m_Myself" for the own identity
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

    /**
     * Ask the server, who is online at the moment and then update the userlist.
     */
    public void processUPDATERequest() {
        Vector userVector = null;
        try {
            userVector = m_MethodCaller.whoIsOnAtServer();
        } catch (Exception ex) {
            errOutput(ex.toString());
        }
        m_UserTreeGenerator.setNewUserList(userVector);

    }

    /**
     * Actually nothing happens here...
     */
    public void processOPTIONSRequest() {
    }

    /**
     * If the ACK-Request is received, the other side has accepted the call and
     * so we can start the RTP-Session. So this method sets the own status to
     * TALKING, modifies the responsible button in the main window and then
     * initializes and starts the RTP-Session.
     */
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

    /**
     * If the CANCEL-Request is received, the other side has denied the call.
     * Because of that this method shows a message and sets the own status
     * back to PICKUP.
     */
    public void processCANCELRequest() {
        String title = "Abgelehnt";
        String message = "Das Gespräch wurde nicht angenommen";
        JOptionPane.showConfirmDialog(this,
                                      message,
                                      title,
                                      JOptionPane.CLOSED_OPTION,
                                      JOptionPane.INFORMATION_MESSAGE);
        setStatusPICKUP();
    }

    /**
     * This method returns the own IP, which is stored in a variable. Dont mix
     * this method with the method "getOwnIP"!
     *
     * @return String - Die eigene IP
     */
    public String getStoredIP() {
        return m_sMyIP;
    }

    /**
     * Determines the own IP and return it as a string.
     *
     * @return String - Die eigene IP
     */
    public String getOwnIP() {
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
     * Start playing the choosen Ringtone. If @param is empty, the ringtone
     * "Ring" will be played.
     *
     * @param ringTone String - The name of the ringtone in the ringtonemap
     */
    public void playRingTone(String ringTone) {
        if (ringTone.equals(null)) {
            m_PlayTunes.playTune(m_sRingtoneName);
        } else {
            m_PlayTunes.playTune(ringTone);
        }
    }

    /**
     * Stop playing the choosen Ringtone. If @param is empty, the ringtone
     * "Ring" will be stoped.
     *
     * @param ringTone String - The name of the ringtone in the ringtonemap
     */
    public void stopRingTone(String ringTone) {
        if (ringTone.equals(null)) {
            m_PlayTunes.stopTune(m_sRingtoneName);
        } else {
            m_PlayTunes.stopTune(ringTone);
        }
    }

    /**
     * This method does some more things. At first write the entered IP from
     * the input field to a variable. Then create my identity and initialize
     * the SIP-Stack for receiving. At last set the own status to LOGIN and
     * enable respectively disable some buttons et cetera.
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

    /**
     * If a INVITE-Request was received, someone is calling us. The inviter is
     * determined by his IP. So this method is trying to find details for this
     * user in the usertree using methods of the UserTreeGenerator. After that
     * a message window will pop up asking for a choice. As the case may be the
     * call is accepted or denied.
     *
     * @param incomingCallIP String - The IP of the caller
     */
    public void processINVITERequest(String incomingCallIP) {
        setStatusINCOMING();
        stdOutput("Call from this IP: " + incomingCallIP);
        this.m_sOpponentIP = incomingCallIP;
        String callerName = m_UserTreeGenerator.getUserName(incomingCallIP);
        stdOutput(callerName);
        String message = callerName +
                         " ruft Sie an!\n Wollen Sie das Gespräch annehmen?";
        playRingTone(m_sRingtoneName);
        int returnvalue = JOptionPane.showConfirmDialog(m_ThinClientGUI,
                message, "Es klingelt!", JOptionPane.YES_NO_OPTION);
        stopRingTone(m_sRingtoneName);
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
     * If the incoming call is accepted, we have to inform the server about
     * that. So at first set the status to TALKING, change the text of the
     * responsible button and call the SOAP-Server with "acceptCall". After that
     * init and start the RTP-Session
     *
     * @param incomingIP String - IP of the caller
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
//        m_InterfaceRTP.enableDebugging();
//        m_InterfaceRTP.DebugErrorMessages(true);
        m_InterfaceRTP.initRtpSession(m_UserTreeGenerator.getIPOfChoosenUser(), null);
        stdOutput("RTP Init finished");
        stdOutput("Starting RTP Session");
        m_InterfaceRTP.startRtpSession();
        stdOutput("RTP Session started");
    }

    /**
     * If the incoming call was not accepted, inform the server about that and
     * set the own status back to PICKUP
     *
     * @param incomingIP String - IP of the caller
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
     * If I terminate the phonecall by myself, I have to call the SOAP-Method
     * "endCall", stop and close the RTP-Session, modify the responsible button
     * and set my status back to PICKUP.
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
     * The other side has closed the connection, so stop and close the RTP-
     * Session, modify the responsible button and set my status to PICKUP.
     */
    public void processBYERequest() {
        m_ThinClientGUI.getButtonHandleCall().setEnabled(false);
        stdOutput("Stopping RTP Session");
        m_InterfaceRTP.stopRtpSession();
        stdOutput("RTP Session stopped");

        m_ThinClientGUI.getButtonHandleCall().setText("Anrufen");
        setStatusPICKUP();

        stdOutput("Closing RTP Session");
        m_InterfaceRTP.closeRtpSession();
        stdOutput("RTP Session closed");
        m_ThinClientGUI.getButtonHandleCall().setEnabled(true);
    }

    /**
     * Creates a new instance of the output window and opens respectively closes
     * it. Additionaly the text of the respnsible button will be modified
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
     * Modifies the text of the button for the output window. This method is
     * used by the output window itself.
     *
     * @param string String - the text for the button
     */
    public void setToggleWindowButtonName(String string) {
        m_ThinClientGUI.getButtonToggleOutputWindow().setText(string);
    }

    /**
     * The button to call or to end a call was pressed. So react as necessary.
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
     * Creates the empty user vector. If you need testvalues in the vector,
     * uncomment the lines in this method.
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
     * The pulldown menu "Exit" was choosen, so terminate the actual call (if
     * calling at the moment), sign me off if I'm not in PICKUP mode, close the
     * player and shut down the SIP-Stack.
     *
     * @return boolean - Actually returns always true...
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
        m_PlayTunes.close_Player(m_sRingtoneName);
        if (m_ThinClientSIPStack != null) {
            stdOutput("Closing SIP stack...");
            m_ThinClientSIPStack.stopAndRemoveSIPStack();
        }
        System.exit(0);
        return true;
    }

    /**
     * This is only a method for tests. Actually it plays the ringtone and if
     * the method is called again, the ringtone will stop.
     *
     * @param ip String - A IP to work with...
     */
    public void methodForTests(String ip) {
        if (m_bStartTest == true) {
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
            m_PlayTunes.playTune(m_sRingtoneName);
            m_bStartTest = false;
        } else {
            /**
                         stdOutput("Stopping RTP Session");
                         m_InterfaceRTP.stopRtpSession();
                         stdOutput("RTP Session stopped");
                         stdOutput("Closing RTP Session");
                         m_InterfaceRTP.closeRtpSession();
                         stdOutput("RTP Session closed");
             */
            m_PlayTunes.stopTune(m_sRingtoneName);
            m_bStartTest = true;
        }
    }
}
