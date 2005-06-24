package de.fh_zwickau.pti.whzintravoip.sip_server;

import java.util.*;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.User;

import org.apache.log4j.*;
import java.net.UnknownHostException;
import java.net.InetAddress;

/**
 * <p>Title: sip_server</p>
 *
 * <p>Description: The SIP Functionality for the Server.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Blurb
 * @version 0.0.2
 */

public class ServerSipCallerImpl implements SipListener {

    private static final Logger logger = Logger.getLogger("PacketCaller.log");

    ///////////////////////////////////////////////////////////////////////////
    // Factories
    ///////////////////////////////////////////////////////////////////////////
    private static AddressFactory addressFactoryCaller = null;
    private static MessageFactory messageFactoryCaller = null;
    private static HeaderFactory headerFactoryCaller = null;
    private static SipFactory sipFactoryCaller = null;

    ///////////////////////////////////////////////////////////////////////////
    // classes etc.
    private ServerSipCallerImpl m_ListenerCaller = null;

    ///////////////////////////////////////////////////////////////////////////
    // Providers
    ///////////////////////////////////////////////////////////////////////////
    private static SipProvider m_ServerSIPProviderUDP = null;
    private static SipProvider m_ServerSIPProviderTCP = null;
    private static SipProvider m_SIPProviderToUse = null;

    ///////////////////////////////////////////////////////////////////////////
    // SIP-Stack
    ///////////////////////////////////////////////////////////////////////////
    private static SipStack m_ServerSIPStack = null;

    ///////////////////////////////////////////////////////////////////////////
    // Listeningpoints
    ///////////////////////////////////////////////////////////////////////////
    private static ListeningPoint m_UDPListeningPoint = null;
    private static ListeningPoint m_TCPListeningPoint = null;


    protected static ClientTransaction clientTid;

    ///////////////////////////////////////////////////////////////////////////
    // User related Stuff
    ///////////////////////////////////////////////////////////////////////////
    private static User m_fromUser;
    private static User m_toUser;

    private static ContactHeader m_ContactHeader;
    private static String m_sTransport;
    private static String m_sPeerHostPort;
    private static String m_sCallerStackName = "Server_Caller";
    private static int m_iCallerPort = 5060;
    private static String m_iReceiverPort = "5070";
    private static String m_iReceiverIP = "";
    private static String m_sServerAddress = "141.32.28.226";
    private static String m_sInitReceiverIP = "127.0.0.1";

    public ServerSipCallerImpl() {
        PropertyConfigurator.configure("/log4j.properties");
        logger.info("Object created!");
    }

    public ServerSipCallerImpl(String serverIP)
    {
        this.m_sServerAddress = serverIP;
        sipFactoryCaller = SipFactory.getInstance();
        sipFactoryCaller.setPathName("gov.nist");
        PropertyConfigurator.configure("/log4j.properties");
        logger.info("Object created! With Server IP: " + m_sServerAddress);
    }

    public boolean initServerSipCaller() throws Exception
    {
        this.stopAndRemoveSIPStack();
        this.initServerSIPStack();
        this.initServerFactories();
        return true;
    }


