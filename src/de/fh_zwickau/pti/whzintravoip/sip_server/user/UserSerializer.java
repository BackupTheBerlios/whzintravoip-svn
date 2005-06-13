package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import java.io.*;

import org.apache.soap.encoding.soapenc.*;
import org.apache.soap.rpc.*;
import org.apache.soap.util.*;
import org.apache.soap.util.xml.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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

    /**
     * Init the logger named PacketCaller.log
     */
    private static final Logger logger = Logger.getLogger("PacketCaller.log");


    public UserSerializer() {
        PropertyConfigurator.configure("/log4j.properties");
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
        logger.info("Serializing started!");
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
        if(user != null)
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
        logger.info("Serializing finished!");
    }




}
