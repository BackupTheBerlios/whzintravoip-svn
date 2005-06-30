package de.fh_zwickau.pti.whzintravoip.thin_client.rtp_comm;

/**
 * <p>Title: WHZintraVoIP</p>
 *
 * <p>Description: Voice communication over RTP</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author H. Seidel (hs@fh-zwickau.de)
 * @version 0.1.0
 */
public class VoIP_RTP_Interface {

    private VoIP_Debug m_Debug = null;
    private VoIP_Connection m_Conn = null;
    private VoIP_Status m_Status = new VoIP_Status();

    private static String m_sIP = null;
    private static int m_iPort = 0;
    private static int m_iGlobalTimeout = 5000; // try 5 seconds standard
    private boolean m_bDegugging = false;

    /**
     * New interface instance.
     */
    public VoIP_RTP_Interface() {
    }

    /**
     * Get the actual used IP.
     *
     * @return String the actual IP used
     */
    public String getIP() {
        return this.m_sIP;
    }

    /**
     * Get the actual used Port.
     *
     * @return int the actual Port used
     */
    public int getPort() {
        return this.m_iPort;
    }

    /**
     * Get the actual used global timeout.
     *
     * @return int the actual Timeout used global
     */
    public int getGlobalTimeout() {
        return this.m_iGlobalTimeout;
    }

    /**
     * Set a new global timeout to use.
     *
     * @param timeout int new timeout in millis
     */
    public void setGlobalTimeout(int timeout) {
        this.m_iGlobalTimeout = timeout;
        m_Conn.setTimeout(m_iGlobalTimeout);
    }

    /**
     * Initialize the whole RTP Session for send and receive
     *
     * @param ip String Connect to this IP
     * @param port String Conect on this Port
     */
    public void initRtpSession(String ip, String port) {
        if (this.m_Conn != null) {
            this.m_Conn = null;
        }
        this.m_sIP = ip;
        if (port != null) {
            this.m_iPort = Integer.valueOf(port).intValue();
        }
        this.m_Conn = new VoIP_Connection(m_sIP, m_iPort);
        try {
            this.m_Conn.initConnection();
        } catch (Exception ex) {
            m_Status.errMessage(ex.toString());
        }
    }

    /**
     * Close the actual connection
     */
    public void closeRtpSession() {
        try {
            m_Conn.closeConnection();
            m_Conn = null;
        } catch (Exception ex) {
            m_Status.errMessage(ex.toString());
        }
    }

    /**
     * Start the actual RTP Session so you can talk and hear now.
     * Connection must be initialized first!
     */
    public void startRtpSession() {
        try {
            m_Conn.startSession();
        } catch (Exception ex) {
            m_Status.errMessage(ex.toString());
        }
    }

    /**
     * Stop the actual RTP Session.
     * Connection must be initialized first!
     */
    public void stopRtpSession() {
        try {
            m_Conn.stopSession();
        } catch (Exception ex) {
            m_Status.errMessage(ex.toString());
        }
    }

    /**
     * Enable debugging messages. A new Window will be shown where all
     * messages within.
     */
    public void enableDebugging() {
        if (m_Debug == null) {
            m_Debug = new VoIP_Debug();
            m_bDegugging = true;
            m_Debug.setVisible(true);
        }
    }

    /**
     * You can also get the errors show in debugging window.
     * Debugging has to be enabled for this!
     *
     * @param error boolean shown (true) or not (false)
     */
    public void DebugErrorMessages(boolean error) {
        if (m_bDegugging) {
            m_Debug.showErrors(error);
        }
    }

    /**
     * Disable degugging will close the window.
     * Debugging has to be enabled for this!
     */
    public void disableDebugging() {
        if (m_bDegugging) {
            m_Debug.setVisible(false);
            m_bDegugging = false;
            m_Debug.disableStatusMessages();
            m_Debug.dispose();
            m_Debug = null;
        }
    }
}
