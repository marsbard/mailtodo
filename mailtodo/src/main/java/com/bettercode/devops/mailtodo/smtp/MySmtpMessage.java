package com.bettercode.devops.mailtodo.smtp;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Header;

public class MySmtpMessage {

	List<Header> headers;
	private String body;
	private String to;
	private String from;
	
	public MySmtpMessage(String from, String to, String body, List<Header> headers){
		this.from = from;
		this.to = to;
		this.body = body;
		this.headers = headers;
	}

	public MySmtpMessage(String from, String to, String body){
		this.from = from;
		this.to = to;
		this.body = body;
		this.headers = new ArrayList<Header>();
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String toString(){
		String out = "To: " + to + "\n";
		out += "From: "  + from + "\n";
		for(Header h: headers){
			out += h.getName() + ": " + h.getValue() + "\n";
		}
		out += "\n" + body;
		
		return out;
	}
}
