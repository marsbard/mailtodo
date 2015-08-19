package com.bettercode.devops.mailtodo.mails;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bettercode.devops.mailtodo.App;
import com.bettercode.devops.mailtodo.smtp.MailOutQueue;
import com.bettercode.devops.mailtodo.smtp.MySmtpMessage;

public class TimeScheduleTests {

	private static Random rand;
	private static App app;
	private static MailOutQueue moQ;

	private String randString(int len) {
		String out = "";
		for (int i = 0; i < len; i++) {
			// http://stackoverflow.com/a/19802302/370191
			out += (char) (rand.nextInt(25) + 97);
		}
		return out;
	}

	private void sendSmtpMail(String to) {
		String body = randString(rand.nextInt(70)) + "\n";
		body += randString(rand.nextInt(70)) + "\n";
		body += randString(rand.nextInt(70)) + "\n";
		body += randString(rand.nextInt(70)) + "\n";
		
		
		sendSmtpMail(to, "user@localhost", "Some random "
				+ randString(10), body);
	}

	private void sendSmtpMail(String to, String from, String subject,
			String body) {
		// Get system properties
		Properties props = System.getProperties();

		// Setup mail server
		props.setProperty("mail.smtp.host", "localhost");
		props.put("mail.smtp.port", App.SMTP_PORT);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(props);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(body);

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rand = new Random();
		app = new App();
		app.go();

		moQ = app.getMailOutQueue();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Thread.sleep(1000);
		app.stop();
	}

	@Before
	public void setUp() throws Exception {
		moQ.setRunning(false); // we want to inspect the results
		moQ.clearBuffer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDateInPast() throws IOException, InterruptedException {
		
		sendSmtpMail("19700101@postits.non");

		MySmtpMessage msg = moQ.pollHead();

		assertThat(msg).isNotNull();
		assertThat(msg.getBody()).isNotNull();
		
		String search = App.RESPONSE_MARKER + ": Error";

		String body = msg.getBody();
		
		assertThat(body).contains(search);
	}

	@Test
	public void testTomorrow() throws InterruptedException {
		sendSmtpMail("tomorrow@postits.blah");

		MySmtpMessage msg = moQ.pollHead();

		assertThat(msg).isNotNull();
		assertThat(msg.getBody()).isNotNull();
		
		String search = App.RESPONSE_MARKER + ": Success";

		String body = msg.getBody();
		
		assertThat(body).contains(search);
	}
	
}
