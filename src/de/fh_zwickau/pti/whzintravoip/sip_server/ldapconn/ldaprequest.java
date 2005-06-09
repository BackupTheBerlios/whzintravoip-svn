package de.fh_zwickau.pti.whzintravoip.sip_server.ldapconn;

import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.Properties;

public class ldaprequest {

    public ldaprequest() {

    }

    public Properties getUserProbs(String userToken)
    {
        /** @todo gettin user properties from ldap with userToken */
        Properties userProbs = new Properties();
        return userProbs;
    }

    /**

    public static void main(String[] args) {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://whz-hrz-00.zw.fh-zwickau.de:389");
        //env.put(Context.PROVIDER_URL, "ldap://ldap.fh-zwickau.de:389");
        env.put(Context.SECURITY_AUTHENTICATION, "none");
        env.put(Context.SECURITY_PRINCIPAL, "OU=intravoip DC=zw,DC=fh-zwickau,DC=de");
        env.put(Context.SECURITY_CREDENTIALS, "An1L#4");
        try {
            // Create the initial directory context
            DirContext ctx = new InitialDirContext(env);

            // Ask for all attributes of the object
            Attributes attrs = ctx.getAttributes("CN=Andy.Reek, OU=022079, DC=zw, DC=fh-zwickau, DC=de");
            Attributes matchAttrs = new BasicAttributes(true);
            // Find the surname attribute ("sn") and print it
            matchAttrs.put(new BasicAttribute("company"));
            System.out.println("company: " + attrs.get("company").get());
            // Search for objects that have those matching attributes
            NamingEnumeration answer = ctx.search("ou=022079, dc=zw, dc=fh-zwickau, dc=de", matchAttrs);
            while (answer.hasMore()) {
                SearchResult sr = (SearchResult) answer.next();
                System.out.println(">>>" + sr.getName());
                //printAttrs(sr.getAttributes());
            }
        } catch (NamingException e) {
            System.err.println("Problem getting attribute:" + e);
        }

    }
    */
}
