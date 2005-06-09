package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import java.util.Vector;
import java.util.Properties;

public class UserManager {

    public static Vector m_vUserList = new Vector();

    public UserManager() {

    }

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
        User dummy;
        for(int i = 0; i < m_vUserList.size(); i++)
        {
            dummy = (User) m_vUserList.get(i);
            if(dummy.getUserIP().equals(userIP)) return true;
        }
        return false;
    }


    public User getUserFromIP(User value)
    {
        User dummy;
        for(int i = 0; i < m_vUserList.size(); i++)
        {
            dummy = (User) m_vUserList.get(i);
            if(dummy.getUserIP().equals(value)) return dummy;
        }
        return null;
    }

    public String getUserStatusFromIP(String userIP)
    {
        User dummy;
        for(int i = 0; i < m_vUserList.size(); i++)
        {
            dummy = (User) m_vUserList.get(i);
            if(dummy.getUserIP().equals(userIP)) return dummy.getStatus();
        }
        return null;
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
}
