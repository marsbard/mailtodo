package com.bettercode.devops.mailtodo.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;

public class MongoConnector {

	private MongoClient mongo;
	private MongoDatabase db;
	private Object credential;

	public MongoConnector(){
		mongo = new MongoClient("localhost", 27017);
		
		db = mongo.getDatabase( "postits" );

	}
	
}
