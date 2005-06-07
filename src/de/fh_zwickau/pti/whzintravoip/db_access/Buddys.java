package de.fh_zwickau.pti.whzintravoip.db_access;

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
public class Buddys {

    private int id;
    private int user_id;
    private int buddy_id;

    public int getId(){return id;}
    public int getUser_id(){return user_id;}
    public int getBuddy_id(){return buddy_id;}

    public void setId(int id){this.id = id;}
    public void setUser_id(int id){this.user_id = id;}
    public void setBuddy_id(int id){this.buddy_id = id;}
}
