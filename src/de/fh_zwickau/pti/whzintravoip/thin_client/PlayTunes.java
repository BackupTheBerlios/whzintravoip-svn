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
    private boolean m_bDebug = false;
    private ThinClientGUI userGUI = null;
    private HashMap m_PlayerMap = new HashMap();
    private String m_sSoundKey = null;
    private int m_iDelay = 0;

    // the Sounds
    private String m_sFile = null;

    // Now work on
    public PlayTunes() {
        // just for test, may be deleted if no longer need
        /*initTune("file:///s1.wav", "ring0", 600);
                 initTune("file:///s2.wav", "ring1", 200);
                 initTune("file:///trick17.mp3", "ring2", 0);
                 playTune("ring2");
                 playTune("ring1");
                 playTune("ring0");*/
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
        // get the player outa map
        if (m_PlayerMap.containsKey(m_sSoundKey)) {
            this.m_Player = ((TuneObject) m_PlayerMap.get(m_sSoundKey)).
                            getPlayer();
            if (m_Player != null) {
                // start if found
                m_Player.start();
            } else {
                this.m_Player = null;
                errMsg(
                        "Error during playTune -> No valid Player found for key: " +
                        m_sSoundKey);
            }
        } else {
            m_Player = null;
            errMsg("Error during playTune -> No Player found on key: " +
                   m_sSoundKey);
        }
    }

    /**
     * Stop playing.
     * @param key String Player key
     */
    public void stopTune(String key) {
        this.m_sSoundKey = key;
        // get the player outa map
        if (m_PlayerMap.containsKey(m_sSoundKey)) {
            this.m_Player = ((TuneObject) m_PlayerMap.get(m_sSoundKey)).
                            getPlayer();
            if (m_Player != null) {
                // stop if found
                m_Player.stop();
                infMsg("Player stopped");
            } else {
                m_Player = null;
                errMsg(
                        "Error during stopTune -> No valid Player found on key: " +
                        m_sSoundKey);
            }
        } else {
            m_Player = null;
            errMsg("Error during stopTune -> No Player found on key: " +
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
     * In Cause of more Players they got added to a hashmap for further access.
     * @param file String The Filename in Java norm
     * @param key String The Player Key to find later
     * @param delay int Delay for playing endles but one times in millis
     */
    public void initTune(String file, String key, int delay) {
        this.m_sFile = file;
        this.m_sSoundKey = key;
        this.m_iDelay = delay;
        try {
            // create a new player with a controler sets an event for end of
            // given media
            m_Player = Manager.createRealizedPlayer(new MediaLocator(m_sFile));
            m_Player.addControllerListener(new ControllerAdapter() {
                public void endOfMedia(EndOfMediaEvent e) {
                    Player p = ((Player) e.getSourceController());
                    p.stop();
                    TuneObject tu;
                    // now iterate the map to get the player's delay the event's for
                    Iterator it = m_PlayerMap.values().iterator();
                    int delay = 0;
                    while (it.hasNext()) {
                        tu = (TuneObject) it.next();
                        if ((tu.getPlayer()).equals(p)) {
                            // the delay, on sero there's no repeat for this one
                            delay = tu.getDelay();
                        }
                    }
                    // test for repeat or not
                    if (delay != 0) {
                        // on repeat with delay
                        long startTime = System.currentTimeMillis();
                        Time time = new Time(0);
                        p.setMediaTime(time);

                        /** @todo optimize the while for lesser resource allocation */
                        while (true) {
                            if ((System.currentTimeMillis() - startTime) >
                                delay) {
                                p.start();
                                break;
                            }
                        }
                    }
                }
            });
            // now get the new player into the map (so here we got no errors while creating)
            TuneObject tune = new TuneObject();
            tune.setPlayer(m_Player);
            tune.setDelay(m_iDelay);
            m_PlayerMap.put(m_sSoundKey, tune);
        } catch (Exception ex) {
            errMsg("Error on initTune: " + ex.toString());
        }
    }

    /**
     * Close the Player when finished.
     * @param key String Player Key
     */
    public void close_Player(String key) {
        this.m_sSoundKey = key;
        // get the player outa map
        if (m_PlayerMap.containsKey(m_sSoundKey)) {
            this.m_Player = ((TuneObject) m_PlayerMap.get(m_sSoundKey)).
                            getPlayer();
            if (m_Player != null) {
                try {
                    // finaly on exit we have to deallocate
                    m_Player.deallocate();
                    m_Player.close();
                    infMsg("Player closed!");
                } catch (Exception ex) {
                    errMsg("Error while close_Player: " + ex.getMessage());
                }
            } else {
                m_Player = null;
                errMsg(
                        "Error during close_Player -> No valid Player found for key: " +
                        m_sSoundKey);
            }
        } else {
            errMsg("Error during close_Player -> No Player found for key: " +
                   m_sSoundKey);
        }
    }
}
