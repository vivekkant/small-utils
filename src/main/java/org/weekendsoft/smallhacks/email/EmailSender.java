package org.weekendsoft.smallhacks.email;

public interface EmailSender {
	
	public void sendPlainTextEmail(String from, String to, String subject, String body) throws Exception;
	
	public void sendPlainHTMLEmail(String from, String to, String subject, String body) throws Exception;

}
