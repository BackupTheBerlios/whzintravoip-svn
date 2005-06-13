package de.fh_zwickau.pti.whzintravoip.thin_client;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
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

     private ThinClientGUI userGUI;
     private String serverURL;
     private String serverURN;
     private String classToMap;

     /**
      * This is the class which connects to the soap-server
      * and calls the specified method
      *
      * @param gui ThinClientGUI - the user interface to give back some information
      * @param serverURL String - the URL-String to the SOAP server
      * @param serverURN String - the URN-String to the SOAP server
      * @param classToMap String - the class to map on the server
      */
     public SOAPMethodCaller(ThinClientGUI gui, String serverURL, String serverURN, String classToMap){
         this.userGUI = gui;
         this.serverURL = serverURL;
         this.serverURN = serverURN;
         this.classToMap = classToMap;
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
     public void registerMyselfAtServer(String methodToCall, User param1, String param2) throws Exception {
         URL url = new URL(serverURL);
         Call call = new Call(); // prepare the service invocation
         call.setTargetObjectURI(serverURN);
         call.setMethodName(methodToCall);
         call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);

         SOAPMappingRegistry soapMappingRegistry = new SOAPMappingRegistry();
         BeanSerializer beanSerializer = new BeanSerializer();
         QName qName = new QName(serverURN, classToMap);
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
         try {
             userGUI.stdOutput("invoke service\n"
                               + "  URL= "
                               + url
                               + "\n  URN ="
                               + serverURN);
             Response response = call.invoke(url, ""); // invoke the service
             if (!response.generatedFault()) {
                 Parameter result = response.getReturnValue(); // response was OK
                 userGUI.stdOutput("Result= " + result.getValue());
             } else {
                 Fault f = response.getFault(); // an error occurred
                 userGUI.errOutput("Fault= " + f.getFaultCode() + ", " +
                                    f.getFaultString());
             }
         } catch (SOAPException e) { // call could not be sent properly
             userGUI.errOutput("SOAPException= " + e.getFaultCode() + ", " +
                                e.getMessage());
         }
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
     public void callSOAPServer(String methodToCall, String param1, String param2) throws Exception {
         URL url = new URL(serverURL);
         Call call = new Call(); // prepare the service invocation
         call.setTargetObjectURI(serverURN);
         call.setMethodName(methodToCall);
         call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
         Vector params = new Vector();
         if(param1 != null){
             params.addElement(new Parameter("inviterIP", String.class, param1, null));
         }
         if(param2 != null){
             params.addElement(new Parameter("recipientIP", String.class, param2, null));
         }
         call.setParams(params);
         try {
             userGUI.stdOutput("invoke service\n"
                               + "  URL= "
                               + url
                               + "\n  URN ="
                               + serverURN);
             Response response = call.invoke(url, ""); // invoke the service
             if (!response.generatedFault()) {
                 Parameter result = response.getReturnValue(); // response was OK
                 userGUI.stdOutput("Result= " + result.getValue());
             } else {
                 Fault f = response.getFault(); // an error occurred
                 userGUI.errOutput("Fault= " + f.getFaultCode() + ", " +
                                    f.getFaultString());
             }
         } catch (SOAPException e) { // call could not be sent properly
             userGUI.errOutput("SOAPException= " + e.getFaultCode() + ", " +
                                e.getMessage());
         }
     }
 }
