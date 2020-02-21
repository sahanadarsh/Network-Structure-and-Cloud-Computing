package com.csye6225.spring2020.courseservice.datamodel;

import java.util.Arrays;
import java.util.HashMap;

public class InMemoryDatabase {

	private static HashMap<String, Professor> professorDB = new HashMap<> ();
	private static HashMap<String, Student> studentDB = new HashMap<> ();
	private static HashMap<String, Program> programDB = new HashMap<> ();
	private static HashMap<String, Course> courseDB = new HashMap<> ();
	private static HashMap<String, Lecture> lectureDB = new HashMap<> ();

	public static HashMap<String, Professor> getProfessorDB() {
		addDefaultProfessorEntry();
		return professorDB;
	}

	//for professor reference adding 2 default professor entries to the HashMap
	public static void addDefaultProfessorEntry() {
		professorDB.put("SahanaAdarsh", new Professor("Sahana", "Adarsh", "SahanaAdarsh", "Computer Science", "2019", Arrays.asList("Network Structure", "Web Development")));
		professorDB.put("AvinavJami", new Professor("Avinav", "Jami", "AvinavJami", "Information Science", "2019", Arrays.asList("Network Structure", "Web Development")));

	}

	public static HashMap<String, Student> getStudentDB() {
		addDefaultStudentEntry();
		return studentDB;
	}

	//for student reference adding 2 default student entries to the HashMap
	public static void addDefaultStudentEntry() {
		studentDB.put("SnehaSomanna", new Student("SnehaSomanna", "Sneha", "Somanna", "http://localhost:8080/courseservice/webapi/students/student1.png", "Information Systems", Arrays.asList("Network Structure", "Web Development")));
		studentDB.put("HemanthAnanth", new Student("HemanthAnanth", "Hemanth", "Ananth", "http://localhost:8080/courseservice/webapi/students/student2.png", "Computer Systems", Arrays.asList("Network Structure", "Web Development")));
	}

	public static HashMap<String, Program> getProgramDB() {
		addDefaultProgramEntry();
		return programDB;
	}

	//for program reference adding 2 default program entries to the HashMap
	public static void addDefaultProgramEntry() { 
		programDB.put("Information Systems", new Program("Information Systems", Arrays.asList("Network Structure", "Web Development"), Arrays.asList("SahanaAdarsh", "AvinavJami"), "2 years", 32));
		programDB.put("Computer Systems", new Program("Computer Systems", Arrays.asList("Network Structure", "Web Development"), Arrays.asList("SahanaAdarsh", "AvinavJami"), "2 years", 32));
	}

	public static HashMap<String, Course> getCourseDB() {
		addDefaultCourseEntry();
		return courseDB;
	}

	//for course reference adding 2 default course entries to the HashMap
	public static void addDefaultCourseEntry() { 
		courseDB.put("Network Structure", new Course("Network Structure", Arrays.asList("Network basics lecture class", "Web basics lecture class"), "board", "roaster", Arrays.asList("SnehaSomanna", "HemanthAnanth"), "SahanaAdarsh", "SnehaSomanna", 4));
		courseDB.put("Web Development", new Course("Web Development", Arrays.asList("Network basics lecture class", "Web basics lecture class"), "board", "roaster", Arrays.asList("SnehaSomanna", "HemanthAnanth"), "AvinavJami", "HemanthAnanth", 4));
	}

	public static HashMap<String, Lecture> getLectureDB() {
		addDefaultLectureEntry();
		return lectureDB;
	}

	//for lecture reference adding 2 default lecture entries to the HashMap
	public static void addDefaultLectureEntry() {
		lectureDB.put("Network basics lecture class", new Lecture("Network basics lecture class", Arrays.asList("cloud basic notes", "why cloud notes"), Arrays.asList("http://localhost:8080/courseservice/webapi/students/CloudBasic.pdf","http://localhost:8080/courseservice/webapi/students/CloudBegineers.pdf"), Arrays.asList("Assignment Deadline sunday by 12pm", "class time extended from 6 to 7"), "3hours"));
		lectureDB.put("Web basics lecture class", new Lecture("Web basics lecture class", Arrays.asList("cloud basic notes", "why cloud notes"), Arrays.asList("http://localhost:8080/courseservice/webapi/students/CloudBasic.pdf","http://localhost:8080/courseservice/webapi/students/CloudBegineers.pdf"), Arrays.asList("Assignment Deadline sunday by 12pm", "class time extended from 6 to 7"), "3hours"));
	}

}

