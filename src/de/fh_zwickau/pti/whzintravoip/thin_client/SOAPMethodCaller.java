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


 public class SOAPMethodCaller {

     private ThinClientGUI userGUI;
     private String serverURL;
     private String serverURN;

     /**
      * This is the class which connects to the soap-server
      * and calls the specified method
      *
      * @param gui ThinClientGUI - the user interface to give back some information
      * @param serverURL String - the URL-String to the SOAP server
      * @param serverURN String - the URN-String to the SOAP server
      */
     public SOAPMethodCaller(ThinClientGUI gui, String serverURL, String serverURN){
         this.userGUI = gui;
         this.serverURL = serverURL;
         this.serverURN = serverURN;
     }

     public static void main(String[] args) throws Exception {
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
         String urn = serverURN;
         Call call = new Call(); // prepare the service invocation
         call.setTargetObjectURI(urn);
         call.setMethodName(methodToCall);
         call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
         Vector params = new Vector();
         if(param1 != null){
             params.addElement(new Parameter("country1", String.class, param1, null));
         }
         if(param2 != null){
             params.addElement(new Parameter("country2", String.class, param2, null));
         }
         call.setParams(params);
         try {
             userGUI.stdOutput("invoke service\n" + "  URL= " + url +
                                "\n  URN =" +
                                urn);
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
