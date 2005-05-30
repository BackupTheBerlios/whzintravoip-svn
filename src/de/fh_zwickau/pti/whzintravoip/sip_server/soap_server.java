package de.fh_zwickau.pti.whzintravoip.sip_server;

public class soap_server {

    public Server_SIPPacketCaller test;

    public static int counts = 0;

    public soap_server() {
    }

    public String initCall()
    {
        counts++;
        test = new Server_SIPPacketCaller("Count " + counts);
        try {
            this.test.initCallerSIPStack();
            this.test.initCallerFactories();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return new String("init Call sucessful!");
    }

    public String makeCall()
    {
        this.test.inviteNow();
        return new String("make Call sucessful!" + this.test.to);
    }

    public String acceptCall()
    {
        return new String("accept Call succesful");
    }

    public String denyCall()
    {
        return new String("deny Call succesful");
    }

    public String endCall()
    {
        return new String("end Call succesful");
    }
}
