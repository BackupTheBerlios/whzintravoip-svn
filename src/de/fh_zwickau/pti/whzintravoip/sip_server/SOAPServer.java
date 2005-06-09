package de.fh_zwickau.pti.whzintravoip.sip_server;

import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

public interface SOAPServer {
    public String initCall(User aUser);
    public String makeCall(String inviterIP, String recipientIP);
    public String acceptCall(String recipientIP);
    public String denyCall(String fromIP, boolean isInviter);
    public String endCall(String fromIP, boolean isInviter);
    public String testDB();
}
