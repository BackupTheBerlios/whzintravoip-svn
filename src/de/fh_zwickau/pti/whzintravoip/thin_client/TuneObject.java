package de.fh_zwickau.pti.whzintravoip.thin_client;

import javax.media.*;

/**
 * <p>Überschrift: WHZintraVoIP</p>
 *
 * <p>Beschreibung: A Tune object with its getter and setter</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author H. Seidel (hs@fh-zwickau.de)
 * @version 0.1.0
 */
public class TuneObject {

    private String m_sKey = null;
    private Player m_Player = null;
    private int m_iDelay = 0;
    private boolean m_bStarted = false;
    private boolean m_bRealStoped = true;
    private boolean m_bFailed = false;
    private boolean m_bClosed = false;
    private String m_sFile = null;


    public TuneObject() {
    }

    public void setKey(String key){
        this.m_sKey = key;
    }

    public void setPlayer(Player player) {
        this.m_Player = player;
    }

    public void setDelay(int delay) {
        this.m_iDelay = delay;
    }

    public void setStarted(boolean set) {
        this.m_bStarted = set;
    }

    public void setRealStoped(boolean set) {
        this.m_bRealStoped = set;
    }

    public void setClosed(boolean set) {
        this.m_bClosed = set;
    }

    public void setFailed(boolean set) {
        this.m_bFailed = set;
    }

    public void setFile(String file){
        this.m_sFile = file;
    }

    public String getKey(){
        return m_sKey;
    }
    public Player getPlayer() {
        return m_Player;
    }

    public int getDelay() {
        return m_iDelay;
    }

    public boolean getStarted() {
        return m_bStarted;
    }

    public boolean getRealStoped() {
        return m_bRealStoped;
    }

    public boolean getClosed() {
        return m_bClosed;
    }

    public boolean getFailed() {
        return m_bFailed;
    }

    public String getFile(){
        return m_sFile;
    }
}
