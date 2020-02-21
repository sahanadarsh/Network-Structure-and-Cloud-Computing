package com.csye6225.spring2020.courseservice.datamodel;

import java.util.List;

public class Program {

	private String programName;
	private List<String> courses;
	private List<String> professors;
	private String progDuration;
	private int graduationCredits;

	public Program() {

	}

	public Program(String programName, List<String> courses, List<String> professors,
			String progDuration, int graduationCredits) {
		this.programName = programName;
		this.courses = courses;
		this.professors = professors;
		this.progDuration = progDuration;
		this.graduationCredits = graduationCredits;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public List<String> getCourses() {
		return courses;
	}

	public void setCourses(List<String> courses) {
		this.courses = courses;
	}

	public List<String> getProfessors() {
		return professors;
	}

	public void setProfessors(List<String> professors) {
		this.professors = professors;
	}

	public String getProgDuration() {
		return progDuration;
	}

	public void setProgDuration(String progDuration) {
		this.progDuration = progDuration;
	}

	public int getGraduationCredits() {
		return graduationCredits;
	}

	public void setGraduationCredits(int graduationCredits) {
		this.graduationCredits = graduationCredits;
	}

}
