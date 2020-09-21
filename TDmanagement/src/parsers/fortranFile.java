
package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Nikos
 */
public class fortranFile extends CodeFile{

    ArrayList<String> methodsName =new ArrayList<>();
    ArrayList<Integer> methodsLocStart =new ArrayList<>();
    ArrayList<Integer> methodsLocStop =new ArrayList<>();
    ArrayList<Integer> methodsCCArray =new ArrayList<>();
    
    ArrayList<Integer> arrayIfStart=new ArrayList<>();
    ArrayList<Integer> arrayElseStart=new ArrayList<>();
    ArrayList<Integer> arrayIfEnd=new ArrayList<>();
    ArrayList<Integer> arrayDoStart=new ArrayList<>();
    HashMap<Integer,Integer> arrayDoStartFlag=new HashMap<>();
    ArrayList<Integer> arrayDoEnd=new ArrayList<>();
    
    ArrayList<Integer> arraySelectCasesStart=new ArrayList<>();
    ArrayList<Integer> arraySelectCasesEnd=new ArrayList<>();
    ArrayList<Integer> arrayCasesStart=new ArrayList<>();
    ArrayList<Integer> arrayCasesEnd=new ArrayList<>();
    
    int caseNumber=0;
    boolean firstCase=false;
    
    boolean module=false;
    
    private boolean f90;
            
    public fortranFile(File file, boolean f90) {
        super(file);
        this.f90=f90;
    }

    @Override
    public void parse() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            fanOut=0;
            int countLOC=0;
            boolean checkForPreviousEnd= false;
            int checkForPreviousEndLine= 0;
            ArrayList<String> useFiles=new ArrayList<>();
            
            String doFlag="";
            int doFlagLine=0;
            
