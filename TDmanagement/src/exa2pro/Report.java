/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import panels_frames.ProjectFrame;
import parsers.CodeFile;
import parsers.fortranFile;

/**
 *
 * @author Nikos
 */
public class Report  implements Serializable{
    private Project project;
    private ArrayList<Issue> issuesList= new ArrayList<>();
    private HashMap<String,Integer> tdOfEachFile= new HashMap<>();
    private HashMap<String,Integer> duplicatedBblocksOfEachFile= new HashMap<>();
    private HashMap<String,Integer> reliabilityEffortOfEachFile= new HashMap<>();
    private HashMap<String,Integer> securityEffortOfEachFile= new HashMap<>();
    private HashMap<String,Integer> codeSmellsOfEachFile= new HashMap<>();
    private Date date;
    private String totalDebt;
    private double totalDebt_Index;
    private int totalCodeSmells;
    private int totalLinesOfCode;
    private String linesOfCodeForAllLanguages;
    private int totalComplexity;
    private int newLinesOfCode=0;
    private double totalTDInterest=0;
    private double TDPrincipalSourceCodeDebt;
    private double TDPrincipalDesignDebt;
    private int duplicated_blocks;
    private int reliability_remediation_effort;
    private int security_remediation_effort;
    
    public Report(Project project){
        this.project=project;
    }
    
