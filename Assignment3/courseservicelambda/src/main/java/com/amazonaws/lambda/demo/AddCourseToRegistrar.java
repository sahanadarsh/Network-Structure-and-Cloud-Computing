package com.amazonaws.lambda.demo;

import java.util.Map;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.lambda.datamodel.Course;
import com.amazonaws.lambda.datamodel.DynamoDbConnector;
import com.amazonaws.lambda.datamodel.Registrar;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AddCourseToRegistrar implements RequestHandler<Course, String> {

	@Override
	public String handleRequest(Course course, Context context) {
		context.getLogger().log("Input: " + course);
		DynamoDBMapper mapper = new DynamoDBMapper(getClient());
		Registrar newRegister = new Registrar();
		newRegister.setDepartment(course.getDepartment());
		String courseId = getCourseNameList(course.getcourseName());
		newRegister.setOfferingId(courseId);    
		newRegister.setOfferingType(course.getcourseName());
		newRegister.setPerUnitPrice(4000);
		mapper.save(newRegister);
		String offeringType = newRegister.getOfferingType();
		return offeringType;
	}

	public static String getCourseNameList(String courseName1) {
		DynamoDbConnector dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		AmazonDynamoDB  client = dynamoDbConnector.getClient(); 
		ScanRequest  scanRequest = new ScanRequest().withTableName("course");
		ScanResult  scanResult = client.scan(scanRequest);
		for (Map<String, AttributeValue> course : scanResult.getItems()){
			try{
				AttributeValue v = course.get("courseName");
				String courseName = v.getS();
				if(courseName.equals(courseName1)) {
					AttributeValue v1 = course.get("id");
					String id = v1.getS();
					return id;
				}
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return "";
	}

	public AmazonDynamoDB getClient() {
		return AmazonDynamoDBClientBuilder
				.standard()
				.withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
				.withRegion("us-west-2")
				.build();
	}

}