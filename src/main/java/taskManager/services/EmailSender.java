package taskManager.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
public class EmailSender {
	private static final String MAIL_CONTENT_TYPE = "text/html; charset=utf-8";
	private static final String MAIL_SETTINGS_PROPERTIES_PATH = "/mail-settings.properties";
	private static final String UTF8 = "UTF8";

	/**
	 * This method sends an email message.
	 * 
	 * @param senderEmail
	 *            - The email address of the sending person
	 * @param receiverEmail
	 *            - The email address of the person the message is being sent to
	 * @param subject
	 *            - The subject of the email message
	 * @param mailParams
	 *            - The actual content of the mail message to send
	 * @throws MessagingException
	 */
	@Asynchronous
	public void sendEmail(String senderEmail, String receiverEmail,
			String subject, String registrationDetails) {

		final Properties prop = new Properties();
		// set mail settings
		loadMailSetting(prop);

		// get session
		Session session = Session.getDefaultInstance(prop, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
						prop.getProperty("mail.user"), prop
								.getProperty("mail.passwd"));
			}
		});
	

		// construct message
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(senderEmail));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					receiverEmail));
			message.setSubject(subject, UTF8);
			message.setContent(registrationDetails, MAIL_CONTENT_TYPE);

			// Send mail
			Transport.send(message);
		} catch (MessagingException me) {
			// TODO Log the exception like: LoggerUtil.error(me);
			// TODO rethrow the exception in order to build proper response
			// message to the front-end
		}
	}

	private void loadMailSetting(Properties properties) {
		InputStream in = EmailSender.class
				.getResourceAsStream(MAIL_SETTINGS_PROPERTIES_PATH);
		try {
			properties.load(in);
			in.close();
		} catch (IOException ioe) {
			// TODO Log the exception like: LoggerUtil.error(ioe);
			// TODO rethrow the exception in order to build proper response
			// message to the front-end
		}
	}
}