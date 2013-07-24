package br.com.automacao.log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import utilitarios.UtilGestaoUnidade;


public class SendMail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] to = {"jose-eduardo.carvalho@serpro.gov.br"}; // added this line
		enviarEmail(to, "Erro no processamento do log", "teste mensagem");
		
	}

	public static void enviarEmail( String[] to, String assunto, String textoMensagem) {
		
		String host = "mail-apl.serpro.gov.br";
		
		
	    String from = UtilGestaoUnidade.getInstanciaUtilitario().getUsuarioEmail();
	    String pass = UtilGestaoUnidade.getInstanciaUtilitario().getPassUsuarioEmail();
	    Properties props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", "true"); // added this line
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.user", from);
	    props.put("mail.smtp.password", pass);
	    //props.put("mail.smtp.port", "465");
	    //props.put("mail.smtp.auth", "true");

	  

	    Session session = Session.getDefaultInstance(props, null);
	    MimeMessage message = new MimeMessage(session);
	    try {
			message.setFrom(new InternetAddress(from));
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	    InternetAddress[] toAddress = new InternetAddress[to.length];

	    // To get the array of addresses
	    for( int i=0; i < to.length; i++ ) { // changed from a while loop
	        try {
				toAddress[i] = new InternetAddress(to[i]);
			} catch (AddressException e) {
				e.printStackTrace();
			}
	    }
	    

	    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
	        try {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    try {
			message.setSubject(assunto);
		    message.setText(textoMensagem);
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
