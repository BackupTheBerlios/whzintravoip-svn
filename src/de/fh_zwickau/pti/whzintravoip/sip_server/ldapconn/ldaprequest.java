package de.fh_zwickau.pti.whzintravoip.sip_server.ldapconn;

import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;

import org.apache.log4j.*;

public class ldaprequest {

    // LDAP access configration
    private String m_sContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
    private String m_sProviderUrl = "ldap://whz-hrz-00.zw.fh-zwickau.de:389";
    private String m_sSecurityAuthentication = "simple";
    private String m_sSecurityPrincipal =
            "CN=intravoip, OU=Gast,DC=zw,DC=fh-zwickau,DC=de";
    private String m_sSecurityCredentials = "An1L#4";
    private Attributes m_Result = null;
    private String m_sRequestAttrib = "mailnickname";

    // Properties configuration
    private Properties m_UserProps = null;
    private String m_sUser_inits = "sip_server.user.USER_INITIAL";
    private String m_sUser_fname = "sip_server.user.USER_FNAME";
    private String m_sUser_lname = "sip_server.user.USER_LNAME";
    private String m_sUser_company = "sip_server.user.USER_COMPANY";
    private String m_sUser_mail = "sip_server.user.USER_MAIL";

    // the search string
    private String m_sSearchString = null;

    /**
     * Init the logger named PacketCaller.log
     */
    private static final Logger logger = Logger.getLogger("PacketCaller.log");


    public ldaprequest() {
        PropertyConfigurator.configure("/log4j.properties");
    }

    /**
     * Try to connect to LDAP and find the entry matching the given nickname.
     * Once we got we save them in our properties and return.
     * @param userToken String The nick to search for.
     * @return Properties Result, null if not found.
     */
    public Properties getUserProps(String userToken) {
        logger.info("Trying LDAP request.");
        this.m_sSearchString = userToken;
        // we doent need to search rest oft the directory if we still found
        boolean found = false;

        m_UserProps = null;
        m_Result = null;

        // initialize connection parameters
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, m_sContextFactory);
        env.put(Context.PROVIDER_URL, m_sProviderUrl);
        env.put(Context.SECURITY_AUTHENTICATION, m_sSecurityAuthentication);
        env.put(Context.SECURITY_PRINCIPAL, m_sSecurityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, m_sSecurityCredentials);

        try {
            // create the initial directory context
            DirContext ctx = new InitialDirContext(env);

            // set the directory attribute we wanne get
            Attributes main = new BasicAttributes(true);
            main.put(new BasicAttribute("OU"));
            // set attribute to search for in each directory
            Attributes matchAttrs = new BasicAttributes(true);
            matchAttrs.put(new BasicAttribute(m_sRequestAttrib, m_sSearchString));

            // start finding all directories entries
            NamingEnumeration dirs = ctx.search("dc=zw, dc=fh-zwickau, dc=de",
                                                main);
            // now iterate over each directory found
            while (dirs.hasMoreElements() & !found) {
                NameClassPair attr = (NameClassPair) dirs.nextElement();
                // try to find the mailnickname in each entry
                StringBuffer find = new StringBuffer();
                find.append(attr.getName().toString());
                find.append(", dc=zw, dc=fh-zwickau, dc=de");
                NamingEnumeration answer = ctx.search(find.toString(),
                        matchAttrs);
                // if we got some matching, get it
                if (answer.hasMoreElements()) {
                    logger.info("User entry found in LDAP. Nick: " + m_sSearchString);
                    found = true;
                    SearchResult sr = (SearchResult) answer.next();
                    m_Result = sr.getAttributes();
                    // now retrieve the entrys we want
                    m_UserProps = new Properties();
                    m_UserProps.setProperty(m_sUser_inits,
                                            ((Attribute)
                                             m_Result.get("mailNickName")).get().
                                            toString());
                    m_UserProps.setProperty(m_sUser_fname,
                                            ((Attribute)
                                             m_Result.get("givenName")).get().
                                            toString());
                    m_UserProps.setProperty(m_sUser_lname,
                                            ((Attribute) m_Result.get("sn")).
                                            get().
                                            toString());
                    m_UserProps.setProperty(m_sUser_company,
                                            ((Attribute) m_Result.get("company")).
                                            get().toString());
                    m_UserProps.setProperty(m_sUser_mail,
                                            ((Attribute) m_Result.get("mail")).
                                            get().toString());
                }
            }
            // now we finished, close the direstory context
            ctx.close();
        } catch (Exception ex) {
            logger.error("Error while LDAP request: " + ex);
        }
        if(m_UserProps == null){
            logger.info("No entry found for: " + m_sSearchString);
        }
        return m_UserProps;
    }
}
