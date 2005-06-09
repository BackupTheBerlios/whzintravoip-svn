package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import org.apache.soap.util.xml.*;
import org.apache.soap.rpc.*;
import java.io.Writer;
import org.apache.soap.encoding.soapenc.SoapEncUtils;
import java.io.IOException;
import org.apache.soap.util.StringUtils;
import org.apache.soap.util.Bean;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

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
        String userName = user.getUserName();

        if(userIP != null)
        {
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          userIP,
                          "userIP",
                          sink,
                          nsStack,
                          ctx);
            xjmr.marshall(inScopeEncStyle,
                          String.class,
                          userName,
                          "userName",
                          sink,
                          nsStack,
                          ctx);
            sink.write(StringUtils.lineSeparator);
        }
        // closing the element
        sink.write("</" + context + ">");
    }




}
