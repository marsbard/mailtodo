package com.bettercode.devops.mailtodo.smtp;

import java.util.HashMap;
import java.util.Map;

import com.bettercode.devops.mailtodo.App;
import com.bettercode.devops.mailtodo.templates.TemplateProcessor;
import com.bettercode.devops.mailtodo.templates.TemplateProcessorException;

public class OutMail {

	private TemplateProcessor templateProcessor;
	private MailOutQueue mailOutQueue;
	private String mailServer;
	private int mailServerPort;

	public void enqueueErrorMail(String msg, String to) throws TemplateProcessorException {
		
		Map<String, String> model = new HashMap<String, String>();
		model.put("errorMessage", App.RESPONSE_MARKER + ": " + msg);
		String body = templateProcessor.process("error-mail", model);
		
		MySmtpMessage message = new MySmtpMessage(App.APP_EMAIL_FROM, to, body);
		mailOutQueue.addToQueue(message );
	}

	public void enqueueSuccessMail(String msg, String to, String msgDocId) throws TemplateProcessorException {
		
		Map<String, String> model = new HashMap<String, String>();
		model.put("successMessage", App.RESPONSE_MARKER + ": " + msg);
		model.put("msgDocId", msgDocId);
		
		String body = templateProcessor.process("success-mail", model);
		
		MySmtpMessage message = new MySmtpMessage(App.APP_EMAIL_FROM, to, body);
		mailOutQueue.addToQueue(message );
	}

	public void setMailOutQueue(MailOutQueue mailOutQueue) {
		this.mailOutQueue = mailOutQueue;
	}

	public void setTemplateProcessor(TemplateProcessor templateProcessor) {
		this.templateProcessor = templateProcessor;
	}


}
