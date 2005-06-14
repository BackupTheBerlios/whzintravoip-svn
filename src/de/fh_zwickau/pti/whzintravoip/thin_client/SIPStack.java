package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP</p>
 *
 * <p>Description: </p>
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
    private String m_sReceiverStackName = "Receiver";
    private int m_iCallerPort = 5060;
    private int m_iReceiverPort = 5070;

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

    public SIPStack() {
    }

    public static void main(String[] args) {
//        SIPPacketReceiver sippacketreceiver = new SIPPacketReceiver();
    }

    /**
     * Initialize the SIP-Stack with the necessary properties
     *
     * @return boolean
     * @throws Exception
     */
    public boolean initReceiverSIPStack() throws Exception {
        m_UserGUI.stdOutput("Using Port " + m_iReceiverPort);
        m_SIPStack = null;
        m_SIPFactory = null;
        m_SIPFactory = SipFactory.getInstance();
        m_SIPFactory.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.IP_ADDRESS", m_sMyAddress);
        properties.setProperty("javax.sip.RETRANSMISSION_FILTER", "true");
        properties.setProperty("javax.sip.STACK_NAME", m_sReceiverStackName);

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
        m_UDPListeningPoint = m_SIPStack.createListeningPoint(m_iReceiverPort, "udp");
        m_TCPListeningPoint = m_SIPStack.createListeningPoint(m_iReceiverPort, "tcp");
        m_ListenerSIPStack = this;
        m_SIPProviderUDP = m_SIPStack.createSipProvider(m_UDPListeningPoint);
        m_UserGUI.stdOutput("udp provider (receiver): " + m_SIPProviderUDP);
        m_SIPProviderUDP.addSipListener(m_ListenerSIPStack);
        m_SIPProviderTCP = m_SIPStack.createSipProvider(m_TCPListeningPoint);
        m_UserGUI.stdOutput("tcp provider (receiver): " + m_SIPProviderTCP);
        m_SIPProviderTCP.addSipListener(m_ListenerSIPStack);
        m_UserGUI.stdOutput("Factories, Listener und Provider für Receiver angelegt");
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
                m_UserGUI.stdOutput("lösche Referenzen für Receiver");
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
                        "Exception beim löschen des SIP-Stack abgefangen");
                ex.printStackTrace();
            }
            m_UserGUI.stdOutput("Listener, Provider und Factories gelöscht");
            return true;
        }else{
            return false;
        }
    }

    /**
     * Process the incoming requests
     * known requests at the moment are INVITE, ACK, BYE
     *
     * @param requestEvent RequestEvent
     */
    public void processRequest(RequestEvent requestEvent){
    this.m_RequestEvent = requestEvent;
        // Event holen
//        Request request = requestEvent.getRequest();
        this.m_Request = requestEvent.getRequest();
        // ID feststellen
//        ServerTransaction serverTransactionId = requestEvent.getServerTransaction();
        this.m_ServerTransaction = requestEvent.getServerTransaction();

        // Infos ausgeben
        m_UserGUI.stdOutput("\n\nRequest "
                          + this.m_Request.getMethod().toString()
                          + " received at "
                          + m_SIPStack.getStackName()
                          + " with server transaction id "
                          + this.m_ServerTransaction);

        if (m_UserGUI.getStatus() == TALKING ) {
            m_UserGUI.stdOutput("besetzt");
            m_UserGUI.denyCall();
        } else if (m_UserGUI.getStatus() != PICKUP) {
            m_UserGUI.stdOutput("z. Z. nicht möglich");
            m_UserGUI.denyCall();
        } else if (this.m_Request.getMethod().equals(Request.INVITE)) {
            m_UserGUI.stdOutput("\nRingedingding!!!!!!!!!!!!!!!!!!!\n");
            m_UserGUI.setStatusINCOMING();
            m_UserGUI.setAcceptButtonTrue();
            m_UserGUI.setDenyButtonTrue();
            //** @todo wiederholtes Abspielen des Ringtones */
            m_UserGUI.playRingTone();
        } else if (this.m_Request.getMethod().equals(Request.ACK)) {
            m_UserGUI.stdOutput("\nACK empfangen\n");
            m_UserGUI.callEstablished();
        } else if (this.m_Request.getMethod().equals(Request.BYE)) {
            m_UserGUI.stdOutput("\nBYE empfangen\n");
            m_UserGUI.endCall();
        } else if (this.m_Request.getMethod().equals(Request.OPTIONS)) {
            m_UserGUI.stdOutput("\nOPTIONS empfangen\n");
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
        m_UserGUI.stdOutput("-----------------------Response erhalten\n");
        Response response = (Response) responseReceivedEvent.getResponse();
        Transaction tid = responseReceivedEvent.getClientTransaction();

        m_UserGUI.stdOutput("Response empfangen mit client transaction id "
                         + tid
                         + ":\nResponse:\n"
                         + response
                         + "\n");
        try {
            if (response.getStatusCode() == Response.OK
                && ((CSeqHeader) response.getHeader(CSeqHeader.NAME))
                .getMethod().equals(Request.INVITE)) {
                Dialog dialog = tid.getDialog();
                Request request = dialog.createRequest(Request.ACK);
                dialog.sendAck(request);
                m_UserGUI.stdOutput("-----------------------Ack gesendet\n");
            }
            Dialog dialog = tid.getDialog();
            m_UserGUI.stdOutput("Dialog Status = " + dialog.getState() + "\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * If a timeout is occouring, print out some information
     *
     * @param timeoutEvent TimeoutEvent
     */
    public void processTimeout(javax.sip.TimeoutEvent timeoutEvent) {
        Transaction transaction;
        if (timeoutEvent.isServerTransaction()) {
            transaction = timeoutEvent.getServerTransaction();
        } else {
            transaction = timeoutEvent.getClientTransaction();
        }
        m_UserGUI.errOutput("--------------------------------------");
        m_UserGUI.errOutput("state = " + transaction.getState());
        m_UserGUI.errOutput("dialog = " + transaction.getDialog());
        m_UserGUI.errOutput("dialogState = " + transaction.getDialog().getState());
        m_UserGUI.errOutput("Transaction Time out");
        m_UserGUI.errOutput("--------------------------------------");
    }

    public void processOptions(RequestEvent requestEvent, ServerTransaction serverTransaction){
        m_UserGUI.stdOutput(requestEvent.getRequest().toString());
        try {
            Response response = m_MessageFactory.createResponse(200, m_Request);
            AllowHeader allowHeader = m_HeaderFactory.createAllowHeader("INVITE");
            response.addHeader(allowHeader);
            allowHeader = m_HeaderFactory.createAllowHeader("BYE");
            response.addHeader(allowHeader);
            allowHeader = m_HeaderFactory.createAllowHeader("OPTIONS");
            response.addHeader(allowHeader);
            serverTransaction.sendResponse(response);
        }catch(Exception ex){
                ex.printStackTrace();
                m_UserGUI.errOutput("Exception bei processOptions...");
        }
    }
}
