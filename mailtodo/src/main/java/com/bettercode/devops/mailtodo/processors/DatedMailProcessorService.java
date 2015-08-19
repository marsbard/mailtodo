package com.bettercode.devops.mailtodo.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.bson.Document;
import org.joda.time.DateTime;

import com.bettercode.devops.mailtodo.i18n.I18n;
import com.bettercode.devops.mailtodo.mongo.MongoConnector;
import com.bettercode.devops.mailtodo.smtp.OutMail;

public class DatedMailProcessorService implements Processor {
	
	private static final int DEFAULT_MIN = 0;
	private static final int DEFAULT_HOUR = 6;
	private CamelContext context;
	private MongoConnector mongo;
	private OutMail outMail;
	private I18n i18n;
	
	public void setCamelContext(CamelContext context){
		this.context = context;
	}

	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
		
		String to = (String) in.getHeader("to");
		
		if(to.matches("^[0-9]{8}@.*$")){
			System.out.println("Found match for: \"^[0-9]{8}@.*$\"");
			System.out.println(in.getBody());			
			System.out.println("timestamp was " + in.getHeader("timestamp"));
		
			
			String numDate = to.split("@")[0];
			
			String day = numDate.substring(6);
			String month = numDate.substring(4, 6);
			String year = numDate.substring(0,4);

			DateTime dt=null;
			try {
				dt = new DateTime(
						Integer.parseInt(year), 
						Integer.parseInt(month), 
						Integer.parseInt(day),
						DEFAULT_HOUR, DEFAULT_MIN
						);
			} catch (Exception e) {
				outMail.enqueueErrorMail(i18n.ERR_BAD_DATETIME + ": " + e.getLocalizedMessage(),(String) in.getHeader("from"));
				return;
			}	
			

			

	

			String msg = validDate(dt);
			if(msg != ""){
				outMail.enqueueErrorMail(msg,(String) in.getHeader("from"));
			} else {
				
				Map<String, Object> attrs = new HashMap<String, Object>();
				attrs.put("from", in.getHeader("from"));
				attrs.put("to", to);
				attrs.put("timestamp", in.getHeader("timestamp"));
				attrs.put("body", in.getBody());
				Document doc = mongo.createDoc(attrs);
				mongo.store("messages", doc);
	
				Map<String, Object> sattrs = new HashMap<String, Object>();
				sattrs.put("to", in.getHeader("from"));
				sattrs.put("at", dt.toDate());
				sattrs.put("msg_id", doc.get("_id"));
				Document sched = mongo.createDoc(sattrs);
				mongo.store("schedule", sched);
			}
		}
		
	}

	/*
	 * Check if date is valid and return with error message if not
	 */
	private String validDate(DateTime dt) {
		
		
		if(dt.isBeforeNow()){
			return i18n.ERR_DATE_BEFORE_NOW;
		}
		
		return "";
	}

	public void setMongo(MongoConnector mongo) {
		this.mongo = mongo;
	}

	public void setOutMail(OutMail outMail) {
		this.outMail = outMail;
	}

}
