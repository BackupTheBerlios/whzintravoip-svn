package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import java.util.Properties;

public class User {

    public static final String LOGIN    = "1";
    public static final String PICKUP   = "2";
    public static final String INCOMING = "3";
    public static final String MAKECALL = "4";
    public static final String CALLING  = "5";
    public static final String TALKING  = "6";

    public static final String INVITER    = "20";
    public static final String RECIPIENT  = "21";

    private String m_sUserIP = "";

    private String m_sUserName = "";
    private String m_sSipAddress = "";
    private String m_sScreenName = "";

    private String m_bRole;
    private String m_bStatus;

    private int m_iIdUser;

    public User()
    {

    }

    public User(String userIP,
                String userName,
                String sipAddress,
                String screenName,
                String role,
                String status)
    {
        this.m_sUserIP = userIP;
        this.m_sUserName = userName;
        this.m_sSipAddress = sipAddress;
        this.m_sScreenName = screenName;
        this.m_bRole = role;
        this.m_bStatus = status;
    }

    public User(Properties userProbs)
    {
        this.m_sUserIP = userProbs.getProperty("sip_server.user.USER_IP");
        this.m_sUserName = userProbs.getProperty("sip_server.user.USER_NAME");
        this.m_sSipAddress = userProbs.getProperty("sip_server.user.SIP_ADDRESS");
        this.m_sScreenName = userProbs.getProperty("sip_server.user.SCREEN_NAME");
        this.m_bRole = userProbs.getProperty("sip_server.user.ROLE");
        this.m_bStatus = userProbs.getProperty("sip_server.user.STATUS");
    }

    public int getIdUser()
    {
        return m_iIdUser;
    }

    public void setIdUser(int id)
    {
        this.m_iIdUser = id;
    }

    public String getUserIP()
    {
        return this.m_sUserIP;
    }

    public void setUserIP(String userIP)
    {
        this.m_sUserIP = userIP;
    }

    public String getUserName()
    {
        return this.m_sUserName;
    }

    public void setUserName(String userName)
    {
        this.m_sUserName = userName;
    }

    public String getSipAddress()
    {
        return this.m_sSipAddress;
    }

    public void setSipAddress(String sipAddress)
    {
        this.m_sSipAddress = sipAddress;
    }

    public String getScreenName()
    {
        return this.m_sScreenName;
    }

    public void setScreenName(String screenName)
    {
        this.m_sScreenName = screenName;
    }

    public String getRole()
    {
        /** @todo implement the get Role Method */
        return this.m_bRole;
    }

    public void setRole(String role)
    {
        this.m_bRole = role;
    }

    public String getStatus()
    {
        /** @todo implement the get Status Method */
        return this.m_bStatus;
    }

    public void setStatus(String status)
    {
        this.m_bStatus = status;
    }


}
