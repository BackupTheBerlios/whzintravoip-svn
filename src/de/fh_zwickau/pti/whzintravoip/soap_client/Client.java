package de.fh_zwickau.pti.whzintravoip.soap_client;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

 import java.net.*;
 import java.util.*;
 import org.apache.soap.*; // Body, Envelope, Fault, Header
 import org.apache.soap.rpc.*; // Call, Parameter, Response


 public class Client {

     public static void main(String[] args) throws Exception {
         URL url = new URL("http://141.32.28.226:8080/soap/servlet/rpcrouter");
         String urn = "urn:demo1:exchange";
         Call call = new Call(); // prepare the service invocation
         call.setTargetObjectURI(urn);
         call.setMethodName("getRate");
         call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
         Vector params = new Vector();
         params.addElement(new Parameter("country1", String.class, "USA", null));
         params.addElement(new Parameter("country2", String.class, "japan", null));
         call.setParams(params);
         try {
             System.out.println("invoke service\n" + "  URL= " + url +
                                "\n  URN =" +
                                urn);
             Response response = call.invoke(url, ""); // invoke the service
             if (!response.generatedFault()) {
                 Parameter result = response.getReturnValue(); // response was OK
                 System.out.println("Result= " + result.getValue());
             } else {
                 Fault f = response.getFault(); // an error occurred
                 System.err.println("Fault= " + f.getFaultCode() + ", " +
                                    f.getFaultString());
             }
         } catch (SOAPException e) { // call could not be sent properly
             System.err.println("SOAPException= " + e.getFaultCode() + ", " +
                                e.getMessage());
         }
     }
 }
