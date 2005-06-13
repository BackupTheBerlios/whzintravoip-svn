package de.fh_zwickau.pti.whzintravoip.sip_server;

import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;

public interface SOAPServer {
    public String registerUser(User regUser);
    public String processCall(String inviterIP, String recipientIP);
    public String acceptCall(String inviterIP, String recipientIP);
    public String denyCall(String fromIP, String toIP);
    public String endCall(String fromIP, String toIP);
}
