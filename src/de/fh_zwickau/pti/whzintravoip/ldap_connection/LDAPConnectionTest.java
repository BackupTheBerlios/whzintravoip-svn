package de.fh_zwickau.pti.whzintravoip.ldap_connection;

import de.fh_zwickau.pti.whzintravoip.sip_server.ldapconn.*;
import java.util.*;

public class LDAPConnectionTest {
    private static String m_sUser_inits = "sip_server.user.USER_INITIAL";
    private static String m_sUser_fname = "sip_server.user.USER_FNAME";
    private static String m_sUser_lname = "sip_server.user.USER_LNAME";
    private static String m_sUser_company = "sip_server.user.USER_COMPANY";
    private static String m_sUser_mail = "sip_server.user.USER_MAIL";

    public static void main(String[] args) {
        ldaprequest request = new ldaprequest();
        Properties prop = null;
        prop = request.getUserProps("hs");
        System.out.println(prop.getProperty(m_sUser_inits));
        System.out.println(prop.getProperty(m_sUser_fname));
        System.out.println(prop.getProperty(m_sUser_lname));
        System.out.println(prop.getProperty(m_sUser_company));
        System.out.println(prop.getProperty(m_sUser_mail));
    }
}