package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import java.util.Properties;

/**
 *
 * <p>Überschrift: User Object</p>
 *
 * <p>Beschreibung: A User Object describes a user of an ThinClient</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author blurb
 * @version 0.0.1
 */

public class User {

    public static final String LOGIN    = "1";
    public static final String PICKUP   = "2";
    public static final String INCOMING = "3";
    public static final String MAKECALL = "4";
    public static final String CALLING  = "5";
    public static final String TALKING  = "6";

    public static final String INVITER    = "20";
    public static final String RECIPIENT  = "21";
    public static final String NONE       = "22";

    private int m_iIdUser;

    private String m_sUserIP = "";
    private String m_sUserInitial = "";
    private String m_sUserFName = "";
    private String m_sUserLName = "";
    private String m_sUserCompany = "";
    private String m_sUserMail = "";

    private String m_sSipName = "";
    private String m_sSipAddress = "";
    private String m_sSipScreenName = "";

    private String m_sRole = NONE;
    private String m_sStatus = PICKUP;

    public User()
    {

    }

    public User(String userIP,
                String userInitial,
                String fName,
                String lName,
                String company,
                String userMail,
                String sipName,
                String sipAddress,
                String screenName,
                String role,
                String status)
    {
        this.m_sUserIP = userIP;
        this.m_sUserInitial = userInitial;
        this.m_sUserFName = fName;
        this.m_sUserLName = lName;
        this.m_sUserCompany = company;
        this.m_sUserMail = userMail;
        this.m_sSipName = sipName;
        this.m_sSipAddress = sipAddress;
        this.m_sSipScreenName = screenName;
        this.m_sRole = role;
        this.m_sStatus = status;
    }

    public User(Properties userProps)
    {
        this.m_sUserIP = userProps.getProperty("sip_server.user.USER_IP");
        this.m_sUserInitial = userProps.getProperty("sip_server.user.USER_INITIAL");
        this.m_sUserFName = userProps.getProperty("sip_server.user.USER_FNAME");
        this.m_sUserLName = userProps.getProperty("sip_server.user.USER_LNAME");
        this.m_sUserCompany = userProps.getProperty("sip_server.user.USER_COMPANY");
        this.m_sUserMail = userProps.getProperty("sip_server.user.USER_MAIL");
        this.m_sSipName = userProps.getProperty("sip_server.user.SIP_NAME");
        this.m_sSipAddress = userProps.getProperty("sip_server.user.SIP_ADDRESS");
        this.m_sSipScreenName = userProps.getProperty("sip_server.user.SCREEN_NAME");
        this.m_sRole = userProps.getProperty("sip_server.user.ROLE");
        this.m_sStatus = userProps.getProperty("sip_server.user.STATUS");
    }

    public void setThinClientProps(Properties thinProps)
    {
        this.m_sUserIP = thinProps.getProperty("sip_server.user.USER_IP");
        this.m_sUserInitial = thinProps.getProperty("sip_server.user.USER_INITIAL");
        this.m_sSipName = thinProps.getProperty("sip_server.user.SIP_NAME");
        this.m_sSipAddress = thinProps.getProperty("sip_server.user.SIP_ADDRESS");
        this.m_sSipScreenName = thinProps.getProperty("sip_server.user.SCREEN_NAME");
    }

    public void setLDAPProps(Properties ldapProps)
    {
        this.m_sUserInitial = ldapProps.getProperty("sip_server.user.USER_INITIAL");
        this.m_sUserFName = ldapProps.getProperty("sip_server.user.USER_FNAME");
        this.m_sUserLName = ldapProps.getProperty("sip_server.user.USER_LNAME");
        this.m_sUserCompany = ldapProps.getProperty("sip_server.user.USER_COMPANY");
        this.m_sUserMail = ldapProps.getProperty("sip_server.user.USER_MAIL");
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

    public String getUserInitial()
    {
        return this.m_sUserInitial;
    }

    public void setUserInitial(String userInitial)
    {
        this.m_sUserInitial = userInitial;
    }

    public String getUserFName()
    {
        return this.m_sUserFName;
    }

    public void setUserFName(String fName)
    {
        this.m_sUserFName = fName;
    }

    public String getUserLName()
    {
        return this.m_sUserLName;
    }

    public void setUserLName(String lName)
    {
        this.m_sUserLName = lName;
    }

    public String getUserCompany()
    {
        return this.m_sUserCompany;
    }

    public void setUserCompany(String company)
    {
        this.m_sUserCompany = company;
    }

    public String getUserMail()
    {
        return this.m_sUserMail;
    }

    public void setUserMail(String userMail)
    {
        this.m_sUserMail = userMail;
    }

    public String getSipName()
    {
        return this.m_sSipName;
    }

    public void setSipName(String userName)
    {
        this.m_sSipName = userName;
    }

    public String getSipAddress()
    {
        return this.m_sSipAddress;
    }

    public void setSipAddress(String sipAddress)
    {
        this.m_sSipAddress = sipAddress;
    }

    public String getSipScreenName()
    {
        return this.m_sSipScreenName;
    }

    public void setSipScreenName(String screenName)
    {
        this.m_sSipScreenName = screenName;
    }

    public String getRole()
    {
        /** @todo implement the get Role Method */
        return this.m_sRole;
    }

    public void setRole(String role)
    {
        this.m_sRole = role;
    }

    public String getStatus()
    {
        return this.m_sStatus;
    }

    public void setStatus(String status)
    {
        this.m_sStatus = status;
    }


}
