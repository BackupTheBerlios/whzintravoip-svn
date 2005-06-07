package de.fh_zwickau.pti.whzintravoip.db_access;

import java.util.*;
/**
 * <p>Überschrift: </p>
 *
 * <p>Beschreibung: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class User {

    private int m_iIdUser;
    private String m_sInitial;
    private String m_sMail;
    private String m_sMatrikel;
    private String m_sFname;
    private String m_sLname;
    private String m_sNick;


    public int getIdUser(){return m_iIdUser;}
    public String getInitial(){return m_sInitial;}
    public String getMail(){return m_sMail;}
    public String getMatrikel(){return m_sMatrikel;}
    public String getFname(){return m_sFname;}
    public String getLname(){return m_sLname;}
    public String getNick(){return m_sNick;}

    public void setIdUser(int id){this.m_iIdUser = id;}
    public void setInitial(String in){this.m_sInitial = in;}
    public void setMail(String mail){this.m_sMail = mail;}
    public void setMatrikel(String matrikel){this.m_sMatrikel = matrikel;}
    public void setFname(String fname){this.m_sFname = fname;}
    public void setLname(String lname){this.m_sLname = lname;}
    public void setNick(String ni){this.m_sNick = ni;}

}
