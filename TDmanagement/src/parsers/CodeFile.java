/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import exa2pro.Exa2Pro;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nikos
 */
public abstract class CodeFile implements Serializable{
    public File file;
    public int fanOut;
    public int lcop;
    public int totalLines;
    public HashSet<String> attributes;
    public HashSet<String> attributesInMethods;
    public HashSet<String> methodInvocations;
    public HashMap<String, String> commonBlockDeclaration;
    public HashMap<String, Integer> methodsLOC;
    public HashMap<String, Integer> methodsCC;
    public HashMap<String, Double> methodsLCOL;
    public double cohesion;
    public ArrayList<String> opportunities;
    
    public CodeFile(File file){
        this.file=file;
        methodsLOC= new HashMap<>();
        methodsLCOL= new HashMap<>();
        methodsCC= new HashMap<>();
        methodInvocations= new HashSet<>();
        commonBlockDeclaration= new HashMap<>();
        attributes= new HashSet<>();
        attributesInMethods= new HashSet<>();
    }
    
    public abstract void parse();
    public abstract void calculateCohesion();
    public abstract void calculateOpportunities(boolean fast, String methodName);
    public abstract boolean exportCSVofAtribute();
    
    public ArrayList<String> runClustering(double threshold){
        ArrayList<String> clusters= new ArrayList<>();
        //For Windows
        if ( Exa2Pro.isWindows() ){
            try {
                //start clustering scrips
                Process proc1 = Runtime.getRuntime().exec("cmd /c \"cd " + Exa2Pro.ClusteringPath + 
                        " && "+ Exa2Pro.pythonRun +" AgglomerativeClustering.py "+ threshold +"\"");
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    clusters.add(line1);
                    System.out.println(line1);
                }
            } catch (IOException ex) {
                Logger.getLogger(CodeFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //For Linux
        else{
            try {
                //start clustering scrips
                ProcessBuilder pbuilder1 = new ProcessBuilder(new String[]{Exa2Pro.pythonRun, Exa2Pro.ClusteringPath+ "/AgglomerativeClustering.py", threshold+""});
                File err1 = new File("err1.txt");
                pbuilder1.directory(new File(Exa2Pro.ClusteringPath));
                pbuilder1.redirectError(err1);
                Process p1 = pbuilder1.start();
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    clusters.add(line1);
                    System.out.println(line1);
                }
            } catch (IOException ex) {
                Logger.getLogger(CodeFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return clusters;
    }
    
}
