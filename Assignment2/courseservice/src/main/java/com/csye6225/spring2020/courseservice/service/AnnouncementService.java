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
import com.csye6225.spring2020.courseservice.datamodel.Announcement;
import com.csye6225.spring2020.courseservice.datamodel.Board;
import com.csye6225.spring2020.courseservice.datamodel.DynamoDbConnector;
import com.csye6225.spring2020.courseservice.exception.DataNotFoundException;

public class AnnouncementService {

	static DynamoDbConnector dynamoDbConnector;
	static DynamoDBMapper mapper;
	static AmazonDynamoDB client;
	static ScanRequest scanRequest;
	static ScanResult scanResult;

	public AnnouncementService(){
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("announcement");
	}

	// Getting a list of all Announcements 
	public List<Announcement> getAllAnnouncements(){
		scanResult = client.scan(scanRequest);
		ArrayList<Announcement> list = new ArrayList<>();
		for (Map<String, AttributeValue> announcementItem : scanResult.getItems()){
			try{
				AttributeValue v = announcementItem.get("id");
				String id = v.getS();
				Announcement announcement = mapper.load(Announcement.class, id);
				list.add(announcement);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("No Announcements in the system");
		}
		return list;
	}

	//adding Announcement
	public Announcement addAnnouncement(Announcement announcement) {
		scanResult = client.scan(scanRequest);
		boolean found = false;
		for (Map<String, AttributeValue> announcementItem : scanResult.getItems()){
			try{
				AttributeValue v = announcementItem.get("announcementName");
				String announcementName = v.getS();
				if(announcementName.equalsIgnoreCase(announcement.getAnnouncementName())) {
					found = true;
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(!found) {
			if(BoardService.doesBoardExit(announcement.getBoardName()) && announcement.getAnnouncementText().length() <= 160) {
				Announcement announce = new Announcement(announcement.getId(), announcement.getAnnouncementName(), announcement.getAnnouncementText(), announcement.getBoardName());
				mapper.save(announce);
				Announcement announcementAdded = mapper.load(Announcement.class, announce.getId());
				return announcementAdded;
			} else {
				throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Board trying to add doesn't exit or announcement text is more than 160 characters").build());
			}
		}else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Announcement already exit").build());
		}

	}

	//helper function to get announcement
	public Announcement getAnnounce(String announcementName) {
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("announcement");
		scanResult = client.scan(scanRequest);
		Announcement announcement = null;
		for (Map<String, AttributeValue> announcementItem : scanResult.getItems()){
			try{
				AttributeValue v = announcementItem.get("announcementName");
				String announceName = v.getS();
				if(announceName.equalsIgnoreCase(announcementName)) {
					v = announcementItem.get("id");
					String id = v.getS();
					announcement = mapper.load(Announcement.class, id);
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return announcement;
	}

	// Getting One Announcement
	public Announcement getAnnouncement(String announcementName) {
		Announcement announcement = getAnnounce(announcementName);
		if(announcement == null) {
			throw new DataNotFoundException("Announcement with announcementName " + announcementName + " not found");
		}
		return announcement;
	}

	// Deleting a Announcement
	public Announcement deleteAnnouncement(String announcementName) {
		Announcement announcement = getAnnounce(announcementName);
		if(announcement == null) {
			throw new DataNotFoundException("Announcement with announcementName " + announcementName + " not found");
		}
		mapper.delete(announcement);
		return announcement;
	}

	// Updating Announcement Info
	public Announcement updateAnnouncement(String announcementName, Announcement announcement) {	
		Announcement announce = getAnnounce(announcementName);
		if(announce == null) {
			throw new DataNotFoundException("Announcement with announcementName " + announcementName + " not found");
		}
		announce.setAnnouncementName(announcement.getAnnouncementName());
		if(announcement.getAnnouncementText().length() <= 160) {
			announce.setAnnouncementText(announcement.getAnnouncementText());
		}else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("announcement text is more than 160 characters").build());
		}
		announce.setBoardName(announcement.getBoardName());
		mapper.save(announce);
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT).build();
		Announcement updatedAnnouncement = mapper.load(Announcement.class, announce.getId(), config);
		return updatedAnnouncement;
	}

	// Get board by a announcement
	public Board getBoardByAnnouncement(String announcementName) {
		Announcement announcement = getAnnouncement(announcementName);
		String boardName = announcement.getBoardName();
		Board board = BoardService.getBoard(boardName);
		if(board == null) {
			throw new DataNotFoundException("Didn't find board for the announcementName " + announcementName);
		}else {
			return board;
		}
	}

	//get all announcements of a particular board
	public List<Announcement> getAnnouncementsByBoardName(String boardName){
		scanResult = client.scan(scanRequest);
		ArrayList<Announcement> list = new ArrayList<>();
		for (Map<String, AttributeValue> boardItem : scanResult.getItems()){
			try{
				AttributeValue v = boardItem.get("boardName");
				String brdName = v.getS();
				if(brdName.equalsIgnoreCase(boardName)) {
					v = boardItem.get("id");
					String id = v.getS();
					Announcement announcement = mapper.load(Announcement.class, id);
					list.add(announcement);
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("Didn't find announcements for the boardName " + boardName);
		}
		return list;
	}

}
