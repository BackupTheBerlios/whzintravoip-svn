package de.fh_zwickau.pti.whzintravoip.thin_client.sip_comm;
import de.fh_zwickau.pti.whzintravoip.thin_client.*;
import de.fh_zwickau.pti.whzintravoip.thin_client.sip_comm.*;
import java.util.*;
import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;

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
public class SIPReceiver implements SipListener {
    public  static SipStack sipStackReceiver;
    private static SipFactory sipFactoryReceiver;
    private static AddressFactory addressFactoryReceiver;
    private static MessageFactory messageFactoryReceiver;
    private static HeaderFactory headerFactoryReceiver;

    // classes etc.
    private static ThinClientGUI userGUI;
    private static SIPReceiver listenerReceiver;

    // Providers
    private static SipProvider sipProviderUDPReceiver;
    private static SipProvider sipProviderTCPReceiver;
    private static SipProvider sipProviderToUse;

    protected ServerTransaction serverTid;
    protected ClientTransaction clientTid;
    protected ServerTransaction serverTransactionFromInvite;
    private ServerTransaction serverTransaction;

    // Listeningpoints
    private ListeningPoint udpListeningPointReceiver;
    private ListeningPoint tcpListeningPointReceiver;

    private Dialog dialog;

    private static final byte LOGIN    = 1;
    private static final byte PICKUP   = 2;
    private static final byte INCOMING = 3;
    private static final byte MAKECALL = 4;
    private static final byte CALLING  = 5;
    private static final byte TALKING  = 6;

    private Request request;
    private RequestEvent requestEvent;


//    private enum Status {
//        LOGIN, PICKUP, INCOMING, MAKECALL, CALLING, TALKING;
//    }

    class ApplicationData {
        protected int ackCount;
    }

    private ContactHeader contactHeader;
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
    public SIPReceiver(ThinClientGUI dialog, String myIP) {
        userGUI = dialog;
        m_sMyAddress = myIP;
        try{
            initReceiverSIPStack();
            initReceiverFactories();
        }catch(Exception ex){
                userGUI.errOutput("Exception beim Initialisieren");
        }
    }

    public SIPReceiver() {
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
        userGUI.stdOutput("Using Port " + m_iReceiverPort);
        sipStackReceiver = null;
        sipFactoryReceiver = null;
        sipFactoryReceiver = SipFactory.getInstance();
        sipFactoryReceiver.setPathName("gov.nist");
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

        sipStackReceiver = sipFactoryReceiver.createSipStack(properties);     // this can throw an exception
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
    headerFactoryReceiver = sipFactoryReceiver.createHeaderFactory();
        addressFactoryReceiver = sipFactoryReceiver.createAddressFactory();
        messageFactoryReceiver = sipFactoryReceiver.createMessageFactory();
        udpListeningPointReceiver = sipStackReceiver.createListeningPoint(m_iReceiverPort, "udp");
        tcpListeningPointReceiver = sipStackReceiver.createListeningPoint(m_iReceiverPort, "tcp");
        listenerReceiver = this;
        sipProviderUDPReceiver = sipStackReceiver.createSipProvider(udpListeningPointReceiver);
        userGUI.stdOutput("udp provider (receiver): " + sipProviderUDPReceiver);
        sipProviderUDPReceiver.addSipListener(listenerReceiver);
        sipProviderTCPReceiver = sipStackReceiver.createSipProvider(tcpListeningPointReceiver);
        userGUI.stdOutput("tcp provider (receiver): " + sipProviderTCPReceiver);
        sipProviderTCPReceiver.addSipListener(listenerReceiver);
        userGUI.stdOutput("Factories, Listener und Provider für Receiver angelegt");
        return true;
    }

