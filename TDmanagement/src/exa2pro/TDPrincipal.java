/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class TDPrincipal {
    
    double totalDebt_Index;
    Project project;
    double SourceCodeDebt=0;
    double DesignDebt=0;
    
    public TDPrincipal(double totalDebt_Index, Project project){
        this.totalDebt_Index= totalDebt_Index;
        this.project= project;
        
        calculateSourceCodeDebt();
        calculateDesignDebt();
    }
    
    public double getSourceCodeDebt(){
        return SourceCodeDebt;
    }
    public double getDesignDebt(){
        return DesignDebt;
    }

    /**
     * Calculate Source Code Debt
     */
    private void calculateSourceCodeDebt() {
        SourceCodeDebt= (totalDebt_Index/60)*39.44;
        
        System.out.println("Source Code Debt: " + SourceCodeDebt);
    }
    
    /**
     * Calculate Design Debt
     */
    private void calculateDesignDebt() {
        HashMap<String, Double> thresholds= exa2pro.PieChart.calculateThresholds();
        HashSet<String> allFilesCBF = new HashSet<>();
        HashSet<String> allFilesLOC = new HashSet<>();
        HashSet<String> allFilesLCOP = new HashSet<>();
        HashSet<String> allLargeFiles = new HashSet<>();
        HashSet<String> allMethodsLCOL = new HashSet<>();
        HashSet<String> allMethodsCC = new HashSet<>();
        for(CodeFile cf: project.getprojectFiles()){
            if(cf.fanOut >= thresholds.get("FanOut"))
                allFilesCBF.add(cf.file.getName());
            if(cf.lcop!=-1 && Math.round(cf.lcop * 10.0)/10.0 >= thresholds.get("LCOP"))
                allFilesLCOP.add(cf.file.getName());
            if(cf.totalLines>=thresholds.get("LOC"))
                allFilesLOC.add(cf.file.getName());
            
            allLargeFiles.addAll(allFilesLOC);
            allLargeFiles.addAll(allFilesLCOP);
            allMethodsCC.addAll(prefixHashMap(cf.methodsCC, cf.file.getName(), thresholds, "CC"));
            allMethodsLCOL.addAll(prefixHashMap(cf.methodsLCOL, cf.file.getName(), thresholds, "LCOL"));
        }
        
        int OverCoupledFiles= allFilesCBF.size();
        int LargeFiles= allLargeFiles.size();
        int LongProcedures= allMethodsLCOL.size();
        int ComplexPorocedures= allMethodsCC.size();
        
        DesignDebt= (LongProcedures+ComplexPorocedures)*6.56 + (LargeFiles+OverCoupledFiles)*9.59;
        
        System.out.println("Design Debt: " + DesignDebt);
    }
    
    private HashSet prefixHashMap(HashMap source, String prefix, HashMap<String, Double> thresholds, String metric) {
        HashSet result = new HashSet();
        Iterator iter = source.entrySet().iterator();
        while(iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof Integer) {
            	if((Integer)value >= thresholds.get(metric))
            		result.add(prefix + '.' + key.toString());
            }
            else {
            	if((Double)value >= thresholds.get(metric))
            		result.add(prefix + '.' + key.toString());
            }
        }
        return result;
    }
}
