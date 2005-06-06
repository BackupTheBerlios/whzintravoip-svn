package de.fh_zwickau.pti.whzintravoip.ldap_connection;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchResult;


import java.util.*;

public class testconnect {
    public testconnect() {
    }

    public static void main(String[] args) {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://whz-hrz-00.zw.fh-zwickau.de:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "CN=Torsten.Schmidt, OU=022079,DC=zw,DC=fh-zwickau,DC=de");
        env.put(Context.SECURITY_CREDENTIALS, "XLA86731");

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
            NamingEnumeration answer = ctx.search("ou=002079, dc=zw, dc=fh-zwickau, dc=de", matchAttrs);
            while (answer.hasMore()) {
                SearchResult sr = (SearchResult) answer.next();
                System.out.println(">>>" + sr.getName());
                //printAttrs(sr.getAttributes());
            }
        } catch (NamingException e) {
            System.err.println("Problem getting attribute:" + e);
        }

    }
}
