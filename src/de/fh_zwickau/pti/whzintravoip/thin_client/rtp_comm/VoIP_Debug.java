package de.fh_zwickau.pti.whzintravoip.thin_client.rtp_comm;

import java.awt.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;

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
public class VoIP_Debug extends JFrame {

    JPanel contentPane;
    XYLayout xYLayout1 = new XYLayout();
    JScrollPane jScrollPane = new JScrollPane();
    JTextArea jTextArea = new JTextArea();

    private VoIP_Status m_Status = null;
    private boolean m_bErr = false;

    /**
     * The Constructor, creating a new window for messages.
     */
    public VoIP_Debug() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // get Status - Class for extern messages to show
        m_Status = new VoIP_Status(this);
    }

    /**
     * Initialize the window components
     *
     * @throws Exception any to parent
     */
    private void jbInit() throws Exception {
        contentPane = (JPanel) getContentPane();
        contentPane.setLayout(xYLayout1);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setResizable(false);
        setSize(new Dimension(400, 600));
        setTitle("VoIP_RTP_Client Debugging Window");
        setLocation(550, 0);
        jTextArea.setEditable(false);
        jTextArea.setText("");
        jTextArea.setLineWrap(true);
        contentPane.setAlignmentX((float) 100.0);
        jScrollPane.getViewport().add(jTextArea);
        contentPane.add(jScrollPane, new XYConstraints(13, 10, 368, 536));
    }

    /**
     * Set an error message to the window.
     *
     * @param msg String the message
     */
    public void errMsg(String msg) {
        if (m_bErr) {
            jTextArea.append("ERROR: " + msg + "\n");
        }
    }

    /**
     * Set an info message to the window
     *
     * @param msg String the message
     */
    public void infMsg(String msg) {
        jTextArea.append("INFO: " + msg + "\n");
    }

    /**
     * Enable or disable the output of errors.
     *
     * @param err boolean show (true) or not (false)
     */
    public void showErrors(boolean err) {
        m_bErr = err;
    }

    /**
     * Disable and further close the message window.
     */
    public void disableStatusMessages() {
        m_Status.disableGui();
    }
}
