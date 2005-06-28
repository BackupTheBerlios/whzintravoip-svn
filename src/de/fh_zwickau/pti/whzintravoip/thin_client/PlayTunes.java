package de.fh_zwickau.pti.whzintravoip.thin_client;

import java.util.*;
import javax.media.*;
import java.*;

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
public class PlayTunes implements ControllerListener {

    private boolean m_bDebug = true;
    private Player m_Player = null;
    private TuneObject m_TuneObj = null;
    private ThinClient client = null;
    private HashMap m_PlayerMap = new HashMap();
    private boolean m_bKind = false;
    private boolean m_bBreaked = false;


    public PlayTunes() {
    }

    /**
     * Instantiate a new PlayTunes within a Message instance.
     * @param client ThinClient the instance
     */
    public PlayTunes(ThinClient client) {
        this.client = client;
    }

    /**
     * Start Playing.
     * @param key String Player key
     */
    public synchronized void playTune(String key) {
        // get the player outa map
        if (m_PlayerMap.containsKey(key)) {
            this.m_TuneObj = (TuneObject) m_PlayerMap.get(key);
            this.m_Player = m_TuneObj.getPlayer();
            if (m_Player != null) {
                if (m_TuneObj.getRealStoped()) {
                    try {
                        m_Player.start();
                        m_TuneObj.setRealStoped(false);
                        m_TuneObj.setStarted(true);
                        infMsg("Player started: " + m_TuneObj.getKey());
                    } catch (Exception ex) {
                        errMsg("playTune error: " + ex.toString());
                    }
                }
            } else {
                this.m_Player = null;
                errMsg("Error during playTune ->");
                errMsg(" No valid Player found for key: " + m_TuneObj.getKey());
            }
        } else {
            m_Player = null;
            errMsg("Error during playTune -> No Player found on key: " + key);
        }
    }

    /**
     * Stop playing.
     * @param key String Player key
     */
    public synchronized void stopTune(String key) {
        m_bBreaked = true;
        // get the player outa map
        if (m_PlayerMap.containsKey(key)) {
            this.m_TuneObj = (TuneObject) m_PlayerMap.get(key);
            this.m_Player = m_TuneObj.getPlayer();
            if (m_Player != null) {
                if (m_TuneObj.getStarted()) {
                    try {
                        m_Player.stop();
                        m_Player.setMediaTime(new Time(0));
                        m_TuneObj.setStarted(false);
                        m_TuneObj.setRealStoped(true);
                        infMsg("Player stopped: " + m_TuneObj.getKey());
                    } catch (Exception ex) {
                        errMsg("stopTune error: " + ex.toString());
                    }
                }
            } else {
                m_Player = null;
                errMsg("Error during stopTune -> ");
                errMsg("No valid Player found on key: " + m_TuneObj.getKey());
            }
        } else {
            m_Player = null;
            errMsg("Error during stopTune -> ");
            errMsg("No Player found on key: " + key);
        }
    }

    private boolean waitDelay(int delay) {
        int fullDelay = delay;
        int effectivDelay = 10;
        int factor = fullDelay / effectivDelay;
        int count = 0;
        m_bBreaked = false;
        /** @todo optimize the while for lesser resource allocation */
        /** @todo get sure of righ numbers for factor */
        infMsg("Player delay of " +
               fullDelay +
               " millis! : " + m_TuneObj.getKey());
        while (!m_bBreaked & (factor != count)) {
            m_bBreaked = false;
            count++;
            try {
                Thread.sleep(effectivDelay);
            } catch (Exception ex) {
                return false;
            }
        }
        if (m_bBreaked) {
            infMsg("Delay breaked : " + m_TuneObj.getKey());
        }
        return true;
    }


    /**
     * Just do soe standard debug output.
     * @param msg String Message
     */
    private void errMsg(String msg) {
        if (m_bDebug) {
            System.out.println("ERROR: " + msg);
        } else {
            client.errOutput(msg);
        }
    }

