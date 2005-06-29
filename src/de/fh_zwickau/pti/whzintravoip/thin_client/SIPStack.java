package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Y. Schumann <ys@fh-zwickau.de>
 * @version 0.1.0
 */

import java.util.*;
import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;

public class SIPStack implements SipListener {

    public  static SipStack m_SIPStack;
    private static SIPStack m_ListenerSIPStack;

    // factories
    private static SipFactory m_SIPFactory;
    private static AddressFactory m_AddressFactory;
    private static MessageFactory m_MessageFactory;
    private static HeaderFactory m_HeaderFactory;

    // Providers
    private static SipProvider m_SIPProviderUDP;
    private static SipProvider m_SIPProviderTCP;
    private static SipProvider m_SIPProviderToUse;

    // Transactions
    protected ServerTransaction m_ServerTid;
    protected ClientTransaction m_ClientTid;
    protected ServerTransaction m_ServerTransactionFromInvite;
    private   ServerTransaction m_ServerTransaction;

    // Listeningpoints
    private ListeningPoint m_UDPListeningPoint;
    private ListeningPoint m_TCPListeningPoint;

    // the states of the program
    private static final byte LOGIN    = 1;
    private static final byte PICKUP   = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING  = 5;
    private static final byte TALKING  = 6;

    // classes etc.
    private static ThinClient m_ThinClient;
    private Dialog dialog;
    private Request m_Request;
    private RequestEvent m_RequestEvent;

    class ApplicationData {
        protected int ackCount;
    }

    private String m_sClientSIPStackName = "ThinClientSIPStack";
    private int m_iClientSIPPort = 5070;

    public static String m_sMyIP = "127.0.0.1";

    /**
     * Initializes the SIP-Stack and the Factories
     *
     * @param client ThinClient
     * @param myIP String
     */
    public SIPStack(ThinClient client, String myIP) {
        this.m_ThinClient = client;
        this.m_sMyIP = myIP;
        try {
            initClientSIPStack();
            initClientSIPFactories();
        } catch (Exception ex) {
            m_ThinClient.errOutput("Exception beim Initialisieren des SIP-Stack");
            m_ThinClient.errOutput(ex.toString());
        }
    }

    /**
     * Initialize the SIP-Stack with the necessary properties
     *
     * @return boolean TRUE, actually not used
     * @throws Exception
     */
    public boolean initClientSIPStack() throws Exception {
        m_ThinClient.stdOutput("Using Port " + m_iClientSIPPort);
        m_SIPStack = null;
        m_SIPFactory = null;
        m_SIPFactory = SipFactory.getInstance();
        m_SIPFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", m_sMyIP);
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
        properties.setProperty("gov.nist.javax.sip.CACHE_SERVER_CONNECTIONS",
                               "false");

        m_SIPStack = m_SIPFactory.createSipStack(properties); // this can throw an exception
        return true;
    }

    /**
     * Initialize the necessary Factories and create the listeningpoints for
     * UDP and TCP
     *
     * @return boolean TRUE, actually not used
     * @throws Exception
     */
    public boolean initClientSIPFactories() throws Exception {
        m_HeaderFactory = m_SIPFactory.createHeaderFactory();
        m_AddressFactory = m_SIPFactory.createAddressFactory();
        m_MessageFactory = m_SIPFactory.createMessageFactory();
        m_UDPListeningPoint = m_SIPStack.createListeningPoint(m_iClientSIPPort,
                "udp");
        m_TCPListeningPoint = m_SIPStack.createListeningPoint(m_iClientSIPPort,
                "tcp");
        m_ListenerSIPStack = this;
        m_SIPProviderUDP = m_SIPStack.createSipProvider(m_UDPListeningPoint);
        m_ThinClient.stdOutput("udp provider (client): " + m_SIPProviderUDP);
        m_SIPProviderUDP.addSipListener(m_ListenerSIPStack);
        m_SIPProviderTCP = m_SIPStack.createSipProvider(m_TCPListeningPoint);
        m_ThinClient.stdOutput("tcp provider (client): " + m_SIPProviderTCP);
        m_SIPProviderTCP.addSipListener(m_ListenerSIPStack);
        m_ThinClient.stdOutput(
                "Factories, Listener und Provider für Client angelegt");
        return true;
    }

