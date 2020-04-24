package com.amazonaws.lambda.demo;

import java.util.Map;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.lambda.datamodel.Board;
import com.amazonaws.lambda.datamodel.Course;
import com.amazonaws.lambda.datamodel.DynamoDbConnector;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;

public class CreateBoard implements RequestHandler<Course, String> {

	@Override
	public String handleRequest(Course course, Context context) {
		context.getLogger().log("Input: " + course);
		DynamoDBMapper mapper = new DynamoDBMapper(getClient());	        
		Board board = new Board();
		board.setCourseName(course.getcourseName());
		board.setBoardName(course.getcourseName()+" board");
		mapper.save(board);
		setCourseAttributes(course,board);
		return board.getBoardName();
	}

	public void setCourseAttributes(Course course,Board board) {
		DynamoDbConnector dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		DynamoDBMapper mapper;
		AmazonDynamoDB  client = dynamoDbConnector.getClient(); 
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		ScanRequest  scanRequest = new ScanRequest().withTableName("course");
		ScanResult  scanResult = client.scan(scanRequest);
		Course crse = null;
		for (Map<String, AttributeValue> courseItem : scanResult.getItems()){
			try{
				AttributeValue v = courseItem.get("courseName");
				String crseName = v.getS();
				if(crseName.equalsIgnoreCase(course.getcourseName())) {
					v = courseItem.get("id");
					String id = v.getS();
					crse = mapper.load(Course.class, id);
					crse.setBoardName(board.getBoardName());
					String topicArn = createTopic();
					crse.setNotificationTopic(topicArn);
					mapper.save(crse);
					break;
				}     
				else {
					System.out.println("Course does not exist");
				}
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
	}

	public String  createTopic() {
		AmazonSNS snsClient = AmazonSNSClientBuilder
				.standard()
				.withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
				.withRegion("us-west-2")
				.build();
		final CreateTopicRequest createTopicRequest = new CreateTopicRequest("MyTopic");
		final CreateTopicResult createTopicResponse = snsClient.createTopic(createTopicRequest);
		System.out.println("TopicArn:" + createTopicResponse.getTopicArn());
		System.out.println("CreateTopicRequest: " + snsClient.getCachedResponseMetadata(createTopicRequest));
		return createTopicResponse.getTopicArn();
	}

	public AmazonDynamoDB getClient() {
		return AmazonDynamoDBClientBuilder
				.standard()
				.withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
				.withRegion("us-west-2")
				.build();
	}
}