    public boolean containsIssue(Issue i){
        for(Issue issue: issuesList){
            if(issue.getIssueName().equals(i.getIssueName()) && issue.getIssueDirectory().equals(i.getIssueDirectory())
                    && issue.getIssueStartLine().equals(i.getIssueStartLine())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * calculate TD Interest in report
     */
    public void startCalculationTDInterest(){
        TDInterest interest= new TDInterest(project);
        totalTDInterest= interest.getTotal();
    }
    
    /**
     * calculate TD Principal
     */
    public void startCalculationTDPrincipal(){
        TDPrincipal principal =new TDPrincipal(totalDebt_Index, project);
        TDPrincipalSourceCodeDebt= principal.getSourceCodeDebt();
        TDPrincipalDesignDebt= principal.getDesignDebt();
    }
    
    /**
     * get TD of each File
     */
    public void getTDLinesOfFilesFromSonarQube(){
        try{
            URL urlTry = new URL(Exa2Pro.sonarURL+"/api/measures/component?component="
                        +project.getCredentials().getProjectName()+"&metricKeys=sqale_index");
                HttpURLConnection connTry = (HttpURLConnection)urlTry.openConnection();
                connTry.setRequestMethod("GET");
                connTry.connect();
                int responsecodeTry = connTry.getResponseCode();
                if(responsecodeTry==200){
                    //for each file
                    for(CodeFile cf: project.getprojectFiles()){
                        //get file name
                        String parentDir= project.getCredentials().getProjectDirectory();
                        String fileNameForSave=cf.file.getName();
                        String fileName="";
                        if(cf instanceof fortranFile){
                            for(Integer key: project.getFortranFilesIndexed().keySet()){
                                if(project.getFortranFilesIndexed().get(key).file.getAbsolutePath()
                                            .equals(cf.file.getAbsolutePath())){
                                    String fEnding= cf.file.getName().split("\\.")[cf.file.getName().split("\\.").length-1];
                                    fileName= key+"."+fEnding;
                                }
                            }
                        }
                        else {
                            fileName= cf.file.getParent().replace(parentDir.replace("//", ""), "");
                            if(!fileName.equals("")) {
                                    fileName= fileName.replace("\\", "/").substring(1).replace(" ", "%20") + "/";
                            }
                            fileName= fileName+cf.file.getName();
                        }
                        fileName= project.getCredentials().getProjectName()+":"+fileName;

                        //get metric new lines
                        getNewLinesOfCodeFromSonarQube(fileName);

                        //get metric TD
                        int metric_sqale_index= 0;
                        int metric_duplicated_blocks= 0;
                        int metric_reliability_remediation_effort= 0;
                        int metric_security_remediation_effort= 0;
                        int metric_code_smells= 0;
                        try {
                            URL url = new URL(Exa2Pro.sonarURL+"/api/measures/component?component="+fileName+
                                            "&metricKeys=sqale_index,duplicated_blocks,reliability_remediation_effort,security_remediation_effort,code_smells");
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();
                            int responsecode = conn.getResponseCode();
                            if(responsecode != 200)
                                    System.err.println(responsecode+ " "+Exa2Pro.sonarURL+"/api/measures/component?component="+fileName+
                                            "&metricKeys=sqale_index,duplicated_blocks,reliability_remediation_effort,security_remediation_effort,code_smells");
                            else{
                                Scanner sc = new Scanner(url.openStream());
                                String inline="";
                                while(sc.hasNext()){
                                    inline+=sc.nextLine();
                                }
                                sc.close();

                                JSONParser parse = new JSONParser();
                                JSONObject jobj = (JSONObject)parse.parse(inline);
                                JSONObject jobj1= (JSONObject) jobj.get("component");
                                JSONArray jsonarr_1 = (JSONArray) jobj1.get("measures");

                                for(int i=0; i<jsonarr_1.size(); i++){
                                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                                    if(jsonobj_1.get("metric").toString().equals("sqale_index"))
                                        metric_sqale_index= Integer.parseInt(jsonobj_1.get("value").toString());
                                    if(jsonobj_1.get("metric").toString().equals("duplicated_blocks"))
                                        metric_duplicated_blocks= Integer.parseInt(jsonobj_1.get("value").toString());
                                    if(jsonobj_1.get("metric").toString().equals("reliability_remediation_effort"))
                                        metric_reliability_remediation_effort= Integer.parseInt(jsonobj_1.get("value").toString());
                                    if(jsonobj_1.get("metric").toString().equals("security_remediation_effort"))
                                        metric_security_remediation_effort= Integer.parseInt(jsonobj_1.get("value").toString());
                                    if(jsonobj_1.get("metric").toString().equals("code_smells"))
                                        metric_code_smells= Integer.parseInt(jsonobj_1.get("value").toString());
                                }
                            }
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException | ParseException ex) {
                            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        tdOfEachFile.put(fileNameForSave, metric_sqale_index);
                        duplicatedBblocksOfEachFile.put(fileNameForSave, metric_duplicated_blocks);
                        reliabilityEffortOfEachFile.put(fileNameForSave, metric_reliability_remediation_effort);
                        securityEffortOfEachFile.put(fileNameForSave, metric_security_remediation_effort);
                        codeSmellsOfEachFile.put(fileNameForSave, metric_code_smells);
                        System.out.println("file:"+fileNameForSave+ "  td:"+metric_sqale_index+"  db:"+metric_duplicated_blocks+
                                "  rre:"+metric_reliability_remediation_effort+"  sre:"+metric_security_remediation_effort+
                                "  cs:"+metric_code_smells);
                    }
                }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Return the new lines of code
     */
    public void getNewLinesOfCodeFromSonarQube(String file){
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/measures/component?component="
                            +file+"&metricKeys=new_lines");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200) {
            	System.err.println(responsecode+" "+ Exa2Pro.sonarURL+"/api/measures/component?component="
                            +file+"&metricKeys=new_lines");
            	//throw new RuntimeException("HttpResponseCode: "+responsecode);
            }
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();

                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                JSONObject jobj1= (JSONObject) jobj.get("component");
                JSONArray jsonarr_1 = (JSONArray) jobj1.get("measures");

                if(!jsonarr_1.isEmpty()) {
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(0);
                    JSONArray jsonarr_2 = (JSONArray) jsonobj_1.get("periods");
                    JSONObject jsonobj_2 = (JSONObject)jsonarr_2.get(0);
                    
                    newLinesOfCode+= Integer.parseInt(jsonobj_2.get("value").toString());
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(ProjectFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Return the issues from Sonar Qube
     */
    public void getIssuesFromSonarQube(){
        int page= (totalCodeSmells-1)/500 + 1;
        
        //if there are more than limit of API 10,000 split 
        if(totalCodeSmells>10000){
        	int issuesN= getIssuesNumbers("&resolved=false&severities=INFO");
            page= (issuesN-1)/500 + 1;
            if(issuesN>0){
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(1,"&resolved=false&severities=INFO");
                }
            }
            
            issuesN= getIssuesNumbers("&resolved=false&severities=MINOR,MAJOR,CRITICAL,BLOCKER");
            page= (issuesN-1)/500 + 1;
            if(issuesN>0 && issuesN<10000){
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(i,"&resolved=false&severities=MINOR,MAJOR,CRITICAL,BLOCKER");
                }
            }
            // if again more than 10,000, then split again
            else if(issuesN>1000){
                page= (getIssuesNumbers("&resolved=false&severities=MINOR,MAJOR")-1)/500 + 1;
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(1,"&resolved=false&severities=MINOR,MAJOR");
                }
                
                page= (getIssuesNumbers("&resolved=false&severities=CRITICAL,BLOCKER")-1)/500 + 1;
                for(int i=1; i<=page; i++){
                    getIssuesFromPage(i,"&resolved=false&severities=CRITICAL,BLOCKER");
                }
            }
        }
        else{   //if rules less than 10,000 then all together
            for(int i=1; i<=page; i++){
                getIssuesFromPage(i,"&resolved=false");
            }
        }
        
        //To get the fixed issues
        //if(Integer.parseInt(project.getProjectVersion())>1){
        //    getIssuesFromPage(i,"resolutions=FIXED");
        //}
    }
    
    /**
     * Get number of issus for a specific api call
     * @param extra the severities we want each time
     */
    private int getIssuesNumbers(String extra){
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/issues/search?pageSize=500&componentKeys="
                    +project.getCredentials().getProjectName()+"&types=CODE_SMELL"+extra+"&p=1");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                String number =inline.split(",",2)[0].replace("{\"total\":", "");
                sc.close();
                return Integer.parseInt(number);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    /**
     * Get Issues From Sonar API
     * @param page the results
     * @param extra extra filtering
     */
    private void getIssuesFromPage(int page, String extra){
        try {
            date= new Date();
            URL url = new URL(Exa2Pro.sonarURL+"/api/issues/search?pageSize=500&componentKeys="
                    +project.getCredentials().getProjectName()+"&types=CODE_SMELL"+extra+"&p="+page);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();
                
                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                JSONArray jsonarr_1 = (JSONArray) jobj.get("issues");
                for(int i=0;i<jsonarr_1.size();i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    JSONObject jsonobj_2=(JSONObject)jsonobj_1.get("textRange");
                    Issue issue;
                    String debt;
                    if(jsonobj_1.get("debt") == null)
                    	debt = "0min";
                    else
                    	debt = jsonobj_1.get("debt").toString();
                    if(jsonobj_2 != null)
                    	issue=new Issue(jsonobj_1.get("rule").toString(), jsonobj_1.get("message").toString()
                            , jsonobj_1.get("severity").toString(), debt
                            , jsonobj_1.get("type").toString(), jsonobj_1.get("component").toString()
                            , jsonobj_2.get("startLine").toString(), jsonobj_2.get("endLine").toString());
                    else
                    	issue=new Issue(jsonobj_1.get("rule").toString(), jsonobj_1.get("message").toString()
                                , jsonobj_1.get("severity").toString(), jsonobj_1.get("debt").toString()
                                , jsonobj_1.get("type").toString(), jsonobj_1.get("component").toString()
                                , "0", "1");
                    issuesList.add(issue);
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Return the metric from Sonar Qube
     * @param metric the metric we want
     */
    public String getMetricFromSonarQube(String metric){
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/measures/component?component="
                    +project.getCredentials().getProjectName()+"&metricKeys="+metric);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();
                
                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                JSONObject jobj1= (JSONObject) jobj.get("component");
                JSONArray jsonarr_1 = (JSONArray) jobj1.get("measures");
                
                if(jsonarr_1.isEmpty())
                    return "0";
                JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(0);
                return jsonobj_1.get("value").toString();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "0";
    }
    
    /*
    * Returns if the project is finished being analyzed
    */
    public boolean isFinishedAnalyzing(){
        boolean finished=false;
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/ce/component?component="+project.getCredentials().getProjectName());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                while(sc.hasNext()){
                    String line=sc.nextLine();
                    if(line.trim().contains("\"analysisId\":") &&
                            line.trim().contains("\"queue\":[],")){
                        finished=true;
                    }
                }
                sc.close();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
        return finished;
    }

    
    // Getters
    public Project getProject() {
        return project;
    }
    public ArrayList<Issue> getIssuesList() {
        return issuesList;
    }
    public Date getDate() {
        return date;
    }
    public String getTotalDebt() {
        return totalDebt;
    }
    public double getTotalDebt_Index() {
        return totalDebt_Index;
    }
    public int getTotalCodeSmells() {
        return totalCodeSmells;
    }
    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }
    public int getTotalComplexity() { 
        return totalComplexity;
    }
    public String getLinesOfCodeForAllLanguages() {
        return linesOfCodeForAllLanguages;
    }
    public int getNewLinesOfCode() {
        return newLinesOfCode;
    }
    public int getDuplicated_blocks() {
        return duplicated_blocks;
    }
    public int getReliability_remediation_effort() {
        return reliability_remediation_effort;
    }
    public int getSecurity_remediation_effort() {
        return security_remediation_effort;
    }
    public HashMap<String, Integer> getTdOfEachFile() {
        return tdOfEachFile;
    }
    public double getTotalTDInterest() {
        return totalTDInterest;
    }
    public double getTDPrincipalSourceCodeDebt() {
        return TDPrincipalSourceCodeDebt;
    }
    public double getTDPrincipalDesignDebt() {
        return TDPrincipalDesignDebt;
    }
    public HashMap<String, Integer> getDuplicatedBblocksOfEachFile() {
        return duplicatedBblocksOfEachFile;
    }
    public HashMap<String, Integer> getReliabilityEffortOfEachFile() {
        return reliabilityEffortOfEachFile;
    }
    public HashMap<String, Integer> getSecurityEffortOfEachFile() {
        return securityEffortOfEachFile;
    }
    public HashMap<String, Integer> getCodeSmellsOfEachFile() {
        return codeSmellsOfEachFile;
    }

    // Setters
    public void setTotalDebt(String totalDebt) {
        this.totalDebt = totalDebt;
    }
    public void setTotalDebt_Index(double totalDebt) {
        this.totalDebt_Index = totalDebt;
    }
    public void setTotalCodeSmells(int totalCodeSmells) {
        this.totalCodeSmells = totalCodeSmells;
    }
    public void setTotalLinesOfCode(int linesOfCode) {
        this.totalLinesOfCode = linesOfCode;
    }
    public void setLinesOfCodeForAllLanguages(String linesOfCodeForAllLanguages) {
        this.linesOfCodeForAllLanguages = linesOfCodeForAllLanguages;
    }
    public void setTotalComplexity(int totalComplexity) {
        this.totalComplexity = totalComplexity;
    }
    public void setNewLinesOfCode(int newLinesOfCode) {
        this.newLinesOfCode = newLinesOfCode;
    }
    public void setDuplicated_blocks(int duplicated_blocks) {
        this.duplicated_blocks = duplicated_blocks;
    }
    public void setReliability_remediation_effort(int reliability_remediation_effort) {
        this.reliability_remediation_effort = reliability_remediation_effort;
    }
    public void setSecurity_remediation_effort(int security_remediation_effort) {
        this.security_remediation_effort = security_remediation_effort;
    }
    public void setTdOfEachFile(HashMap<String, Integer> tdOfEachFile) {
        this.tdOfEachFile = tdOfEachFile;
    }
    public void setDuplicatedBblocksOfEachFile(HashMap<String, Integer> duplicatedBblocksOfEachFile) {
        this.duplicatedBblocksOfEachFile = duplicatedBblocksOfEachFile;
    }
    public void setReliabilityEffortOfEachFile(HashMap<String, Integer> reliabilityEffortOfEachFile) {
        this.reliabilityEffortOfEachFile = reliabilityEffortOfEachFile;
    }
    public void setSecurityEffortOfEachFile(HashMap<String, Integer> securityEffortOfEachFile) {
        this.securityEffortOfEachFile = securityEffortOfEachFile;
    }
    public void setCodeSmellsOfEachFile(HashMap<String, Integer> codeSmellsOfEachFile) {
        this.codeSmellsOfEachFile = codeSmellsOfEachFile;
    }
}
