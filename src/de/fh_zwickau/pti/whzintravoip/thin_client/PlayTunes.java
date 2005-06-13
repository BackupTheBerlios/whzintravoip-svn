package de.fh_zwickau.pti.whzintravoip.thin_client;

import javax.media.*;
import java.util.*;

/**
 * <p>Überschrift: WHZintraVoIP</p>
 *
 * <p>Beschreibung: Play some tunes on actions</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author H. Seidel (hs@fh-zwickau.de)
 * @version 1.0
 */
public class PlayTunes {

    private Player m_Player = null;
    private boolean m_bDebug = true;
    private ThinClientGUI userGUI = null;
    private HashMap m_PlayerMap = new HashMap();
    private String m_sSoundKey = null;
    private int m_iDelay = 0;

    // the Sounds
    private String m_sFile = null;

    // Now work on
    public PlayTunes() {
        // just for test, may be deleted if no longer need
        /*initTune("file:///s2.wav", "ring", 2000);
        playTune("ring");*/
    }

    /**
     * Instantiate a new PlayTunes within a Message instance.
     * @param userGUI ThinClientGUI the instance
     */
    public PlayTunes(ThinClientGUI userGUI) {
        this.userGUI = userGUI;
    }

    // just for test, may be deleted if no longer need
    /*public static void main(String[] args) {
        new PlayTunes();
    }*/

    /**
     * Start Playing.
     * @param key String Player key
     */
    public void playTune(String key) {
        this.m_sSoundKey = key;
        if (m_PlayerMap.containsKey(m_sSoundKey)) {
            this.m_Player = (Player) m_PlayerMap.get(m_sSoundKey);
        } else {
            m_Player = null;
        }
        if (m_Player != null) {
            this.m_Player = (Player) m_PlayerMap.get(m_sSoundKey);
            m_Player.start();
        } else {
            errMsg("Error during playTune -> no Player found on key: " +
                   m_sSoundKey);
        }
    }

    /**
     * Stop playing.
     * @param key String Player key
     */
    public void stopTune(String key) {
        this.m_sSoundKey = key;
        if (m_PlayerMap.containsKey(m_sSoundKey)) {
            this.m_Player = (Player) m_PlayerMap.get(m_sSoundKey);
        } else {
            m_Player = null;
        }
        if (m_Player != null) {
            this.m_Player = (Player) m_PlayerMap.get(m_sSoundKey);
            m_Player.stop();
        } else {
            errMsg("Error during stopTune -> no Player found on key: " +
                   m_sSoundKey);
        }
    }

    /**
     * Just do soe standard debug output.
     * @param msg String Message
     */
    private void errMsg(String msg) {
        if (m_bDebug) {
            System.out.println(msg);
        } else {
            userGUI.errOutput(msg);
        }
    }

    /**
     * Just do some standard debug output.
     * @param msg String Message
     */
    private void infMsg(String msg) {
        if (m_bDebug) {
            System.out.println(msg);
        } else {
            userGUI.stdOutput(msg);
        }
    }

    /**
     * Create a Player over JMF Api to play some ringing tune.
     * @param file String The Filename in Java norm
     * @param key String The Player Key
     * @param delay int Delay for playing endles than one times in millis
     * @throws Exception
     */
    public void initTune(String file, String key, int delay) {
        this.m_sFile = file;
        this.m_sSoundKey = key;
        this.m_iDelay = delay;
        try {
            m_Player = Manager.createRealizedPlayer(new MediaLocator(m_sFile));
            Time ti = m_Player.getMediaTime();
            m_Player.addControllerListener(new ControllerAdapter() {
                public void endOfMedia(EndOfMediaEvent e) {
                    boolean delay = true;
                    if (m_iDelay != 0) {
                        long startTime = System.currentTimeMillis();
                        m_Player.stop();
                        Time time = new Time(0);
                        m_Player.setMediaTime(time);
                        while (true) {
                            if ((System.currentTimeMillis() - startTime) >
                                m_iDelay) {
                                m_Player.start();
                                break;
                            }
                        }
                    } else {
                        m_Player.stop();
                    }
                }
            });
        } catch (Exception ex) {
            errMsg("Error on initTune: " + ex.toString());
        }
        if (m_Player != null) {
            m_PlayerMap.put(m_sSoundKey, m_Player);
        }
    }

    /**
     * Close the Player when finished.
     * @param key String Player Key
     */
    public void close_Player(String key) {
        this.m_sSoundKey = key;
        if (m_PlayerMap.containsKey(m_sSoundKey)) {
            this.m_Player = (Player) m_PlayerMap.get(m_sSoundKey);
        } else {
            m_Player = null;
        }
        if (m_Player != null) {
            try {
                m_Player.deallocate();
                m_Player.close();
                infMsg("Player closed!");
            } catch (Exception ex) {
                errMsg("Error while closing Player: " + ex.getMessage());
            }
        } else {
            errMsg("No Player found for key: " + m_sSoundKey);
        }
    }
}
