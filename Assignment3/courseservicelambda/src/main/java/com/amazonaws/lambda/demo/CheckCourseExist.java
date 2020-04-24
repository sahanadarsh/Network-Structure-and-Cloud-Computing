package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.lambda.datamodel.Course;
import com.amazonaws.lambda.datamodel.DynamoDbConnector;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CheckCourseExist implements RequestHandler<Course, Boolean> {

	@Override
	public Boolean handleRequest(Course course, Context context) {		
		boolean value =doesCourseExit(course.getcourseName().toLowerCase());
		return value;
	}

	public static boolean doesCourseExit(String courseName) {
		List<String> courses = getCourseNameList();
		return courses.contains(courseName);
	}

	public static List<String> getCourseNameList() {
		List<String> courses = new ArrayList<>();
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
				courses.add(courseName.toLowerCase());
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return courses;
	}

}