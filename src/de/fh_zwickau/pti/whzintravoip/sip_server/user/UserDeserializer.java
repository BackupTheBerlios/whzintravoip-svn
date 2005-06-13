package de.fh_zwickau.pti.whzintravoip.sip_server.user;

import org.apache.soap.rpc.*;
import org.apache.soap.util.*;
import org.apache.soap.util.xml.*;
import org.w3c.dom.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * <p>Überschrift: Deserializer for User Objects</p>
 *
 * <p>Beschreibung: A Deserializer must override the Deserializer Interface
 * and must have an Nor Argument Constructor.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Organisation: </p>
 *
 * @author Blurb
 * @version 0.0.1
 */

public class UserDeserializer extends Object implements Deserializer {

    /**
     * Init the logger named PacketCaller.log
     */
    private static final Logger logger = Logger.getLogger("PacketCaller.log");


    public UserDeserializer() {
         PropertyConfigurator.configure("/log4j.properties");
    }

    public Bean unmarshall(String inScopeEncStyle,
                           QName elementType,
                           Node src,
                           XMLJavaMappingRegistry xjmr,
                           SOAPContext ctx)
            throws IllegalArgumentException
    {
        logger.info("Starting Deserializing!");
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
            if(tagName.equals("userInitial"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setUserInitial((String) param.getValue());
            }
            if(tagName.equals("userFName"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setUserFName((String) param.getValue());
            }
            if(tagName.equals("userLName"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setUserLName((String) param.getValue());
            }
            if(tagName.equals("userCompany"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setUserCompany((String) param.getValue());
            }
            if(tagName.equals("userMail"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setUserMail((String) param.getValue());
            }
            if(tagName.equals("sipName"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setSipName((String) param.getValue());
            }
            if(tagName.equals("sipAddress"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setSipAddress((String) param.getValue());
            }
            if(tagName.equals("sipScreenName"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setSipScreenName((String) param.getValue());
            }
            if(tagName.equals("role"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setRole((String) param.getValue());
            }
            if(tagName.equals("status"))
            {
                Bean bean = xjmr.unmarshall(inScopeEncStyle,
                                            RPCConstants.Q_ELEM_PARAMETER,
                                            childElement,
                                            ctx);
                Parameter param = (Parameter) bean.value;
                user.setStatus((String) param.getValue());
            }
            childElement = DOMUtils.getNextSiblingElement(childElement);
        }
        logger.info("Deserializing finished!");
        return new Bean(User.class, user);
    }

}
