package com.bettercode.devops.mailtodo.smtp;

import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author mcosgrave
 *
 */
public class MailOutQueue {

	private boolean running=true;

	private SynchronousQueue<MySmtpMessage>  buffer;
	
	public MailOutQueue(){
		clearBuffer();
	}
	
	public void setRunning(boolean b) {
		running = b;
		if(running){
			// TODO transfer our java.util.Queue to the real JMS queue
		}
	}
	
	public void addToQueue(MySmtpMessage message){
		System.out.println("Return mail:\n" + message);
		if(running){
			// TODO write the message to the JMS queue
		} else {
			buffer.add(message);
		}
	}

	public void clearBuffer() {
		buffer = new SynchronousQueue<MySmtpMessage>();
	}

	public MySmtpMessage pollHead() throws InterruptedException {
		return buffer.poll(500, TimeUnit.MILLISECONDS);
	}

}
