package de.fh_zwickau.pti.whzintravoip.thin_client;

import javax.media.*;

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

    // the Sounds
    private String m_sRing = "file:///../../../../s1.wav";

    // Now work on
    public PlayTunes() {
        playRing();
    }

    public static void main(String[] args) {
        new PlayTunes();
    }

    /**
     * Just do soe standard debug output.
     * @param msg String Message
     */
    private void errMsg(String msg) {
        if (m_bDebug) {
            System.out.println(msg);
        }
    }

    /**
     * Just do some standard debug output.
     * @param msg String Message
     */
    private void infMsg(String msg) {
        if (m_bDebug) {
            System.out.println(msg);
        }
    }

    /**
     * Create a Player over JMF Api to play some ringing tune.
     */
    public void playRing() {
        try {
            m_Player = Manager.createRealizedPlayer(new MediaLocator(m_sRing));
            m_Player.addControllerListener(new ControllerAdapter() {
                public void endOfMedia(EndOfMediaEvent e) {
                    m_Player.stop();
                        close_Player();
                }
            });

        } catch (Exception ex) {
            errMsg("Error during playRing: " + ex.getMessage());
        }
        infMsg("Player init!");
        m_Player.start();
    }

    /**
     * Close the Player when finished.
     */
    private void close_Player() {
        if (m_Player != null) {
            try{
                m_Player.deallocate();
                m_Player.close();
             infMsg("Player closed!");
            }catch (Exception ex){
                errMsg("Error while closing Player: " + ex.getMessage());
            }
        } else {
            errMsg("No Player initialized yet!");
        }
    }
}
