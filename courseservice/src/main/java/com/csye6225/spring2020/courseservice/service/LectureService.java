package com.csye6225.spring2020.courseservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import com.csye6225.spring2020.courseservice.datamodel.InMemoryDatabase;
import com.csye6225.spring2020.courseservice.datamodel.Lecture;
import com.csye6225.spring2020.courseservice.exception.DataNotFoundException;

public class LectureService {

	static HashMap<String, Lecture> lecture_Map = InMemoryDatabase.getLectureDB();

	public LectureService() {
	}

	public List<Lecture> getAllLectures() {
		ArrayList<Lecture> list = new ArrayList<>();
		for (Lecture lecture : lecture_Map.values()) {
			list.add(lecture);
		}
		if(list.isEmpty()) {
			throw new DataNotFoundException("No lectures in the system");
		}else {
			return list ;
		}
	}

	// Adding a lecture
	public Lecture addLecture(Lecture lecture) {
		lecture.setLectureName(lecture.getLectureName());
		boolean found = false;
		for (Lecture lect : lecture_Map.values()) {
			if((lect.getLectureName()).equalsIgnoreCase(lecture.getLectureName())){
				found = true;
			}
		}
		if (!found) {
			Lecture lect = new Lecture(lecture.getLectureName(), lecture.getNotes(), lecture.getAssociatedMaterials(), lecture.getNotifications(), lecture.getDuration());
			lecture_Map.put(lect.getLectureName(), lect);
			return lect;
		}else {
			throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity("Lecture already exit").build());
		}
	}

	// Getting One lecture
	public static Lecture getLecture(String lectureName) {
		if(lecture_Map.containsKey(lectureName)) {
			Lecture lecture = lecture_Map.get(lectureName);
			return lecture;
		}else {
			throw new DataNotFoundException("Lecture with lectureName " + lectureName + " not found");
		}
	}

	// Deleting a lecture
	public Lecture deleteLecture(String lectureName) {
		if(lecture_Map.containsKey(lectureName)) {
			return lecture_Map.remove(lectureName);
		}else {
			throw new DataNotFoundException("Lecture with lectureName " + lectureName + " not found");
		}
	}

	// Updating lecture Info
	public Lecture updateLectureInformattion(String lectureName, Lecture lecture) {
		if(lecture_Map.containsKey(lectureName)) {
			if(!lecture.getLectureName().equalsIgnoreCase(lectureName)) {
				lecture_Map.remove(lectureName);
			}
			lecture_Map.put(lecture.getLectureName(), lecture);
			return lecture;
		}else {
			throw new DataNotFoundException("Lecture with lectureName " + lectureName + " not found");
		}
	}

	// Get notes by a lecture 
	public List<String> getNotesByLecture(String lectureName) {	
		for (Lecture lecture : lecture_Map.values()) {
			if (lecture.getLectureName().equalsIgnoreCase(lectureName)) {
				return lecture.getNotes();
			}
		}
		throw new DataNotFoundException("Didn't find notes of the lecture " + lectureName);
	}

	// Get associated material by a lecture
	public List<String> getAssociatedMaterialsByLecture(String lectureName) {	
		for (Lecture lecture : lecture_Map.values()) {
			if (lecture.getLectureName().equalsIgnoreCase(lectureName)) {
				return lecture.getAssociatedMaterials();
			}
		}
		throw new DataNotFoundException("Didn't find associated materials of the lecture " + lectureName);
	}

	// Get duration by a lecture
	public String getDurationByLecture(String lectureName) {	
		for (Lecture lecture : lecture_Map.values()) {
			if (lecture.getLectureName().equalsIgnoreCase(lectureName)) {
				return lecture.getDuration();
			}
		}
		throw new DataNotFoundException("Didn't find duration of the lecture " + lectureName);
	}


	// Get notifications by a lecture
	public List<String> getNotificationsByLecture(String lectureName) {	
		for (Lecture lecture : lecture_Map.values()) {
			if (lecture.getLectureName().equalsIgnoreCase(lectureName)) {
				return lecture.getNotifications();
			}
		}
		throw new DataNotFoundException("Didn't find notifications of the lecture " + lectureName);
	}

	public static boolean doesLecturesExit(List<String> lectures){
		for(int i = 0; i < lectures.size(); i++) {
			if(!lecture_Map.containsKey(lectures.get(i))){
				return false;
			}
		}
		return true;
	}


}

