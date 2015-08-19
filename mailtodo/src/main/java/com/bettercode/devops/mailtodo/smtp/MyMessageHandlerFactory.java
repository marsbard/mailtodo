package com.bettercode.devops.mailtodo.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;

import com.bettercode.devops.mailtodo.App;

public class MyMessageHandlerFactory implements MessageHandlerFactory {

	private static final String DIRECT_SMTP_IN = "direct:smtp-in";
	private CamelContext context;
	
	public MessageHandler create(MessageContext ctx) {
		return new Handler(ctx);
	}

	class Handler implements MessageHandler {
		MessageContext ctx;
		
		// TODO HMMMM very not sure about this, find a better way to get the 'to'
		// address into the camel header (subetha docs...) or else make things
		// synchronised
		private String lastTo;
		private String lastFrom;

		public Handler(MessageContext ctx) {
			this.ctx = ctx;
		}

		public void from(String from) throws RejectException {
			System.out.println("FROM:" + from);
			lastFrom = from;
		}

		public void recipient(String recipient) throws RejectException {
			System.out.println("RECIPIENT:" + recipient);
			lastTo = recipient;
					
		}

		public void data(InputStream data) throws IOException {
			String inBody = this.convertStreamToString(data);
			
			ProducerTemplate template = context.createProducerTemplate();
			Map<String, Object> headers = new HashMap<String, Object>();
			
			headers.put("to", lastTo);
			headers.put("from", lastFrom);
			headers.put("timestamp",  System.currentTimeMillis()/1000);
			template.sendBodyAndHeaders(DIRECT_SMTP_IN, inBody, headers );

		}

		public void done() {
			System.out.println("Finished");
		}

		public String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return sb.toString();
		}

	}

	public void setCamelContext(CamelContext context) throws MailTodoSmtpException {
		this.context = context;
		
		try {
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					from(DIRECT_SMTP_IN)
					.to(App.AMQ_ENDPOINT);
				}
			});
		} catch (Exception e) {
			throw new MailTodoSmtpException(e);
		}
	}
}
