package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: WHZIntraVoIP - SOAPMethodCaller</p>
 *
 * <p>Description: The class to connect to the SOAP-Server</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Y. Schumann ys@fh-zwickau.de
 * @version 0.0.1
 */

 import java.net.*;
 import java.util.*;
 import org.apache.soap.*; // Body, Envelope, Fault, Header
 import org.apache.soap.rpc.*; // Call, Parameter, Response
 import org.apache.soap.util.xml.QName;
 import org.apache.soap.encoding.SOAPMappingRegistry;
 import org.apache.soap.encoding.soapenc.BeanSerializer;
 import de.fh_zwickau.pti.whzintravoip.sip_server.user.*;


 public class SOAPMethodCaller {

     private ThinClient m_ThinClient;
     private String m_sServerURL;
     private String m_sServerURN;
     private String m_sClassToMap;

     /**
      * This is the class which connects to the soap-server
      * and calls the specified method
      *
      * @param gui ThinClientGUI - the user interface to give back some information
      * @param serverURL String - the URL-String to the SOAP server
      * @param serverURN String - the URN-String to the SOAP server
      * @param classToMap String - the class to map on the server
      */
     public SOAPMethodCaller(ThinClient gui, String serverURL, String serverURN, String classToMap){
         this.m_ThinClient = gui;
         this.m_sServerURL = serverURL;
         this.m_sServerURN = serverURN;
         this.m_sClassToMap = classToMap;
     }

     /**
      * This method calls the specified method on the SOAP-server
      * with the given arguments
      *
      * @param methodToCall String - the name of the method to call
      * @param param1 User - the user object (can be empty)
      * @param param2 String - the second parameter (can be empty)
      * @throws Exception -
      */
     public String registerMyselfAtServer(String methodToCall, User param1, String param2) throws Exception {
         URL url = new URL(m_sServerURL);
         Call call = new Call(); // prepare the service invocation
         call.setTargetObjectURI(m_sServerURN);
         call.setMethodName(methodToCall);
         call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);

         SOAPMappingRegistry soapMappingRegistry = new SOAPMappingRegistry();
         BeanSerializer beanSerializer = new BeanSerializer();
         QName qName = new QName(m_sServerURN, m_sClassToMap);
         soapMappingRegistry.mapTypes(Constants.NS_URI_SOAP_ENC,
                                      qName,
                                      User.class,
                                      beanSerializer,
                                      beanSerializer);
         call.setSOAPMappingRegistry(soapMappingRegistry);

         Vector params = new Vector();
         if(param1 != null){
             params.addElement(new Parameter("regUser", User.class, param1, null));
         }
         if(param2 != null){
             params.addElement(new Parameter("recipientIP", String.class, param2, null));
         }
         call.setParams(params);
         Parameter result = null;
         try {
             m_ThinClient.stdOutput("invoke service\n"
                               + "  URL= "
                               + url
                               + "\n  URN ="
                               + m_sServerURN);
             Response response = call.invoke(url, ""); // invoke the service
             if (!response.generatedFault()) {
                 result = response.getReturnValue(); // response was OK
                 m_ThinClient.stdOutput("Result= " + result.getValue());
             } else {
                 Fault f = response.getFault(); // an error occurred
                 m_ThinClient.errOutput("Fault= " + f.getFaultCode() + ", " +
                                    f.getFaultString());
             }
         } catch (SOAPException e) { // call could not be sent properly
             m_ThinClient.errOutput("SOAPException= " + e.getFaultCode() + ", " +
                                e.getMessage());
         }
         StringTokenizer tok = new StringTokenizer((String)result.getValue(), "=");
         String resultString = tok.nextToken();
         return resultString;
     }
     /**
      * This method calls the specified method on the SOAP-server
      * with the given arguments
      *
      * @param methodToCall String - the name of the method to call
      * @param param1 User - the user object (can be empty)
      * @param param2 String - the second parameter (can be empty)
      * @throws Exception -
      */
     public Vector whoIsOnAtServer() throws Exception {
         m_ThinClient.stdOutput("Who is on at the moment?");
         URL url = new URL(m_sServerURL);
         Call call = new Call(); // prepare the service invocation
         call.setTargetObjectURI(m_sServerURN);
         call.setMethodName("whoIsOn");
         call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);

         SOAPMappingRegistry soapMappingRegistry = new SOAPMappingRegistry();
         BeanSerializer beanSerializer = new BeanSerializer();
         QName qName = new QName(m_sServerURN, m_sClassToMap);
         soapMappingRegistry.mapTypes(Constants.NS_URI_SOAP_ENC,
                                      qName,
                                      User.class,
                                      beanSerializer,
                                      beanSerializer);
         call.setSOAPMappingRegistry(soapMappingRegistry);

         try {
             m_ThinClient.stdOutput("invoke service\n"
                               + "  URL= "
                               + url
                               + "\n  URN ="
                               + m_sServerURN);
             Response response = call.invoke(url, ""); // invoke the service
             if (!response.generatedFault()) {
                 Parameter result = response.getReturnValue(); // response was OK
                 m_ThinClient.stdOutput("Result= " + result.getValue());
                 m_ThinClient.stdOutput(result.toString());
                 return (Vector) result.getValue();
             } else {
                 Fault f = response.getFault(); // an error occurred
                 m_ThinClient.errOutput("Fault= " + f.getFaultCode() + ", " +
                                    f.getFaultString());
             }
         } catch (SOAPException e) { // call could not be sent properly
             m_ThinClient.errOutput("SOAPException= " + e.getFaultCode() + ", " +
                                e.getMessage());
         }
         return null;
     }

     /**
      * This method calls the specified method on the SOAP-server
      * with the given arguments
      *
      * @param methodToCall String - the name of the method to call
      * @param param1 String - the first parameter (can be empty)
      * @param param2 String - the second parameter (can be empty)
      * @throws Exception -
      */
     public String callSOAPServer(String methodToCall, String param1, String param2) throws Exception {
         URL url = new URL(m_sServerURL);
         Call call = new Call(); // prepare the service invocation
         call.setTargetObjectURI(m_sServerURN);
         call.setMethodName(methodToCall);
         call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
         Vector params = new Vector();
         if(param1 != null){
             params.addElement(new Parameter("fromIP", String.class, param1, null));
         }
         if(param2 != null){
             params.addElement(new Parameter("toIP", String.class, param2, null));
         }
         call.setParams(params);
         Parameter result = null;
         try {
             m_ThinClient.stdOutput("invoke service\n"
                               + "  URL= "
                               + url
                               + "\n  URN ="
                               + m_sServerURN);
             Response response = call.invoke(url, ""); // invoke the service
             if (!response.generatedFault()) {
                 result = response.getReturnValue(); // response was OK
                 m_ThinClient.stdOutput("Result= " + result.getValue());
             } else {
                 Fault f = response.getFault(); // an error occurred
                 m_ThinClient.errOutput("Fault= " + f.getFaultCode() + ", " +
                                    f.getFaultString());
             }
         } catch (SOAPException e) { // call could not be sent properly
             m_ThinClient.errOutput("SOAPException= " + e.getFaultCode() + ", " +
                                e.getMessage());
         }
         StringTokenizer tok = new StringTokenizer((String)result.getValue(), "=");
         String resultString = tok.nextToken();
         return resultString;
     }
 }
