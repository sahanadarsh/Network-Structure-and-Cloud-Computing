package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import com.amazonaws.lambda.datamodel.Course;
import com.amazonaws.lambda.datamodel.DynamoDbConnector;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AddNewCourseToCourseTable implements RequestHandler<Course, String> {

	@Override
	public String handleRequest(Course course, Context context) {
		context.getLogger().log("Input: " + course);
		Course newCourse = new Course();
		DynamoDbConnector dynamoDbConnector = new DynamoDbConnector();
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		boolean profExist = doesProfessorExist(course.getProfessorId().toLowerCase());
		if(profExist) {
			newCourse.setcourseName(course.getcourseName());
			newCourse.setProfessorId(course.getProfessorId());
			newCourse.setDepartment(course.getDepartment());
			newCourse.setBoardName(" ");
			newCourse.setRegisteredStudents(new ArrayList<String>());
			newCourse.setStudentTA(" ");
			newCourse.setNotificationTopic(" ");
			mapper.save(newCourse);
		}
		else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("professor doesn't exit").build());
		}
		return newCourse.getcourseName();
	}

	public static boolean doesProfessorExist(String prof) {
		List<String> professors = getProfNameList();
		return professors.contains(prof);
	}

	public static List<String> getProfNameList() {
		List<String> professors = new ArrayList<>();
		DynamoDbConnector dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		AmazonDynamoDB client = dynamoDbConnector.getClient(); 
		ScanRequest scanRequest = new ScanRequest().withTableName("professor");
		ScanResult scanResult = client.scan(scanRequest);
		for (Map<String, AttributeValue> profs : scanResult.getItems()){
			try{
				AttributeValue v = profs.get("professorId");
				String professorName = v.getS();
				professors.add(professorName.toLowerCase());
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return professors;
	}

}