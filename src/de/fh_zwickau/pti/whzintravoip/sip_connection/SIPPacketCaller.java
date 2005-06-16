package de.fh_zwickau.pti.whzintravoip.sip_connection;
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
public class SIPPacketCaller implements SipListener {
    // Factories
    private static AddressFactory addressFactoryCaller;
    private static MessageFactory messageFactoryCaller;
    private static HeaderFactory headerFactoryCaller;
    private static SipFactory sipFactoryCaller = null;

    // classes etc.
    private static SIPConnector userdialog;
    private static SIPPacketCaller listenerCaller;

    // Providers
    private static SipProvider sipProviderUDPCaller;
    private static SipProvider sipProviderTCPCaller;
    private static SipProvider sipProviderToUse;

    // SIP-Stacks
    public static SipStack sipStackCaller;

    protected ServerTransaction serverTid;
    protected ClientTransaction clientTid;
    protected ServerTransaction serverTransactionFromInvite;
    private ServerTransaction serverTransaction;
    private Request request;
    private RequestEvent requestEvent;


    // Listeningpoints
    private ListeningPoint udpListeningPointCaller;
    private ListeningPoint tcpListeningPointCaller;

    private Dialog m_Dialog;

    class ApplicationData {
        protected int ackCount;
    }

    private ContactHeader contactHeader;
    private String m_sTransport;
    private String m_sPeerHostPort;
    private String m_sCallerStackName = "Caller";
    private int m_iCallerPort = 5060;
    private int m_iReceiverPort = 5070;

//    private static String PEER_ADDRESS = Shootme.myAddress;

//    public static final String m_sMyAddress = "127.0.0.1";
    public static String m_sMyAddress = "127.0.0.1";
    public static String m_sOtherAddress = "127.0.0.1";

    public SIPPacketCaller(SIPConnector dialog, String myIP, String callIP) {
        this.userdialog = dialog;
        m_sMyAddress = myIP;
        m_sOtherAddress = callIP;
    }