    /**
     * Wait until the thread is not working and then shut down the SIP-Stack
     * by removing the listeners, deleting the providers and set the variables
     * to NULL
     *
     * @return boolean
     */
    public boolean stopAndRemoveSIPStack() {
        if (m_SIPStack != null) {
            try {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                m_ThinClient.stdOutput("lösche Referenzen für Client");
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
            } catch (Exception ex) {
                m_ThinClient.errOutput(
                        "Exception beim löschen des SIP-Stack abgefangen");
                ex.printStackTrace();
                return false;
            }
            m_SIPStack = null;
            m_SIPProviderTCP = null;
            m_SIPProviderUDP = null;
            m_SIPProviderToUse = null;
            this.m_ServerTid = null;
            m_AddressFactory = null;
            m_HeaderFactory = null;
            m_MessageFactory = null;
            this.m_UDPListeningPoint = null;
            this.m_TCPListeningPoint = null;
            System.gc();
            m_ThinClient.stdOutput("Listener, Provider und Factories gelöscht");
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method will send a Response 100 (Trying) back to the caller. We need
     * this to let the server know, that the request was received and the server
     * now stops to send new requests.
     *
     * @todo Implement sending of responses even if we have no RequestEvent and
     * review the whole method...
     */
    public void answerRequest() {
        SipProvider sipProvider = (SipProvider) m_RequestEvent.getSource();
        Request request = m_RequestEvent.getRequest();
        m_ThinClient.stdOutput("Got an REQUEST");
        try {
            m_ThinClient.stdOutput("sending 100 (Trying)\n");
            Response response = m_MessageFactory.createResponse(100, request);
            ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
            toHeader.setTag("4321"); // Application is supposed to set.
            Address address = m_AddressFactory.createAddress(
                    "VoIP-Client <sip:"
                    + m_sMyIP + ":"
                    + m_iClientSIPPort + "> \n");
            ContactHeader contactHeader = m_HeaderFactory.createContactHeader(
                    address);
            response.addHeader(contactHeader);

            if (m_ServerTransaction == null) {
                m_ServerTransaction = sipProvider.getNewServerTransaction(
                        request);
                if (m_ServerTransaction.getDialog().getApplicationData() == null) {
                    m_ServerTransaction.getDialog().setApplicationData(new
                            ApplicationData());
                }
            } else {
                // If Server transaction is not null, then
                // this is a re-invite.
                m_ThinClient.stdOutput("This is a RE-INVITE ");
                if (m_ServerTransaction.getDialog() != dialog) {
                    m_ThinClient.errOutput("Whoopsa Daisy Dialog Mismatch");
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
            m_ServerTransaction.sendResponse(response);
            m_ThinClient.stdOutput("\n--- Response 100 (Trying) gesendet ---\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            m_ThinClient.errOutput("Exception bei answerRequest...");
            System.exit(0);
        }
    }

    /**
     * Process the incoming requests. Actually it will handle the following
     * requests and calls the responsible methods on ThinClient:
     * INVITE  -> processINCOMINGRequest
     * ACK     -> processACKRequest
     * BYE     -> processBYERequest
     * CANCEL  -> processCANCELRequest
     * UPDATE  -> processUPDATERequest
     * OPTIONS -> processOPTIONSRequest
     *
     * @param requestEvent RequestEvent
     */
    public void processRequest(RequestEvent requestEvent) {
        this.m_RequestEvent = requestEvent;
        this.m_Request = requestEvent.getRequest();
        this.m_ServerTransaction = requestEvent.getServerTransaction();

        String callerIP = extractIPFromURI(m_Request);

        // Infos ausgeben
        m_ThinClient.stdOutput("\n\nRequest:\n"
                               + this.m_Request.getMethod().toString()
                               + "\nreceived at:\n"
                               + m_SIPStack.getStackName()
                               + "\nwith server transaction id:\n"
                               + this.m_ServerTransaction
                               + "\nRequest was from this IP: "
                               + callerIP);

        if ((m_ThinClient.getStatus() == PICKUP) &&
            (this.m_Request.getMethod().equals(Request.INVITE))) {
            m_ThinClient.stdOutput("processing INVITE-Request");
            // the server has to stop sending Requests, so let him know
            // that we received the request...
            answerRequest();
            m_ThinClient.processINVITERequest(callerIP);
        } else if ((m_ThinClient.getStatus() == MAKECALL) &&
                   (this.m_Request.getMethod().equals(Request.ACK))) {
            m_ThinClient.stdOutput("processing ACK-Request");
            m_ThinClient.processACKRequest();
        } else if ((m_ThinClient.getStatus() == MAKECALL) &&
                   (this.m_Request.getMethod().equals(Request.CANCEL))) {
            m_ThinClient.stdOutput("processing CANCEL-Request");
            // the server has to stop sending Requests, so let him know
            // that we received the request...
//            answerRequest();
            m_ThinClient.processCANCELRequest();
        } else if (this.m_Request.getMethod().equals(Request.UPDATE)) {
            m_ThinClient.stdOutput("processing UPDATE-Request");
            m_ThinClient.processUPDATERequest();
        } else if ((m_ThinClient.getStatus() == TALKING) &&
                   (this.m_Request.getMethod().equals(Request.BYE))) {
            m_ThinClient.stdOutput("processing BYE-Request");
            // the server has to stop sending Requests, so let him know
            // that we received the request...
//            answerRequest();
            m_ThinClient.processBYERequest();
        } else if (this.m_Request.getMethod().equals(Request.OPTIONS)) {
            m_ThinClient.stdOutput("processing OPTIONS-Request");
            m_ThinClient.processOPTIONSRequest();
        } else if (m_ThinClient.getStatus() == TALKING) {
            m_ThinClient.stdOutput(
                    "Request received but I'm talking at the moment");
        } else if (m_ThinClient.getStatus() != PICKUP) {
            m_ThinClient.stdOutput(
                    "Request received but I'm busy at the moment");
        }
    }

    /**
     * Process the incoming responses. Actually only a message is written
     * because the ThinClient does not need to receive Responses from the server.
     *
     * @param responseReceivedEvent ResponseEvent
     */
    public void processResponse(ResponseEvent responseReceivedEvent) {
        m_ThinClient.stdOutput(
                "RESPONSE erhalten --> Darauf erfolgt keine Reaktion...");
    }

    /**
     * If a timeout is occouring, print out some information
     *
     * @param timeoutEvent TimeoutEvent
     */
    public void processTimeout(javax.sip.TimeoutEvent timeoutEvent) {
        m_ThinClient.stdOutput(
                "TIMEOUT erhalten --> Darauf erfolgt keine Reaktion...");
    }

    /**
     * Extract the IP out of a Request. The method searches for the Header
     * "Caller-IP" and returns the value of this header
     *
     * @param request Request - the Reguest
     * @return String - the IP
     */
    private String extractIPFromURI(Request request) {
        Header header = request.getHeader("Caller-IP");
        String headerField = "127.0.0.1"; // just for initializing
        headerField = header.toString();
        StringTokenizer st = new StringTokenizer(headerField, " ");
        String ip = st.nextToken();
        ip = st.nextToken();
        ip = ip.substring(0, (ip.length() - 2)); // cut of the LF and CR
        return ip;
    }
}
