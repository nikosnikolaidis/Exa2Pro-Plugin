/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import semi.Analyser;
import semi.JavaClass;
import semi.Method;
import semi.MethodOppExtractor;
import semi.MethodOppExtractorSettings;
import semi.clustering.Opportunity;

/**
 *
 * @author Nikos
 */
public class ParsedFilesController {
    private ArrayList<JavaClass> classResults = new ArrayList<>();
    private String selected_metric = "SIZE";// "LCOM1", "LCOM2", "LCOM4", "COH", "CC" //***???
    public MethodOppExtractorSettings extractor_settings =new MethodOppExtractorSettings();
    private int number_of_refs = 0;
        
    int methodLineStart;
    int methodLineStop;
    
    public double doAnalysisLcom(File file) {
        Analyser analyser = new Analyser();
        analyser.setFile(file);
        JavaClass myclass=analyser.performAnalysis();
        
        //Calculate sum of all methods lcom2
        double sum=0;
        for(int i=0; i< myclass.getMethods().size(); i++){
            sum += myclass.getMethods().get(i).getMetricIndexFromName("lcom2");
        }
        
	File fileDel = new File("./" + file.getName() + "_parsed.txt");
        fileDel.delete();
        
        //return average
        int num=myclass.getMethods().size();
        if(num==0)
            num=1;
        return sum/num;
    }
    
    
    public ArrayList<String> calculateOpportunities(boolean fast ,File file, String language,
                    HashMap<String, Integer> methodsLocDecl) throws FileNotFoundException {
        ArrayList<String> opportunitiesList=new ArrayList<>();
        
        //get files lines
        File parsedFile = new File(file.getAbsolutePath());
        Scanner input = new Scanner(new FileInputStream(parsedFile));
        ArrayList<String> fileLines = new ArrayList<String>();
        
        while (input.hasNextLine()) {
            fileLines.add(input.nextLine());
        }
        input.close();
        
        //change small if to one line
        if(fast){
            ExtraParseUtils epu=new ExtraParseUtils();
            epu.convertSimpleIfsToLine(file.getName() + "_parsed.txt");
        }
        
        //Get All start lines of methods in order
        ArrayList<Integer> allStartLines= new ArrayList<>();
        Iterator it = methodsLocDecl.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            allStartLines.add((int) pair.getValue());
        }
        Collections.sort(allStartLines);
        
        //Start analysis and opp extractor
        Analyser analyser = new Analyser();
        analyser.setFile(file);
        //JavaClass myclass=analyser.performAnalysis();
        