    /**
     * Ask for possibilities of the "other side"
     *
     * @throws Exception
     */
    public void askForOptions() throws Exception {
    try {
            userGUI.stdOutput("Asking for Options: " + dialog);
            Request options = dialog.createRequest(Request.OPTIONS);
//            ClientTransaction ct = sipProviderToUse.getNewClientTransaction(options);
            ClientTransaction ct = sipProviderUDPReceiver.getNewClientTransaction(options);
            dialog.sendRequest(ct);
        } catch (Exception ex) {
            ex.printStackTrace();
            userGUI.errOutput("Exception bei Options-Request...");
            //             System.exit(0);
        }
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
        if(sipStackReceiver != null){
            try {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                userGUI.stdOutput("lösche Referenzen für Receiver");
                sipStackReceiver.deleteListeningPoint(udpListeningPointReceiver);
                sipStackReceiver.deleteListeningPoint(tcpListeningPointReceiver);
                // This will close down the stack and exit all threads
                sipProviderTCPReceiver.removeSipListener(this);
                sipProviderUDPReceiver.removeSipListener(this);
                while (true) {
                    try {
                        sipStackReceiver.deleteSipProvider(
                                sipProviderTCPReceiver);
                        sipStackReceiver.deleteSipProvider(
                                sipProviderUDPReceiver);
                        break;
                    } catch (ObjectInUseException ex) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }
                sipStackReceiver = null;
                sipProviderTCPReceiver = null;
                sipProviderUDPReceiver = null;
                sipProviderToUse = null;
                this.serverTid = null;
                this.contactHeader = null;
                addressFactoryReceiver = null;
                headerFactoryReceiver = null;
                messageFactoryReceiver = null;
                this.udpListeningPointReceiver = null;
                this.tcpListeningPointReceiver = null;
                //            this.reInviteCount = 0;
                System.gc();
            } catch (Exception ex) {
                userGUI.errOutput(
                        "Exception beim löschen des SIP-Stack abgefangen");
                ex.printStackTrace();
            }
            userGUI.stdOutput("Listener, Provider und Factories gelöscht");
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
    this.requestEvent = requestEvent;
        // Event holen
//        Request request = requestEvent.getRequest();
        this.request = requestEvent.getRequest();
        // ID feststellen
//        ServerTransaction serverTransactionId = requestEvent.getServerTransaction();
        this.serverTransaction = requestEvent.getServerTransaction();

        // Infos ausgeben
        userGUI.stdOutput("\n\nRequest "
                          + this.request.getMethod().toString()
                          + " received at "
                          + sipStackReceiver.getStackName()
                          + " with server transaction id "
                          + this.serverTransaction);

        if (userGUI.getStatus() == TALKING ) {
            userGUI.stdOutput("besetzt");
        } else if (userGUI.getStatus() != PICKUP) {
            userGUI.stdOutput("z. Z. nicht möglich");
        } else if (this.request.getMethod().equals(Request.INVITE)) {
            userGUI.stdOutput("\nINVITE empfangen\n");
            userGUI.setAcceptButtonTrue();
//            processInvite(requestEvent, serverTransaction);
        } else if (this.request.getMethod().equals(Request.ACK)) {
            userGUI.stdOutput("\nACK empfangen\n");
//            processAck(requestEvent, serverTransaction);
        } else if (this.request.getMethod().equals(Request.BYE)) {
            userGUI.stdOutput("\nBYE empfangen\n");
//            processBye(requestEvent, serverTransaction);
        } else if (this.request.getMethod().equals(Request.OPTIONS)) {
            userGUI.stdOutput("\nOPTIONS empfangen\n");
//            processOptions(requestEvent, serverTransaction);
        }
    }

    /**
     * Create and send a BYE-Request. If the servertransaction is NULL, a new
     * servertransaction will be created.
     *
     * @param requestEvent RequestEvent
     * @param tid ServerTransaction
     */
    public void sendBye(RequestEvent requestEvent, ServerTransaction tid){
    try {
            userGUI.stdOutput("Sending bye from Receiver: " + tid);

            if (tid == null) {
                tid = sipProviderToUse.getNewServerTransaction(request);
                if (tid.getDialog().getApplicationData() == null) {
                    tid.getDialog().setApplicationData(new ApplicationData());
                }
            }

            Dialog dialog = tid.getDialog();
            Request bye = dialog.createRequest(Request.BYE);
            SipProvider provider;
            provider = sipProviderUDPReceiver;
            ClientTransaction ct = provider.getNewClientTransaction(bye);
            dialog.sendRequest(ct);
        } catch (Exception ex) {
            ex.printStackTrace();
            userGUI.errOutput("Exception bei sendBye...");
            //             System.exit(0);
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
        userGUI.stdOutput("-----------------------Response erhalten\n");
        Response response = (Response) responseReceivedEvent.getResponse();
        Transaction tid = responseReceivedEvent.getClientTransaction();

        userGUI.stdOutput("Response empfangen mit client transaction id "
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
                userGUI.stdOutput("-----------------------Ack gesendet\n");
            }
            Dialog dialog = tid.getDialog();
            userGUI.stdOutput("Dialog Status = " + dialog.getState() + "\n");
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
        userGUI.errOutput("--------------------------------------");
        userGUI.errOutput("state = " + transaction.getState());
        userGUI.errOutput("dialog = " + transaction.getDialog());
        userGUI.errOutput("dialogState = " + transaction.getDialog().getState());
        userGUI.errOutput("Transaction Time out");
        userGUI.errOutput("--------------------------------------");
    }

    /**
     * Process the ACK request.
     *
     * @param requestEvent RequestEvent
     * @param serverTransaction ServerTransaction
     */
    public void processAck(RequestEvent requestEvent, ServerTransaction serverTransaction) {
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        try {
            userGUI.stdOutput("----------------------- SIPPacketReceiver: got an ACK\n " + requestEvent.getRequest());
            int ackCount = ((ApplicationData) dialog.getApplicationData()).ackCount;
            if (ackCount == 1) {
                dialog = serverTid.getDialog();
                this.sendReInvite(sipProvider);
                /*
                 Request byeRequest = dialog.createRequest(Request.BYE);
                 ClientTransaction tr = sipProvider.getNewClientTransaction(byeRequest);
                 userGUI.stdOutput("Receiver: Got an ACK -- sending Bye! ");
                 dialog.sendRequest(tr);
                 System.out.println("Dialog State = " + dialog.getState());
                 */
            } else {
                ((ApplicationData) dialog.getApplicationData()).ackCount++;
            }
            userGUI.stdOutput("Dialog State = " + dialog.getState());
        } catch (Exception ex) {
            ex.printStackTrace();
            userGUI.errOutput("Exception bei processAck...");
            System.exit(0);
        }
    }

    /**
     * Process the invite request.
     *
     * @param requestEvent RequestEvent
     * @param serverTransaction ServerTransaction
     */
    public void processInvite(RequestEvent requestEvent, ServerTransaction serverTransaction) {
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        userGUI.stdOutput("Got an INVITE"
                             + "\n-------------------------- This is the request:\n"
                             + request
                             + "\n-------------------------- This was the request\n");
        try {
            userGUI.stdOutput("SIPPacketReceiver: got an Invite sending 180 (Ringing)\n");
            Response response = messageFactoryReceiver.createResponse(180, request);
            ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
            toHeader.setTag("4321"); // Application is supposed to set.
            Address address = addressFactoryReceiver.createAddress("SIPPacketReceiver <sip:"
                    + m_sMyAddress + ":"
//                    + m_iMyPort + ">");
                    + m_iReceiverPort + "> \n");
            ContactHeader contactHeader = headerFactoryReceiver.createContactHeader(address);
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
                userGUI.stdOutput("This is a RE-INVITE ");
                if (st.getDialog() != dialog) {
                    userGUI.errOutput("Whoopsa Daisy Dialog Mismatch");
                    System.exit(0);
                }
            }
            serverTransactionFromInvite = st;
            // Thread.sleep(5000);
            userGUI.stdOutput("got a server transaction: " + st);
            byte[] content = request.getRawContent();
            if (content != null) {
                userGUI.stdOutput(" content = " + new String(content));
                ContentTypeHeader contentTypeHeader =
                        headerFactoryReceiver.createContentTypeHeader("application",
                        "sdp");
                userGUI.stdOutput("response = " + response);
                response.setContent(content, contentTypeHeader);
            }
            dialog = st.getDialog();
            if (dialog != null) {
                userGUI.stdOutput("Dialog " + dialog);
                userGUI.stdOutput("Dialog state " + dialog.getState());
            }
            st.sendResponse(response);
            userGUI.stdOutput("\n--- Response 180 (Ringing) gesendet ---\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            userGUI.errOutput("Exception bei processInvite...");
            System.exit(0);
        }
    }

    /**
     * Accept the INVITE-Request by sending a "200"-Response. This means the
     * "OK"-Response
     *
     * @param requestEvent RequestEvent
     * @param serverTransactionFromInvite ServerTransaction
     */
    private void acceptCall(RequestEvent requestEvent, ServerTransaction serverTransactionFromInvite) {
    SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        try {
            userGUI.stdOutput("SIPPacketReceiver: got an Invite sending OK now\n");
            Response response = messageFactoryReceiver.createResponse(200, request);
            ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
            toHeader.setTag("4321");
            Address address = addressFactoryReceiver.createAddress("SIPPacketReceiver <sip:"
                    + m_sMyAddress + ":"
                    + m_iReceiverPort + "> \n");
            ContactHeader contactHeader = headerFactoryReceiver.createContactHeader(address);
            response.addHeader(contactHeader);
//            ServerTransaction st = requestEvent.getServerTransaction();
            ServerTransaction st = serverTransactionFromInvite;

            if (st == null) {
                st = sipProvider.getNewServerTransaction(request);
                if (st.getDialog().getApplicationData() == null) {
                    st.getDialog().setApplicationData(new ApplicationData());
                }
            } else {
                // If Server transaction is not null, then
                // this is a re-invite.
                userGUI.stdOutput("This is a RE-INVITE ");
                if (st.getDialog() != dialog) {
                    userGUI.errOutput("Whoopsa Daisy Dialog Mismatch");
                    System.exit(0);
                }
            }
            // Thread.sleep(5000);
            userGUI.stdOutput("got a server transaction: " + st);
            byte[] content = request.getRawContent();
            if (content != null) {
                userGUI.stdOutput(" content = " + new String(content));
                ContentTypeHeader contentTypeHeader =
                        headerFactoryReceiver.createContentTypeHeader("application",
                        "sdp");
                userGUI.stdOutput("response = " + response);
                response.setContent(content, contentTypeHeader);
            }
            dialog = st.getDialog();
            if (dialog != null) {
                userGUI.stdOutput("Dialog " + dialog);
                userGUI.stdOutput("Dialog state " + dialog.getState());
            }
            st.sendResponse(response);
            userGUI.stdOutput("\n--- Response 200 (OK) gesendet --------\n");
            this.serverTid = serverTransactionFromInvite;
        } catch (Exception ex) {
            ex.printStackTrace();
            userGUI.errOutput("Exception bei processInvite...");
            System.exit(0);
        }
    }

    /**
     * sendReInvite
     *
     * @param sipProvider SipProvider
     * @throws Exception
     */
    public void sendReInvite(SipProvider sipProvider) throws Exception {
        Request inviteRequest = dialog.createRequest(Request.INVITE);
        ((SipURI) inviteRequest.getRequestURI()).removeParameter("transport");
        ((ViaHeader) inviteRequest.getHeader(ViaHeader.NAME)).setTransport(
                "udp");
        Address address = addressFactoryReceiver.createAddress
                          ("SIPPacketReceiver <sip:"
                           + m_sMyAddress + ":"
//                           + m_iMyPort + ">");
                           + m_iReceiverPort + ">");
        ContactHeader contactHeader = headerFactoryReceiver.createContactHeader(address);
        inviteRequest.addHeader(contactHeader);
        ClientTransaction ct = sipProvider.getNewClientTransaction(inviteRequest);
        this.clientTid = ct;
        dialog.sendRequest(ct);
        userGUI.stdOutput("\n--- ReInvite Request gesendet -------------\n");
    }

    /**
     * Process the BYE request by sending out the 200-Response (OK)
     *
     * @param requestEvent RequestEvent
     * @param serverTransactionId ServerTransaction
     */
    public void processBye(RequestEvent requestEvent, ServerTransaction serverTransactionId) {
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        try {
            userGUI.stdOutput("SIPPacketReceiver: got a bye, sending OK.");
            Response response;
/**
            if (serverTransactionId == null) {
                serverTransactionId = sipProvider.getNewServerTransaction(request);
                if (serverTransactionId.getDialog().getApplicationData() == null) {
                    serverTransactionId.getDialog().setApplicationData(new ApplicationData());
                }
            }
   **/
            response = messageFactoryReceiver.createResponse(200, request);
            serverTransactionId.sendResponse(response);
            userGUI.stdOutput("\n--- Response 200 nach Bye gesendet ----\n");
            userGUI.stdOutput("Dialog State is " + serverTransactionId.getDialog().getState());
//            this.stopAndRemoveSIPStack();
        } catch (Exception ex) {
            ex.printStackTrace();
            userGUI.errOutput("Exception bei processBye...");
            System.exit(0);
        }
    }

    public void processOptions(RequestEvent requestEvent, ServerTransaction serverTransaction){
        userGUI.stdOutput(requestEvent.getRequest().toString());
        try {
            Response response = messageFactoryReceiver.createResponse(200, request);
            AllowHeader allowHeader = headerFactoryReceiver.createAllowHeader("INVITE");
            response.addHeader(allowHeader);
            allowHeader = headerFactoryReceiver.createAllowHeader("BYE");
            response.addHeader(allowHeader);
            allowHeader = headerFactoryReceiver.createAllowHeader("OPTIONS");
            response.addHeader(allowHeader);
            serverTransaction.sendResponse(response);
        }catch(Exception ex){
                ex.printStackTrace();
                userGUI.errOutput("Exception bei processOptions...");
        }
    }
}
