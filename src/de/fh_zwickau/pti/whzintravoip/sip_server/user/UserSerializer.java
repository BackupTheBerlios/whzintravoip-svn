package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import java.io.*;

import org.apache.soap.encoding.soapenc.*;
import org.apache.soap.rpc.*;
import org.apache.soap.util.*;
import org.apache.soap.util.xml.*;

/**
 *
 * <p>Überschrift: Serializer for UserObjects</p>
 *
 * <p>Beschreibung: A Serializer must overwrite the Serializer Interface and
 * must implement a No Argument Constructor.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author blurb
 * @version 0.0.1
 */

public class UserSerializer extends Object implements Serializer {

    public UserSerializer() {
    }

    public void marshall(String inScopeEncStyle,
                         Class javaType,
                         Object src,
                         Object context,
                         Writer sink,
                         NSStack nsStack,
                         XMLJavaMappingRegistry xjmr,
                         SOAPContext ctx)
            throws IllegalArgumentException, IOException
    {
        //pushing the Scope
        nsStack.pushScope();
        //generating the header structure
        SoapEncUtils.generateStructureHeader(inScopeEncStyle,
                                             javaType,
                                             context,
                                             sink,
                                             nsStack,
                                             xjmr);
        sink.write(StringUtils.lineSeparator);
        //obtaining the User object out of the argument
        User user = (User) src;
        String userIP = user.getUserIP();
        String sipName = user.getSipName();


        if(userIP != null)
        {
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getUserIP(),
                          "userIP",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getUserInitial(),
                          "userInitial",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getUserFName(),
                          "userFName",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getUserLName(),
                          "userLName",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getUserCompany(),
                          "userCompany",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getUserMail(),
                          "userMail",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getSipName(),
                          "sipName",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getSipAddress(),
                          "sipAddress",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getSipScreenName(),
                          "sipScreenName",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getRole(),
                          "role",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          user.getStatus(),
                          "status",
                          sink,
                          nsStack,
                          ctx);
            sink.write(StringUtils.lineSeparator);
        }
        // closing the element
        sink.write("</" + context + ">");
    }




}
