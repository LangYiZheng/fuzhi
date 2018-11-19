package com.guyu.android.gis.config;

import java.util.ArrayList;

public class ProjectsConfig {

	private String currentProjectName;
	public String getCurrentProjectName() {
		return currentProjectName;
	}
	public void setCurrentProjectName(String currentProjectName) {
		this.currentProjectName = currentProjectName;
	}
	public ArrayList<Project> getProjects() {
		return projects;
	}
	public void setProjects(ArrayList<Project> projects) {
		this.projects = projects;
	}
	private ArrayList<Project> projects;
}