    public SIPPacketCaller() {
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
    public boolean initCallerSIPStack() throws Exception {
        userdialog.stdOutput("Using Port " + m_iCallerPort);
        sipStackCaller = null;
        sipFactoryCaller = null;
        sipFactoryCaller = SipFactory.getInstance();
        sipFactoryCaller.setPathName("gov.nist");
        Properties properties = new Properties();
        properties.setProperty("javax.sip.RETRANSMISSION_FILTER", "true");
        properties.setProperty("javax.sip.STACK_NAME", m_sCallerStackName);
//        properties.setProperty("javax.sip.ROUTER_PATH", "de.fh_zwickau.pti.whzintravoip.sip_connection.MyRouter");
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
        m_sTransport = "udp";
        m_sPeerHostPort = m_sOtherAddress + ":" + m_iReceiverPort;
        properties.setProperty("javax.sip.IP_ADDRESS", m_sMyAddress);
//        properties.setProperty("javax.sip.OUTBOUND_PROXY", m_sPeerHostPort + "/" + m_sTransport);

        sipStackCaller = sipFactoryCaller.createSipStack(properties);     // this can throw an exception

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
        headerFactoryCaller = sipFactoryCaller.createHeaderFactory();
        addressFactoryCaller = sipFactoryCaller.createAddressFactory();
        messageFactoryCaller = sipFactoryCaller.createMessageFactory();
        udpListeningPointCaller = sipStackCaller.createListeningPoint(m_iCallerPort, "udp");
        tcpListeningPointCaller = sipStackCaller.createListeningPoint(m_iCallerPort, "tcp");
        listenerCaller = this;
        sipProviderUDPCaller = sipStackCaller.createSipProvider(udpListeningPointCaller);
        userdialog.stdOutput("udp provider (Caller): " + sipProviderUDPCaller);
        sipProviderUDPCaller.addSipListener(listenerCaller);
        sipProviderTCPCaller = sipStackCaller.createSipProvider(tcpListeningPointCaller);
        userdialog.stdOutput("tcp provider (Caller): " + sipProviderTCPCaller);
        sipProviderTCPCaller.addSipListener(listenerCaller);
        userdialog.stdOutput("Factories, Listener und Provider für Caller angelegt");
        return true;
    }

    /**
     * Invite "the other side" to a call
     */
    public void inviteNow() {
        try {
            this.sipProviderToUse = m_sTransport.equalsIgnoreCase("udp") ? sipProviderUDPCaller : sipProviderTCPCaller;

            String fromName = "Yves";
            String fromSipAddress= "www.fh-zwickau.de/~ys";
            String fromDisplayName = "StarWarsFan";

            String toUser = "Holger";
            String toSipAddress = "www.atknights.dyndns.org";
            String toDisplayName = "Blue Cable Knight";

            // create From Header
            SipURI fromAddress = addressFactoryCaller.createSipURI(fromName, fromSipAddress);
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
            Request request =
                    messageFactoryCaller.createRequest(
                            requestURI,
                            Request.INVITE,
                            callIdHeader,
                            cSeqHeader,
                            fromHeader,
                            toHeader,
                            viaHeaders,
                            maxForwards);
            // Create contact headers
            String host = sipStackCaller.getIPAddress();
            SipURI contactUrl = addressFactoryCaller.createSipURI(fromName, host);
            contactUrl.setPort(tcpListeningPointCaller.getPort());

            // Create the contact name address.
            SipURI contactURI = addressFactoryCaller.createSipURI(fromName, host);
            contactURI.setPort(sipProviderToUse.getListeningPoint().getPort());
            Address contactAddress = addressFactoryCaller.createAddress(contactURI);

            // Add the contact address.
            contactAddress.setDisplayName(fromName);

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

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Ask for possibilities of the "other side"
     *
     * @throws Exception
     */
    public void askForOptions() throws Exception {
        if(m_Dialog != null){
            try {
                userdialog.stdOutput("Asking for Options: " + m_Dialog);
                Request options = m_Dialog.createRequest(Request.OPTIONS);
                ClientTransaction ct = sipProviderToUse.getNewClientTransaction(
                        options);
                m_Dialog.sendRequest(ct);
            } catch (Exception ex) {
                ex.printStackTrace();
                userdialog.errOutput("Exception bei Options-Request...");
                //             System.exit(0);
            }
        }else{
            try {
                this.sipProviderToUse = m_sTransport.equalsIgnoreCase("udp") ? sipProviderUDPCaller : sipProviderTCPCaller;

                String fromName = "Yves";
                String fromSipAddress= "www.fh-zwickau.de/~ys";
                String fromDisplayName = "StarWarsFan";

                String toUser = "Holger";
                String toSipAddress = "www.atknights.dyndns.org";
                String toDisplayName = "Blue Cable Knight";

                // create From Header
                SipURI fromAddress = addressFactoryCaller.createSipURI(fromName, fromSipAddress);
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
//                ContentTypeHeader contentTypeHeader = headerFactoryCaller.createContentTypeHeader("application", "sdp");

                // Create a new CallId header
                CallIdHeader callIdHeader = sipProviderToUse.getNewCallId();

                // Create a new Cseq header
                CSeqHeader cSeqHeader = headerFactoryCaller.createCSeqHeader(1, Request.UPDATE);

                // Create a new MaxForwardsHeader
                MaxForwardsHeader maxForwards = headerFactoryCaller.createMaxForwardsHeader(70);

                // Create the request.
                Request request =
                        messageFactoryCaller.createRequest(
                                requestURI,
                                Request.UPDATE,
                                callIdHeader,
                                cSeqHeader,
                                fromHeader,
                                toHeader,
                                viaHeaders,
                                maxForwards);
                // Create contact headers
/**                String host = sipStackCaller.getIPAddress();
                SipURI contactUrl = addressFactoryCaller.createSipURI(fromName, host);
                contactUrl.setPort(tcpListeningPointCaller.getPort());

                // Create the contact name address.
                SipURI contactURI = addressFactoryCaller.createSipURI(fromName, host);
                contactURI.setPort(sipProviderToUse.getListeningPoint().getPort());
                Address contactAddress = addressFactoryCaller.createAddress(contactURI);

                // Add the contact address.
                contactAddress.setDisplayName(fromName);

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
   */
                // Create the client transaction.
                listenerCaller.clientTid = sipProviderToUse.getNewClientTransaction(request);

                // send the request out.
                listenerCaller.clientTid.sendRequest();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
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
        if(sipStackCaller != null){
            try {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                userdialog.stdOutput("lösche Referenzen für Caller");
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
                userdialog.errOutput(
                        "Exception beim löschen des SIP-Stack abgefangen");
                ex.printStackTrace();
            }
            userdialog.stdOutput("Listener, Provider und Factories gelöscht");
            userdialog.jButtonStop.setEnabled(false);
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
        userdialog.stdOutput("\n\nRequest "
                             + this.request.getMethod().toString()
                             + " received at "
                             + sipStackCaller.getStackName()
                             + " with server transaction id "
                             + this.serverTransaction);

        if (this.request.getMethod().equals(Request.INVITE)) {
            userdialog.stdOutput("\nINVITE empfangen\n");
 //           processInvite(requestEvent, serverTransaction);
 //           userdialog.jButtonAcceptCall.setEnabled(true);
        } else if (this.request.getMethod().equals(Request.ACK)) {
            userdialog.stdOutput("\nACK empfangen\n");
            processAck(requestEvent, serverTransaction);
        } else if (this.request.getMethod().equals(Request.BYE)) {
            userdialog.stdOutput("\nBYE empfangen\n");
            processBye(requestEvent, serverTransaction);
        } else if (this.request.getMethod().equals(Request.OPTIONS)) {
            userdialog.stdOutput("\nOPTIONS empfangen\n");
            processOptions(requestEvent, serverTransaction);
        }
    }

    /**
     * call the sendBye-method
     */
    public void sendBye(){
        sendBye(m_Dialog, sipProviderToUse);
    }

    /**
     * create a BYE-request to terminate the dialog and send the request out
     *
     * @param dialog Dialog
     * @param sipProviderToUse SipProvider
     */
    public void sendBye(Dialog dialog, SipProvider sipProviderToUse){
        try {
            userdialog.stdOutput("Sending bye from Caller: " + dialog);
            Request bye = dialog.createRequest(Request.BYE);
            ClientTransaction ct = sipProviderToUse.getNewClientTransaction(bye);
            dialog.sendRequest(ct);
        } catch (Exception ex) {
            ex.printStackTrace();
            userdialog.errOutput("Exception bei sendBye...");
            //             System.exit(0);
        }
    }

    /**
     * process the BYE-request and set the Invite-button to visible
     */
    public void processBye(){
        processBye(requestEvent, serverTransaction);
        userdialog.jButtonInvite.setEnabled(true);
    }

    /**
     * Process the incoming responses.
     * Sending an ACK if the Response is an OK to an INVITE
     *
     * @param responseReceivedEvent ResponseEvent
     */
    public void processResponse(ResponseEvent responseReceivedEvent){
        userdialog.stdOutput("-----------------------Response erhalten\n");
        Response response = (Response) responseReceivedEvent.getResponse();
        Transaction tid = responseReceivedEvent.getClientTransaction();

        userdialog.stdOutput("Response empfangen mit client transaction id "
                         + tid
                         + ":\nResponse:\n"
                         + response
                         + "\n");
        try {
            if (response.getStatusCode() == Response.OK
                && ((CSeqHeader) response.getHeader(CSeqHeader.NAME))
                .getMethod().equals(Request.INVITE)) {
                Dialog dialog = tid.getDialog();
                this.m_Dialog = dialog;        // wird benötigt für BYE
                Request request = dialog.createRequest(Request.ACK);
                dialog.sendAck(request);
                userdialog.stdOutput("-----------------------Ack gesendet\n");
                userdialog.jButtonSendBye.setEnabled(true);
                userdialog.jButtonTalk.setEnabled(true);
                userdialog.jButtonInvite.setEnabled(false);
                userdialog.jButtonOptions.setEnabled(true);
            }
            Dialog dialog = tid.getDialog();
            userdialog.stdOutput("Dialog Status = " + dialog.getState() + "\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * print out some information if a timeout is reached
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
        userdialog.errOutput("--------------------------------------");
        userdialog.errOutput("state = " + transaction.getState());
        userdialog.errOutput("dialog = " + transaction.getDialog());
        userdialog.errOutput("dialogState = " + transaction.getDialog().getState());
        userdialog.errOutput("Transaction Time out");
        userdialog.errOutput("--------------------------------------");
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
            userdialog.stdOutput("\n----------------------- SIPPacketReceiver: got an ACK\n " + requestEvent.getRequest());
            int ackCount = ((ApplicationData) m_Dialog.getApplicationData()).ackCount;
            if (ackCount == 1) {
                m_Dialog = serverTid.getDialog();
//                this.sendReInvite(sipProvider);
                /*
                 Request byeRequest = dialog.createRequest(Request.BYE);
                 ClientTransaction tr = sipProvider.getNewClientTransaction(byeRequest);
                 userdialog.stdOutput("Receiver: Got an ACK -- sending Bye! ");
                 dialog.sendRequest(tr);
                 System.out.println("Dialog State = " + dialog.getState());
                 */
            } else {
                ((ApplicationData) m_Dialog.getApplicationData()).ackCount++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            userdialog.errOutput("Exception bei processAck...");
            System.exit(0);
        }
    }

    /**
     * sendReInvite
     *
     * @param sipProvider SipProvider
     * @throws Exception
     */
/**
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
        userdialog.stdOutput("\n--- ReInvite Request gesendet -------------\n");
    }
**/
    /**
     * Process the BYE request.
     *
     * @param requestEvent RequestEvent
     * @param serverTransactionId ServerTransaction
     */
    public void processBye(RequestEvent requestEvent, ServerTransaction serverTransactionId) {
        SipProvider sipProvider = (SipProvider) requestEvent.getSource();
        Request request = requestEvent.getRequest();
        try {
            userdialog.stdOutput("SIPPacketReceiver: got a bye, sending OK.");
            Response response;
/**
            if (serverTransactionId == null) {
                serverTransactionId = sipProvider.getNewServerTransaction(request);
                if (serverTransactionId.getDialog().getApplicationData() == null) {
                    serverTransactionId.getDialog().setApplicationData(new ApplicationData());
                }
            }
**/
            response = messageFactoryCaller.createResponse(200, request);
            serverTransactionId.sendResponse(response);
            userdialog.stdOutput("\n--- Response 200 nach Bye gesendet ----\n");
            userdialog.stdOutput("Dialog State is " + serverTransactionId.getDialog().getState());
//            this.stopAndRemoveSIPStack();
        } catch (Exception ex) {
            ex.printStackTrace();
            userdialog.errOutput("Exception bei processBye...");
            System.exit(0);
        }
        userdialog.jButtonInitFactories.setEnabled(false);
        userdialog.jButtonInitSIPStack.setEnabled(false);
        userdialog.setAllSessionButtonsFalse();
        userdialog.jButtonIamCaller.setEnabled(true);
        userdialog.jButtonIamReceiver.setEnabled(true);
    }

    public void processOptions(RequestEvent requestEvent, ServerTransaction serverTransaction){
        userdialog.stdOutput(requestEvent.getRequest().toString());
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
                userdialog.errOutput("Exception bei processOptions...");
        }
    }
}
