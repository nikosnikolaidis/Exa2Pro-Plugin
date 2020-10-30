/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsers.CodeFile;
import parsers.fortranFile;

/**
 *
 * @author Nikos
 */
public class Analysis {

    private Project project;

    public Analysis(Project project) {
        this.project = project;
    }
    
    //Execute the Analysis in this project
    public void executeAnalysis() {
        if(project.containsFortran()){
            try {
                project.copyFortranFilesToSiglePlace();
            } catch (IOException ex) {
                Logger.getLogger(Analysis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //For Windows
        if ( Exa2Pro.isWindows() )
            runAnalysisWindows();
        //For Linux
        else
            runAnalysisLinux();
        
        
        //export csv file 
        File tempFile= new File(project.getCredentials().getProjectName()+".csv");
        new csvControlers.CSVReadANDWrite(project.getprojectFiles(),project.getCredentials().getProjectName());
        
        
        //restor Temp Fortran Files
        try {
            project.restoreTempFortranFiles();
        } catch (IOException ex) {
            Logger.getLogger(Analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    // Creates the sonar-project.properties file
    // required to run sonar-scanner
    public void createPropertiesFile() {
        try {
            String propertiesFile= project.getCredentials().getProjectDirectory()+ "sonar-project.properties";
            if(!Exa2Pro.isWindows())
                propertiesFile= project.getCredentials().getProjectDirectory()+ "/sonar-project.properties";
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(propertiesFile));
            writer.write("sonar.projectKey=" + project.getCredentials().getProjectKey() + System.lineSeparator());
            writer.append("sonar.projectName=" + project.getCredentials().getProjectName() + System.lineSeparator());
            writer.append("sonar.projectVersion=" + project.getProjectVersion() + System.lineSeparator());

            writer.append("sonar.sources=." + System.lineSeparator());

            writer.append("sonar.sourceEncoding=UTF-8"+ System.lineSeparator());
            if(project.containsFortran()){
                writer.append("sonar.icode.launch=true" +System.lineSeparator());
                writer.append("sonar.icode.path="+ Exa2Pro.iCodePath.replace("\\", "\\\\") +System.lineSeparator());
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Runs our metrics
    public void runCustomCreatedMetrics() {
        for (CodeFile file : project.getprojectFiles()) {
            file.parse();
            file.calculateCohesion();
        }
        
        //FanOut from invocations
        //for each file
        for (CodeFile file : project.getprojectFiles()) {
            if(file instanceof fortranFile){
                HashSet<String> fanOutFiles= new HashSet<>();
                //for each invocation
                for (String entry : file.methodInvocations) {
                    String[] table= entry.split(";",2);
                    //check if not in same file
                    boolean methodMine= false;
                    for (Map.Entry<String, Integer> entry2 : file.methodsLOC.entrySet()) {
                        if(table[0].equalsIgnoreCase(entry2.getKey())){
                            methodMine= true;
                        }
                    }
                    //check in every other file the methods
                    if(!methodMine){
                        for (CodeFile file2 : project.getprojectFiles()) {
                            for (Map.Entry<String, Integer> entry2 : file2.methodsLOC.entrySet()) {
                                if(table[0].equalsIgnoreCase(entry2.getKey()) 
                                            || table[0].equalsIgnoreCase(entry2.getKey().replaceAll("static|void", "").trim())){
                                    fanOutFiles.add(file2.file.getName());
                                }
                            }
                        }
                    }
                }
                //fanOut update
                file.fanOut+= fanOutFiles.size();
            }
        }
        
        //FanOut from common blocks
        //for each file
        for (CodeFile file : project.getprojectFiles()) {
            HashSet<String> fanOutFiles= new HashSet<>();
            //for each common block
            for (Map.Entry<String, String> entry : file.commonBlockDeclaration.entrySet()) {
                //check in every other file the common block
                for (CodeFile file2 : project.getprojectFiles()) {
                    if(!file.file.getName().equals(file2.file.getName())){
                        for (Map.Entry<String, String> entry2 : file2.commonBlockDeclaration.entrySet()) {
                            if(entry.getKey().split("/")[1].trim().equalsIgnoreCase(entry2.getKey().split("/")[1].trim())){
                                fanOutFiles.add(file2.file.getName());
                            }
                        }
                    }
                }
            }
            //fanOut update
            file.fanOut+= fanOutFiles.size();
        }
        
        
        //LCOM real
        //for each file
        for (CodeFile file : project.getprojectFiles()) {
            if(!file.attributes.isEmpty()){
                int numberOfMethods= file.methodsLOC.size();
                ArrayList<String> methodPairsShareAttr=new ArrayList<>();
                int shareAttrib= 0;
                for(String str: file.attributesInMethods){
                    for(String str2: file.attributesInMethods){
                        if(!str.equals(str2) && str.split(" ",2)[0].equals(str2.split(" ",2)[0])){
                            shareAttrib++;
                            methodPairsShareAttr.add(str.split(" ",2)[1] +" "+ str2.split(" ",2)[1]);
                        }
                    }
                }
                shareAttrib=shareAttrib/2;

                ArrayList<String> methodPairs=new ArrayList<>();
                for(String str: file.methodsLOC.keySet()){
                    for(String str2: file.methodsLOC.keySet()){
                        if(!str.equals(str2) 
                                && !methodPairsShareAttr.contains(str +" "+ str2)
                                && !methodPairsShareAttr.contains(str2 +" "+ str)
                                && !methodPairs.contains(str +" "+ str2)
                                && !methodPairs.contains(str2 +" "+ str) )
                            methodPairs.add(str +" "+ str2);
                    }
                }

                int lcop= 0;
                if(methodPairs.size()>shareAttrib)
                    lcop= methodPairs.size() - shareAttrib;

                //System.out.println("----file: "+ file.file.getName()+" meth: "+numberOfMethods+" methodPairs:"+methodPairs.size()+" shareAttrib:"+shareAttrib+ " lcom: "+ lcop);

                file.lcop=lcop;
            }
            else
                file.lcop= -1;
        }
        
        //print
        for (CodeFile file : project.getprojectFiles()) {
            if(file.lcop!=-1)
                System.out.println("File: "+file.file.getPath()+" ---> "+"fan-out:"+file.fanOut
                        +"  lcop:"+file.lcop + "  lcol:"+file.cohesion);
            else
                System.out.println("File: "+file.file.getPath()+" ---> "+"fan-out:"+file.fanOut
                        + "  lcol:"+file.cohesion);
        }
    }

    // Windows
    // Runs in CMD sonar-scanner
    private void runAnalysisWindows() {
        try {
            //TODO
            // Add waiting animation

            System.out.println(System.getProperty("user.dir"));
            Process proc = Runtime.getRuntime().exec("cmd /c \"cd " + project.getCredentials().getProjectDirectory() + " && "
                    + Exa2Pro.sonarScannerPath+ "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //waits till sonar scanner finishes
            String line;
            while ((line = reader.readLine()) != null) {    
                System.out.println(line);
                if (line.contains("INFO: Final Memory")) {
                    break;
                }
            }
            //create a report and wait till sonarqube finishes analysing
            Report report = new Report(project);
            project.setProjectReport(report);
            while (!report.isFinishedAnalyzing()) {
                Thread.sleep(1000);
            }
            Thread.sleep(500);
            
            // Can Read Reports now
            double dept = Double.parseDouble(report.getMetricFromSonarQube("sqale_index"));
            report.setTotalDebt(formatTehnicalDebt(dept));
            report.setTotalDebt_Index(dept);
            report.setTotalCodeSmells(Integer.parseInt(report.getMetricFromSonarQube("code_smells")));
            report.setTotalLinesOfCode(Integer.parseInt(report.getMetricFromSonarQube("ncloc")));
            report.setTotalComplexity(Integer.parseInt(report.getMetricFromSonarQube("complexity")));
            report.setLinesOfCodeForAllLanguages(report.getMetricFromSonarQube("ncloc_language_distribution").replace(";", "\n"));
            
            report.getIssuesFromSonarQube();
            report.getTDLinesOfFilesFromSonarQube();
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Formats the Technical Debt from number to string with days, hours or minutes
     * @param dept the total debt in minutes
     */
    private String formatTehnicalDebt(double dept) {
        String str = "";
        DecimalFormat df = new DecimalFormat("#.#");
        if (dept > 59) {
            dept = dept / 60;
        } else {
            str = "min";
        }
        if (dept > 7) {
            dept = dept / 8;
            str = "d";
        } else {
            str = "h";
        }
        return Double.parseDouble(df.format(dept)) + str;
    }

    // Linux
    // runs in terminal sonar-scanner
    private void runAnalysisLinux() {                     
        try {
            ProcessBuilder pbuilder = new ProcessBuilder("bash", "-c", 
                    "cd '"+project.getCredentials().getProjectDirectory()+"' ; '"+
                    Exa2Pro.sonarScannerPath+ "'");
            File err = new File("err.txt");
            pbuilder.redirectError(err);
            Process p = pbuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            System.out.println("Sonar Scanner in project folder");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("INFO: Final Memory")) {
                    break;
                }
            }
            //create a report and wait till sonarqube finishes analysing
            Report report = new Report(project);
            project.setProjectReport(report);
            while (!report.isFinishedAnalyzing()) {
                Thread.sleep(1000);
            }
            Thread.sleep(500);
            
            // Can Read Reports now
            double dept = Double.parseDouble(report.getMetricFromSonarQube("sqale_index"));
            report.setTotalDebt(formatTehnicalDebt(dept));
            report.setTotalDebt_Index(dept);
            report.setTotalCodeSmells(Integer.parseInt(report.getMetricFromSonarQube("code_smells")));
            report.setTotalLinesOfCode(Integer.parseInt(report.getMetricFromSonarQube("ncloc")));
            report.setTotalComplexity(Integer.parseInt(report.getMetricFromSonarQube("complexity")));
            report.setLinesOfCodeForAllLanguages(report.getMetricFromSonarQube("ncloc_language_distribution").replace(";", "\n"));
            
            report.getIssuesFromSonarQube();
            report.getTDLinesOfFilesFromSonarQube();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
