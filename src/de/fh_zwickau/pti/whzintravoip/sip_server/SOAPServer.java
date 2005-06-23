package de.fh_zwickau.pti.whzintravoip.sip_server;

import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;
import java.util.Vector;

public interface SOAPServer {
    public String registerUser(User regUser);
    public String processCall(String fromIP, String toIP);
    public String acceptCall(String fromIP, String toIP);
    public String denyCall(String fromIP, String toIP);
    public String endCall(String fromIP, String toIP);
    public Vector whoIsOn();
}
