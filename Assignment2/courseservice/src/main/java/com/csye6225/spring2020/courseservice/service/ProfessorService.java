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
import com.csye6225.spring2020.courseservice.datamodel.DynamoDbConnector;
import com.csye6225.spring2020.courseservice.datamodel.Professor;
import com.csye6225.spring2020.courseservice.exception.DataNotFoundException;

public class ProfessorService{

	static DynamoDbConnector dynamoDbConnector;
	static DynamoDBMapper mapper;
	static AmazonDynamoDB client;
	static ScanRequest scanRequest;
	static ScanResult scanResult;


	public ProfessorService(){
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("professor");
	}

	// Getting a list of all professor 
	public List<Professor> getAllProfessors(){
		scanResult = client.scan(scanRequest);
		ArrayList<Professor> list = new ArrayList<>();
		for (Map<String, AttributeValue> profItem : scanResult.getItems()){
			try{
				AttributeValue v = profItem.get("id");
				String id = v.getS();
				Professor professor = mapper.load(Professor.class, id);
				list.add(professor);
			} catch (NumberFormatException e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("No professors in the system");
		}
		return list;
	}

	//adding a professor
	public Professor addProfessor(Professor professor) {
		professor.setProfessorId(professor.getFirstName()+ professor.getLastName());
		scanResult = client.scan(scanRequest);
		boolean found = false;
		for (Map<String, AttributeValue> profItem : scanResult.getItems()){
			try{
				AttributeValue v = profItem.get("professorId");
				String profId = v.getS();
				if(profId.equalsIgnoreCase(professor.getProfessorId())) {
					found = true;
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(!found) {
			Professor prof = new Professor(professor.getId(), professor.getProfessorId(), professor.getFirstName(), professor.getLastName(), professor.getDepartment(), professor.getJoiningDate());
			mapper.save(prof);
			Professor profAdded = mapper.load(Professor.class, prof.getId());
			return profAdded;
		}else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Professor with same first and last name already exit").build());
		}

	}

	//helper function to get professor
	public static Professor getProf(String professorId) {
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		client = dynamoDbConnector.getClient(); 
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		scanRequest = new ScanRequest().withTableName("professor");
		scanResult = client.scan(scanRequest);
		Professor prof = null;
		for (Map<String, AttributeValue> profItem : scanResult.getItems()){
			try{
				AttributeValue v = profItem.get("professorId");
				String profId = v.getS();
				if(profId.equalsIgnoreCase(professorId)) {
					v = profItem.get("id");
					String id = v.getS();
					prof = mapper.load(Professor.class, id);
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}	
		return prof;
	}

	// Getting One Professor
	public static Professor getProfessor(String professorId) {		
		Professor prof = getProf(professorId);
		if(prof == null) {
			throw new DataNotFoundException("Professor with professorId " + professorId + " not found");
		}
		return prof;
	}

	// Deleting a professor
	public Professor deleteProfessor(String professorId) {
		Professor prof = getProf(professorId);
		if(prof == null) {
			throw new DataNotFoundException("Professor with professorId " + professorId + " not found");
		}
		mapper.delete(prof);
		return prof;
	}

	// Updating Professor Info
	public Professor updateProfessorInformation(String professorId, Professor newProf) {	
		Professor prof = getProf(professorId);
		if(prof == null) {
			throw new DataNotFoundException("Professor with professorId " + professorId + " not found");
		}
		prof.setFirstName(newProf.getFirstName());
		prof.setLastName(newProf.getLastName());
		prof.setProfessorId(newProf.getFirstName()+newProf.getLastName());
		prof.setDepartment(newProf.getDepartment());
		prof.setJoiningDate(newProf.getJoiningDate());
		mapper.save(prof);
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT).build();
		Professor updatedProfessor = mapper.load(Professor.class, prof.getId(), config);
		return updatedProfessor;
	}

	//helper function to get professor list for a particular key
	public List<Professor> getProfList(String key, String pathParam){
		scanResult = client.scan(scanRequest);
		ArrayList<Professor> list = new ArrayList<>();
		for (Map<String, AttributeValue> profItem : scanResult.getItems()){
			try{
				AttributeValue v = profItem.get(key);
				String value = v.getS();
				if(value.equalsIgnoreCase(pathParam)) {
					v = profItem.get("id");
					String id = v.getS();
					Professor professor = mapper.load(Professor.class, id);
					list.add(professor);
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return list;
	}

	// Get professors in a department 
	public List<Professor> getProfessorsByDepartment(String department){
		List<Professor> list = getProfList("department", department);
		if(list.isEmpty()) {
			throw new DataNotFoundException("Didn't find professors for the department " + department);
		}
		return list;
	}

	// Get professors by joining date
	public List<Professor> getProfessorsByJoiningDate(String joiningDate) {	
		List<Professor> list = getProfList("joiningDate", joiningDate);
		if(list.isEmpty()) {
			throw new DataNotFoundException("Didn't find professors for the joining date " + joiningDate);
		}
		return list;
	}

	//check whether the professor exit
	public static boolean doesProfessorExit(String professorId) {
		List<String> professors = new ArrayList<>();
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("professor");
		scanResult = client.scan(scanRequest);
		for (Map<String, AttributeValue> prof : scanResult.getItems()){
			try{
				AttributeValue v = prof.get("professorId");
				String profId = v.getS();
				professors.add(profId);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return professors.contains(professorId);
	}

}
