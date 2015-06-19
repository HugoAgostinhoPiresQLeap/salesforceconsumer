package demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.xml.sax.SAXException;

public final class SoapLoginUtil {

    // The enterprise SOAP API endpoint used for the login call in this example.
    private static final String SERVICES_SOAP_PARTNER_ENDPOINT = "/services/Soap/u/33.0/";

    private static final String ENV_START =
            "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
                    + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
                    "xmlns:urn='urn:partner.soap.sforce.com'><soapenv:Body>";

    private static final String ENV_END = "</soapenv:Body></soapenv:Envelope>";

    private static byte[] soapXmlForLogin(String username, String password) 
        throws UnsupportedEncodingException {
        return (ENV_START +
                "  <urn:login>" +
                "    <urn:username>" + username + "</urn:username>" +
                "    <urn:password>" + password + "</urn:password>" +
                "  </urn:login>" +
                ENV_END).getBytes("UTF-8");
    }

    public static String[] login(HttpClient client, String username, String password)
            throws IOException, InterruptedException, SAXException, 
                ParserConfigurationException {

        ContentExchange exchange = new ContentExchange();
        exchange.setMethod("POST");
        exchange.setURL(getSoapURL());
        exchange.setRequestContentSource(new ByteArrayInputStream(soapXmlForLogin(username, password)));
        exchange.setRequestHeader("Content-Type", "text/xml");
        exchange.setRequestHeader("SOAPAction", "''");
        exchange.setRequestHeader("PrettyPrint", "Yes");

        client.send(exchange);
        exchange.waitForDone();
        String response = exchange.getResponseContent();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();

        LoginResponseParser parser = new LoginResponseParser();
        ByteArrayInputStream bais = new ByteArrayInputStream(response.getBytes("UTF-8"));
        //Print
        SoapLoginUtil.print(response.getBytes("UTF-8"));
        
        saxParser.parse(bais, parser);

        if (parser.getSessionId() == null || parser.getServerUrl() == null) {
            System.out.println("Login Failed!\n" + response);
            return null;
        }

        URL soapEndpoint = new URL(parser.getServerUrl());
        StringBuilder endpoint = new StringBuilder()
            .append(soapEndpoint.getProtocol())
            .append("://")
            .append(soapEndpoint.getHost());

        if (soapEndpoint.getPort() > 0) 
        	endpoint.append(":").append(soapEndpoint.getPort());
        return new String[] {parser.getSessionId(), endpoint.toString()};
    }

    private static String getSoapURL() throws MalformedURLException {
        return new URL(StreamingClientExample.LOGIN_ENDPOINT + getSoapUri()).toExternalForm();
    }

    private static String getSoapUri() {
        return SERVICES_SOAP_PARTNER_ENDPOINT;
    }
    
    private static void print(byte[] b)throws IOException {

//        ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
//
//        while( bOutput.size()!= 10 ) {
//           // Gets the inputs from the user
//           bOutput.write(System.in.read()); 
//        }
//
//        byte b [] = bOutput.toByteArray();
//        System.out.println("Print the content");
//        for(int x= 0 ; x < b.length; x++) {
//           // printing the characters
//           System.out.print((char)b[x]  + "   "); 
//        }
//        System.out.println("   ");
//
        int c;

        ByteArrayInputStream bInput = new ByteArrayInputStream(b);

        System.out.println("Converting characters to Upper case " );
        for(int y = 0 ; y < 1; y++ ) {
           while(( c= bInput.read())!= -1) {
              System.out.print(Character.toUpperCase((char)c));
           }
           bInput.reset(); 
        }
     }
}
