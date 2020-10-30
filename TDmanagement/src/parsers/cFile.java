
package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Nikos
 */
public class cFile extends CodeFile{

    boolean commentBlock=false;
    String line;
    HashMap<String, Integer> methodsLocDecl=new HashMap<>();
    HashMap<String, Integer> methodsLocDeclReal=new HashMap<>();
    
    public cFile(File file) {
        super(file);
    }

    @Override
    public void parse() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            fanOut=0;
            int countLOC=0;
            int countLOCr=0;
            boolean lineContinuous=false;
            int lineContinuousParOpen=0;
            int lineContinuousParClose=0;
            ArrayList<String> includeFiles=new ArrayList<>();
            ArrayList<String> allInvocations=new ArrayList<>();
            String pre_Inv="";
            
            boolean methodStarted=false;
            int methodCC=0;
            int lineMethodStarts=-1;
            int lineMethodDecl=-1;
            int lineMethodDeclR=-1;
            int methodBrackOpen=0;
            int methodBrackClose=0;
            String methodName="";
            
            System.out.println(file.getAbsolutePath()+"  "+file.getName());
            String linePre="asd";
            while ((line = br.readLine()) != null) {
                countLOCr++;
                if (!line.trim().equals("")){
                    line= line.trim().replace(" +", " ");
                    
                    String[] lineTable= line.trim().split(" ");
                    if( !isCommentLine(line.trim()) && !commentBlock ){
                        countLOC ++;
                    
                        // Method Invocation
                        if(Pattern.matches(".*[^\\s]*\\(.*", line)){
                            Pattern p = Pattern.compile("[^\\s]*\\(");
                            Matcher m = p.matcher(replaceWithSpaces(line).replaceAll("\\(", "\\( "));
                            while(m.find()) {
                                String temp= (String) m.group().subSequence(0, m.group().length()-1);
                                String method= methodName.split(" ")[methodName.split(" ").length-1];
                                if(!temp.equals("") && !pre_Inv.equals(method) ){
                                    allInvocations.add(temp +";"+ method);
                                }
                                pre_Inv=temp;
                            }
                        }
                        
                        // For fan-out
                        if( lineTable[0].equals("#include") ){
                            String temp=line.replace("#include", "");
                            if(temp.trim().charAt(0)=='"' && !includeFiles.contains(temp.trim())){
                                includeFiles.add(temp.trim());
                                fanOut ++;
                            }
                        }
                        else if( lineTable[0].equals("#") && lineTable[1].equals("include") ){
                            String temp=line.replace("# include", "");
                            if(temp.trim().charAt(0)=='"' && !includeFiles.contains(temp.trim())){
                                includeFiles.add(temp.trim());
                                fanOut ++;
                            }
                        }
                        
                        //For LCOM real(LCOP)
                        if(methodsLocDecl.isEmpty() && line.contains(";")){
                            String temp= line.replaceAll("=.*", "").trim().replaceAll("\\(.*\\)", "").replaceAll("\\[.*\\]", "").trim().replaceAll("::", " ");
                            String[] tempTable= temp.split(" ");
                            temp= tempTable[tempTable.length-1];
                            attributes.add(temp);
                        }
                        if(!methodsLocDecl.isEmpty()){
                            String[] lineVar= replaceWithSpaces(line).replaceAll("\\(", " ").split(" ");
                            for(String str: lineVar){
                                if( attributes.contains(str.trim()) ){
                                    attributesInMethods.add(str.trim()+" "+methodName);
                                }
                            }
                        }
                        
                        
                        // For start count LOC in function/subroutine
                        if(lineContinuous && (lineContinuousParOpen == lineContinuousParClose) && !line.contains("{")){
                            lineContinuous=false;
                        }
                        
                        if(!line.contains(";") && line.contains("(") && !lineContinuous && !methodStarted 
                                && !lineTable[0].contains("#") && linePre.charAt(linePre.length()-1)!='\\' 
                                && lineTable[0].charAt(0)!='{'){
                            //System.out.println(line);
                            String[] methodDecl=line.split("\\(");
                            methodName= methodDecl[0];
                            lineMethodDecl=countLOC;
                            lineMethodDeclR=countLOCr;
                            for (int i = 0; i < line.length(); i++) {
                                if (line.charAt(i) == '(')
                                    lineContinuousParOpen++;
                                if (line.charAt(i) == ')')
                                    lineContinuousParClose++;
                            }
                            if(lineContinuousParOpen == lineContinuousParClose){
                                if(line.contains("{")){
                                    lineMethodStarts=countLOC;
                                    methodsLocDecl.put(methodName,lineMethodDecl);
                                    methodsLocDeclReal.put(methodName, lineMethodDeclR);
                                    methodStarted=true;
                                    lineContinuous=false;
                                }
                                else{
                                    lineContinuous=true;
                                }
                            }
                            else{
                                lineContinuous=true;
                            }
                        }
                        else if(lineContinuous){
                            for (int i = 0; i < line.length(); i++) {
                                if (line.charAt(i) == '(')
                                    lineContinuousParOpen++;
                                if (line.charAt(i) == ')')
                                    lineContinuousParClose++;
                            }
                            if(lineContinuousParOpen == lineContinuousParClose){
                                if(line.contains("{")){
                                    lineMethodStarts=countLOC;
                                    methodsLocDecl.put(methodName,lineMethodDecl);
                                    methodsLocDeclReal.put(methodName, lineMethodDeclR);
                                    methodStarted=true;
                                    lineContinuous=false;
                                }
                                else{
                                    lineContinuous=true;
                                }
                            }
                            else{
                                lineContinuous=true;
                            }
                        }
                        
                        // For stop count LOC in function/subroutine
                        if(methodStarted){
                            lineContinuousParOpen=0;
                            lineContinuousParClose=0;
                            for (int i = 0; i < line.length(); i++) {
                                if(line.charAt(i) == '{')
                                    methodBrackOpen++;
                                if(line.charAt(i) == '}')
                                    methodBrackClose++;
                            }
                            if(methodBrackOpen == methodBrackClose){
                                methodsLOC.put(methodName,(countLOC-lineMethodStarts -1));
                                methodsCC.put(methodName, methodCC);
                                methodStarted=false;
                                methodBrackOpen=0;
                                methodBrackClose=0;
                                methodCC=0;
                            }
                            else{
                                //calculate cc
                                if(lineTable.length>0) {
	                            	if(lineTable[0].equalsIgnoreCase("if") || lineTable[0].equalsIgnoreCase("else") ||
                                                lineTable[0].equalsIgnoreCase("for") || lineTable[0].equalsIgnoreCase("while") ||
	                                        (line.toLowerCase().contains("case") && line.contains(":"))){
	                                    methodCC++;
	                                }
                            	}
                            	if(lineTable.length>1) {
                            		if((lineTable[0].equals("}") && lineTable[1].equalsIgnoreCase("else")))
                            			methodCC++;
                            	}
                            }
                        }
                        if(line.contains("/*") && !line.contains("*/")){
                            commentBlock=true;
                        }
                    }
                    else if(commentBlock){          //TODO test if ->  */ <code>
                        stopCommentBlock(line);
                    }
                    
                linePre=line;
                }
            }
            