            //System.out.println(file.getAbsolutePath()+"  "+file.getName());
            while ((line = br.readLine()) != null) {
                countLOC ++;
                if (!line.trim().equals("")){
                    String[] lineTable= line.trim().replaceAll("\\s\\s+", " ").split(" ");
                    if( !isCommentLine(lineTable[0]) ){
                    
                        // For fan-out
                        //Method invocation
                        if((line.trim().toLowerCase().startsWith("call ") && !(line.toLowerCase().contains("if") || line.toLowerCase().contains("do") )) ||
                                    (line.contains("call ") && !line.matches(".*\\\".*call.*?\\\".*")) ){
                            String[] callMethodSplit= line.toLowerCase().split("call ")[1].split("\\(");
                            int tempN= methodsName.size();
                            String meth;
                            if(tempN==0)
                                meth="";
                            else
                                meth=methodsName.get(methodsName.size()-1);
                            methodInvocations.put(callMethodSplit[0], meth);
                        }
                        
                        // For fan-out
                        //"common blocks" for same variables
                        if(line.trim().toLowerCase().startsWith("common")){
                            int tempN= methodsName.size();
                            String meth;
                            if(tempN==0)
                                meth="";
                            else
                                meth=methodsName.get(methodsName.size()-1);
                            commonBlockDeclaration.put(line.trim().toLowerCase(), meth);
                        }
                        
                        // For fan-out
                        //"use" for module
                        String[] use= lineTable[0].split(",");
                        if( use[0].equalsIgnoreCase("use") ){
                            if (line.contains("::")){       //<use,INTRINSIC :: name>
                                String[] useNameTemp= line.split("::");
                                String[] useName= useNameTemp[1].split(",");
                                if(!useFiles.contains(useName[0])){
                                    useFiles.add(useName[0]);
                                    fanOut ++;
                                }
                            }
                            else{   //<use name>
                                String[] useName= lineTable[1].split(",");
                                if(!useFiles.contains(useName[0])){
                                    useFiles.add(useName[0]);
                                    fanOut ++;
                                }
                            }
                        }
                        
                        //For LCOM real(LCOP)
                        if(module && methodsName.isEmpty()){
                            if(line.contains("::")){
                                String[] declareAtr= line.trim().split("::", 2);
                                if(!declareAtr[0].replaceAll("public|private", "").trim().equals("")){
                                    attributes.add(declareAtr[1].trim().replaceAll("=.*|!.*|\\(.*", "").trim());
                                }
                            }
                        }
                        if(lineTable[0].equalsIgnoreCase("module") || Pattern.matches(".module", lineTable[0])){
                            module= true;
                        }
                        
                        if(!methodsName.isEmpty()){
                            String[] lineVar= replaceWithSpaces(line).split(" ");
                            for(String str: lineVar){
                                if( attributes.contains(str.trim()) ){
                                    attributesInMethods.add(str.trim()+" "+methodsName.get(methodsName.size()-1));
                                }
                            }
                        }
                        
                        
                        // For start count LOC in function/subroutine
                        if( lineTable[0].equalsIgnoreCase("function") || lineTable[0].equalsIgnoreCase("subroutine") ){
                            methodStartsHere(checkForPreviousEnd,checkForPreviousEndLine,lineTable,1,countLOC);
                        }
                        else if( !lineTable[0].equalsIgnoreCase("end") ){
                            int tempCom=10;
                            for(int i=0; i<lineTable.length; i++){
                                if(isCommentStarts(lineTable[i]))
                                    tempCom=i;
                            }
                            if(lineTable.length>2 && tempCom>1){
                                if(lineTable[1].equalsIgnoreCase("function") || lineTable[1].equalsIgnoreCase("subroutine") )
                                    methodStartsHere(checkForPreviousEnd,checkForPreviousEndLine,lineTable,2,countLOC);
                            }
                            if(lineTable.length>3 && tempCom>2){
                                if(lineTable[2].equalsIgnoreCase("function") || lineTable[2].equalsIgnoreCase("subroutine") )
                                    methodStartsHere(checkForPreviousEnd,checkForPreviousEndLine,lineTable,3,countLOC);
                            }
                            if(lineTable.length>4 && tempCom>3){
                                if(lineTable[3].equalsIgnoreCase("function") || lineTable[3].equalsIgnoreCase("subroutine") )
                                    methodStartsHere(checkForPreviousEnd,checkForPreviousEndLine,lineTable,4,countLOC);
                            }
                        }
                        
                        // For stop count LOC in function/subroutine
                        if( lineTable[0].equalsIgnoreCase("end") && lineTable.length>1 ){
                            if( lineTable[1].equalsIgnoreCase("function") || lineTable[1].equalsIgnoreCase("subroutine") ){
                                methodEndsHere(countLOC);
                            }
                            if( arrayDoStart.size()==arrayDoEnd.size() && arrayIfStart.size()==arrayIfEnd.size()){
                                methodEndsHere(countLOC);
                            }
                        }
                        else if( lineTable[0].equalsIgnoreCase("endfunction") || lineTable[0].equalsIgnoreCase("endsubroutine")){
                            methodEndsHere(countLOC);
                        }
                        else if (lineTable[0].equalsIgnoreCase("end") && lineTable.length==1){
                            if(!checkForPreviousEnd){
                                checkForPreviousEnd= true;
                                checkForPreviousEndLine= countLOC;
                            }
                        }
                        if( checkForPreviousEndLine!= countLOC)
                            checkForPreviousEnd= false;
                        
                        
                        // For CC
                        if( lineTable[0].equalsIgnoreCase("if") || lineTable[0].toLowerCase().contains("if(")
                        		|| lineTable[0].equalsIgnoreCase("else") || lineTable[0].equalsIgnoreCase("elseif")
                        		|| lineTable[0].toLowerCase().contains("elseif(")
                                || line.toLowerCase().contains("do while") || lineTable[0].equalsIgnoreCase("do")
                                || lineTable[0].equalsIgnoreCase("case")){
                            if( methodsLocStart.size() == methodsLocStop.size()+1){
                                int cc= methodsCCArray.get(methodsLocStart.size()-1);
                                methodsCCArray.set(methodsLocStart.size()-1, (cc+1) );
                            }
                            else if(methodsLocStart.size() == methodsLocStop.size()){
                                for(int i=methodsLocStop.size()-1; i>=0; i--){
                                    if(methodsLocStop.get(i)==0){
                                        int cc= methodsCCArray.get(i);
                                        methodsCCArray.set(i, (cc+1) );
                                        break;
                                    }
                                }
                            }
                        }
                        
                        //get all if/endif  and else
                        if(lineTable[0].equalsIgnoreCase("if") || lineTable[0].toLowerCase().contains("if(")){
                            arrayIfStart.add(countLOC);
                            if(arrayIfEnd.size()+1<arrayIfStart.size())
                                arrayIfEnd.add(0);
                        }
                        if(lineTable[0].equalsIgnoreCase("endif") || line.toLowerCase().contains("end if") ){
                            if(arrayIfEnd.size()+1==arrayIfStart.size()){
                                arrayIfEnd.add(countLOC);
                            }
                            else if(arrayIfEnd.size()==arrayIfStart.size()){
                                int ifcounter=0;
                                for(int i=0;i<arrayIfEnd.size();i++){
                                    if(arrayIfEnd.get(i)==0)
                                        ifcounter=i;
                                }
                                arrayIfEnd.set(ifcounter, countLOC);
                            }
                        }
                        if(lineTable[0].equalsIgnoreCase("else") || lineTable[0].equalsIgnoreCase("elseif")){
                            arrayElseStart.add(countLOC);
                        }
                        
                        //get all do/enddo
                        if(lineTable[0].equalsIgnoreCase("do") || line.toLowerCase().contains("do while") 
                                    || line.toLowerCase().contains(": do") || line.toLowerCase().contains(":do")){
                            arrayDoStart.add(countLOC);
                            if(lineTable.length>1 && Pattern.matches("[0-9]*", lineTable[1]) ){
                                arrayDoStartFlag.put(Integer.parseInt(lineTable[1]), arrayDoStart.size()-1);
                                doFlag= lineTable[1];
                                doFlagLine= countLOC;
                            }
                            if(arrayDoEnd.size()+1<arrayDoStart.size())
                                arrayDoEnd.add(0);
                        }
                        if(lineTable[0].equalsIgnoreCase("enddo") || line.toLowerCase().contains("end do") ){
                            if(arrayDoEnd.size()+1==arrayDoStart.size()){
                                arrayDoEnd.add(countLOC);
                            }
                            else if(arrayDoEnd.size()==arrayDoStart.size()){
                                int ifcounter=0;
                                for(int i=0;i<arrayDoEnd.size();i++){
                                    if(arrayDoEnd.get(i)==0)
                                        ifcounter=i;
                                }
                                arrayDoEnd.set(ifcounter, countLOC);
                            }
                        }
                        if(line.toLowerCase().contains("continue")){
                            String t= line.replaceAll("\\s\\s+", " ");
                            if(t.charAt(0) == ' ')
                                t= t.substring(1);
                            String[] temp= t.split(" ");
                            if(temp.length>1 && Pattern.matches("[0-9]*", temp[0]) && temp[1].equalsIgnoreCase("continue")){
                                if(arrayDoStartFlag.containsKey(Integer.parseInt(temp[0])) ){
                                    if(arrayDoEnd.size()+1==arrayDoStart.size()){
                                        arrayDoEnd.add(countLOC);
                                    }
                                    else if(arrayDoEnd.size()==arrayDoStart.size()){
                                        arrayDoEnd.set(arrayDoStartFlag.get(Integer.parseInt(temp[0])), countLOC);
                                    }
                                }
                            }
                        }
                        else if(lineTable[0].equals(doFlag) ){
                            if(arrayDoEnd.size()+1==arrayDoStart.size()){
                                arrayDoEnd.add(countLOC);
                            }
                            else if(arrayDoEnd.size()==arrayDoStart.size()){
                                arrayDoEnd.set(arrayDoStartFlag.get(Integer.parseInt(lineTable[0])), countLOC);
                            }
                            
                        }
                        
                        //get all select cases
                        if(line.toLowerCase().contains("select case(") || line.toLowerCase().contains("select case (")){
                            arraySelectCasesStart.add(countLOC);
                            if(arraySelectCasesEnd.size()+1<arraySelectCasesStart.size())
                                arraySelectCasesEnd.add(0);
                            firstCase= true;
                        }
                        if( line.toLowerCase().contains("end select") || line.toLowerCase().contains("endselect") ){
                            if(arraySelectCasesEnd.size()+1==arraySelectCasesStart.size()){
                                arraySelectCasesEnd.add(countLOC);
                            }
                            else if(arraySelectCasesEnd.size()==arraySelectCasesStart.size()){
                                int casescounter=0;
                                for(int i=0;i<arraySelectCasesEnd.size();i++){
                                    if(arraySelectCasesEnd.get(i)==0)
                                        casescounter=i;
                                }
                                arraySelectCasesEnd.set(casescounter, countLOC);
                            }
                        }
                        
                        //get all cases
                        if(!firstCase && caseNumber>0 && (lineTable[0].toLowerCase().contains("case(") || lineTable[0].equalsIgnoreCase("case")
                                || line.toLowerCase().contains("end select"))){
                            if(arrayCasesEnd.size()+1==arrayCasesStart.size()){
                                arrayCasesEnd.add(countLOC);
                            }
                            else if(arrayCasesEnd.size()==arrayCasesStart.size()){
                                int casescounter=0;
                                for(int i=0;i<arrayCasesEnd.size();i++){
                                    if(arrayCasesEnd.get(i)==0)
                                        casescounter=i;
                                }
                                arrayCasesEnd.set(casescounter, countLOC);
                            }
                        }
                        if( lineTable[0].toLowerCase().contains("case(") || lineTable[0].equalsIgnoreCase("case") ){
                            arrayCasesStart.add(countLOC);
                            if(arrayCasesEnd.size()+1<arrayCasesStart.size())
                                arrayCasesEnd.add(0);
                            caseNumber++;
                            if(firstCase)
                                firstCase=false;
                        }
                        
                    }
                }
            }
            if(checkForPreviousEnd)
                methodEndsHere(checkForPreviousEndLine);
            