    /**
     * Just do some standard debug output.
     * @param msg String Message
     */
    private void infMsg(String msg) {
        if (m_bDebug) {
            System.out.println("INFO :" + msg);
        } else {
            client.stdOutput(msg);
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
        m_TuneObj = new TuneObject();
        m_TuneObj.setFile(file);
        m_TuneObj.setKey(key);
        m_TuneObj.setDelay(delay);
        try {
            // create a new player with a controler sets an event for end of
            // given media
            m_Player = Manager.createRealizedPlayer(new MediaLocator(m_TuneObj.
                    getFile()));
            m_Player.addControllerListener(this);
            m_Player.addControllerListener(new ControllerAdapter() {
                public void endOfMedia(EndOfMediaEvent e) {
                    Player p = ((Player) e.getSourceController());
                    TuneObject tune = null;
                    // now iterate over the map to get the player's delay the event's for
                    Iterator it = m_PlayerMap.values().iterator();
                    while (it.hasNext()) {
                        tune = (TuneObject) it.next();
                        if ((tune.getPlayer()).equals(p)) {
                            break;
                        }
                    }
                    endOfTune(tune.getKey());
                }
            });
            // now get the new player into the map (so here we got no errors while creating)
            m_TuneObj.setPlayer(m_Player);
            m_PlayerMap.put(m_TuneObj.getKey(), m_TuneObj);
            infMsg("Tune init and stored to MAP! " + m_TuneObj.getKey());
        } catch (Exception ex) {
            errMsg("Error on initTune: " + ex.toString());
        }
    }

    /**
     * Close the Player when finished.
     * @param key String Player Key
     */
    public void close_Player(String key) {
        // get the player outa map
        if (m_PlayerMap.containsKey(key)) {
            this.m_TuneObj = (TuneObject) m_PlayerMap.get(key);
            this.m_Player = m_TuneObj.getPlayer();
            this.m_bKind = m_TuneObj.getClosed();
            if (m_Player != null) {
                if (!m_bKind) {
                    try {
                        m_Player.deallocate();
                        m_Player.close();
                        m_Player.removeControllerListener(this);
                        m_TuneObj.setPlayer(null);
                        infMsg("Player closed! " + m_TuneObj.getKey());
                    } catch (Exception ex) {
                        errMsg("close_Player error: " + ex.toString());
                    }
                }
            } else {
                m_Player = null;
                errMsg("Error during close_Player -> ");
                errMsg("No valid Player found for key: " + m_TuneObj.getKey());
            }
        }

        else {
            errMsg("Error during close_Player -> ");
            errMsg("No Player found for key: " + key);
        }
    }

    private void restartTune(String key) {
        if (m_PlayerMap.containsKey(key)) {
            this.m_TuneObj = (TuneObject) m_PlayerMap.get(key);
            this.m_Player = m_TuneObj.getPlayer();
            if (!m_TuneObj.getRealStoped()) {
                if (!waitDelay(m_TuneObj.getDelay())) {
                    errMsg("Delay failed!");
                }
                m_Player.start();
                infMsg("Restart: " + m_TuneObj.getKey());
            }
        }
    }

    private synchronized void endOfTune(String key) {
        if (m_PlayerMap.containsKey(key)) {
            this.m_TuneObj = (TuneObject) m_PlayerMap.get(key);
            this.m_Player = m_TuneObj.getPlayer();
        }
        m_Player.setMediaTime(new Time(0));
        if (m_TuneObj.getDelay() != 0) {
            restartTune(m_TuneObj.getKey());
            //playTune(m_TuneObj.getKey());
            //m_Player.stop();
        } else {
            stopTune(m_TuneObj.getKey());
        }
        infMsg("End of file! " + m_TuneObj.getKey());
        //playTune(m_TuneObj.getKey());
    }

    public synchronized void controllerUpdate(ControllerEvent ce) {
        Player p = ((Player) ce.getSourceController());
        // now iterate the map to get the player's delay the event's for
        Iterator it = m_PlayerMap.values().iterator();
        while (it.hasNext()) {
            m_TuneObj = (TuneObject) it.next();
            if ((m_TuneObj.getPlayer()).equals(p)) {
                break;
            }
        }
        if (ce instanceof ControllerErrorEvent) {
            m_TuneObj.setFailed(true);
        } else if (ce instanceof ControllerClosedEvent) {
            m_TuneObj.setClosed(true);
        } else {
            return;
        }
        notifyAll();
    }
}
