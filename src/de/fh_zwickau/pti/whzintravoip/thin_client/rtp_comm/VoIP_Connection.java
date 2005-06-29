package de.fh_zwickau.pti.whzintravoip.thin_client.rtp_comm;

import java.net.*;
import java.util.*;
import javax.media.protocol.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.rtp.rtcp.*;

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
 * @version 1.0
 */
public class VoIP_Connection implements SessionListener,
        SendStreamListener, ReceiveStreamListener {

    private VoIP_Status m_Status = new VoIP_Status();
    private VoIP_Output m_Output = new VoIP_Output();
    private VoIP_Process m_Process = new VoIP_Process();

    private static String m_sIP = "127.0.0.1";
    private static int m_iPort = 3000;
    private RTPManager m_RtpManager = null;
    private SessionAddress localAddress = null, remoteAddress = null;
    private SendStream m_SendStream = null;
    private DataSource m_Received = null, m_Captured = null, m_Decoded = null;
    private ReceiveStream stream = null;
    private static int m_iTimeout = 1000; // 30 seconds standard till timeout
    private boolean m_bReceiveEvent = false;

    /**
     * Instanziate a new RTP Session.
     *
     * @param ip String IP to connect to
     * @param port int Port to connect to
     */
    public VoIP_Connection(String ip, int port) {
        if (ip != null) {
            this.m_sIP = ip;
        }
        if (port != 0) {
            this.m_iPort = port;
        }
        m_Status.infoMessage("Trying initialize connection to:\n" + m_sIP +
                             " : " + m_iPort + " ...");
    }

    /**
     * Set the timout for session interrupt on any fail.
     *
     * @param timeout int Timout in millis
     */
    public void setTimeout(int timeout) {
        m_Process.setTimeout(timeout);
        m_Output.setTimeout(timeout);
    }

    /**
     * Set the timeout for waiting till receiving stream.
     *
     * @param timeout int timeout in millis
     */
    public void setReceivingTimeout(int timeout) {
        this.m_iTimeout = timeout;
    }

    /**
     * Start the Session. Session must be initialized before.
     *
     * @throws Exception any to parent
     */
    public void startSession() throws Exception {
        long startTime = System.currentTimeMillis();
        // start the processor for capture
        m_Process.startProcessing(m_Process.m_iCapture);
        // test for existing receive stream
        if (m_bReceiveEvent) {
            // start player if a receive stream exists
            m_Output.start_Player();
            m_Status.infoMessage("Session start!");
            m_bReceiveEvent = false;
        } else {
            synchronized(this){
                while (!m_bReceiveEvent) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ie) {}
                    if (System.currentTimeMillis() - startTime > m_iTimeout) {
                        m_Status.errMessage("Timout of " + m_iTimeout +
                                            " milliseconds for receiving reached! Going to interrupt!");
                        break;
                    }
                }
            }
/*            if (m_bReceiveEvent) {
                // start player if a receive stream exists
                m_Output.start_Player();
                m_Status.infoMessage("Session start!");
            } else {
                m_Status.errMessage("Receive Player not started !!!");
            }*/
        }
    }

    /**
     * Stop the Session.
     *
     * @throws Exception any to parent
     */
    public void stopSession() throws Exception {
        // stop processing
        m_Process.stopProcessing(m_Process.m_iCapture);
        // stop decoding and player
        m_Process.stopProcessing(m_Process.m_iReceive);
        m_Output.stop_Player();
        m_Status.infoMessage("Session stopt!");
    }

    /**
     * Initialize a Session.
     *
     * @throws Exception any to parent
     */
    public void initConnection() throws Exception {
        // for ReceiveStreamEvent be sure we get a new one
        m_bReceiveEvent = false;
        // create the RTP Manager
        m_RtpManager = RTPManager.newInstance();
        // add needed listeners
        m_RtpManager.addSessionListener(this);
        m_RtpManager.addSendStreamListener(this);
        m_RtpManager.addReceiveStreamListener(this);
        // create Sender Information (only computer name here)
        SourceDescription[] sdes = {new SourceDescription(SourceDescription.
                SOURCE_DESC_CNAME,
                SourceDescription.generateCNAME(), 1, false)
        };
        // try to create the local and remote adress
        try {
            InetAddress receiver = InetAddress.getByName(m_sIP);

            localAddress = new SessionAddress(InetAddress.getLocalHost(),
                                              m_iPort);
            remoteAddress = new SessionAddress(receiver, m_iPort, 0);
        } catch (Exception ex) {
            m_Status.errMessage("Wrong IP or Port! Couldnt resolve ...");
        }
        m_RtpManager.initialize(
                new SessionAddress[] {localAddress},
                sdes,
                0.03,
                1.0,
                new EncryptionInfo(EncryptionInfo.NO_ENCRYPTION, new byte[] {})
                );
        m_RtpManager.addTarget(remoteAddress);
        // create a processor for capture, get the output and
        // initialize a send stream within
        this.m_Captured = m_Process.initProcessing(m_Process.m_iCapture, null);
        m_SendStream = m_RtpManager.createSendStream(m_Captured, 0);
        // start the send stream
        m_SendStream.start();
        m_Status.infoMessage("Connection init!");
    }

    /**
     * Close the Session.
     *
     * @throws Exception any
     */
    public void closeConnection() throws Exception {
        // stop the send stream
        m_Status.infoMessage("Closing Connection");
        m_SendStream.stop();
        // close the connection
        m_RtpManager.removeTarget(remoteAddress, "client disconnected");
        m_RtpManager.dispose();
        // close capture processor
        m_Process.closeProcessor(m_Process.m_iCapture);
        // close receiving processor and player
        m_Process.closeProcessor(m_Process.m_iReceive);
        m_Output.close_Player();
        m_Status.infoMessage("Connection closed!");
    }


    public void update(SessionEvent se) {
        if (se instanceof NewParticipantEvent) {
            Participant newReceiver =
                    ((NewParticipantEvent) se).getParticipant();
            String cname = newReceiver.getCNAME();
            m_Status.infoMessage("Neuer Teilnehmer: " + cname);
        }
    }

    public void update(SendStreamEvent sse) {
        if (sse instanceof NewSendStreamEvent) {
            m_Status.infoMessage("Ein neuer RTP Datenstrom wurde erzeugt!");
        } else if (sse instanceof StreamClosedEvent) {
            m_Status.infoMessage("RTP Datenstrom wurde geschlossen!");
        }
    }

    public synchronized void update(ReceiveStreamEvent event) {
        if (event instanceof NewReceiveStreamEvent) {
            try {
                m_Status.infoMessage("Receive Stream detected!");
                // get a handle over the ReceiveStream
                stream = ((NewReceiveStreamEvent) event)
                         .getReceiveStream();
                // get a handle over the ReceiveStream datasource
                m_Received = stream.getDataSource();
                // passing datasource to the Processor and initialize
                m_Decoded = m_Process.initProcessing(m_Process.m_iReceive,
                        m_Received);
                m_Process.startProcessing(m_Process.m_iReceive);
                // realize the Output
                m_Output.init_Player(m_Decoded);
                m_bReceiveEvent = true;
                // start due stream
                m_Output.start_Player();
            } catch (Exception e) {
                m_Status.errMessage("NewReceiveStreamEvent exception "
                                    + e.getMessage());
                return;
            }
        }
    }
}