    /**
     * Initialize the SIP-Stack with the necessary properties
     *
     * @return boolean
     * @throws Exception
     */
    public boolean initServerSIPStack() throws Exception {
        // userdialog.stdOutput("Using Port " + m_iCallerPort);
        m_ServerSIPStack = null;
        //sipFactoryCaller = null;
        //sipFactoryCaller = SipFactory.getInstance();
        //sipFactoryCaller.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", m_sServerAddress);
        properties.setProperty("javax.sip.RETRANSMISSION_FILTER", "true");
        properties.setProperty("javax.sip.STACK_NAME", m_sCallerStackName);
        //properties.setProperty("javax.sip.ROUTER_PATH", "de.fh_zwickau.pti.whzintravoip.sip_connection.MyRouter");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                               "sippacketCaller-debug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                               "sippacketCaller-log.txt");
        // You need  16 for logging traces. 32 for debug + traces.
        // Your code will limp at 32 but it is best for debugging.
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        // Guard against starvation.
        properties.setProperty("gov.nist.javax.sip.READ_TIMEOUT", "1000");
        // properties.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "4096");
        properties.setProperty("gov.nist.javax.sip.CACHE_SERVER_CONNECTIONS", "false");
        // If you want to try TCP transport change the following to
//        m_sTransport = "tcp";
        m_sTransport = "udp";
//        String peerHostPort = PEER_ADDRESS+":5070";
        m_sPeerHostPort = m_sInitReceiverIP + ":" + m_iReceiverPort;
        //properties.setProperty("javax.sip.OUTBOUND_PROXY", m_sPeerHostPort + "/" + m_sTransport);
        //properties.setProperty("javax.sip.OUTBOUND_PROXY", "141.32.28.226/" + m_sTransport);
        this.m_ServerSIPStack = sipFactoryCaller.createSipStack(properties);     // this can throw an exception
        logger.info("Creating Sip Stack fromIP " + m_sServerAddress);
        logger.info("Creating Sip Stack toIP " + m_sInitReceiverIP);
        logger.info("Sip Stack Impl : " + m_ServerSIPStack.toString());
        return true;
    }



    /**
     * Initialize the necessary Factories and
     * create the listeningpoints for UDP and TCP
     *
     * @return boolean
     * @throws Exception
     */
    public boolean initServerFactories() throws Exception {
        logger.info("Sip Stack Impl : " + m_ServerSIPStack.toString());
        headerFactoryCaller = sipFactoryCaller.createHeaderFactory();
        addressFactoryCaller = sipFactoryCaller.createAddressFactory();
        messageFactoryCaller = sipFactoryCaller.createMessageFactory();

        logger.info("Header / Adress / Message Factory created!");
        try {
            m_UDPListeningPoint = this.m_ServerSIPStack.createListeningPoint(
//                    m_iCallerPort, "udp");
                    5062, "udp");
        } catch (TransportNotSupportedException ex) {
            logger.error("TransportNotSupportedException on creating UDP ListeningPoint: " +
                         ex.getMessage());
        } catch (InvalidArgumentException ex) {
            logger.error("InvalidArgumentException on creating UDP ListeningPoint: " +
                         ex.getMessage());
        } catch (Exception ex) {
            logger.error("Exception on creating UDP ListeningPoint: " +
                         ex.getMessage());
        }
        logger.info("UDP Listening Point created from SIPStack: "
                    + m_ServerSIPStack.toString()
                    + "\nUDP Listening Point: "
                    + m_UDPListeningPoint.toString());
        try {
            m_TCPListeningPoint = this.m_ServerSIPStack.createListeningPoint(
//                    m_iCallerPort, "tcp");
                    5062, "tcp");
        } catch (TransportNotSupportedException ex) {
            logger.error("TransportNotSupportedException on creating TCP ListeningPoint: " +
                         ex.getMessage());
        } catch (InvalidArgumentException ex) {
            logger.error("InvalidArgumentException on creating TCP ListeningPoint: " +
                         ex.getMessage());
        } catch (Exception ex) {
            logger.error("Exception on creating TCP ListeningPoint: " +
                        ex.getMessage());
        }
        logger.info("TCP Listening Point created from SIPStack: "
                    + m_ServerSIPStack.toString()
                    + "\nUDP Listening Point: "
                    + m_TCPListeningPoint.toString());
        m_ListenerCaller = this;
        m_ServerSIPProviderUDP = m_ServerSIPStack.createSipProvider(m_UDPListeningPoint);
        logger.info("udp provider (Caller): " + m_ServerSIPProviderUDP);
        m_ServerSIPProviderUDP.addSipListener(m_ListenerCaller);
        m_ServerSIPProviderTCP = m_ServerSIPStack.createSipProvider(m_TCPListeningPoint);
        logger.info("tcp provider (Caller): " + m_ServerSIPProviderTCP);
        m_ServerSIPProviderTCP.addSipListener(m_ListenerCaller);
        logger.info("Factories, Listener und Provider für Caller angelegt");
        return true;
    }

