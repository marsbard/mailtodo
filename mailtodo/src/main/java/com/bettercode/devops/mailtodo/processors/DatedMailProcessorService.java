package com.bettercode.devops.mailtodo.processors;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.bson.Document;
import org.joda.time.DateTime;

import com.bettercode.devops.mailtodo.i18n.I18n;
import com.bettercode.devops.mailtodo.templates.TemplateProcessorException;

public class DatedMailProcessorService  extends BasicProcessorService implements Processor {
	
	public static final int DEFAULT_MIN = 0;
	public static final int DEFAULT_HOUR = 6;
	private I18n i18n;
	
	public void process(Exchange exchange) throws Exception {
		
		Message in = exchange.getIn();
		
		String to = (String) in.getHeader("to");
		
		if(to.matches("^tomorrow@.*")){
			DateTime now = new DateTime(new Date());
			
			DateTime tomorrow = new DateTime(
					now.getYear(),
					now.getMonthOfYear(),
					now.getDayOfMonth(),
					DEFAULT_HOUR,
					DEFAULT_MIN
					).plusDays(1);
			enqueueScheduledMessage(in, to, tomorrow);
		}
		
		if(to.matches("^[0-9]{8}@.*$")){
			System.out.println("Found match for: \"^[0-9]{8}@.*$\"");
			System.out.println(in.getBody());			
			System.out.println("timestamp was " + in.getHeader("timestamp"));
		
			
			String numDate = to.split("@")[0];
			
			String day = numDate.substring(6);
			String month = numDate.substring(4, 6);
			String year = numDate.substring(0,4);

			DateTime at=null;
			try {
				at = new DateTime(
						Integer.parseInt(year), 
						Integer.parseInt(month), 
						Integer.parseInt(day),
						DEFAULT_HOUR, DEFAULT_MIN
						);
			} catch (Exception e) {
				outMail.enqueueErrorMail(i18n.ERR_BAD_DATETIME + ": " + e.getLocalizedMessage(),(String) in.getHeader("from"));
				return;
			}	
			
			String msg = validDate(at);
			if(msg != ""){
				outMail.enqueueErrorMail(msg, (String) in.getHeader("from"));
			} else {
				
				enqueueScheduledMessage(in, to, at);
			}
		}
		
	}

	private void enqueueScheduledMessage(Message in, String to, DateTime at)
			throws TemplateProcessorException {
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("from", in.getHeader("from"));
		attrs.put("to", to);
		attrs.put("timestamp", in.getHeader("timestamp"));
		attrs.put("body", in.getBody());
		Document doc = mongo.createDoc(attrs);
		mongo.store("messages", doc);

		Map<String, Object> sattrs = new HashMap<String, Object>();
		sattrs.put("to", in.getHeader("from"));
		sattrs.put("at", at.toDate());
		sattrs.put("msg_id", doc.get("_id"));
		Document sched = mongo.createDoc(sattrs);
		mongo.store("schedule", sched);
		
		outMail.enqueueSuccessMail("Success: Some kind of success", (String) in.getHeader("from"), doc.get("_id").toString());
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

}
