package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP - SIP-Stack</p>
 *
 * <p>Description: Receiver-SIP-Stack f�r Thin-Client</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Y. Schumann
 * @version 0.0.2
 */

import java.util.*;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;

public class SIPStack implements SipListener {
    public  static SipStack m_SIPStack;
    private static SipFactory m_SIPFactory;
    private static AddressFactory m_AddressFactory;
    private static MessageFactory m_MessageFactory;
    private static HeaderFactory m_HeaderFactory;

    // classes etc.
    private static ThinClientGUI m_UserGUI;
    private static SIPStack m_ListenerSIPStack;

    // Providers
    private static SipProvider m_SIPProviderUDP;
    private static SipProvider m_SIPProviderTCP;
    private static SipProvider m_SIPProviderToUse;

    protected ServerTransaction m_ServerTid;
    protected ClientTransaction m_ClientTid;
    protected ServerTransaction m_ServerTransactionFromInvite;
    private ServerTransaction m_ServerTransaction;

    // Listeningpoints
    private ListeningPoint m_UDPListeningPoint;
    private ListeningPoint m_TCPListeningPoint;

    private Dialog dialog;

    private static final byte LOGIN    = 1;
    private static final byte PICKUP   = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING  = 5;
    private static final byte TALKING  = 6;

    private Request m_Request;
    private RequestEvent m_RequestEvent;


//    private enum Status {
//        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
//    }

    class ApplicationData {
        protected int ackCount;
    }

    private ContactHeader m_ContactHeader;
    private String m_sTransport;
    private String m_sPeerHostPort;
    private String m_sClientSIPStackName = "Client_SIP_Stack";
    private int m_iClientSIPPort = 5070;

//    private static String PEER_ADDRESS = Shootme.myAddress;
    public static String m_sMyAddress = "127.0.0.1";

    /**
     * Constuctor for the receiver.
     * @param dialog SIPConnector ..
     * @param myIP String .. the own IP
     */
    public SIPStack(ThinClientGUI dialog, String myIP) {
        m_UserGUI = dialog;
        m_sMyAddress = myIP;
        try{
            initReceiverSIPStack();
            initReceiverFactories();
        }catch(Exception ex){
                m_UserGUI.errOutput("Exception beim Initialisieren");
        }
    }

