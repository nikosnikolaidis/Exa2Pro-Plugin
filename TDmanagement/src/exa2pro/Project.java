/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsers.CodeFile;
import parsers.cFile;
import parsers.fortranFile;

/**
 *
 * @author Nikos
 */
public class Project implements Serializable {
    private ProjectCredentials credentials;
    private String projectVersion;
    private ArrayList<CodeFile> projectFiles=new ArrayList<>();
    private Report projectReport;
    
    private HashMap<Integer, CodeFile> fortranFilesIndexed= new HashMap<>();
    private int countFortran=0;
    
    public Project(ProjectCredentials c, String version){
        credentials=c;
        credentials.addProject(this);
        projectVersion=version;
        
        getFilesForAnalysis(credentials.getProjectDirectory());
    }
    
    /**
     * Creates a small analysis and executes it
     */
    public void projectVersionAnalysis(){
        //delete issues of prv for memory opt
        if(getCredentials().getProjects().size()>1)
            getCredentials().getProjects().get(getCredentials().getProjects().size()-2).projectReport.getIssuesList().clear();
        //analyze
        Analysis a= new Analysis(this);
        a.runCustomCreatedMetrics();
        
        saveToFile();
    }
    
    /**
     * Creates a Full analysis and executes it
     */
    public void projectVersionAnalysisFull(){
        //delete issues of prv for memory opt
        if(getCredentials().getProjects().size()>1)
            getCredentials().getProjects().get(getCredentials().getProjects().size()-2).projectReport.getIssuesList().clear();
        //analyze
        Analysis a= new Analysis(this);
        a.runCustomCreatedMetrics();
        a.createPropertiesFile();
        a.executeAnalysis();
        
        saveToFile();
    }
    
