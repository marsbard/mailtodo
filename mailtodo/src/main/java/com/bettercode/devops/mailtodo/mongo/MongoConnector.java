package com.bettercode.devops.mailtodo.mongo;

import java.util.Map;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoConnector {

	private MongoClient mongo;
	private MongoDatabase db;
	private Object credential;
	private MongoCollection<Document> messages;
	private MongoCollection<Document> schedule;


	public MongoConnector(){
		mongo = new MongoClient("localhost", 27017);
		
		db = mongo.getDatabase( "postits" );

		messages = db.getCollection("messages");
		
		schedule = db.getCollection("schedule");
	}
	
	public Document createDoc(Map<String, Object> attributes){
		Document doc = new Document();
		
		for(String key: attributes.keySet()){
			doc.append(key, attributes.get(key));
		}
		return doc;
	}
	
	public Object store(String collection, Document document){
		switch(collection){
		case "messages":
			messages.insertOne(document);
		break;
		case "schedule":
			schedule.insertOne(document);
			break;
		}
		return document.get("_id");
	}
	
}
