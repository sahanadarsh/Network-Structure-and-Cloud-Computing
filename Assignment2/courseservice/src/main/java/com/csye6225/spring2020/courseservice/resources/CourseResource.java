package com.csye6225.spring2020.courseservice.resources;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.csye6225.spring2020.courseservice.datamodel.Board;
import com.csye6225.spring2020.courseservice.datamodel.Course;
import com.csye6225.spring2020.courseservice.datamodel.Professor;
import com.csye6225.spring2020.courseservice.datamodel.Student;
import com.csye6225.spring2020.courseservice.service.CourseService;

@Path("courses")
public class CourseResource {

	CourseService courseService = new CourseService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> getAllCourses() {
		return courseService.getAllCourses();
	}	

	@GET
	@Path("/{courseName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Course getCourse(@PathParam("courseName") String courseName) {
		if (courseName == null) {
			return null;
		}
		return CourseService.getCourse(courseName);
	}

	@GET
	@Path("{courseName}/registeredStudents")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getRegisteredStudentsByCourse(
			@PathParam("courseName") String courseName) {		
		if (courseName == null) {
			return null;
		}
		return courseService.getRegisteredStudentsByCourse(courseName);
	}

	@GET
	@Path("{courseName}/studentTA")
	@Produces(MediaType.APPLICATION_JSON)
	public Student getTAByCourse(@PathParam("courseName") String courseName) {		
		if (courseName == null) {
			return null;
		}
		return courseService.getTAByCourse(courseName);
	}

	@GET
	@Path("{courseName}/professor")
	@Produces(MediaType.APPLICATION_JSON)
	public Professor getProfessorByCourse(@PathParam("courseName") String courseName) {		
		if (courseName == null) {
			return null;
		}
		return courseService.getProfessorByCourse(courseName);
	}

	@GET
	@Path("{courseName}/board")
	@Produces(MediaType.APPLICATION_JSON)
	public Board getBoardByCourse(
			@PathParam("courseName") String courseName) {		
		if (courseName == null) {
			return null;
		}
		return courseService.getBoardByCourse(courseName);
	}

	@GET
	@Path("department/{department}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> getCoursesByDepartment(
			@PathParam("department") String department) {		
		if (department == null) {
			return null;
		}
		return courseService.getCoursesByDepartment(department);
	}

	@DELETE
	@Path("/{courseName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Course deleteCourse(@PathParam("courseName") String courseName) {
		if(courseName == null) {
			return null;
		}
		return courseService.deleteCourse(courseName);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Course addCourse(Course course) {
		if(course == null) {
			return null;
		}
		return CourseService.addCourse(course);
	}

	@PUT
	@Path("/{courseName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Course updateCourse(@PathParam("courseName") String courseName, Course course) {
		if(courseName == null) {
			return null;
		}
		return courseService.updateCourse(courseName, course);
	}

}


