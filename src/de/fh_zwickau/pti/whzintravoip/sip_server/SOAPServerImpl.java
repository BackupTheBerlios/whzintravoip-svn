
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

    public String registerUser(String userIP, String userToken)
    {
        logger.info("Register User with IP: " + userIP + " and Token " + userToken);
        // LDAP request with userToken
        logger.info("Getting User Properties from LDAP Server!");
        Properties userProbs = this.m_LDAPRequest.getUserProbs(userToken);
        // Build the User Object with the Probs from the LDAP Request
        logger.info("Build User Object and Map it!");
        User regUser = new User(userProbs);
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
     * Add the Users with the INVITER and the RECIPIENT Role to the UserManager.
     * With the Data from the Hibernate Database.
     *
     * @param fromIP String the IP from the user who want to init a session
     * @param toIP String the IP from the other user who want to be invited
     * @return String Just a simple logging back to ThinClient
     */
    public String initCall(User aUser)
    {
        logger.info("Test: IP " + aUser.getUserIP() + " Name " + aUser.getUserName());
        /**
        Properties inviterProbs = new Properties();
        inviterProbs.setProperty("sip_server.user.USER_IP", inviterIP);
        inviterProbs.setProperty("sip_server.user.USER_NAME", "Harald");
        inviterProbs.setProperty("sip_server.user.SIP_ADDRESS", "Harald@Schaf");
        inviterProbs.setProperty("sip_server.user.SCREEN_NAME", "Mähhh");
        inviterProbs.setProperty("sip_server.user.ROLE", User.INVITER);
        inviterProbs.setProperty("sip_server.user.STATUS", User.PICKUP);
        // add the User with the Inviter role

        logger.info("Added User " + "Harald" + " with IP: " + inviterIP + " as Inviter!");
        this.m_UserManager.addNewUser(inviterProbs);
        // add the User with the Recipient role
        Properties recipientProbs = new Properties();
        inviterProbs.setProperty("sip_server.user.USER_IP", inviterIP);
        inviterProbs.setProperty("sip_server.user.USER_NAME", "Harald");
        inviterProbs.setProperty("sip_server.user.SIP_ADDRESS", "Harald@Schaf");
        inviterProbs.setProperty("sip_server.user.SCREEN_NAME", "Mähhh");
        inviterProbs.setProperty("sip_server.user.ROLE", User.INVITER);
        inviterProbs.setProperty("sip_server.user.STATUS", User.PICKUP);
        this.m_UserManager.addNewUser(recipientProbs);

        logger.info("Added User " + "Mist" + " with IP: " + inviterIP + " as Recipient!");
        m_mUserIPMap.put(inviterIP, recipientIP);

        //this.initCall(recipientIP);
        //logger.info("Init Call ended!");*/
        return new String("init Call sucessful!");
    }

    /**
     * Processing the Invite with the m_SIPPacketCaller object which was saved
     * in the init process. If the the users IP is unknown there is something
     * wrong --> exit.
     *
     * @param fromIP String the IP from the user who want to send the Invite
     * @return String Just a simple logging back to ThinClient
     */

    public String makeCall(String inviterIP, String recipientIP)
    {
        // Init the Call, get the User Objects and place them in UserManager
        logger.info("Get the User Objects from Database!");
        try {
            this.m_UserManager.addNewUser(this.m_UserMapping.getUserWithIp(inviterIP));
        } catch (Exception ex){
            logger.error("Error during getUser (Inviter) with IP! " + ex.toString());
            return new String("Something is going wrong getting the Inviter Data!");
        }
        try {
            this.m_UserManager.addNewUser(this.m_UserMapping.getUserWithIp(recipientIP));
        } catch (Exception ex){
            logger.error("Error during getUser (Recipient) with IP! " + ex.toString());
            return new String("Something is going wrong getting the Recipient Data!");
        }
        logger.info("User Objects succesfully retrieved from Database!");
        // Test if the Inviter and the Recipient really exists, and test for
        // the right Status of the Recipient
        if(m_UserManager.containsUserWithIP(inviterIP) &&
                m_UserManager.containsUserWithIP(recipientIP))
        {
            if (m_UserManager.getUserStatusFromIP(recipientIP).equals(User.
                    PICKUP)) {
                logger.info("Make Call started!");
                /** @todo Init the Header from the Request */
                m_SIPPacketCaller.setRecipientIPforRequest(recipientIP);
                logger.info("Send INVITE to IPAdress: " + recipientIP);
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
                                  m_UserManager.getUserStatusFromIP(recipientIP));
            }
        } else return new String("Inviter oder Recipient ist not known!");
        logger.info("Make Call ended!");
        logger.info("Set Users to new Status");
        if(this.m_UserManager.setUserStatusFromIP(inviterIP, User.CALLING))
            logger.info("Status from Inviter succesfully set to CALLING!");
        else logger.error("Set Inviter to CALLING failed!");
        if(this.m_UserManager.setUserStatusFromIP(recipientIP, User.INCOMING))
            logger.info("Status from Recipient succesfully set to INCOMING!");
        else logger.error("Set Recipient to INCOMING failed!");
        return new String("make Call sucessful!");
    }

    public String acceptCall(String RecipientIP)
    {
        m_SIPPacketCaller = null;
        if(m_mUserIPMap.containsValue(RecipientIP))
        {
            logger.info("Accept Call started!");
            Set entries = m_mUserIPMap.entrySet();
            Iterator it = entries.iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if(entry.getValue().equals(RecipientIP))
                {
                    m_SIPPacketCaller = (ServerSipCallerImpl) m_mUserCallerMap.get(entry.getKey());
                    m_SIPPacketCaller.setRecipientIPforRequest((String) entry.getKey());
                    logger.info("Accept Call with Recipient: " + RecipientIP);
                    logger.info("Accept Call with Inviter:   " + entry.getKey());
                    logger.info("Accept Call with SIPStack: " + m_SIPPacketCaller.getSipStackAdress());
                }
            }
        } else return new String("Recipient not known!");
        try {
            m_SIPPacketCaller.sendRequest("ACK");
        } catch (Exception ex) {
            logger.error("Error during acceptCall(): " + ex.toString());
            return new String("Error during acceptCall(): " + ex.toString());
        }
        logger.info("Accept Call ended!");
        return new String("Accept Call succesful");
    }

    public String denyCall(String fromIP, boolean isInviter)
    {
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

    public String endCall(String fromIP, boolean isInviter) {
        logger.info("End Call started!");
        if (isInviter) {
            logger.info("End Call from Inviter with IP: " + fromIP);
            if (m_mUserIPMap.containsKey(fromIP)) {
                logger.info("Inviter verified!");
                m_SIPPacketCaller = (ServerSipCallerImpl) m_mUserCallerMap.
                                    get(fromIP);
                m_SIPPacketCaller.setRecipientIPforRequest((String)
                        m_mUserIPMap.get(fromIP));
                logger.info("Send BYE to IPAdress: " +
                            (String) m_mUserIPMap.get(fromIP));
            } else {
                return new String("Your InviterIP is not registered! " + fromIP);
            }
        } else {
            logger.info("End Call from Recipient with IP: " + fromIP);
            if (m_mUserIPMap.containsValue(fromIP)) {
                logger.info("Recipient verified!");
                Set entries = m_mUserIPMap.entrySet();
                Iterator it = entries.iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (entry.getValue().equals(fromIP)) {
                        m_SIPPacketCaller = (ServerSipCallerImpl)
                                            m_mUserCallerMap.get(entry.getKey());
                        m_SIPPacketCaller.setRecipientIPforRequest((String)
                                entry.getKey());
                        logger.info("Send BYE to IPAdress: " + entry.getKey());
                    }
                }
            } else {
                return new String("Your RecipientIP ist not registered! " +
                                  fromIP);
            }

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

        return new String("End Call succesful!");
    }

    public String testDB()
    {
        /**
        logger.info("Starting DB Test");
        db_work work = new db_work();
        try {
            work.saveTestUser();
        } catch (Exception ex) {
            logger.info("Error during meld down! " + ex.toString());
        }*/
        return new String("Test DB succesful!");
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




}
