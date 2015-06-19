package demo;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class LoginResponseParser extends DefaultHandler {

    private boolean inSessionId;
    private String sessionId;

    private boolean inServerUrl;
    private String serverUrl;

    @Override
    public void characters(char[] ch, int start, int length) {
        if (inSessionId) sessionId = new String(ch, start, length);
        if (inServerUrl) serverUrl = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName != null) {
            if (localName.equals("sessionId")) {
                inSessionId = false;
            }
            
            if (localName.equals("serverUrl")) {
                inServerUrl = false;
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, 
        String qName, Attributes attributes) {
        if (localName != null) {
            if (localName.equals("sessionId")) {
                inSessionId = true;
            }

            if (localName.equals("serverUrl")) {
                inServerUrl = true;
            }
        }
    }

	public boolean isInSessionId() {
		return inSessionId;
	}

	public void setInSessionId(boolean inSessionId) {
		this.inSessionId = inSessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isInServerUrl() {
		return inServerUrl;
	}

	public void setInServerUrl(boolean inServerUrl) {
		this.inServerUrl = inServerUrl;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
}
