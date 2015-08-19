package com.bettercode.devops.mailtodo;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.ServiceSupport;

import com.bettercode.devops.mailtodo.mongo.MongoConnector;
import com.bettercode.devops.mailtodo.processors.DatedMailProcessorService;
import com.bettercode.devops.mailtodo.smtp.MailOutQueue;
import com.bettercode.devops.mailtodo.smtp.MailTodoSmtpException;
import com.bettercode.devops.mailtodo.smtp.OutMail;
import com.bettercode.devops.mailtodo.smtp.SmtpServer;
import com.bettercode.devops.mailtodo.templates.TemplateProcessor;
import com.bettercode.devops.mailtodo.templates.TemplateProcessorException;

/**
 * Hello world!
 *
 */
public class App 
{
	public static final int SMTP_PORT = 2525;
	public static final String AMQ_ENDPOINT = "activemq:queue:mailtodo";
	protected static final String WEB_ENDPOINT = "jetty:http://0.0.0.0:8191/todoq";
	public static final String TEMPLATE_STORAGE_DIR = "src/main/resources/templates";
	public static final String RESPONSE_MARKER = "-RESULT";
	private static final Boolean IS_CAMEL_DEBUGGING = false;
	public static final String APP_EMAIL_FROM = "postits@wherever.wat";
	public static final String MAIL_TEMPLATES_PATH = "src/main/resources/templates";

	public static void main( String[] args ) throws Exception
    {
        (new App()).go();
        System.out.println( AMQ_ENDPOINT);
        System.out.println( WEB_ENDPOINT);
    }


	private MailOutQueue mailOutQueue;
	private SmtpServer smtpServer;
	private ActiveMQConnectionFactory connectionFactory;
	private Connection conn;
	private DefaultCamelContext context;
	private MongoConnector mongo;
	private OutMail outMail;

	public void go() throws MailTodoException, JMSException, MailTodoSmtpException, TemplateProcessorException {
		
		mongo = new MongoConnector();
		
		mailOutQueue = new MailOutQueue();
		
		connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
		
		conn = connectionFactory.createConnection();
		
		context = new DefaultCamelContext();
		context.setTracing(IS_CAMEL_DEBUGGING); 

		context.addComponent("activemq", activeMQComponent("vm://localhost?broker.persistent=true"));
		
		smtpServer = new SmtpServer();
		smtpServer.setCamelContext(context);
		smtpServer.start();

		TemplateProcessor templateProcessor = new TemplateProcessor();

		
		outMail = new OutMail();
		outMail.setMailOutQueue(mailOutQueue);
		outMail.setTemplateProcessor(templateProcessor);
		
		final DatedMailProcessorService datedMailProcessorService = new DatedMailProcessorService();
		datedMailProcessorService.setCamelContext(context);
		datedMailProcessorService.setMongo(mongo);
		datedMailProcessorService.setOutMail(outMail);
		
		try {
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					from(AMQ_ENDPOINT)
					.choice()
					.when(header("to").regex("^[0-9]{8}@.*$"))
						.process(datedMailProcessorService)
					.when(header("to").regex("^[0-9]{1,2}(a|p)m@.*$"))
						.process(datedMailProcessorService)
					.otherwise()
						.process(new TodoQueueService());
				}
			});
		} catch (Exception e) {
			throw new MailTodoException(e);
		}
		
		try {
			context.start();
		} catch (Exception e) {
			throw new MailTodoException(e);
		}
	}

	public void stop() {
		smtpServer.stop();
		try {
			context.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


    public class MailTodoException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5118547813311696832L;

		public MailTodoException() {
			// TODO Auto-generated constructor stub
		}

		public MailTodoException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		public MailTodoException(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}

		public MailTodoException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public MailTodoException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			// TODO Auto-generated constructor stub
		}

	}


	public MailOutQueue getMailOutQueue() {
		return mailOutQueue;
	}



}
