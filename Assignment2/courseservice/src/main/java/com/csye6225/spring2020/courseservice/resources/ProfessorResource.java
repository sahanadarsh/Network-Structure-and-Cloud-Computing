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

import com.csye6225.spring2020.courseservice.datamodel.Professor;
import com.csye6225.spring2020.courseservice.service.ProfessorService;

@Path("professors")
public class ProfessorResource {

	ProfessorService profService = new ProfessorService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Professor> getAllProfessors() {
		return profService.getAllProfessors();
	}	

	@GET
	@Path("department/{department}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Professor> getProfessorsByDeparment(@PathParam("department") String department) {
		if(department == null) {
			return null;
		}
		return profService.getProfessorsByDepartment(department);
	}

	@GET
	@Path("joiningDate/{joiningDate}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Professor> getProfessorsByJoingingDate(@PathParam("joiningDate") String joiningDate) {
		if(joiningDate == null) {
			return null;
		}
		return profService.getProfessorsByJoiningDate(joiningDate);		
	}

	@GET
	@Path("/{professorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Professor getProfessor(@PathParam("professorId") String profId) {
		if(profId == null) {
			return null;
		}
		return ProfessorService.getProfessor(profId);
	}

	@DELETE
	@Path("/{professorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Professor deleteProfessor(@PathParam("professorId") String profId) {
		if(profId == null) {
			return null;
		}
		return profService.deleteProfessor(profId);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Professor addProfessor(Professor prof) {
		if(prof == null) {
			return null;
		}
		return profService.addProfessor(prof);
	}

	@PUT
	@Path("/{professorId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Professor updateProfessor(@PathParam("professorId") String profId, Professor prof) {
		if(profId == null) {
			return null;
		}
		return profService.updateProfessorInformation(profId, prof);
	}

}