    /**
     * Finds all the files in the directory that will be analyzed
     * @param directoryName the directory to search for files
     */
    public void getFilesForAnalysis(String directoryName){
        //get previous countFortran
        if(credentials.getProjects().size()>1){
            countFortran= credentials.getProjects().get(credentials.getProjects().size()-2).countFortran;
        }
        
        File directory = new File(directoryName);
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null){
            for (File file : fList) {
                if (file.isFile() && file.getName().contains(".") && file.getName().charAt(0)!='.') {
                    String[] str=file.getName().split("\\.");
                    // For all the filles of this dirrecory get the extension
                    if(str[str.length-1].equalsIgnoreCase("F90") ){
                        CodeFile cf= new fortranFile(file,true);
                        projectFiles.add(cf);
                        addFortranFileToIndexedHashMap(cf);
                    }
                    else if(str[str.length-1].equalsIgnoreCase("f") || str[str.length-1].equalsIgnoreCase("f77")
                            || str[str.length-1].equalsIgnoreCase("for") || str[str.length-1].equalsIgnoreCase("fpp")
                            || str[str.length-1].equalsIgnoreCase("ftn")){
                        CodeFile cf= new fortranFile(file,false);
                        projectFiles.add(cf);
                        addFortranFileToIndexedHashMap(cf);
                    }
                    else if(str[str.length-1].equalsIgnoreCase("c") || str[str.length-1].equalsIgnoreCase("h") ||
                            str[str.length-1].equalsIgnoreCase("cpp") || str[str.length-1].equalsIgnoreCase("hpp") ||
                            str[str.length-1].equalsIgnoreCase("cc") || str[str.length-1].equalsIgnoreCase("cp") ||
                            str[str.length-1].equalsIgnoreCase("cxx") || str[str.length-1].equalsIgnoreCase("c++") ||
                            str[str.length-1].equalsIgnoreCase("hh") || str[str.length-1].equalsIgnoreCase("h++") ||
                            str[str.length-1].equalsIgnoreCase("hp") || str[str.length-1].equalsIgnoreCase("hxx") ||
                            str[str.length-1].equalsIgnoreCase("cu") || str[str.length-1].equalsIgnoreCase("hcu") )
                        projectFiles.add(new cFile(file));
                } else if (file.isDirectory()) {
                    getFilesForAnalysis(file.getAbsolutePath());
                }
            }
        }
    }
    
    /**
     * Add Fortran File to list with the given index
     * @param cf fortran file
     */
    private void addFortranFileToIndexedHashMap(CodeFile cf){
        if(credentials.getProjects().size()<2){
            fortranFilesIndexed.put(countFortran, cf);
            countFortran++;
        }else{
            HashMap<Integer, CodeFile> previousFortranFiles= credentials.getProjects().get(credentials.getProjects().size()-2).getFortranFilesIndexed();
            boolean found=false;
            for(Integer key: previousFortranFiles.keySet()){
                if(previousFortranFiles.get(key).file.getAbsolutePath().equals(cf.file.getAbsolutePath())){
                    fortranFilesIndexed.put(key, cf);
                    found=true;
                    break;
                }
            }
            if(!found){
                fortranFilesIndexed.put(countFortran, cf);
                countFortran++;
            }
        }
    }
    
    /**
     * Copies all Fortran files to home directory of project
     * in order for icode to analyze them
     */
    public void copyFortranFilesToSiglePlace() throws IOException {
        for(Integer key: fortranFilesIndexed.keySet()){
            String[] str= fortranFilesIndexed.get(key).file.getName().split("\\.");
            Files.copy(fortranFilesIndexed.get(key).file.toPath(), Paths.get(credentials.getProjectDirectory()+"/"+key+"."+str[str.length-1]));
            fortranFilesIndexed.get(key).file.delete();
        }
    }
    
    /**
     * Restore official Fortran files
     * @throws IOException 
     */
    public void restoreTempFortranFiles() throws IOException {
        File[] files = new File(credentials.getProjectDirectory()).listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if(f.isFile() && f.getName().contains(".")){
                    String fName=f.getName().split("\\.")[0];
                    String fEnding=f.getName().split("\\.")[1];
                    if(fEnding.equalsIgnoreCase("f90") || fEnding.equalsIgnoreCase("f") || fEnding.equalsIgnoreCase("f77")
                                || fEnding.equalsIgnoreCase("for") || fEnding.equalsIgnoreCase("fpp")
                                || fEnding.equalsIgnoreCase("ftn")){
                        try {
                            Files.copy(f.toPath(), fortranFilesIndexed.get(Integer.parseInt(fName)).file.toPath());
                            f.delete();
                        }
                        catch (NoSuchFileException e) {
                            System.out.println("java.nio.file.NoSuchFileException");
                        }
                    }
                }
            }
        }
    }
    
    // Returns true or flase if this projects contains Fortran code
    public boolean containsFortran() {
        for(CodeFile cf: projectFiles){
            String[] str=cf.file.getName().split("\\.");
            if(str[str.length-1].equalsIgnoreCase("f") || str[str.length-1].equalsIgnoreCase("f77")
                            || str[str.length-1].equalsIgnoreCase("for") || str[str.length-1].equalsIgnoreCase("fpp")
                            || str[str.length-1].equalsIgnoreCase("ftn") || str[str.length-1].equalsIgnoreCase("F90")){
                return true;
            }
        }
        return false;
    }
    
    // Saves to File the List of projects
    public static void saveToFile(){
        try {
            FileOutputStream f = new FileOutputStream(new File("myProjects.txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(Exa2Pro.projecCredentialstList);
            o.close();
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Getters
    public String getProjectVersion() {
        return projectVersion;
    }
    public ArrayList<CodeFile> getprojectFiles() {
        return projectFiles;
    }
    public Report getprojectReport() {
        return projectReport;
    }
    public ProjectCredentials getCredentials(){
        return credentials;
    }
    public HashMap<Integer, CodeFile> getFortranFilesIndexed(){
        return fortranFilesIndexed;
    }
    public Integer getCountFortran(){
        return countFortran;
    }

    public void setProjectReport(Report projectReport) {
        this.projectReport = projectReport;
    }
    
}