    /**
     * Initialize the SIP-Stack with the necessary properties
     *
     * @return boolean
     * @throws Exception
     */
    public boolean initReceiverSIPStack() throws Exception {
        m_UserGUI.stdOutput("Using Port " + m_iClientSIPPort);
        m_SIPStack = null;
        m_SIPFactory = null;
        m_SIPFactory = SipFactory.getInstance();
        m_SIPFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", m_sMyAddress);
        properties.setProperty("javax.sip.RETRANSMISSION_FILTER", "true");
        properties.setProperty("javax.sip.STACK_NAME", m_sClientSIPStackName);
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
                               "sippacketreceiver-debug.txt");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
                               "sippacketreceiver-log.txt");
        // You need  16 for logging traces. 32 for debug + traces.
        // Your code will limp at 32 but it is best for debugging.
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "32");
        // Guard against starvation.
        properties.setProperty("gov.nist.javax.sip.READ_TIMEOUT", "1000");
        // properties.setProperty("gov.nist.javax.sip.MAX_MESSAGE_SIZE", "4096");
        properties.setProperty("gov.nist.javax.sip.CACHE_SERVER_CONNECTIONS", "false");

        m_SIPStack = m_SIPFactory.createSipStack(properties);     // this can throw an exception
        return true;
    }

    /**
     * Initialize the necessary Factories and
     * create the listeningpoints for UDP and TCP
     *
     * @return boolean
     * @throws Exception
     */
    public boolean initReceiverFactories() throws Exception {
    m_HeaderFactory = m_SIPFactory.createHeaderFactory();
        m_AddressFactory = m_SIPFactory.createAddressFactory();
        m_MessageFactory = m_SIPFactory.createMessageFactory();
        m_UDPListeningPoint = m_SIPStack.createListeningPoint(m_iClientSIPPort, "udp");
        m_TCPListeningPoint = m_SIPStack.createListeningPoint(m_iClientSIPPort, "tcp");
        m_ListenerSIPStack = this;
        m_SIPProviderUDP = m_SIPStack.createSipProvider(m_UDPListeningPoint);
        m_UserGUI.stdOutput("udp provider (client): " + m_SIPProviderUDP);
        m_SIPProviderUDP.addSipListener(m_ListenerSIPStack);
        m_SIPProviderTCP = m_SIPStack.createSipProvider(m_TCPListeningPoint);
        m_UserGUI.stdOutput("tcp provider (client): " + m_SIPProviderTCP);
        m_SIPProviderTCP.addSipListener(m_ListenerSIPStack);
        m_UserGUI.stdOutput("Factories, Listener und Provider f�r Client angelegt");
        return true;
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
        if(m_SIPStack != null){
            try {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                m_UserGUI.stdOutput("l�sche Referenzen f�r Client");
                m_SIPStack.deleteListeningPoint(m_UDPListeningPoint);
                m_SIPStack.deleteListeningPoint(m_TCPListeningPoint);
                // This will close down the stack and exit all threads
                m_SIPProviderTCP.removeSipListener(this);
                m_SIPProviderUDP.removeSipListener(this);
                while (true) {
                    try {
                        m_SIPStack.deleteSipProvider(
                                m_SIPProviderTCP);
                        m_SIPStack.deleteSipProvider(
                                m_SIPProviderUDP);
                        break;
                    } catch (ObjectInUseException ex) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
                m_SIPStack = null;
                m_SIPProviderTCP = null;
                m_SIPProviderUDP = null;
                m_SIPProviderToUse = null;
                this.m_ServerTid = null;
                this.m_ContactHeader = null;
                m_AddressFactory = null;
                m_HeaderFactory = null;
                m_MessageFactory = null;
                this.m_UDPListeningPoint = null;
                this.m_TCPListeningPoint = null;
                //            this.reInviteCount = 0;
                System.gc();
            } catch (Exception ex) {
                m_UserGUI.errOutput(
                        "Exception beim l�schen des SIP-Stack abgefangen");
                ex.printStackTrace();
            }
            m_UserGUI.stdOutput("Listener, Provider und Factories gel�scht");
            return true;
        }else{
            return false;
        }
    }

    /**
     * This method will send an 100 (Trying) back to the caller. We need this
     * to let the server know, that the request has arrived and the server stops
     * to send new requests.
     *
     * @param requestEvent RequestEvent
     * @param serverTransaction ServerTransaction
     */
    public void answerRequest(RequestEvent requestEvent, ServerTransaction serverTransaction) {
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        m_UserGUI.stdOutput("Got an REQUEST"
                             + "\n-------------------------- This is the request:\n"
                             + request
                             + "\n-------------------------- This was the request\n");
        try {
            m_UserGUI.stdOutput("sending 100 (Trying)\n");
            Response response = m_MessageFactory.createResponse(100, request);
            ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
            toHeader.setTag("4321"); // Application is supposed to set.
            Address address = m_AddressFactory.createAddress("VoIP-Client <sip:"
                    + m_sMyAddress + ":"
//                    + m_iMyPort + ">");
                    + m_iClientSIPPort + "> \n");
            ContactHeader contactHeader = m_HeaderFactory.createContactHeader(address);
            response.addHeader(contactHeader);
            ServerTransaction st = requestEvent.getServerTransaction();

            if (st == null) {
                st = sipProvider.getNewServerTransaction(request);
                if (st.getDialog().getApplicationData() == null) {
                    st.getDialog().setApplicationData(new ApplicationData());
                }
            } else {
                // If Server transaction is not null, then
                // this is a re-invite.
                m_UserGUI.stdOutput("This is a RE-INVITE ");
                if (st.getDialog() != dialog) {
                    m_UserGUI.errOutput("Whoopsa Daisy Dialog Mismatch");
                    System.exit(0);
                }
            }
//            serverTransactionFromInvite = st;
            // Thread.sleep(5000);
            /**
            m_UserGUI.stdOutput("got a server transaction: " + st);
            byte[] content = request.getRawContent();
            if (content != null) {
                m_UserGUI.stdOutput(" content = " + new String(content));
                ContentTypeHeader contentTypeHeader =
                        m_HeaderFactory.createContentTypeHeader("application",
                        "sdp");
                m_UserGUI.stdOutput("response = " + response);
                response.setContent(content, contentTypeHeader);
            }
            dialog = st.getDialog();
            if (dialog != null) {
                m_UserGUI.stdOutput("Dialog " + dialog);
                m_UserGUI.stdOutput("Dialog state " + dialog.getState());
            }
             */
            st.sendResponse(response);
            m_UserGUI.stdOutput("\n--- Response 100 (Trying) gesendet ---\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            m_UserGUI.errOutput("Exception bei answerRequest...");
            System.exit(0);
        }
    }
    /**
     * Process the incoming requests
     * known requests at the moment are INVITE, ACK, BYE
     *
     * @param requestEvent RequestEvent
     */
    public void processRequest(RequestEvent requestEvent) {
        this.m_RequestEvent = requestEvent;
        this.m_Request = requestEvent.getRequest();
        this.m_ServerTransaction = requestEvent.getServerTransaction();

//        String callerIP = extractIPFromURI(m_Request.getRequestURI().toString());
        String callerIP = extractIPFromURI(m_Request);

        // Infos ausgeben
        m_UserGUI.stdOutput("\n\nRequest '"
                            + this.m_Request.getMethod().toString()
                            + "' received at '"
                            + m_SIPStack.getStackName()
                            + "'\nwith server transaction id '"
                            + this.m_ServerTransaction
                            + "'\nRequest was from this IP: "
                            + callerIP);
        // the server has to stop sending Requests, so let him know that we
        // receive the request...
        answerRequest(requestEvent, m_ServerTransaction);
        if (m_UserGUI.getStatus() == TALKING) {
            m_UserGUI.stdOutput("Request received but I'm talking at the moment");
//            m_UserGUI.denyCall(callerIP);
        } else if (m_UserGUI.getStatus() != PICKUP) {
            m_UserGUI.stdOutput("Request received but I'm busy at the moment");
//            m_UserGUI.denyCall(callerIP);
        } else if (this.m_Request.getMethod().equals(Request.INVITE)) {
            m_UserGUI.stdOutput("INVITE-Request received");
            m_UserGUI.processIncomingCall(callerIP);
        } else if (this.m_Request.getMethod().equals(Request.ACK)) {
            m_UserGUI.stdOutput("ACK-Request received");
            m_UserGUI.processACKRequest();
        } else if (this.m_Request.getMethod().equals(Request.UPDATE)) {
            m_UserGUI.stdOutput("UPDATE-Request received");
            m_UserGUI.updateUserList();
        } else if (this.m_Request.getMethod().equals(Request.BYE)) {
            m_UserGUI.stdOutput("BYE-Request received");
            m_UserGUI.endCall();
        } else if (this.m_Request.getMethod().equals(Request.OPTIONS)) {
            m_UserGUI.stdOutput("OPTIONS-Request received");
            m_UserGUI.processOptionsRequest();
        }
    }

    /**
     * Process the incoming responses.
     * Sending an ACK if the Response is an OK to an INVITE
     * This method may not be used in the receiver-stack because the ACK
     * is only created by the caller!
     *
     * @param responseReceivedEvent ResponseEvent
     */
    public void processResponse(ResponseEvent responseReceivedEvent){
        m_UserGUI.stdOutput("RESPONSE erhalten --> Darauf erfolgt keine Reaktion...");
    }

    /**
     * If a timeout is occouring, print out some information
     *
     * @param timeoutEvent TimeoutEvent
     */
    public void processTimeout(javax.sip.TimeoutEvent timeoutEvent) {
        m_UserGUI.stdOutput("TIMEOUT erhalten --> Darauf erfolgt keine Reaktion...");
    }

    /**
     * Extrahiert die IP aus dem Request. Es wird nach dem Header "Caller-IP"
     * gesucht und dann dessen Wert ausgelesen.
     *
     * @param request Request - der Reguest
     * @return String - die IP
     */
//    private String extractIPFromURI(String callID) {
    private String extractIPFromURI(Request request) {
        String textOfRequest = request.toString();
        Header header = request.getHeader("Caller-IP");
        m_UserGUI.stdOutput(textOfRequest);
        String headerField = "127.0.0.1";
        headerField = header.toString();
        m_UserGUI.stdOutput("ausgelesener Header: " + headerField);
        StringTokenizer st = new StringTokenizer(headerField," ");
        String ip = st.nextToken();
        ip = st.nextToken();
        ip = ip.substring(0, (ip.length() - 2));
        return ip;
    }
}
