package com.csye6225.spring2020.courseservice.datamodel;

import java.util.List;

public class Lecture {

	private String lectureName;
	private List<String> notes;
	private List<String> associatedMaterials;
	private List<String> notifications;
	private String duration;

	public Lecture() {

	}

	public Lecture(String lectureName, List<String> notes, List<String> associatedMaterials, List<String> notifications,
			String duration) {
		this.lectureName = lectureName;
		this.notes = notes;
		this.associatedMaterials = associatedMaterials;
		this.notifications = notifications;
		this.duration = duration;
	}

	public String getLectureName() {
		return lectureName;
	}

	public void setLectureName(String lectureName) {
		this.lectureName = lectureName;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	public List<String> getAssociatedMaterials() {
		return associatedMaterials;
	}

	public void setAssociatedMaterials(List<String> associatedMaterials) {
		this.associatedMaterials = associatedMaterials;
	}

	public List<String> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<String> notifications) {
		this.notifications = notifications;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
