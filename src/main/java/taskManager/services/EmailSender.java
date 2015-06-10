package taskManager.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

@Stateless
public class EmailSender {
	private static final String MAIL_BODY_TEMPLATE_PATH = "/mail-body-template.vm";
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
	public void sendEmail(String senderEmail, String receiverEmail,
			String subject, Map<String, Object> mailParams) {

		// initialize velocity engine
		VelocityEngine ve = new VelocityEngine();
		initVelocityEngine(ve);

		// get mail template
		Template t = ve.getTemplate(MAIL_BODY_TEMPLATE_PATH);
		t.setEncoding(UTF8);

		// make a context
		VelocityContext context = new VelocityContext(mailParams);

		// render a template
		StringWriter stringWriter = new StringWriter();
		ve.mergeTemplate(t.getName(), UTF8, context, stringWriter);
		String registrationDetails = stringWriter.toString();

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

	private void initVelocityEngine(VelocityEngine ve) {
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		ve.init();
	}
}