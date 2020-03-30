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

import com.csye6225.spring2020.courseservice.datamodel.Course;
import com.csye6225.spring2020.courseservice.datamodel.Student;
import com.csye6225.spring2020.courseservice.service.StudentService;

@Path("students")
public class StudentResource {

	StudentService studService = new StudentService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getAllStudentss() {
		return studService.getAllStudents();
	}	

	@GET
	@Path("department/{department}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getStudentsByDepartment(
			@PathParam("department") String department) {
		return studService.getStudentsByDepartment(department);

	}

	@GET
	@Path("joiningDate/{joiningDate}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Student> getStudentsByJoiningDate(
			@PathParam("joiningDate") String joiningDate) {
		return studService.getStudentsByJoiningDate(joiningDate);

	}

	@GET
	@Path("{studentId}/registeredCourses")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> getEnrolledCoursesByStudent(
			@PathParam("studentId") String studentId) {
		return studService.getEnrolledCoursesByStudent(studentId);

	}

	@GET
	@Path("/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Student getStudent(@PathParam("studentId") String studentId) {
		return StudentService.getStudent(studentId);
	}

	@DELETE
	@Path("/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Student deleteStudent(@PathParam("studentId") String studentId) {
		return studService.deleteStudent(studentId);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Student addStudent(Student student) {
		return studService.addStudent(student);
	}

	@PUT
	@Path("/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Student updateStudent(@PathParam("studentId") String studentId, Student student) {
		return studService.updateStudentInformation(studentId, student);
	}

}
