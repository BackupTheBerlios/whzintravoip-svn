package de.fh_zwickau.pti.whzintravoip.soap_client;

/**
 * <p>Title: Client </p>
 *
 * <p>Description: Test Client for testing the SOAP methods</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @Blurb
 * @version 0.0.1
 */

import java.net.*;
import java.util.*;
import org.apache.soap.*; // Body, Envelope, Fault, Header
import org.apache.soap.rpc.*; // Call, Parameter, Response

public class Client {

    public Client() {

    }

    public static void main(String[] args) throws Exception {
        try {
            makeinitCall("141.32.28.226", "141.32.22.88");
            makerealCall("141.32.28.226");
            makeAcceptCall("141.32.22.88");
            //makeinitCall("141.32.28.226", "127.0.0.20");
            //makerealCall("141.32.28.226");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static void makeinitCall(String myIP, String otherIP) throws
            Exception {
        URL url = new URL("http://localhost:8080/soap/servlet/rpcrouter");
        String urn = "urn:sip_server:soapserver:appscope";
        Call call = new Call(); // prepare the service invocation
        call.setTargetObjectURI(urn);
        call.setMethodName("initCall");
        call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
        Vector params = new Vector();
        params.addElement(new Parameter("InviterIP", String.class, myIP, null));
        params.addElement(new Parameter("RecipiantIP", String.class, otherIP, null));
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


    public static void makerealCall(String fromIP) throws Exception {
        URL url = new URL("http://localhost:8080/soap/servlet/rpcrouter");
        String urn = "urn:sip_server:soapserver:appscope";
        Call call = new Call(); // prepare the service invocation
        call.setTargetObjectURI(urn);
        call.setMethodName("makeCall");
        call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
        Vector params = new Vector();
        params.addElement(new Parameter("InviterIP", String.class, fromIP, null));
        //params.addElement(new Parameter("country2", String.class, "japan", null));
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

    public static void makeAcceptCall(String fromIP) throws Exception {
        URL url = new URL("http://localhost:8080/soap/servlet/rpcrouter");
        String urn = "urn:sip_server:soapserver:appscope";
        Call call = new Call(); // prepare the service invocation
        call.setTargetObjectURI(urn);
        call.setMethodName("acceptCall");
        call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
        Vector params = new Vector();
        params.addElement(new Parameter("RecipientIP", String.class, fromIP, null));
        //params.addElement(new Parameter("country2", String.class, "japan", null));
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
