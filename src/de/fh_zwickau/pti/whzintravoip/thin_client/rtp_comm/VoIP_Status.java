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
public class VoIP_Status {

    private static VoIP_Debug m_Gui = null; // the message window used

    /**
     * Constructor for Satus messages.
     * @param gui VoIP_Debug Instance of the window
     */
    public VoIP_Status(VoIP_Debug gui) {
        this.m_Gui = gui;
    }

    /**
     * Default Constructor for use methods in every class needed.
     */
    public VoIP_Status() {
    }

    /**
     * Send an error message to the window.
     *
     * @param msg String the message
     */
    public void errMessage(String msg) {
        if (m_Gui != null) {
            m_Gui.errMsg(msg);
        }
    }

    /**
     * Send an info message to the window.
     *
     * @param msg String the message
     */
    public void infoMessage(String msg) {
        if (m_Gui != null) {
            m_Gui.infMsg(msg);
        }
    }

    /**
     * On closing window the local reference here must be set null.
     */
    public void disableGui() {
        this.m_Gui = null;
    }
}
