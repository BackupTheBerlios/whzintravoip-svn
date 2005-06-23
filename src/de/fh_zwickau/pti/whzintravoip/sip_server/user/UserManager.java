package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import java.util.Vector;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.Enumeration;

/**
 *
 * <p>Überschrift: UserManger</p>
 *
 * <p>Beschreibung: The Usermangaer is the gateway between the SOAPServer
 * and the UserObjects. It provides a width area of functions for the User
 * Object List.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Blurb
 * @version 0.0.1
 */

public class UserManager {

    /**
     * Init the logger named PacketCaller.log
     */
    private static final Logger logger = Logger.getLogger("PacketCaller.log");

    /**
     * List of all Users which are currently in a Session.
     */
    public static Vector m_vUserList = new Vector();

    public UserManager() {
        PropertyConfigurator.configure("/log4j.properties");
    }

    /**
     * Add a new User with a Properties List
     *
     * @param userProbs Properties The Properties of the desired User
     * @return boolean
     */
    public boolean addNewUser(Properties userProbs)
    {
        m_vUserList.add(new User(userProbs));
        return true;
    }


    public boolean addNewUser(User aUser)
    {
        m_vUserList.add(aUser);
        return true;
    }

    public boolean containsUser(User value)
    {
        if (m_vUserList.contains(value)) return true;
        return false;
    }

    public boolean containsUserWithIP(String userIP)
    {
        User dummy = null;
        logger.info("Search User with IP: " + userIP);

        for(Enumeration e = m_vUserList.elements(); e.hasMoreElements();)
        {
            logger.info("Bin hier");
            try {
                dummy = (User) e.nextElement();
            } catch (Exception ex) {
                logger.error("Exception retrieve User Object: " + ex.toString());
            }
            logger.info("User IP: " + dummy.getUserIP());
            if(dummy.getUserIP().equals(userIP)) {
                logger.info("User with IP: " + userIP + " found!");
                return true;
            }
        }
        logger.info("User with IP: " + userIP + " not found!");
        return false;
    }


    public User getUserFromIP(String userIP)
    {
        User dummy;
        for(int i = 0; i < m_vUserList.size(); i++)
        {
            dummy = (User) m_vUserList.get(i);
            if(dummy.getUserIP().equals(userIP)) return dummy;
        }
        return null;
    }

    public String getUserStatusFromIP(String userIP)
    {
        User dummy = null;
        for(int i = 0; i < m_vUserList.size(); i++)
        {
            dummy = (User) m_vUserList.get(i);
            if (dummy.getUserIP().equals(userIP)) {
                logger.info("User with IP: " + userIP + " and stat: " + dummy.getStatus() + " found!");
                return dummy.getStatus();
            }
        }
        logger.info("User with IP: " + userIP + " and stat: " + dummy.getStatus() + " found!");
        return new String(" ");
    }

    public boolean setUserStatusFromIP(String userIP, String newStatus)
    {
        User dummy;
        for(int i = 0; i < m_vUserList.size(); i++)
        {
            dummy = (User) m_vUserList.get(i);
            if(dummy.getUserIP().equals(userIP))
            {
                dummy.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    public int getUserCount()
    {
        return m_vUserList.size();
    }

    public boolean removeUser(User aUser)
    {
        if(this.m_vUserList.contains(aUser))
        {
            this.m_vUserList.remove(aUser);
            return true;
        }
        return false;
    }
}
