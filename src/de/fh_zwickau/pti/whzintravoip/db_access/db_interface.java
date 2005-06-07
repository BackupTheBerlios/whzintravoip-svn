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
public class db_interface {

    // Keys for properties
    private static String init = "user.initial";
    private static String mail = "user.mail";
    private static String matri = "user.matrikel";
    private static String fname = "user.fname";
    private static String lname = "user.lname";
    private static String nick = "user.nick";

    private User m_user = null;
    private db_work m_work = null;

    public db_interface() {
        m_work = new db_work();
    }

    public void addUser(Properties prop) {
        String buf = null;
        m_user = new User();
        int size = prop.size();
        if ((buf = prop.getProperty(init).toLowerCase()) != null) {
            m_user.setInitial(buf);
        }
        if ((buf = prop.getProperty(mail)) != null) {
            m_user.setMail(buf);
        }
        if ((buf = prop.getProperty(matri)) != null) {
            m_user.setMatrikel(buf);
        }
        if ((buf = prop.getProperty(fname)) != null) {
            m_user.setFname(buf);
        }
        if ((buf = prop.getProperty(lname)) != null) {
            m_user.setLname(buf);
        }
        if ((buf = prop.getProperty(nick)) != null) {
            m_user.setNick(buf);
        }
        m_work.addUser(m_user);
    }

    public void delUser(String inits){
       User user = m_work.findUser(inits);
       m_work.delUser(user);
    }

    public List getAllUser() {
        List users = null;
        users = m_work.getAllUser();
        return users;
    }

    public void addBuddy(String user_inits, String buddy_inits){
        User actual = m_work.findUser(user_inits);
        User buddy = m_work.findUser(buddy_inits);
        Buddys bud = new Buddys();
        bud.setUser_id(actual.getIdUser());
        bud.setBuddy_id(buddy.getIdUser());
        m_work.addBuddy(bud);
    }

    public void delBuddy(String user_inits, String buddy_inits){
        User actual = m_work.findUser(user_inits);
        User buddy = m_work.findUser(buddy_inits);
        Buddys bud = m_work.getBuddy(actual.getIdUser(), buddy.getIdUser());
        m_work.delBuddy(bud);
    }

    public List getBuddys(String inits){
        User actual = m_work.findUser(inits);
        List buds = m_work.getBuddys(actual.getIdUser());
        return buds;
    }
}
