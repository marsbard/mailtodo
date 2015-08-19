package com.bettercode.devops.mailtodo.processors;

import org.apache.camel.CamelContext;

import com.bettercode.devops.mailtodo.mongo.MongoConnector;
import com.bettercode.devops.mailtodo.smtp.OutMail;

public class BasicProcessorService {

	private CamelContext context;
	protected MongoConnector mongo;
	protected OutMail outMail;

	public void setCamelContext(CamelContext context) {
		this.context = context;
	}

	public void setMongo(MongoConnector mongo) {
		this.mongo = mongo;
	}

	public void setOutMail(OutMail outMail) {
		this.outMail = outMail;
	}

}
