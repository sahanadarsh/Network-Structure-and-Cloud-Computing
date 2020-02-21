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
import com.csye6225.spring2020.courseservice.datamodel.Professor;
import com.csye6225.spring2020.courseservice.datamodel.Program;
import com.csye6225.spring2020.courseservice.service.ProgramService;

@Path("programs")
public class ProgramResource {

	ProgramService progService = new ProgramService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Program> getAllPrograms() {
		return progService.getAllPrograms();
	}	

	@GET
	@Path("/{programName}")
	@Produces(MediaType.APPLICATION_JSON)
	public static Program getProfessor(@PathParam("programName") String programName) {
		return ProgramService.getProgram(programName);
	}

	@GET
	@Path("{programName}/courses")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> getCoursesByProgram(
			@PathParam("programName") String programName) {		
		return progService.getCoursesByProgram(programName);
	}

	@GET
	@Path("{programName}/professors")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Professor> getProfessorsByProgram(@PathParam("programName") String programName) {
		return progService.getProfessorsByProgram(programName);
	}

	@GET
	@Path("{programName}/graduationCredits")
	@Produces(MediaType.APPLICATION_JSON)
	public int getGraduationCredits(@PathParam("programName") String programName) {
		return progService.getCreditsByProgram(programName);
	}

	@GET
	@Path("{programName}/duration")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDuration(@PathParam("programName") String programName) {
		return progService.getDurationByProgram(programName);
	}

	@DELETE
	@Path("/{programName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Program deleteProgram(@PathParam("programName") String programName) {
		return progService.deleteProgram(programName);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Program addProgram(Program prog) {
		return	progService.addProgram(prog);
	}

	@PUT
	@Path("/{programName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Program updateProgram(@PathParam("programName") String programName, 
			Program prog) {
		return progService.updateProgramInformattion(programName, prog);
	}

}

