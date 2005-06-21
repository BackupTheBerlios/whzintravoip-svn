
package de.fh_zwickau.pti.whzintravoip.sip_server;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import java.util.*;
import java.net.UnknownHostException;
import java.net.InetAddress;

import de.fh_zwickau.pti.whzintravoip.sip_server.ldapconn.*;
import de.fh_zwickau.pti.whzintravoip.sip_server.hibernate.*;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

/**
 *
 * <p>Überschrift: SoapServer_Interface </p>
 *
 * <p>Beschreibung: This Class ist the Interface between the ThinClient and
 *                  the SIPStack Caller. Via Soap. </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Blurb
 * @version 0.0.1
 */

public class SOAPServerImpl implements SOAPServer {

    /**
     * Init the logger named PacketCaller.log
     */
    private static final Logger logger = Logger.getLogger("PacketCaller.log");

    /**
     * Handle the SIPPacketCaller
     */
    private static ServerSipCallerImpl m_SIPPacketCaller;

    /**
     * Instance of the LDAPRequest class for LDAP requests
     */
    private static ldaprequest m_LDAPRequest = new ldaprequest();

    /**
     * Instance of the Hibernate UserMapping to save/load user Objects
     */
    private static UserMapping m_UserMapping = new UserMapping();


    /**
     * A List of user ip's, which are on
     */
    public static UserManager m_UserManager= new UserManager();


    public static HashMap m_mUserIPMap = new HashMap();

    /**
     * A List of SIPPacketCallers for the users
     */
    public static HashMap m_mUserCallerMap = new HashMap();

    /**
     * Every time a SOAPServerObject will be intantiated the ServerSipCallerImpl
     * must be intantiated with the correct IP! The logger must be configured
     * with the current properties. And the SipStack and Factories must be
     * initiated!.
     */
    public SOAPServerImpl()
    {
        PropertyConfigurator.configure("/log4j.properties");
        this.m_SIPPacketCaller = new ServerSipCallerImpl(this.getOwnIP());
        this.initSipServer();
    }

    /**
     * Init the SIPPacketCaller on startup or better first use. This means
     * the SIPPacketCaller initiate the SIPStack and the Factorys for later use.
     *
     * @return String Just a Comment
     */
    private String initSipServer()
    {
        logger.info("Init SIP started!");
        try {
            this.m_SIPPacketCaller.initServerSipCaller();
        } catch (Exception ex) {
            logger.error("Error during init SIP:" + ex.toString());
            return new String("Error during init SIP: " + ex.toString());
        }
        logger.info("Init SIP ended!");
        return new String("Init SIP sucessful!");
    }

    /**
     * The ThinClient will use the Method to register himself oder better his
     * user to the Server.
     *
     * @param userIP String The IP from the thinclient user
     * @param userToken String The users token.
     * @return String Just a Comment
     */
    public String registerUser(User regUser)
    {
        logger.info("Register User with IP: " + regUser.getUserIP() + " and Initial " + regUser.getUserInitial());
        // LDAP request with userToken
        logger.info("Getting User Properties from LDAP Server!");
        logger.info("Object specified by: ");
        logger.info("UserIP: " + regUser.getUserIP());
        logger.info("UserInitial : " + regUser.getUserInitial());
        logger.info("UserFName: " + regUser.getUserFName());
        logger.info("UserLName: " + regUser.getUserLName());
        logger.info("UserCompany: " + regUser.getUserCompany());
        logger.info("UserMail: " + regUser.getUserMail());
        logger.info("SipName: " + regUser.getSipName());
        logger.info("SipAddress: " + regUser.getSipAddress());
        logger.info("SipScreenName: " + regUser.getSipScreenName());
        logger.info("Role: " + regUser.getRole());
        logger.info("Status: " + regUser.getStatus());
        //Properties ldapProps = this.m_LDAPRequest.getUserProps(regUser.getUserInitial());
        // Build the User Object with the Probs from the LDAP Request
        logger.info("Build User Object and Map it!");
        //regUser.setLDAPProps(ldapProps);
        // Map the User Object to Database with Hibernate
        try {
            this.m_UserMapping.mapUserObject(regUser);
        } catch (Exception ex){
            logger.error("Error during User Mapping: " + ex.toString());
            return new String("ERROR");
        }
        logger.info("User succesful registered!");

        return new String("OK");
    }

