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
import com.csye6225.spring2020.courseservice.datamodel.Professor;
import com.csye6225.spring2020.courseservice.datamodel.Student;
import com.csye6225.spring2020.courseservice.exception.DataNotFoundException;

public class CourseService {

	static DynamoDbConnector dynamoDbConnector;
	static DynamoDBMapper mapper;
	static AmazonDynamoDB client;
	static ScanRequest scanRequest;
	static ScanResult scanResult;

	public CourseService(){
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("course");
	}

	//get all courses
	public List<Course> getAllCourses() {
		scanResult = client.scan(scanRequest);
		ArrayList<Course> list = new ArrayList<>();
		for (Map<String, AttributeValue> courseItem : scanResult.getItems()){
			try{
				AttributeValue v = courseItem.get("id");
				String id = v.getS();
				Course course = mapper.load(Course.class, id);
				list.add(course);
			} catch (NumberFormatException e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("No courses in the system");
		}
		return list;
	}

	// Adding a course
	public static Course addCourse(Course course) {
		scanResult = client.scan(scanRequest);
		boolean found = false;
		for (Map<String, AttributeValue> courseItem : scanResult.getItems()){
			try{
				AttributeValue v = courseItem.get("courseName");
				String courseName = v.getS();
				if(courseName.equalsIgnoreCase(course.getcourseName())) {
					found = true;
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(!found) {
			if(ProfessorService.doesProfessorExit(course.getProfessorId()) && StudentService.doesStudentsExit(course.getRegisteredStudents()) &&
					StudentService.doesStudentExit(course.getStudentTA()) && BoardService.doesBoardExit(course.getBoardName())) {
				Course crse = new Course(course.getId(), course.getcourseName(), course.getBoardName(), course.getRegisteredStudents(), course.getProfessorId(), course.getStudentTA(), course.getDepartment());
				mapper.save(crse);
				Course courseAdded = mapper.load(Course.class, crse.getId());
				return courseAdded;
			}else {
				throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("registered students or TA or professor trying to add doesn't exit").build());
			}
		}else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Course already exit").build());
		}

	}

	//helper function to get course
	public static Course getCrse(String courseName) {
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		mapper = new DynamoDBMapper(dynamoDbConnector.getClient());
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("course");
		scanResult = client.scan(scanRequest);
		Course course = null;
		for (Map<String, AttributeValue> courseItem : scanResult.getItems()){
			try{
				AttributeValue v = courseItem.get("courseName");
				String crseName = v.getS();
				if(crseName.equalsIgnoreCase(courseName)) {
					v = courseItem.get("id");
					String id = v.getS();
					course = mapper.load(Course.class, id);
					break;
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return course;
	}

	// Getting One course
	public static Course getCourse(String courseName) {
		Course course = getCrse(courseName);
		if(course == null) {
			throw new DataNotFoundException("Course with courseName " + courseName + " not found");
		}
		return course;
	}

	// Deleting a course
	public Course deleteCourse(String courseName) {
		Course course = getCrse(courseName);
		if(course == null) {
			throw new DataNotFoundException("Course with courseName " + courseName + " not found");
		}
		mapper.delete(course);
		return course;
	}

	// Updating course Info
	public Course updateCourse(String courseName, Course course) {
		Course crse = getCrse(courseName);
		if(crse == null) {
			throw new DataNotFoundException("Course with courseName " + courseName + " not found");
		}
		crse.setcourseName(course.getcourseName());
		crse.setBoardName(course.getBoardName());
		crse.setDepartment(course.getDepartment());
		crse.setProfessorId(course.getProfessorId());
		crse.setRegisteredStudents(course.getRegisteredStudents());
		crse.setStudentTA(course.getStudentTA());
		mapper.save(crse);
		DynamoDBMapperConfig config = DynamoDBMapperConfig.builder().withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT).build();
		Course updatedCourse = mapper.load(Course.class, crse.getId(), config);
		return updatedCourse;
	}

	//Get courses by department
	public List<Course> getCoursesByDepartment(String department){
		scanResult = client.scan(scanRequest);
		ArrayList<Course> list = new ArrayList<>();
		for (Map<String, AttributeValue> courseItem : scanResult.getItems()){
			try{
				AttributeValue v = courseItem.get("department");
				String dept = v.getS();
				if(dept.equalsIgnoreCase(department)) {
					v = courseItem.get("id");
					String id = v.getS();
					Course course = mapper.load(Course.class, id);
					list.add(course);
				}				
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("Didn't find courses for the department " + department);
		}
		return list;
	}


	// Get studentTA by a course 
	public Student getTAByCourse(String courseName) {
		Course crse = getCourse(courseName);
		String studentId = crse.getStudentTA();
		Student student = StudentService.getStudent(studentId);
		if(student == null) {
			throw new DataNotFoundException("Didn't find TA for the courseName " + courseName);
		}else {
			return student;
		}
	}

	// Get professor by a course 
	public Professor getProfessorByCourse(String courseName) {	
		Course crse = getCourse(courseName);
		String professorId = crse.getProfessorId();
		Professor prof = ProfessorService.getProfessor(professorId);
		if(prof == null) {
			throw new DataNotFoundException("Didn't find TA for the courseName " + courseName);
		}else {
			return prof;
		}
	}

	// Get board by a course 
	public Board getBoardByCourse(String courseName) {	
		Course crse = getCourse(courseName);
		String boardName = crse.getBoardName();
		Board board = BoardService.getBoard(boardName);
		if(board == null) {
			throw new DataNotFoundException("Didn't find Board for the courseName " + courseName);
		}else {
			return board;
		}
	}

	// Get enrolled students by a course
	public List<Student> getRegisteredStudentsByCourse(String courseName) {	
		Course crse = getCourse(courseName);
		List<Student> students = new ArrayList<>();
		List<String> regStudents = crse.getRegisteredStudents();
		for(int i = 0; i < regStudents.size(); i++) {
			students.add(StudentService.getStudent(regStudents.get(i)));
		}
		if(students.isEmpty()) {
			throw new DataNotFoundException("No students with the given courseName " + courseName + " exit");
		}
		return students;
	}

	//helper function to get courseNames list
	public static List<String> getCourseNameList() {
		List<String> courseNames = new ArrayList<>();
		dynamoDbConnector = new DynamoDbConnector();
		dynamoDbConnector.init();
		client = dynamoDbConnector.getClient(); 
		scanRequest = new ScanRequest().withTableName("course");
		scanResult = client.scan(scanRequest);
		for (Map<String, AttributeValue> course : scanResult.getItems()){
			try{
				AttributeValue v = course.get("courseName");
				String courseName = v.getS();
				courseNames.add(courseName);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		return courseNames;
	}

	public static boolean doesCoursesExit(List<String> courseNameList) {
		List<String> allCourseNames = getCourseNameList() ;
		return allCourseNames.containsAll(courseNameList);
	}

	public static boolean doesCourseExit(String courseName) {
		List<String> courses = getCourseNameList();
		return courses.contains(courseName);
	}

}

