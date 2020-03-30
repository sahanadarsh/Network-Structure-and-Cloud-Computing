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
import com.csye6225.spring2020.courseservice.service.BoardService;

@Path("boards")
public class BoardResource {

	BoardService boardService = new BoardService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Board> getAllBoards() {
		return boardService.getAllBoards();
	}

	@GET
	@Path("/{boardName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Board getBoard(@PathParam("boardName") String boardName) {
		if (boardName == null) {
			return null;
		}
		return BoardService.getBoard(boardName);
	}

	@GET
	@Path("{boardName}/course")
	@Produces(MediaType.APPLICATION_JSON)
	public Course getCourseByBoard(@PathParam("boardName") String boardName) {
		if (boardName == null) {
			return null;
		}
		return BoardService.getCourseByBoard(boardName);
	}

	@GET
	@Path("courseName/{courseName}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Board> getBoardsByCourse(@PathParam("courseName") String courseName) {
		if (courseName == null) {
			return null;
		}
		return boardService.getBoardsByCourse(courseName);
	}

	@DELETE
	@Path("/{boardName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Board deleteProfessor(@PathParam("boardName") String boardName) {
		return boardService.deleteBoard(boardName);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Board addProfessor(Board board) {
		return boardService.addBoard(board);
	}

	@PUT
	@Path("/{boardName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Board updateBoard(@PathParam("boardName") String boardName, Board board) {
		return boardService.updateBoard(boardName, board);
	}

}
