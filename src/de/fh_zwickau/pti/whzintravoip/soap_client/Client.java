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
import org.apache.soap.util.xml.QName;
import org.apache.soap.encoding.SOAPMappingRegistry;
import de.fh_zwickau.pti.whzintravoip.sip_server.user.User;

import org.apache.soap.encoding.soapenc.BeanSerializer;

public class Client {

    public Client() {

    }

    public static void main(String[] args) throws Exception {
        try {
            makeUserTest();

            //makeDBTest();

            //makeinitCall("141.32.28.226", "141.32.22.88");
            //makerealCall("141.32.28.226");
            //makeAcceptCall("141.32.22.88");

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

    public static void makeUserTest() throws
            Exception {
        URL url = new URL("http://localhost:8080/soap/servlet/rpcrouter");
        String urn = "urn:sip_server:soapserver:appscope:withmap4";
        Call call = new Call(); // prepare the service invocation
        call.setTargetObjectURI(urn);
        call.setMethodName("initCall");
        call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
        SOAPMappingRegistry smr = new SOAPMappingRegistry();
        BeanSerializer bsr = new BeanSerializer();
        QName qn = new QName("urn:sip_server:soapserver:appscope:withmap4",
                             "de.fh_zwickau.pti.whzintravoip.sip_server.User");
        smr.mapTypes(Constants.NS_URI_SOAP_ENC,
                     qn,
                     User.class, bsr, bsr);
        call.setSOAPMappingRegistry(smr);
        Vector params = new Vector();
        User aUser = new User("192.168.0.1",
                              "Harald",
                              "Harald@sip",
                              "sheepy",
                              User.INVITER,
                              User.PICKUP);
        params.addElement(new Parameter("aUser", User.class, aUser, null));
        //params.addElement(new Parameter("RecipiantIP", String.class, otherIP, null));
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


    public static void makeDBTest() throws
            Exception {
        URL url = new URL("http://localhost:8080/soap/servlet/rpcrouter");
        String urn = "urn:sip_server:dbtest";
        Call call = new Call(); // prepare the service invocation
        call.setTargetObjectURI(urn);
        call.setMethodName("testDB");
        call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);
        Vector params = new Vector();
        //params.addElement(new Parameter("InviterIP", String.class, myIP, null));
        //params.addElement(new Parameter("RecipiantIP", String.class, otherIP, null));
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
