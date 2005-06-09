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

public class UserDeserializer extends Object implements Deserializer {
    public UserDeserializer() {
    }

    public Bean unmarshall(String inScopeEncStyle,
                           QName elementType,
                           Node src,
                           XMLJavaMappingRegistry xjmr,
                           SOAPContext ctx)
            throws IllegalArgumentException
    {
        // Creating an Instance of the User Oject
        User user = new User();
        Element userElement = (Element) src;
        Element childElement = DOMUtils.getFirstChildElement(userElement);
        while(childElement != null)
        {
            String tagName = childElement.getTagName();
            if(tagName.equals("userIP"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setUserIP((String) param.getValue());
            }
            if(tagName.equals("userName"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setUserName((String) param.getValue());
            }
            childElement = DOMUtils.getNextSiblingElement(childElement);
        }
        return new Bean(User.class, user);
    }

}