    /**
     * Processing the Invite with the m_SIPPacketCaller object which was saved
     * in the init process. If the the users IP is unknown there is something
     * wrong --> exit.
     *
     * @param fromIP String the IP from the user who want to send the Invite
     * @return String Just a simple logging back to ThinClient
     */

    public String processCall(String fromIP, String toIP)
    {
        // Init the Call, get the User Objects and place them in UserManager
        logger.info("Get the User Objects from Database!");
        try {
            this.m_UserManager.addNewUser(this.m_UserMapping.getUserWithIp(fromIP));
        } catch (Exception ex){
            logger.error("Error during getUser (fromIP) with IP! " + ex.toString());
            return new String("Something is going wrong getting the Inviter Data!");
        }
        try {
            this.m_UserManager.addNewUser(this.m_UserMapping.getUserWithIp(toIP));
        } catch (Exception ex){
            logger.error("Error during getUser (toIP) with IP! " + ex.toString());
            return new String("Something is going wrong getting the Recipient Data!");
        }
        logger.info("User Objects succesfully retrieved from Database!");
        // Test if the Inviter and the Recipient really exists, and test for
        // the right Status of the Recipient
        if(m_UserManager.containsUserWithIP(fromIP) &&
                m_UserManager.containsUserWithIP(toIP))
        {
            if (m_UserManager.getUserStatusFromIP(toIP).equals(User.
                    PICKUP)) {
                logger.info("Make Call started!");
                /** @todo Init the Header from the Request */
                //m_SIPPacketCaller.setRecipientIPforRequest(recipientIP);
                m_SIPPacketCaller.setFromUser(this.m_UserManager.getUserFromIP(fromIP));
                m_SIPPacketCaller.setToUser(this.m_UserManager.getUserFromIP(toIP));
                logger.info("Send INVITE to IPAdress: " + toIP);
                logger.info("PacketCaller INVITE TO: " +
                            m_SIPPacketCaller.getPeerHostPort());
                logger.info("INVITE with SIPStack: " +
                            m_SIPPacketCaller.getSipStackAdress());
                try {
                    m_SIPPacketCaller.sendRequest("INVITE");
                } catch (Exception ex) {
                    logger.error("Error during makeCall(): " + ex.toString());
                    return new String("Error during makeCall()!" + ex.toString());
                }
            } else {
                return new String("Recipient is not in PICKUP Mode: " +
                                  m_UserManager.getUserStatusFromIP(toIP));
            }
        } else return new String("Inviter oder Recipient ist not known!");
        logger.info("Make Call ended!");

        // If all is going well we can now refresh the state of the Users
        // in the UserManager
        logger.info("Set Users to new Status");
        if(this.m_UserManager.setUserStatusFromIP(fromIP, User.CALLING))
            logger.info("Status from Inviter successfully set to CALLING!");
        else logger.error("Set Inviter to CALLING failed!");
        if(this.m_UserManager.setUserStatusFromIP(toIP, User.INCOMING))
            logger.info("Status from Recipient successfully set to INCOMING!");
        else logger.error("Set Recipient to INCOMING failed!");

        // in the User Database
        try {
            if (this.m_UserMapping.updateUserWithIP(fromIP, User.CALLING))
                logger.info("Updated Inviter successfully to CALLING (Database)");
            else logger.info("Update Inviter to CALLING failed");
            if (this.m_UserMapping.updateUserWithIP(toIP, User.INCOMING))
                logger.info("Updated Recipient successfully to INCOMING (Database)");
            else logger.info("Update Recipient to INCOMING failed");
        } catch (Exception ex) {
            logger.error("Error close Session during Update: " + ex.toString());
        }
        return new String("make Call sucessful!");
    }

