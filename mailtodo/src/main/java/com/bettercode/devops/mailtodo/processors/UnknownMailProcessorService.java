package com.bettercode.devops.mailtodo.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class UnknownMailProcessorService implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO implement processing for unknown 'to' addresses
		// probably just chuck back an error
	}

}
