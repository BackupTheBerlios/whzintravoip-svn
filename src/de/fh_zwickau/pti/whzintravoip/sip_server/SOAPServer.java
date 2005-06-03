package de.fh_zwickau.pti.whzintravoip.sip_server;

public interface SOAPServer {
    public String initCall(String InviterIP, String RecipientIP);
    public String makeCall(String InviterIP);
    public String acceptCall(String RecipientIP);
    public String denyCall(String fromIP, boolean isInviter);
    public String endCall(String fromIP, boolean isInviter);
}