    /**
     * Set the IP for the Recipient of the Request
     *
     * @param recipientIP String The IP from the Recipient of the Request
     */
    /**
    public void setRecipientIPforRequest(String recipientIP)
    {
        m_sPeerHostPort = recipientIP + ":" + m_iReceiverPort;
    }
    */

    /**
     * A call to sendRequest send the specific
     * Request from this User. And set the specific fromUser Sip Header.
     *
     * @param recUser User the sending User
     */
    public void setFromUser(User fromUser)
    {
        this.m_fromUser = fromUser;

    }

    /**
     * A call to sendRequest send the specific
     * Request to this User. And set the specific toUser Sip Header.
     *
     * @param toUser User the recipient User
     */
    public void setToUser(User toUser)
    {
        this.m_toUser = toUser;
        m_sPeerHostPort = this.m_toUser.getUserIP() + ":" + m_iReceiverPort;
    }

    /**
     * Invite "the other side" to a call
     */
    public void sendRequest(String requestMethod) throws Exception {
        this.m_SIPProviderToUse = m_sTransport.equalsIgnoreCase("udp") ?
                                  m_ServerSIPProviderUDP :
                                  m_ServerSIPProviderTCP;

        String fromUser = this.m_fromUser.getSipName();
        String fromSipAddress = this.m_fromUser.getSipAddress();
        String fromDisplayName = this.m_fromUser.getSipScreenName();

        String toUser = this.m_toUser.getSipName();
        String toSipAddress = this.m_toUser.getSipAddress();
        String toDisplayName = this.m_toUser.getSipScreenName();

        // create From Header
        logger.info("Creating SipURI 'from'");
        SipURI fromAddress = addressFactoryCaller.createSipURI(fromUser,
                fromSipAddress);

        logger.info("Creating Address 'from'");
        Address fromNameAddress = addressFactoryCaller.createAddress(
                fromAddress);
        fromNameAddress.setDisplayName(fromDisplayName);

        logger.info("Creating FromHeader");
        FromHeader fromHeader = headerFactoryCaller.createFromHeader(
                fromNameAddress, "12345");

        // create To Header
        logger.info("Creating SipURI 'to'");
        SipURI toAddress = addressFactoryCaller.createSipURI(toUser,
                toSipAddress);

        logger.info("Creating Address 'to'");
        Address toNameAddress = addressFactoryCaller.createAddress(toAddress);
        toNameAddress.setDisplayName(toDisplayName);

        logger.info("Creating ToHeader");
        ToHeader toHeader = headerFactoryCaller.createToHeader(toNameAddress, null);

        // create Request URI
        logger.info("Creating RequestURI");
        SipURI requestURI = addressFactoryCaller.createSipURI(toUser,
                m_sPeerHostPort);

        // Create ViaHeaders
        logger.info("Creating ViaHeaders");
        ArrayList viaHeaders = new ArrayList();
        logger.info("Arraylist created");
        int port = this.m_SIPProviderToUse.getListeningPoint().getPort();
        logger.info("Port: " + port);
        ViaHeader viaHeader = headerFactoryCaller.createViaHeader(
                m_ServerSIPStack.getIPAddress(),
                this.m_SIPProviderToUse.getListeningPoint().getPort(),
                m_sTransport,
                null);
        logger.info("ViaHeaders filled");

        // add via headers
        viaHeaders.add(viaHeader);

        // Create ContentTypeHeader
//            ContentTypeHeader contentTypeHeader = headerFactoryCaller.createContentTypeHeader("application", "sdp");

        // Create a new CallId header
        logger.info("Creating CallIDHeader");
        CallIdHeader callIdHeader = m_SIPProviderToUse.getNewCallId();

        // Create a new Cseq header
        logger.info("Creating CSeqHeader");
        CSeqHeader cSeqHeader = headerFactoryCaller.createCSeqHeader(1,
                Request.INVITE);

        // Create a new MaxForwardsHeader
        logger.info("Creating MaxForwardsHeader");
        MaxForwardsHeader maxForwards = headerFactoryCaller.
                                        createMaxForwardsHeader(70);

        // Create the request.
        Request request;
        logger.info("Creating Request");
        if (requestMethod.equals("INVITE")) {
            request = messageFactoryCaller.createRequest(
                    requestURI,
                    Request.INVITE,
                    callIdHeader,
                    cSeqHeader,
                    fromHeader,
                    toHeader,
                    viaHeaders,
                    maxForwards);
            logger.info("Sending Invite to: " + m_sPeerHostPort);
        } else if (requestMethod.equals("ACK")) {
            request = messageFactoryCaller.createRequest(
                    requestURI,
                    Request.ACK,
                    callIdHeader,
                    cSeqHeader,
                    fromHeader,
                    toHeader,
                    viaHeaders,
                    maxForwards);
            logger.info("Sending ACK to: " + m_sPeerHostPort);
        } else if (requestMethod.equals("BYE")) {
            request = messageFactoryCaller.createRequest(
                    requestURI,
                    Request.BYE,
                    callIdHeader,
                    cSeqHeader,
                    fromHeader,
                    toHeader,
                    viaHeaders,
                    maxForwards);
            logger.info("Sending BYE to: " + m_sPeerHostPort);
        } else if (requestMethod.equals("UPDATE")) {
            request = messageFactoryCaller.createRequest(
                    requestURI,
                    Request.UPDATE,
                    callIdHeader,
                    cSeqHeader,
                    fromHeader,
                    toHeader,
                    viaHeaders,
                    maxForwards);
            logger.info("Sending UPDATE to: " + m_sPeerHostPort);
        } else {
            request = messageFactoryCaller.createRequest(
                    requestURI,
                    Request.CANCEL,
                    callIdHeader,
                    cSeqHeader,
                    fromHeader,
                    toHeader,
                    viaHeaders,
                    maxForwards);
            logger.info("Sending CANCEL to: " + m_sPeerHostPort);
        }
        /**
                    // Create contact headers
                    String host = m_ServerSIPStack.getIPAddress();
         SipURI contactUrl = addressFactoryCaller.createSipURI(fromUser, host);
                    contactUrl.setPort(m_TCPListeningPoint.getPort());

                    // Create the contact name address.
         SipURI contactURI = addressFactoryCaller.createSipURI(fromUser, host);
         contactURI.setPort(m_SIPProviderToUse.getListeningPoint().getPort());
         Address contactAddress = addressFactoryCaller.createAddress(contactURI);

                    // Add the contact address.
                    contactAddress.setDisplayName(fromUser);

         m_ContactHeader = headerFactoryCaller.createContactHeader(contactAddress);
                    request.addHeader(m_ContactHeader);

                    // Add the extension header.
                    Header extensionHeader = headerFactoryCaller.createHeader("My-Header", "my header value");
                    request.addHeader(extensionHeader);

                    String sdpData =
                              "v=0\r\n"
         + "o=4855 13760799956958020 13760799956958020 IN IP4  129.6.55.78\r\n"
                            + "s=mysession session\r\n"
                            + "p=+46 8 52018010\r\n"
                            + "c=IN IP4  129.6.55.78\r\n"
                            + "t=0 0\r\n"
                            + "m=audio 6022 RTP/AVP 0 4 18\r\n"
                            + "a=rtpmap:0 PCMU/8000\r\n"
                            + "a=rtpmap:4 G723/8000\r\n"
                            + "a=rtpmap:18 G729A/8000\r\n"
                            + "a=ptime:20\r\n";

                    request.setContent(sdpData, contentTypeHeader);

                    extensionHeader = headerFactoryCaller.createHeader(
                                    "My-Other-Header",
                                    "my new header value ");
                    request.addHeader(extensionHeader);

                    Header callInfoHeader = headerFactoryCaller.createHeader(
                                    "Call-Info",
                                    "<http://www.antd.nist.gov>");
                    request.addHeader(callInfoHeader);
         */
        // Create the client transaction.
        m_ListenerCaller.clientTid = m_SIPProviderToUse.getNewClientTransaction(
                request);

        // send the request out.
        m_ListenerCaller.clientTid.sendRequest();
    }

