package com.csye6225.spring2020.courseservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.csye6225.spring2020.courseservice.datamodel.Board;
import com.csye6225.spring2020.courseservice.datamodel.Course;
import com.csye6225.spring2020.courseservice.datamodel.DynamoDbConnector;
import com.csye6225.spring2020.courseservice.exception.DataNotFoundException;

public class BoardService {

	static DynamoDbConnector dynamoDbConnector;
	static DynamoDBMapper mapper;
	static AmazonDynamoDB client;
	static ScanRequest scanRequest;
	static ScanResult scanResult;

	public BoardService(){
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("board");
	}

	// Getting a list of all boards 
	public List<Board> getAllBoards(){
		scanResult = client.scan(scanRequest);
		ArrayList<Board> list = new ArrayList<>();
		for (Map<String, AttributeValue> boardItem : scanResult.getItems()){
			try{
				AttributeValue v = boardItem.get("id");
				String id = v.getS();
				Board board = mapper.load(Board.class, id);
				list.add(board);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("No boards in the system");
		}
		return list;
	}

	//adding a board
	public Board addBoard(Board board) {
		scanResult = client.scan(scanRequest);
		boolean found = false;
		for (Map<String, AttributeValue> boardItem : scanResult.getItems()){
			try{
				AttributeValue v = boardItem.get("boardName");
				String boardName = v.getS();
				if(boardName.equalsIgnoreCase(board.getBoardName())) {
					found = true;
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(!found) {
			if(CourseService.doesCourseExit(board.getCourseName())) {
				Board brd = new Board(board.getId(), board.getBoardName(), board.getCourseName());
				mapper.save(brd);
				Board boardAdded = mapper.load(Board.class, brd.getId());
				return boardAdded;
			} else {
				throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Course trying to add doesn't exit").build());
			}
		}else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Board already exit").build());
		}

	}

	//helper function to get board
	public static Board getBrd(String boardName) {
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("board");
		scanResult = client.scan(scanRequest);
		Board board = null;
		for (Map<String, AttributeValue> boardItem : scanResult.getItems()){
			try{
				AttributeValue v = boardItem.get("boardName");
				String brdName = v.getS();
				if(brdName.equalsIgnoreCase(boardName)) {
					v = boardItem.get("id");
					String id = v.getS();
					board = mapper.load(Board.class, id);
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return board;
	}

	// Getting One board
	public static Board getBoard(String boardName) {
		Board board = getBrd(boardName);
		if(board == null) {
			throw new DataNotFoundException("Board with boardName " + boardName + " not found");
		}
		return board;
	}

	// Deleting a Board
	public Board deleteBoard(String boardName) {
		Board board = getBrd(boardName);
		if(board == null) {
			throw new DataNotFoundException("Board with boardName " + boardName + " not found");
		}
		mapper.delete(board);
		return board;
	}

	// Updating Board
	public Board updateBoard(String boardName, Board board) {	
		Board brd = getBrd(boardName);
		if(brd == null) {
			throw new DataNotFoundException("Board with boardName " + boardName + " not found");
		}
		brd.setBoardName(board.getBoardName());
		brd.setCourseName(board.getCourseName());
		mapper.save(brd);
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT).build();
		Board updatedBoard = mapper.load(Board.class, brd.getId(), config);
		return updatedBoard;
	}

	// Get course by a Board 
	public static Course getCourseByBoard(String boardName) {
		Board board = getBoard(boardName);
		String courseName = board.getCourseName();
		Course course = CourseService.getCourse(courseName);
		if(course == null) {
			throw new DataNotFoundException("Didn't find course for the boardName " + boardName);
		}else {
			return course;
		}
	}

	//get all boards of a particular course
	public List<Board> getBoardsByCourse(String courseName){
		scanResult = client.scan(scanRequest);
		ArrayList<Board> list = new ArrayList<>();
		for (Map<String, AttributeValue> boardItem : scanResult.getItems()){
			try{
				AttributeValue v = boardItem.get("courseName");
				String crsName = v.getS();
				if(crsName.equalsIgnoreCase(courseName)) {
					v = boardItem.get("id");
					String id = v.getS();
					Board board = mapper.load(Board.class, id);
					list.add(board);
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("Didn't find boards for the courseName " + courseName);
		}
		return list;
	}


	public static boolean doesBoardExit(String boardId) {
		List<String> boards = new ArrayList<>();
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("board");
		scanResult = client.scan(scanRequest);
		for (Map<String, AttributeValue> prof : scanResult.getItems()){
			try{
				AttributeValue v = prof.get("boardName");
				String bId = v.getS();
				boards.add(bId);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return boards.contains(boardId);
	}

}
