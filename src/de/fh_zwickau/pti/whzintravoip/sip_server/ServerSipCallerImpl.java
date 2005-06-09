package de.fh_zwickau.pti.whzintravoip.sip_server;

import java.util.*;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;

import org.apache.log4j.*;

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
    private static AddressFactory addressFactoryCaller;
    private static MessageFactory messageFactoryCaller;
    private static HeaderFactory headerFactoryCaller;
    private static SipFactory sipFactoryCaller = null;

    ///////////////////////////////////////////////////////////////////////////
    // classes etc.
    private ServerSipCallerImpl listenerCaller;

    ///////////////////////////////////////////////////////////////////////////
    // Providers
    ///////////////////////////////////////////////////////////////////////////
    private static SipProvider sipProviderUDPCaller;
    private static SipProvider sipProviderTCPCaller;
    private static SipProvider sipProviderToUse;

    ///////////////////////////////////////////////////////////////////////////
    // SIP-Stack
    ///////////////////////////////////////////////////////////////////////////
    private static SipStack sipStackCaller;

    ///////////////////////////////////////////////////////////////////////////
    // Listeningpoints
    ///////////////////////////////////////////////////////////////////////////
    private static ListeningPoint udpListeningPointCaller;
    private static ListeningPoint tcpListeningPointCaller;


    protected static ClientTransaction clientTid;

    ///////////////////////////////////////////////////////////////////////////
    // User related Stuff
    ///////////////////////////////////////////////////////////////////////////
    private static ContactHeader contactHeader;
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
        this.initCallerSIPStack();
        this.initCallerFactories();
        return true;
    }


    /**
     * Initialize the SIP-Stack with the necessary properties
     *
     * @return boolean
     * @throws Exception
     */
    public boolean initCallerSIPStack() throws Exception {
        // userdialog.stdOutput("Using Port " + m_iCallerPort);
        sipStackCaller = null;
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
        sipStackCaller = sipFactoryCaller.createSipStack(properties);     // this can throw an exception
        logger.info("Creating Sip Stack fromIP " + m_sServerAddress);
        logger.info("Creating Sip Stack toIP " + m_sInitReceiverIP);
        logger.info("Sip Stack Impl : " + sipStackCaller.toString());
        return true;
    }



    /**
     * Initialize the necessary Factories and
     * create the listeningpoints for UDP and TCP
     *
     * @return boolean
     * @throws Exception
     */
    public boolean initCallerFactories() throws Exception {
        logger.info("Sip Stack Impl : " + sipStackCaller.toString());
        headerFactoryCaller = sipFactoryCaller.createHeaderFactory();
        addressFactoryCaller = sipFactoryCaller.createAddressFactory();
        messageFactoryCaller = sipFactoryCaller.createMessageFactory();

        logger.info("Header / Adress / Message Factory created!");
        udpListeningPointCaller = sipStackCaller.createListeningPoint(m_iCallerPort, "udp");
        tcpListeningPointCaller = sipStackCaller.createListeningPoint(m_iCallerPort, "tcp");
        logger.info("UDP and TCP Listening Point created from SIPStack: " +
                    sipStackCaller.toString());
        listenerCaller = this;
        sipProviderUDPCaller = sipStackCaller.createSipProvider(udpListeningPointCaller);
        logger.info("udp provider (Caller): " + sipProviderUDPCaller);
        sipProviderUDPCaller.addSipListener(listenerCaller);
        sipProviderTCPCaller = sipStackCaller.createSipProvider(tcpListeningPointCaller);
        logger.info("tcp provider (Caller): " + sipProviderTCPCaller);
        sipProviderTCPCaller.addSipListener(listenerCaller);
        logger.info("Factories, Listener und Provider für Caller angelegt");
        return true;
    }

    /**
     * Set the IP for the Recipient of the Request
     *
     * @param recipientIP String The IP from the Recipient of the Request
     */
    public void setRecipientIPforRequest(String recipientIP)
    {
        m_sPeerHostPort = recipientIP + ":" + m_iReceiverPort;
    }

    /**
     * Invite "the other side" to a call
     */
    public void sendRequest(String requestMethod) throws Exception {
            this.sipProviderToUse = m_sTransport.equalsIgnoreCase("udp") ? sipProviderUDPCaller : sipProviderTCPCaller;

            String fromUser = "Yves";
            String fromSipAddress= "www.fh-zwickau.de/~ys";
            String fromDisplayName = "StarWarsFan";

            String toUser = "Holger";
            String toSipAddress = "www.atknights.dyndns.org";
            String toDisplayName = "Blue Cable Knight";

            // create From Header
            SipURI fromAddress = addressFactoryCaller.createSipURI(fromUser, fromSipAddress);
            Address fromNameAddress = addressFactoryCaller.createAddress(fromAddress);
            fromNameAddress.setDisplayName(fromDisplayName);
            FromHeader fromHeader = headerFactoryCaller.createFromHeader(fromNameAddress, "12345");

            // create To Header
            SipURI toAddress = addressFactoryCaller.createSipURI(toUser, toSipAddress);
            Address toNameAddress = addressFactoryCaller.createAddress(toAddress);
            toNameAddress.setDisplayName(toDisplayName);
            ToHeader toHeader = headerFactoryCaller.createToHeader(toNameAddress, null);

            // create Request URI
            SipURI requestURI = addressFactoryCaller.createSipURI(toUser, m_sPeerHostPort);

            // Create ViaHeaders
            ArrayList viaHeaders = new ArrayList();
            int port = sipProviderToUse.getListeningPoint().getPort();
            ViaHeader viaHeader =
                    headerFactoryCaller.createViaHeader(
                            sipStackCaller.getIPAddress(),
                            sipProviderToUse.getListeningPoint().getPort(),
                            m_sTransport,
                            null);

            // add via headers
            viaHeaders.add(viaHeader);

            // Create ContentTypeHeader
            ContentTypeHeader contentTypeHeader = headerFactoryCaller.createContentTypeHeader("application", "sdp");

            // Create a new CallId header
            CallIdHeader callIdHeader = sipProviderToUse.getNewCallId();

            // Create a new Cseq header
            CSeqHeader cSeqHeader = headerFactoryCaller.createCSeqHeader(1, Request.INVITE);

            // Create a new MaxForwardsHeader
            MaxForwardsHeader maxForwards = headerFactoryCaller.createMaxForwardsHeader(70);

            // Create the request.
            Request request;
            if (requestMethod.equals("INVITE")) {
                request =
                        messageFactoryCaller.createRequest(
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
                request =
                        messageFactoryCaller.createRequest(
                                requestURI,
                                Request.ACK,
                                callIdHeader,
                                cSeqHeader,
                                fromHeader,
                                toHeader,
                                viaHeaders,
                                maxForwards);
                logger.info("Sending ACK to: " + m_sPeerHostPort);
            } else if (requestMethod.equals("BYE")){
                request =
                        messageFactoryCaller.createRequest(
                                requestURI,
                                Request.BYE,
                                callIdHeader,
                                cSeqHeader,
                                fromHeader,
                                toHeader,
                                viaHeaders,
                                maxForwards);
                logger.info("Sending BYE to: " + m_sPeerHostPort);
            } else {
                request =
                        messageFactoryCaller.createRequest(
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

            // Create contact headers
            String host = sipStackCaller.getIPAddress();
            SipURI contactUrl = addressFactoryCaller.createSipURI(fromUser, host);
            contactUrl.setPort(tcpListeningPointCaller.getPort());

            // Create the contact name address.
            SipURI contactURI = addressFactoryCaller.createSipURI(fromUser, host);
            contactURI.setPort(sipProviderToUse.getListeningPoint().getPort());
            Address contactAddress = addressFactoryCaller.createAddress(contactURI);

            // Add the contact address.
            contactAddress.setDisplayName(fromUser);

            contactHeader = headerFactoryCaller.createContactHeader(contactAddress);
            request.addHeader(contactHeader);

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

            // Create the client transaction.
//            listener.inviteTid = sipProvider.getNewClientTransaction(request);
            listenerCaller.clientTid = sipProviderToUse.getNewClientTransaction(request);

            // send the request out.
//            listener.inviteTid.sendRequest();
            listenerCaller.clientTid.sendRequest();


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
        if(sipStackCaller != null){
            try {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                // userdialog.stdOutput("lösche Referenzen für Caller");
                sipStackCaller.deleteListeningPoint(udpListeningPointCaller);
                sipStackCaller.deleteListeningPoint(tcpListeningPointCaller);
                // This will close down the stack and exit all threads
                sipProviderTCPCaller.removeSipListener(this);
                sipProviderUDPCaller.removeSipListener(this);
                while (true) {
                    try {
                        sipStackCaller.deleteSipProvider(sipProviderTCPCaller);
                        sipStackCaller.deleteSipProvider(sipProviderUDPCaller);
                        break;
                    } catch (ObjectInUseException ex) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
                sipStackCaller = null;
                sipProviderTCPCaller = null;
                sipProviderUDPCaller = null;
                this.clientTid = null;
                this.contactHeader = null;
                addressFactoryCaller = null;
                headerFactoryCaller = null;
                messageFactoryCaller = null;
                this.udpListeningPointCaller = null;
                this.tcpListeningPointCaller = null;
                //            this.reInviteCount = 0;
                System.gc();
            } catch (Exception ex) {
                // userdialog.errOutput(
                //        "Exception beim löschen des SIP-Stack abgefangen");
                ex.printStackTrace();
            }
            // userdialog.stdOutput("Listener, Provider und Factories gelöscht");
            // userdialog.jButtonStop.setEnabled(false);
            return true;
        }else{
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
        return this.sipStackCaller.toString();
    }

}
