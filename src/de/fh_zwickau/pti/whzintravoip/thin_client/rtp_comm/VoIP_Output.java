package de.fh_zwickau.pti.whzintravoip.thin_client.rtp_comm;

import javax.media.*;
import javax.media.protocol.*;

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
public class VoIP_Output implements ControllerListener {

    private VoIP_Status m_Status = new VoIP_Status();

    // intern used
    private Player m_Player = null;
    private static int m_iTimeout = 5000; // 5 seconds till timeout
    private DataSource m_PlayerSource = null;

    // Status variables
    private boolean m_bRealized = false;
    private boolean m_bPrefetched = false;
    private boolean m_bFailed = false;
    private boolean m_bClosed = false;

    /**
     * A new instance.
     */
    public VoIP_Output() {
    }

    /**
     * Set the timeout used for status switches.
     *
     * @param timeout int timeout in millis
     */
    public void setTimeout(int timeout) {
        this.m_iTimeout = timeout;
    }

    /**
     * Start the player. Player must be initialized first.
     *
     * @throws Exception any to parent
     */
    public void start_Player() {
        if (m_Player != null) {
            m_Player.start();
            m_Status.infoMessage("Player started!");
        } else {
            m_Status.errMessage("No Player initialized yet!");
        }
    }

    /**
     * Stop the player. Player must be initialized first.
     *
     * @throws Exception any to parent
     */
    public void stop_Player() {
        if (m_Player != null) {
            m_Player.stop();
            m_Status.infoMessage("Player stopt!");
        } else {
            m_Status.errMessage("No Player initialized yet!");
        }
    }

    /**
     * Close the player. Player must be initialized first.
     *
     * @throws Exception any to parent
     */
    public void close_Player() {
        if (m_Player != null) {
            synchronized(this){
                long time = System.currentTimeMillis();
                try {

                    m_Player.deallocate();
                    m_Player.close();
                    while (!m_bClosed && !m_bFailed) {
                        Thread.sleep(10);
                        if ((System.currentTimeMillis() - time) > m_iTimeout) {
                            break;
                        }
                    }

                } catch (Exception ex) {
                    m_Status.errMessage("Close Player: " + ex.toString());
                }
            }
            m_Player.removeControllerListener(this);
            m_Status.infoMessage("Player closed!");
        } else {
            m_Status.errMessage("No Player initialized yet!");
        }
    }

    /**
     * Initialize a new Player.
     *
     * @param src DataSource source to be used
     * @throws Exception any to parent
     */
    public void init_Player(DataSource src) throws Exception {
        this.m_PlayerSource = src;
        m_Player = Manager.createPlayer(m_PlayerSource);
        m_Player.addControllerListener(this);
        m_bRealized = false;
        m_bPrefetched = false;
        m_bFailed = false;
        m_bClosed = false;
        realize_Player();
        prefetch_Player();
        m_Status.infoMessage("Player init!");
    }

    /**
     * Realize the player.
     */
    private void realize_Player() {
        synchronized(this){
            long time = System.currentTimeMillis();
            try {
                m_Player.realize();
                while (!m_bRealized && !m_bFailed) {
                    Thread.sleep(10);
                    if ((System.currentTimeMillis() - time) > m_iTimeout) {
                        break;
                    }
                }

            } catch (Exception ex) {
                m_Status.errMessage("Realize Player: " + ex.toString());
            }
        }
    }

    /**
     * Prefetch the player.
     */
    private void prefetch_Player() {
        synchronized(this){
            long time = System.currentTimeMillis();
            try {
                m_Player.prefetch();
                while (!m_bPrefetched && !m_bFailed) {
                    Thread.sleep(10);
                    if ((System.currentTimeMillis() - time) > m_iTimeout) {
                        break;
                    }
                }

            } catch (Exception ex) {
                m_Status.errMessage("Prefetch Player: " + ex.toString());
            }
        }
    }

    public synchronized void controllerUpdate(ControllerEvent ce) {
        if (ce instanceof ControllerErrorEvent) {
            m_bFailed = true;
        } else if (ce instanceof ControllerClosedEvent) {
            m_bClosed = true;
        } else if (ce instanceof RealizeCompleteEvent) {
            m_bRealized = true;
        } else if (ce instanceof PrefetchCompleteEvent) {
            m_bPrefetched = true;
        } else {
            return;
        }
        notifyAll();
    }
}
