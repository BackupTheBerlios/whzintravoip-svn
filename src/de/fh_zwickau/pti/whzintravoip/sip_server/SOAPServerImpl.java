
package de.fh_zwickau.pti.whzintravoip.sip_server;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import java.util.*;

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
    public ServerSipCallerImpl m_SIPPacketCaller;

    /**
     * A List of user ip's, which are on
     */
    //public static Vector m_vUserIPList = new Vector();

    public static HashMap m_mUserIPMap = new HashMap();

    /**
     * A List of SIPPacketCallers for the users
     */
    public static HashMap m_mUserCallerMap = new HashMap();

    /**
     * First add the users ip to list (if not exists) and then initiate a
     * SIPPacketCaller to initiate the call. The SipPacketCaller will be saved
     * in the map with the corresponding ip of the user. So you can use this
     * SIPPacketCaller later to make an Invite and so on.
     *
     * Initiating the SIPStack / SIPProvider and ListeningPoints for later use
     *
     * @param fromIP String the IP from the user who want to init a session
     * @param toIP String the IP from the other user who want to be invited
     * @return String Just a simple logging back to ThinClient
     */
    public String initCall(String InviterIP, String RecipiantIP)
    {
        m_SIPPacketCaller = null;
        if(! m_mUserIPMap.containsKey(InviterIP))
        {
            // add the IP from the Inviter
            //m_vUserIPList.add(InviterIP);
            // add the IP from the Recipiant
            m_mUserIPMap.put(InviterIP, RecipiantIP);
            // add a SIPPacketCallerClass for the Inviter Map
            m_SIPPacketCaller = new ServerSipCallerImpl(InviterIP);
            m_mUserCallerMap.put(InviterIP, m_SIPPacketCaller);
        }
        else return new String("You are already listed. Fuck Up!");
        PropertyConfigurator.configure("/log4j.properties");
        logger.info("Init Call started!");
        //m_SIPPacketCaller = new Server_SIPPacketCaller(fromIP);
        try {
            this.m_SIPPacketCaller.initCallerSIPStack(RecipiantIP);
        } catch (Exception ex) {
            logger.error("Error during init SIP Stack :" + ex.toString());
        }
        try {
            this.m_SIPPacketCaller.initCallerFactories();
        } catch (Exception ex) {
            logger.error("Error during init Factories: " + ex.toString());
        }
        logger.info("Init Call ended!");
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

    public String makeCall(String InviterIP)
    {
        PropertyConfigurator.configure("/log4j.properties");
        if(m_mUserIPMap.containsKey(InviterIP))
        {
            logger.info("Make Call started!");
            String recipientIP = (String) m_mUserIPMap.get(InviterIP);
            m_SIPPacketCaller = (ServerSipCallerImpl) m_mUserCallerMap.get(InviterIP);
            m_SIPPacketCaller.setRecipientIPforRequest(recipientIP);
            logger.info("Send INVITE to IPAdress: " + recipientIP);
            logger.info("PacketCaller INVITE TO: " + m_SIPPacketCaller.m_sPeerHostPort);
            logger.info("INVITE with SIPStack: " + m_SIPPacketCaller.sipStackCaller.toString());
            try {
                m_SIPPacketCaller.sendRequest("INVITE");
            } catch (Exception ex) {
                logger.error("Error during makeCall(): " + ex.toString());
            }

        } else return new String("Inviter has no initiated SIPStack here! ");
        logger.info("Make Call ended!");
        return new String("make Call sucessful!" + this.m_SIPPacketCaller.to);
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
                    logger.info("Accept Call with Recipient: " + RecipientIP);
                    logger.info("Accept Call with Inviter:   " + entry.getKey());
                    logger.info("Accept Call with SIPStack: " + m_SIPPacketCaller.sipStackCaller.toString());
                }
            }
        } else return new String("Recipient not known!");
        try {
            m_SIPPacketCaller.sendRequest("ACK");
        } catch (Exception ex) {
            logger.error("Error during acceptCall(): " + ex.toString());
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
                    m_SIPPacketCaller.sipStackCaller.toString());
        try {
            m_SIPPacketCaller.sendRequest("CANCEL");
        } catch (Exception ex) {
            logger.error("Error during denyCall(): " + ex.toString());
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
                    m_SIPPacketCaller.sipStackCaller.toString());
        try {
            m_SIPPacketCaller.sendRequest("BYE");
        } catch (Exception ex) {
            logger.error("Error during denyCall(): " + ex.toString());
        }
        logger.info("End Call ended!");

        return new String("End Call succesful");
    }


}
