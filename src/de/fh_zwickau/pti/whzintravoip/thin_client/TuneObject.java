package de.fh_zwickau.pti.whzintravoip.thin_client;

import javax.media.Player;

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
public class TuneObject {

    private Player m_Player = null;
    private int m_iDelay = 0;

    public TuneObject() {
    }

    public void setPlayer(Player player) {
        this.m_Player = player;
    }

    public void setDelay(int delay) {
        this.m_iDelay = delay;
    }

    public Player getPlayer() {
        return m_Player;
    }

    public int getDelay() {
        return m_iDelay;
    }
}
