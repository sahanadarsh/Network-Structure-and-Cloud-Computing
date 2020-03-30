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
import com.csye6225.spring2020.courseservice.datamodel.Course;
import com.csye6225.spring2020.courseservice.datamodel.DynamoDbConnector;
import com.csye6225.spring2020.courseservice.datamodel.Student;
import com.csye6225.spring2020.courseservice.exception.DataNotFoundException;

public class StudentService {

	static DynamoDbConnector dynamoDbConnector;
	static DynamoDBMapper mapper;
	static AmazonDynamoDB client;
	static ScanRequest scanRequest;
	static ScanResult scanResult;

	public StudentService(){
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("student");
	}

	//get all students
	public List<Student> getAllStudents() {
		scanResult = client.scan(scanRequest);
		ArrayList<Student> list = new ArrayList<>();
		for (Map<String, AttributeValue> student : scanResult.getItems()){
			try{
				AttributeValue v = student.get("id");
				String id = v.getS();
				Student stud = mapper.load(Student.class, id );
				list.add(stud);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("No Students in the system");
		}
		return list;
	}

	//adding a student
	public Student addStudent(Student student) {
		student.setStudentId(student.getFirstName()+ student.getLastName());
		scanResult = client.scan(scanRequest);
		boolean found = false;
		for (Map<String, AttributeValue> studentItem : scanResult.getItems()){
			try{
				AttributeValue v = studentItem.get("studentId");
				String studId = v.getS();
				if(studId.equalsIgnoreCase(student.getStudentId())) {
					found = true;
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(!found) {
			if(CourseService.doesCoursesExit(student.getRegisteredCourses())) {
				Student stud = new Student(student.getId(), student.getStudentId(), student.getFirstName(), student.getLastName(), student.getRegisteredCourses(), student.getDepartment(), student.getJoiningDate());
				mapper.save(stud);
				Student studAdded = mapper.load(Student.class, stud.getId());
				return studAdded;
			}else {
				throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Courses trying to register doesn't exit").build());
			}
		}else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Student with same first and last name already exit").build());
		}

	}

	//helper function to get student
	public static Student getStud(String studentId) {
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("student");
		scanResult = client.scan(scanRequest);
		Student stud = null;
		for (Map<String, AttributeValue> studItem : scanResult.getItems()){
			try{
				AttributeValue v = studItem.get("studentId");
				String studId = v.getS();
				if(studId.equalsIgnoreCase(studentId)) {
					v = studItem.get("id");
					String id = v.getS();
					stud = mapper.load(Student.class, id);
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return stud;
	}

	// Getting One student
	public static Student getStudent(String studentId) {
		Student stud = getStud(studentId);
		if(stud == null) {
			throw new DataNotFoundException("Student with studentId " + studentId + " not found");
		}
		return stud;
	}

	// Deleting a student
	public Student deleteStudent(String studentId) {
		Student stud = getStud(studentId);
		if(stud == null) {
			throw new DataNotFoundException("Student with studentId " + studentId + " not found");
		}
		mapper.delete(stud);
		return stud;
	}

	//Updating student Info
	public Student updateStudentInformation(String studentId, Student student) {
		Student stud = getStud(studentId);
		if(stud == null) {
			throw new DataNotFoundException("Student with studentId " + studentId + " not found");
		}
		stud.setFirstName(student.getFirstName());
		stud.setLastName(student.getLastName());
		stud.setStudentId(student.getFirstName()+student.getLastName());
		stud.setDepartment(student.getDepartment());
		stud.setJoiningDate(student.getJoiningDate());
		stud.setRegisteredCourses(student.getRegisteredCourses());
		mapper.save(stud);
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT).build();
		Student updatedStudent = mapper.load(Student.class, stud.getId(), config);
		return updatedStudent;
	}

	//helper function to get student list for a particular key
	public List<Student> getStudList(String key, String pathParam){
		scanResult = client.scan(scanRequest);
		ArrayList<Student> list = new ArrayList<>();
		for (Map<String, AttributeValue> profItem : scanResult.getItems()){
			try{
				AttributeValue v = profItem.get(key);
				String value = v.getS();
				if(value.equalsIgnoreCase(pathParam)) {
					v = profItem.get("id");
					String id = v.getS();
					Student student = mapper.load(Student.class, id);
					list.add(student);
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return list;
	}

	// Get students by a department 
	public List<Student> getStudentsByDepartment(String department) {	
		List<Student> list = getStudList("department", department);
		if(list.isEmpty()) {
			throw new DataNotFoundException("Didn't find student for the department " + department);
		}
		return list;
	}

	// Get students by a joiningDate 
	public List<Student> getStudentsByJoiningDate(String joiningDate) {	
		List<Student> list = getStudList("joiningDate", joiningDate);
		if(list.isEmpty()) {
			throw new DataNotFoundException("Didn't find students for the joining date " + joiningDate);
		}
		return list;
	}

	//	 Get enrolled courses by a student
	public List<Course> getEnrolledCoursesByStudent(String studentId) {
		Student stud = getStudent(studentId);
		List<Course> courses = new ArrayList<>();
		List<String> regCourses = stud.getRegisteredCourses();
		for(int i = 0; i < regCourses.size(); i++) {
			courses.add(CourseService.getCourse(regCourses.get(i)));
		}
		if(courses.isEmpty()) {
			throw new DataNotFoundException("No courses with the given studentId " + studentId + " exit");
		}
		return courses;
	}

	//helper function to get studentIds list
	public static List<String> getStudList() {
		List<String> students = new ArrayList<>();
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("student");
		scanResult = client.scan(scanRequest);
		for (Map<String, AttributeValue> stud : scanResult.getItems()){
			try{
				AttributeValue v = stud.get("studentId");
				String studentId = v.getS();
				students.add(studentId);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return students;
	}

	public static boolean doesStudentsExit(List<String> studentIds) {
		List<String> students = getStudList();
		return students.containsAll(studentIds);
	}

	public static boolean doesStudentExit(String studentId) {
		List<String> students = getStudList();
		return students.contains(studentId);
	}

}