        try {
        
        classResults.add(analyser.performAnalysis());
        JavaClass clazz = classResults.get(classResults.size()-1);
        for (int index = 0; index < clazz.getMethods().size(); index++) {
            boolean needsRefactoring = clazz.getMethods().get(index).needsRefactoring(selected_metric);	
            
            if(needsRefactoring) {
                ArrayList<String> opportunitiesListMethod=new ArrayList<>();
            	String className = file.getName().replaceFirst("./", "");
            	String methodName = clazz.getMethods().get(index).getName();
            	
            	MethodOppExtractor extractor = new MethodOppExtractor(file, clazz.getMethods().get(index).getName(), extractor_settings, classResults.get(classResults.size()-1));

            	Method method = clazz.getMethods().getMethodByName(methodName);
            	ArrayList<Opportunity> opportunities = method.getOpportunityList().getOptimals();
            	
                System.out.println(method.getName()+"  "+method.getMethodEnd());
                
                int cnt = 1;
                for (int i = 0; i < opportunities.size(); i++) {
                    Opportunity opp = opportunities.get(i);
                    if (!opp.isBeaten()) {
                        
                        //normalize numbers in case of ifs in one line
                        int start= opp.getStartLineCluster();
                        int stop= opp.getEndLineCluster();
                        if(language.equals("f90") || language.equals("f77")){
                            if(start-2>=0){
                                String lineBeforeStart= fileLines.get(start-2);
                                if(lineBeforeStart.toLowerCase().contains("if ") || lineBeforeStart.toLowerCase().contains("if("))
                                    start=start-1;
                            }
                            if(stop+2< method.getMethodEnd()){
                                String lineAfterStop= fileLines.get(stop);
                                if(lineAfterStop.contains("end")){
                                    int countCstart=0;
                                    int countCstop=0;
                                    for(int k=start; k<=stop; k++){

                                        if( (language.equals("f90") && hasIFStartF90(fileLines,k-1))
                                                || (language.equals("f77") && hasIFStartF77(fileLines,k-1))
                                                || fileLines.get(k-1).toLowerCase().trim().startsWith("do"))
                                            countCstart++;
                                        if(fileLines.get(k-1).toLowerCase().trim().startsWith("end") && !fileLines.get(k-1).toLowerCase().contains("select"))
                                            countCstop++;
                                    }
                                    if(countCstart>countCstop)
                                        stop=stop+1;
                                }
                            }
                        }
                        
                        //Before add check same start and stop conditions
                        if(areSameStartsStopsConditions(language,start,stop,fileLines)){
                            if(!fast){
                                //save opportunity
                                opportunitiesListMethod.add(start +" " +stop+ " "+ methodName);
                                //print
                                System.out.println("---"+cnt++ + "  " + opp.getStartLineCluster() +" to "+ opp.getEndLineCluster());
                            }
                            else{
                                //save opportunity
                                opportunitiesListMethod.add(start +" " +stop+ " "+ methodName.replaceFirst("\\(.*\\)", "")+"() "+ opp.lcom2_benefit);
                                //print
                                System.out.println("---"+cnt++ + "  " + opp.getStartLineCluster() +" to "+ opp.getEndLineCluster()
                                        +"  benef:"+ opp.lcom2_benefit);
                            }
                        }
                    }
                }
                
                double initialMethodCohesion;
                
                if(!fast){
                //get method line start and stop
                methodLineStart= 0;
                methodLineStop= method.getMethodEnd();
                Iterator it2 = methodsLocDecl.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair = (Map.Entry)it2.next();
                    if( pair.getKey().equals(methodName.split("\\(", 2)[0]) ){
                        methodLineStart= (int) pair.getValue();
                        break;
                    }
                }
                System.out.println("----------Method: "+methodName+"    "+methodLineStart+"-"+methodLineStop);
                
                //original method
                initialMethodCohesion= calculateOriginalMethodCohesion(file, language, fileLines);
                }
                else{
                    initialMethodCohesion=1;
                }
                
                if(initialMethodCohesion != 0){
                System.out.println("----------Method: "+methodName+"    "+methodLineStart+"-"+methodLineStop);
                    //Filter Opportunities
                    int countAdded=0;
                    String added="";
                    for(String str: opportunitiesListMethod){
                        boolean add=true;
                        int startL= Integer.parseInt(str.split(" ")[0]);
                        int stopL= Integer.parseInt(str.split(" ")[1]);
                        for(String str2: opportunitiesListMethod){
                            if(!str.equals(str2)){
                                if(startL>=Integer.parseInt(str2.split(" ")[0]) && stopL<=Integer.parseInt(str2.split(" ")[1])){
                                    add=false;
                                    break;
                                }
                            }
                        }
                        if(add){
                            countAdded++;
                            added=str;
                            if(!fast){
                                //new method
                                double newMethodCohesion = calculateNewMethodCohesion(file, startL, stopL, fileLines);
                                //extracted method
                                double extractMethodCohesion = calculateExtractedMethodCohesion(file, fileLines, startL, stopL);

                                // Benefit
                                double lcom2_benefit = initialMethodCohesion - Math.max(newMethodCohesion, extractMethodCohesion);

                                opportunitiesList.add(str+" "+lcom2_benefit);
                            }
                            else{
                                opportunitiesList.add(str);
                            }
                        }
                    }
                    
                    //if only one big opportunity added, add the next ones as well
                    if(countAdded==1){
                        opportunitiesListMethod.remove(added);
                        for(String str: opportunitiesListMethod){
                            boolean add=true;
                            int startL= Integer.parseInt(str.split(" ")[0]);
                            int stopL= Integer.parseInt(str.split(" ")[1]);
                            for(String str2: opportunitiesListMethod){
                                if(!str.equals(str2)){
                                    if(startL>=Integer.parseInt(str2.split(" ")[0]) && stopL<=Integer.parseInt(str2.split(" ")[1])){
                                        add=false;
                                        break;
                                    }
                                }
                            }
                            if(add){
                                if(!fast){
                                    //new method
                                    double newMethodCohesion = calculateNewMethodCohesion(file, startL, stopL, fileLines);
                                    //extracted method
                                    double extractMethodCohesion = calculateExtractedMethodCohesion(file, fileLines, startL, stopL);

                                    // Benefit
                                    double lcom2_benefit = initialMethodCohesion - Math.max(newMethodCohesion, extractMethodCohesion);

                                    opportunitiesList.add(str+" "+lcom2_benefit);
                                }
                                else{
                                    opportunitiesList.add(str);
                                }
                            }
                        }
                    }
                    
                }
                
                System.out.println("File Name:"+className+" M:"+methodName+ "   " + method.getMetricIndexFromName("lcom2"));
            }
        }
        
        }catch (OutOfMemoryError E) {
            System.out.println("Out of memory!");
	}
        
        
	File fileDel = new File("./" + file.getName() + "_parsed.txt");
        fileDel.delete();
        
        return opportunitiesList;
    }
    
    private boolean areSameStartsStopsConditions(String language, int start, int stop, ArrayList<String> fileLines){
        int countCstart=0;
        int countCstop=0;
        if(language.equals("f90") || language.equals("f77")){
            for(int k=start; k<=stop; k++){
                if( (language.equals("f90") && hasIFStartF90(fileLines,k-1))
                        || (language.equals("f77") && hasIFStartF77(fileLines,k-1))
                        || fileLines.get(k-1).toLowerCase().trim().startsWith("do"))
                    countCstart++;
                if(fileLines.get(k-1).toLowerCase().trim().startsWith("end") && !fileLines.get(k-1).toLowerCase().contains("select"))
                    countCstop++;
            }
        }
        else{
            for(int k=start; k<=stop; k++){
                if(fileLines.size()>=stop){
                    if( fileLines.get(k-1).contains("{") && !fileLines.get(k-1).matches(".*\\\".*(\\{|\\}).*\\\".*") )
                        countCstart++;
                    if( fileLines.get(k-1).contains("}") && !fileLines.get(k-1).matches(".*\\\".*(\\{|\\}).*\\\".*") )
                        countCstop++;
                }
            }
        }
        return countCstart == countCstop;
    }

    /**
     * Create new file for original method and calculate lcom2
     * @param file
     * @param methodLineStart
     * @param methodLineStop
     * @param fileLines
     * @return 
     */
    private double calculateOriginalMethodCohesion(File file, String language, ArrayList<String> fileLines) {
        int lastOneInit=0;
        
        if(language.equals("c") && !areSameStartsStopsConditions(language, methodLineStart, methodLineStop, fileLines)){
            int lineN=methodLineStop+1;
            for(int i=lineN; i<=fileLines.size(); i++){
                if(fileLines.get(i-1).contains("}") 
                            && areSameStartsStopsConditions(language, methodLineStart, i, fileLines)){
                    methodLineStop=i+1;
                    break;
                }
                if(!fileLines.get(i-1).trim().equals("") && !fileLines.get(i-1).trim().startsWith("#")
                            && !fileLines.get(i-1).trim().startsWith("//")){
                    break;
                }
            }
        }
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("temp_"+file.getName(), true));
            for(int i=methodLineStart-1; i<methodLineStop-1; i++){
                if(!fileLines.get(i).trim().equals(""))
                    lastOneInit=i;
                    writer.append(fileLines.get(i)+System.lineSeparator());
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ParsedFilesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double initialMethodCohesion=0;
        if( (language.equals("c") && !areSameStartsStopsConditions(language, methodLineStart, methodLineStop, fileLines))
                || ( (language.equals("f90") || language.equals("f77")) 
                        && !fileLines.get(lastOneInit).toLowerCase().contains("end subroutine") 
                        && !fileLines.get(lastOneInit).toLowerCase().contains("end function")
                        && !fileLines.get(lastOneInit).toLowerCase().contains("endsubroutine") 
                        && !fileLines.get(lastOneInit).toLowerCase().contains("endfunction")) ){
            File fileDel = new File("./temp_" + file.getName());
            fileDel.delete();
        }
        else{
            fortranFile ffInitial = new fortranFile(new File("./temp_"+file.getName()), true);
            ffInitial.parse();
            ffInitial.calculateCohesion();
            
            File fileDelInit = new File("./temp_"+file.getName());
            fileDelInit.delete();
            initialMethodCohesion= ffInitial.cohesion;
        }
        
        return initialMethodCohesion;
    }

    /**
     * Create new file for extracted method and calculate lcom2
     * @param file
     * @param fileLines
     * @param methodLineStart
     * @param methodLineStop
     * @param startL
     * @param stopL
     * @return 
     */
    private double calculateExtractedMethodCohesion(File file, ArrayList<String> fileLines, int startL, int stopL) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("temp_"+file.getName(), true));
            writer.append("subroutine extracted()" + System.lineSeparator());
            for(int i=methodLineStart-1; i<=methodLineStop-1; i++){
                if(i+1>=startL && i+1<=stopL){
                    writer.append(fileLines.get(i)+System.lineSeparator());
                }
            }
            writer.append("end subroutine");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ParsedFilesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        fortranFile ffExtractMethod = new fortranFile(new File("./temp_"+file.getName()), true);
        ffExtractMethod.parse();
        ffExtractMethod.calculateCohesion();
        File fileDelEx = new File("./temp_"+file.getName());
        fileDelEx.delete();
        return ffExtractMethod.cohesion;
    }

    /**
     * Create new file for new method and calculate lcom2
     * @param file
     * @param methodLineStart
     * @param methodLineStop
     * @param startL
     * @param stopL
     * @param fileLines
     * @return 
     */
    private double calculateNewMethodCohesion(File file, int startL, int stopL, ArrayList<String> fileLines) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("temp_"+file.getName(), true));
            int lastOne=0;
            for(int i=methodLineStart-1; i<methodLineStop-1; i++){
                if(i+1<startL || i+1>stopL){
                    if(!fileLines.get(i).trim().equals(""))
                        lastOne=i;
                    writer.append(fileLines.get(i)+System.lineSeparator());
                }
            }
            if(!fileLines.get(lastOne).toLowerCase().contains("end subroutine")
                    && !fileLines.get(lastOne).toLowerCase().contains("end function")
                    && !fileLines.get(lastOne).toLowerCase().contains("endsubroutine")
                    && !fileLines.get(lastOne).toLowerCase().contains("endfunction")){
                writer.append("end subroutine");
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ParsedFilesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        fortranFile ffNewMethod = new fortranFile(new File("./temp_"+file.getName()), true);
        ffNewMethod.parse();
        ffNewMethod.calculateCohesion();
        File fileDelNew = new File("./temp_"+file.getName());
        fileDelNew.delete();
        return ffNewMethod.cohesion;
    }
    
    /*
     * Checks if starts in line
     *  and recursive in next lines for Fortran 90
     * @param line the number of line to check
    */
    public boolean hasIFStartF90(ArrayList<String> fileLines, int line){
        if(fileLines.get(line).trim().toLowerCase().startsWith("if")){
            if(fileLines.get(line).trim().toLowerCase().endsWith("then")){
                return true;
            }
            else if(fileLines.get(line).trim().toLowerCase().endsWith("&")){
                hasIFStartF90(fileLines, line+1);
            }
        }
        else{
            if(fileLines.get(line).trim().toLowerCase().endsWith("then")){
                return true;
            }
            else if(fileLines.get(line).trim().toLowerCase().endsWith("&")){
                hasIFStartF90(fileLines, line+1);
            }
            else{
                return false;
            }
        }
        return false;
    }
    
    /*
     * Checks if starts in line
     *  and recursive in next lines for Fortran 77
     * @param line the number of line to check
    */
    public boolean hasIFStartF77(ArrayList<String> fileLines, int line){
        if(fileLines.get(line).trim().toLowerCase().startsWith("if")){
            if(fileLines.get(line).trim().toLowerCase().endsWith("then")){
                return true;
            }
            else if(fileLines.get(line+1).trim().toLowerCase().startsWith("&")){
                hasIFStartF77(fileLines, line+1);
            }
        }
        else{
            if(fileLines.get(line).trim().toLowerCase().endsWith("then")){
                return true;
            }
            else if(fileLines.get(line+1).trim().toLowerCase().startsWith("&")){
                hasIFStartF77(fileLines, line+1);
            }
            else{
                return false;
            }
        }
        return false;
    }
}
