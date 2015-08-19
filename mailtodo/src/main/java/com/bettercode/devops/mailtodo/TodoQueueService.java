package com.bettercode.devops.mailtodo;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;


public class TodoQueueService implements Processor {
	
	private ActiveMQQueue queue;
	private Session session;
	private MessageConsumer consumer;

	public TodoQueueService() throws JMSException, IOException{
		
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();
		queue = new ActiveMQQueue(App.AMQ_ENDPOINT);
		
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(queue);
	}

	public void process(Exchange exchange) throws Exception {

		Message in = exchange.getIn();
		
		List<Message> msgs = new ArrayList<Message>();
		
		int timeout = 50;
		for (int i=0; i< 10; i++){
			Message msg = (Message) consumer.receive(timeout);
			if(msg==null) break;
			msgs.add(msg);
		}
		
		Map<String, Object> root = new HashMap<String, Object>();
		
		root.put("messages", msgs);
		
//		Template listView = cfg.getTemplate("listView.ftl");
//		Writer w = new StringWriter();
//		listView.process(root, w);
//		
//		Message out = new DefaultMessage();
//		out.setBody(w.toString());
//		exchange.setOut(out);
	}

}
