package com.csye6225.spring2020.courseservice.datamodel;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class DynamoDbConnector {

	static AmazonDynamoDB dynamoDb ;

	public void init() {
		if (dynamoDb == null) {	
			dynamoDb = AmazonDynamoDBClientBuilder
					.standard()
					.withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
					.withRegion("us-west-2")
					.build();
			System.out.println("I created the client");
		} 
	}

	public AmazonDynamoDB getClient() {
		return dynamoDb;
	}
}
