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

import com.csye6225.spring2020.courseservice.datamodel.Announcement;
import com.csye6225.spring2020.courseservice.datamodel.Board;
import com.csye6225.spring2020.courseservice.service.AnnouncementService;

@Path("announcements")
public class AnnouncementResource {


	AnnouncementService annnouncementService = new AnnouncementService();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Announcement> getAllAnnouncements() {
		return annnouncementService.getAllAnnouncements();
	}

	@GET
	@Path("/{announcementName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Announcement getAnnouncement(@PathParam("announcementName") String announcementName) {
		return annnouncementService.getAnnouncement(announcementName);
	}

	@GET
	@Path("{announcementName}/board")
	@Produces(MediaType.APPLICATION_JSON)
	public Board getBoardByAnnouncement(@PathParam("announcementName") String announcementName) {
		if (announcementName == null) {
			return null;
		}
		return annnouncementService.getBoardByAnnouncement(announcementName);
	}

	@GET
	@Path("boardName/{boardName}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Announcement> getAnnouncementsByBoardName(@PathParam("boardName") String boardName) {
		if (boardName == null) {
			return null;
		}
		return annnouncementService.getAnnouncementsByBoardName(boardName);
	}

	@DELETE
	@Path("/{announcementName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Announcement deleteAnnouncement(@PathParam("announcementName") String announcementName) {
		return annnouncementService.deleteAnnouncement(announcementName);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Announcement addAnnouncement(Announcement announcement) {
		return annnouncementService.addAnnouncement(announcement);
	}

	@PUT
	@Path("/{announcementName}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Announcement updateAnnouncement(@PathParam("announcementName") String announcementName, Announcement announcement) {
		return annnouncementService.updateAnnouncement(announcementName, announcement);
	}


}
