/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Nikos
 */
public class ProjectCredentials implements Serializable{
    private String projectKey;
    private String projectName;
    private String projectDirectory;
    private ArrayList<Project> projects;
    
    public ProjectCredentials(String projectKey, String projectName, String projectDirectory){
        this.projectKey=projectKey;
        this.projectName=projectName;
        projects=new ArrayList<>();
        
        if(Exa2Pro.isWindows())
            this.projectDirectory=projectDirectory+"\\";
        else
            this.projectDirectory=projectDirectory;
    }
    
    public void addProject(Project p){
        projects.add(p);
    }
    public void removeProject(Project p){
        projects.remove(p);
    }

    //Getters
    public String getProjectKey() {
        return projectKey;
    }
    public String getProjectName() {
        return projectName;
    }
    public String getProjectDirectory() {
        return projectDirectory;
    }
    public ArrayList<Project> getProjects() {
        return projects;
    }
}