    /**
     * Wait until the thread is not working and then remove the SIP-Stack
     * by removing the listeners, deleting the providers and set the variables
     * to NULL
     *
     * @return boolean
     * @throws Exception
     */
    public boolean stopAndRemoveSIPStack() {
        if(this.m_ServerSIPStack != null){
            try {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                this.m_ServerSIPStack.deleteListeningPoint(this.m_UDPListeningPoint);
                this.m_ServerSIPStack.deleteListeningPoint(this.m_TCPListeningPoint);
                // This will close down the stack and exit all threads
                this.m_ServerSIPProviderTCP.removeSipListener(this);
                this.m_ServerSIPProviderUDP.removeSipListener(this);
                while (true) {
                    try {
                        this.m_ServerSIPStack.deleteSipProvider(this.m_ServerSIPProviderTCP);
                        this.m_ServerSIPStack.deleteSipProvider(this.m_ServerSIPProviderUDP);
                        this.m_ServerSIPStack.deleteSipProvider(this.m_SIPProviderToUse);
                        break;
                    } catch (ObjectInUseException ex) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
                this.m_ServerSIPStack = null;
                this.m_ServerSIPProviderTCP = null;
                this.m_ServerSIPProviderUDP = null;
                this.m_SIPProviderToUse = null;
                this.clientTid = null;
                this.m_ContactHeader = null;
                this.addressFactoryCaller = null;
                this.headerFactoryCaller = null;
                this.messageFactoryCaller = null;
                this.m_UDPListeningPoint = null;
                this.m_TCPListeningPoint = null;
                System.gc();
            } catch (Exception ex) {
                logger.error("Exception beim löschen des SIP-Stack abgefangen"
                             + ex.getMessage());
            }
            logger.info("Listener, Provider und Factories gelöscht");
            return true;
        }else{
            logger.info("unable to remove Listener, Provider and Factories, no SIPStack");
            return false;
        }
    }

    /**
     * Don't need this method on server
     *
     * @param responseReceivedEvent ResponseEvent
     */
    public void processResponse(ResponseEvent responseReceivedEvent){

    }

    /**
     * Don't need this method on server
     *
     * @param timeoutEvent TimeoutEvent
     */
    public void processTimeout(javax.sip.TimeoutEvent timeoutEvent) {

    }

    /**
     * If there ist someon who ask for options!
     *
     * @param requestEvent RequestEvent
     * @param serverTransaction ServerTransaction
     */
    /**
    public void processOptions(RequestEvent requestEvent, ServerTransaction serverTransaction){
        // userdialog.stdOutput(requestEvent.getRequest().toString());
        try {
            Response response = messageFactoryCaller.createResponse(200, request);
            AllowHeader allowHeader = headerFactoryCaller.createAllowHeader("INVITE");
            response.addHeader(allowHeader);
            allowHeader = headerFactoryCaller.createAllowHeader("BYE");
            response.addHeader(allowHeader);
            allowHeader = headerFactoryCaller.createAllowHeader("OPTIONS");
            response.addHeader(allowHeader);
            serverTransaction.sendResponse(response);
        }catch(Exception ex){
                ex.printStackTrace();
                // userdialog.errOutput("Exception bei processOptions...");
        }
    }
*/
    public void processRequest(RequestEvent requestEvent){

    }

    public String getPeerHostPort()
    {
        return this.m_sPeerHostPort;
    }

    public String getSipStackAdress()
    {
        return this.m_ServerSIPStack.toString();
    }

}
