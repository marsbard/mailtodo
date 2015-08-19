package com.bettercode.devops.mailtodo.smtp;

import org.apache.camel.CamelContext;
import org.subethamail.smtp.server.SMTPServer;

import com.bettercode.devops.mailtodo.App;

public class SmtpServer {
	
	private SMTPServer smtpServer;
	private CamelContext context;

	public void start() throws MailTodoSmtpException{
        MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory() ;
        myFactory.setCamelContext(context);
        smtpServer = new SMTPServer(myFactory);
        smtpServer.setPort(App.SMTP_PORT);
        smtpServer.start();
	}

	public void stop() {
		smtpServer.stop();
	}

	public void setCamelContext(CamelContext context) {
		this.context = context;
	}
}