    /**
     * The thinclient will use this method to accept the Call of an
     * Inviter. The ThinClient who calls this is in the recipient Role.
     * A ACK will be send to the inviter thinclient and if all ist going well
     * the thinclients will be in state TALKING.
     *
     * @param inviterIP String The one who has send the INVITE
     * @param recipientIP String The one who has pressed the Button ACCEPT
     * @return String Just a Comment
     */

    public String acceptCall(String fromIP, String toIP)
    {
        if(this.m_UserManager.containsUserWithIP(fromIP))
        {
            logger.info("Accept Call started!");
            //m_SIPPacketCaller.setRecipientIPforRequest(inviterIP);
            m_SIPPacketCaller.setFromUser(this.m_UserManager.getUserFromIP(fromIP));
            m_SIPPacketCaller.setToUser(this.m_UserManager.getUserFromIP(toIP));
            logger.info("Accept Call with Recipient: " + toIP);
            logger.info("Accept Call with Inviter:   " + fromIP);
            logger.info("Accept Call with SIPStack: " +
                        m_SIPPacketCaller.getSipStackAdress());
        } else return new String("Recipient not known!");
        try {
            m_SIPPacketCaller.sendRequest("ACK");
        } catch (Exception ex) {
            logger.error("Error during acceptCall(): " + ex.toString());
            return new String("Error during acceptCall(): " + ex.toString());
        }
        logger.info("Accept Call ended!");

        // If all is going well refresh state of the Users
        // In the UserManager
        logger.info("Set Users to new Status");
        if(this.m_UserManager.setUserStatusFromIP(fromIP, User.TALKING))
            logger.info("Status from Inviter succesfully set to TALKING!");
        else logger.error("Set Inviter to TALKING failed!");
        if(this.m_UserManager.setUserStatusFromIP(toIP, User.TALKING))
            logger.info("Status from Recipient succesfully set to TALKING!");
        else logger.error("Set Recipient to TALKING failed!");

        // in the database
        try {
            if (this.m_UserMapping.updateUserWithIP(fromIP, User.TALKING))
                logger.info("Updated Inviter successfully to TALKING (Database)");
            else logger.info("Update Inviter to TALKING failed");
            if (this.m_UserMapping.updateUserWithIP(toIP, User.TALKING))
                logger.info("Updated Recipient successfully to TALKING (Database)");
            else logger.info("Update Recipient to TALKING failed");
        } catch (Exception ex) {
            logger.error("Error close Session during Update: " + ex.toString());
        }
        return new String("Accept Call succesful");
    }

    public String denyCall(String fromIP, String toIP)
    {
        if(this.m_UserManager.getUserStatusFromIP(toIP).equals(User.CALLING))
        {

        }
        /**
        logger.info("Deny Call started!");
        if(isInviter)
        {
            logger.info("Deny Call from Inviter with IP: " + fromIP);
            if(m_mUserIPMap.containsKey(fromIP))
            {
                logger.info("Inviter verified!");
                m_SIPPacketCaller = (ServerSipCallerImpl) m_mUserCallerMap.
                                    get(fromIP);
                m_SIPPacketCaller.setRecipientIPforRequest((String) m_mUserIPMap.get(fromIP));
                logger.info("Send CANCEL to IPAdress: " + (String) m_mUserIPMap.get(fromIP));
            } else {
                return new String("Your InviterIP is not registered! " + fromIP);
            }
        } else {
            logger.info("Deny Call from Recipient with IP: " + fromIP);
            if(m_mUserIPMap.containsValue(fromIP))
            {
                logger.info("Recipient verified!");
                Set entries = m_mUserIPMap.entrySet();
                Iterator it = entries.iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (entry.getValue().equals(fromIP)) {
                        m_SIPPacketCaller = (ServerSipCallerImpl)
                                            m_mUserCallerMap.get(entry.getKey());
                        m_SIPPacketCaller.setRecipientIPforRequest((String) entry.getKey());
                        logger.info("Send CANCEL to IPAdress: " + entry.getKey());
                    }
                }
            } else {
                return new String("Your RecipientIP ist not registered! " + fromIP);
            }

        }
        */
        logger.info("CANCEL with SIPStack: " +
                    m_SIPPacketCaller.getSipStackAdress());
        try {
            m_SIPPacketCaller.sendRequest("CANCEL");
        } catch (Exception ex) {
            logger.error("Error during denyCall(): " + ex.toString());
            return new String("Error during denyCall(): " + ex.toString());
        }
        logger.info("Deny Call ended!");

        return new String("deny Call succesful");
    }

