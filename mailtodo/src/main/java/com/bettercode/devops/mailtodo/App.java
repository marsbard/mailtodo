package com.bettercode.devops.mailtodo;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Hello world!
 *
 */
public class App 
{

	protected static final String SMTP_ENDPOINT = "smtp:localhost:2525";
	protected static final String AMQ_ENDPOINT = "activemq:queue:mailtodo";
	protected static final String WEB_ENDPOINT = "jetty:http://0.0.0.0:8191/todoq";
	public static final String TEMPLATE_STORAGE_DIR = "src/main/resources/templates";

	public static void main( String[] args ) throws Exception
    {
        (new App()).go();
        System.out.println( SMTP_ENDPOINT);
        System.out.println( AMQ_ENDPOINT);
        System.out.println( WEB_ENDPOINT);
    }

	private void go() throws MailTodoException, JMSException {
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
		
		Connection conn = connectionFactory.createConnection();
		
		CamelContext context = new DefaultCamelContext();
		context.addComponent("activemq", activeMQComponent("vm://localhost?broker.persistent=true"));
		
		try {
			context.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					from(SMTP_ENDPOINT)
						.to(AMQ_ENDPOINT);
					
//					from(WEB_ENDPOINT).process(new TodoQueueService());
					
					from(AMQ_ENDPOINT).process(new TodoQueueService());
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

}