            br.close();
            
            //for last if condition of one line
            if(arrayIfEnd.size()+1==arrayIfStart.size())
                arrayIfEnd.add(0);
            //for if condition of one line
            for(int i=0;i<arrayIfStart.size();i++){
                if(arrayIfEnd.get(i)==0){
                    arrayIfEnd.set(i, arrayIfStart.get(i));
                }
            }
            
            //System.out.println("N= " +fanOut);
            for(int i=0; i<methodsName.size(); i++){
//                System.out.println("Method: "+methodsName.get(i)+" lines: "
//                        + methodsLocStop.get(i)+"-"+(methodsLocStart.get(i)-1) +" CC:"+ methodsCCArray.get(i));
                methodsLOC.put(methodsName.get(i), (methodsLocStop.get(i)-methodsLocStart.get(i)-1));
                methodsCC.put(methodsName.get(i), methodsCCArray.get(i));
            }
            
//                System.out.println("do");
//                for(int i=0; i<arrayDoStart.size(); i++){
//                    System.out.println("start: "+arrayDoStart.get(i)+ " end: "+ arrayDoEnd.get(i));
//                }
//                System.out.println("if");
//                for(int i=0; i<arrayIfStart.size(); i++){
//                    System.out.println("start: "+arrayIfStart.get(i)+ " end: "+ arrayIfEnd.get(i));
//                }

//                if(module && methodsLocStart.size()>1 && !attributes.isEmpty())
//                    System.out.println("Fileeeee Module: "+file.getName());
//                else
//                    System.out.println("Fileeeee Not Module: "+file.getName());

        } catch (IOException ex) {
            Logger.getLogger(fortranFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     /**
     * Its called to Start the method 
     * @param checkForPreviousEnd to check for previous methods ends
     * @param checkForPreviousEndLine the line of the previous method end
     * @param lineTable to take the name of the method
     * @param countLOC where method starts
     */
    private void methodStartsHere(boolean checkForPreviousEnd,int checkForPreviousEndLine,
                    String[] lineTable,int nameInt, int countLOC){
        if(checkForPreviousEnd){
            methodEndsHere(checkForPreviousEndLine);
        }
        if(methodsLocStop.size() != methodsLocStart.size()){
            methodsLocStop.add(0);
        }
        String[] methodName= lineTable[nameInt].split("\\(");
        methodsName.add(methodName[0]);
        methodsLocStart.add(countLOC);
        methodsCCArray.add(0);
    }
    
    /**
     * Its called to Stop the method
     * @param countLOC the line of code
     */
    private void methodEndsHere( int countLOC ){
        if( methodsLocStart.size() == methodsLocStop.size()+1){
            methodsLocStop.add(countLOC);
        }
        else if(methodsLocStart.size() == methodsLocStop.size()){
            for(int i=methodsLocStop.size()-1; i>=0; i--){
                if(methodsLocStop.get(i)==0){
                    methodsLocStop.set(i, countLOC);
                    break;
                }
            }
        }
    }
    
    /**
     * It returns if there is a comment
     * @param word word to analyze
     */
    private boolean isCommentLine(String word){
        if (word.charAt(0)== '#')
            return true;
        if(f90)
            return word.charAt(0) == '!' || word.equalsIgnoreCase("c") || word.toLowerCase().startsWith("c ") || word.toLowerCase().startsWith("c-");
        else
            return (word.equalsIgnoreCase("c") || word.toLowerCase().startsWith("c ") || word.toLowerCase().startsWith("c-") || word.charAt(0) == '!' || word.charAt(0) == '*');
    }
    private boolean isCommentStarts(String word){
        if(word.length()>0){
        if(f90)
            return word.contains("!");
        else
            return ( word.toLowerCase().charAt(0)=='c' || word.toLowerCase().charAt(word.length()-1)=='c'
                    || word.contains("!") || word.contains("*") );
        }
        else
            return false;
    }

    
    /**
     * Calculates cohesion by creating file for semi
     */
    @Override
    public void calculateCohesion() {
        //Deletes previous if exist
        File fileDelPrev = new File("./" + file.getName() + "_parsed.txt");
        if(fileDelPrev.exists())
            fileDelPrev.delete();
        
        //calculate cohesion
        fortranParserSemi fortranSemi= new fortranParserSemi(file, f90,methodsLocStart, methodsLocStop, methodsName,
                arrayIfStart, arrayElseStart, arrayIfEnd, arrayDoStart, arrayDoEnd,
                arraySelectCasesStart, arraySelectCasesEnd, arrayCasesStart, arrayCasesEnd);
        
        fortranSemi.parse();
        if(!fortranSemi.error){
            ParsedFilesController paFC=new ParsedFilesController();
            this.cohesion = paFC.doAnalysisLcom(file);
        }
        else
            this.cohesion=0;
    }
    
    @Override
    public void calculateOpportunities(boolean fast){
        //Deletes previous if exist
        File fileDelPrev = new File("./" + file.getName() + "_parsed.txt");
        if(fileDelPrev.exists())
            fileDelPrev.delete();
        
        //create _parsed.txt file
        fortranParserSemi fortranSemi= new fortranParserSemi(file, f90,methodsLocStart, methodsLocStop, methodsName,
                arrayIfStart, arrayElseStart, arrayIfEnd, arrayDoStart, arrayDoEnd,
                arraySelectCasesStart, arraySelectCasesEnd, arrayCasesStart, arrayCasesEnd);
        fortranSemi.parse();
        
        //calculate opportunities
        try {
            HashMap<String, Integer> methodsLocDecl=new HashMap<>();
            for(int i=0; i<methodsName.size(); i++){
                methodsLocDecl.put(methodsName.get(i), methodsLocStart.get(i));
                System.out.println(methodsName.get(i));
            }
            
            String lang;
            if(f90)
                lang="f90";
            else
                lang="f77";
            ParsedFilesController paFC=new ParsedFilesController();
            this.opportunities = paFC.calculateOpportunities(fast, file, lang, methodsLocDecl);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(fortranFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        //print
        int i=0;
        for(String str: this.opportunities){
            System.out.println(i++ +" "+str);
        }
    }
    
    /**
     * It deletes all non important
     */
    private String replaceWithSpaces(String line){
        line= line.replaceAll("\\\".*?\\\"", " ");
        line= line.replaceAll("\\+", " ");
        line= line.replaceAll("-", " ");
        line= line.replaceAll("\\(", " ");
        line= line.replaceAll("\\)", " ");
        line= line.replaceAll("\\*", " ");
        line= line.replaceAll("/", " ");
        line= line.replaceAll("%", " ");
        line= line.replaceAll("=", " ");
        line= line.replaceAll("&", " ");
        line= line.replaceAll("#", " ");
        line= line.replaceAll(":", " ");
        line= line.replaceAll(";", " ");
        line= line.replaceAll("\\[", " ");
        line= line.replaceAll("\\]", " ");
        line= line.replaceAll("\\{", " ");
        line= line.replaceAll("\\}", " ");
        line= line.replaceAll(",", " ");
        line= line.replaceAll(">", " ");
        line= line.replaceAll("<", " ");
        line= line.replaceAll("\"", " ");
        line= line.replaceAll("'", " ");
        line= line.replaceAll("\\|\\|", " ");
        line= line.replaceAll("\\?", " ");
        
        line= line.replaceAll("use ", " ");
        line= line.replaceAll("implicit", " ");
        line= line.replaceAll("none", " ");
        line= line.replaceAll("only", " ");
        
        return line;
    }
    
    /**
     * It returns if a string is a number
     * @param str string to check
     */
    public boolean isNumeric(final String str) {
        return (str.chars().allMatch(Character::isDigit) ||
                Pattern.matches("([0-9]*)\\.([0-9,D]*)", str) );
    }
}