    public String endCall(String fromIP, String toIP) {
        logger.info("End Call started!");
        if (this.m_UserManager.containsUserWithIP(fromIP) &&
            this.m_UserManager.containsUserWithIP(toIP)) {
            logger.info("Send BYE to IPAdress: " + toIP);
            //m_SIPPacketCaller.setRecipientIPforRequest(toIP);
            m_SIPPacketCaller.setFromUser(this.m_UserManager.getUserFromIP(fromIP));
            m_SIPPacketCaller.setToUser(this.m_UserManager.getUserFromIP(toIP));
        } else {
            return new String(
                    "The Users for the giben IP's cannot found in the UserManager!");
        }
        logger.info("BYE with SIPStack: " +
                    m_SIPPacketCaller.getSipStackAdress());
        try {
            m_SIPPacketCaller.sendRequest("BYE");
        } catch (Exception ex) {
            logger.error("Error during endCall(): " + ex.toString());
            return new String("Error during endCall(): " + ex.toString());
        }
        logger.info("End Call ended!");

        // If all is going well, we can now delete the Users from UserManager
        if(this.m_UserManager.removeUser(this.m_UserManager.getUserFromIP(fromIP)))
            logger.info("User with IP " + fromIP + " successfully removed!");
        else logger.error("Error removing User with IP " + fromIP);
        if(this.m_UserManager.removeUser(this.m_UserManager.getUserFromIP(toIP)))
            logger.info("User with IP " + toIP + " succesfully removed!");
        else logger.error("Error removing User with IP " + toIP);

        // Update the states for the user in the database
        try {
            if (this.m_UserMapping.updateUserWithIP(fromIP, User.PICKUP))
                logger.info("Updated FromIP successfully to PICKUP (Database)");
            else logger.info("Update FromIP to PICKUP failed");
            if (this.m_UserMapping.updateUserWithIP(toIP, User.PICKUP))
                logger.info("Updated ToIP successfully to PICKUP (Database)");
            else logger.info("Update ToIP to PICKUP failed");
        } catch (Exception ex) {
            logger.error("Error close Session during Update: " + ex.toString());
        }
        return new String("End Call succesful!");
    }

    public List whoIsOn()
    {
        try {
            return this.m_UserMapping.getAllUsers();
        } catch (Exception ex) {
            logger.error("Error getting all registered Users: " + ex.toString());
            return null;
        }
    }

    private String getOwnIP(){
       String ip = null;
       try {
         InetAddress myIP = InetAddress.getLocalHost();
         ip = myIP.getHostAddress();
       }
       catch (UnknownHostException ex) {
         System.err.println(ex);
       }
       return ip;
   }

   public boolean removeInviterSIPPacketCaller(String InviterIP) {
       //m_vUserIPList.remove(InviterIP);
       m_mUserIPMap.remove(InviterIP);
       m_mUserCallerMap.remove(InviterIP);
       m_SIPPacketCaller.stopAndRemoveSIPStack();
       m_SIPPacketCaller = null;
       logger.info("Number ob Items in Inviter Map: " + m_mUserCallerMap.size());
       return true;
   }

   static {
       Properties p = System.getProperties();
       Set s = p.keySet();
       for(Iterator i = s.iterator(); i.hasNext(); )
       {
           Object key = i.next();
           logger.info(key.toString() + " : " + p.get(key));
       }

   }


}