            //keep only relevant invocations
            ArrayList<String> invocations=new ArrayList<>();
            for(String str: allInvocations){
                for(String str2: methodsLOC.keySet()){
                    String meth= str2.split(" ")[str2.split(" ").length-1];
                    if(str.split(";")[0].equals(meth)){
                        invocations.add(str);
                        break;
                    }
                }
            }
            for(int i=0; i<invocations.size()-1; i++){
                if(!invocations.get(i).split(";")[0].equals(invocations.get(i+1).split(";")[1])){
                    this.methodInvocations.add(invocations.get(i));
                }
                //the last one is lost!
            }
            
            totalLines=countLOCr;
            /*Print methods
            System.out.println("N= " +fanOut);
            for(String str: methodsLOC.keySet()){
                System.out.println("Method: "+str+"  LOC: "+  methodsLOC.get(str)+" CC:"+ methodsCC.get(str));
            }*/
//            if(!attributes.isEmpty())
//                    System.out.println("Fileeeee not attr: "+file.getName());
        } catch (IOException ex) {
            Logger.getLogger(cParserSemiLatest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * It returns if there is a comment
     * @param word string to analyze
     */
    private boolean isCommentLine(String word){
        if(word.length()>1){
            if(word.charAt(0)=='#'){
                if(!word.contains("include"))
                    return true;
            }
            if (word.substring(0, 2).equals("/*") ){
                String lastTwo = word.substring(word.length() - 2);
                if(lastTwo.equals("*/"))
                    return true;
                else if (word.contains("*//*")){
                    commentBlock=true;
                    return true;
                }
                else if (word.contains("*/")){
                    String[] newLine= word.split("\\*/");
                    this.line=newLine[1];
                    return false;
                }
                commentBlock=true;
                return true;
            }
            return (word.substring(0, 2).equals("//"));
        }
        return false;
    }
    
    /**
     * It returns if comment block stops
     * @param line string to analyze
     */
    private void stopCommentBlock(String line){
        if(line.contains("*/")){
            commentBlock=false;
            if (line.contains("*//*"))
                commentBlock=true;
        }
    }
    
    /**
     * Calculates cohesion by creating file for semi
     */
    @Override
    public void calculateCohesion() {
        cParserSemiLatest ss = new cParserSemiLatest(file,methodsLocDecl);
        ss.parse();
        ParsedFilesController paFC=new ParsedFilesController();
        paFC.doAnalysisLcom(this);
        
        //calculateOpportunities();
    }
    
    @Override
    public void calculateOpportunities(boolean fast){
        //Deletes previous if exist
        File fileDelPrev = new File("./" + file.getName() + "_parsed.txt");
        if(fileDelPrev.exists())
            fileDelPrev.delete();
        
        //calculate opportunities
        HashMap<String, Integer> methodsLocDeclNew=new HashMap<>();
        Iterator it = methodsLocDeclReal.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String[] methodSplit=pair.getKey().toString().split(" ");
            methodsLocDeclNew.put(methodSplit[methodSplit.length-1], Integer.parseInt(pair.getValue().toString()));
        }
        
        cParserSemiLatest ss = new cParserSemiLatest(file,methodsLocDeclNew);
        ss.parse();
        
        try {
            ParsedFilesController paFC=new ParsedFilesController();
            this.opportunities = paFC.calculateOpportunities(fast, file, "c", methodsLocDeclNew);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(fortranFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //print
        int i=0;
        for(String str: this.opportunities){
            System.out.println(i++ +" "+str);
        }
    }
    
    private String replaceWithSpaces(String line) {
		line = line.replaceAll("\\+", " ");
		line = line.replaceAll("\\.", " ");
		line = line.replaceAll("-", " ");
		line = line.replaceAll("\\)", " ");
		line = line.replaceAll("\\*", " ");
		line = line.replaceAll("/", " ");
		line = line.replaceAll("%", " ");
		line = line.replaceAll("=", " ");
		line = line.replaceAll("&", " ");
		line = line.replaceAll("::", " ");
		line = line.replaceAll(":", " ");
		line = line.replaceAll(";", " ");
		line = line.replaceAll("\\[", " ");
		line = line.replaceAll("\\]", " ");
		line = line.replaceAll("\\{", " ");
		line = line.replaceAll("\\}", " ");
		line = line.replaceAll(",", " ");
		line = line.replaceAll(">", " ");
		line = line.replaceAll("<", " ");
		line = line.replaceAll("\"", " ");
		line = line.replaceAll("'", " ");
		line = line.replaceAll("^", " ");
		line = line.replaceAll("!", " ");
		line = line.replaceAll("\\?", " ");
		line = line.replaceAll("\\|\\|", " ");
		line = line.replaceAll("\\|", " ");
		return line;
	}
}
