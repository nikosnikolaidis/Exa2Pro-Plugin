
package parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Nikos
 */
public class fortranParserSemi {
    File file;
    String line;
    boolean f90;
    int countLOC=0;
    boolean error=false;
    boolean continueDeclaringPar=false;
    boolean continueDeclaringTheSameParameter=false;
    ArrayList<Integer> methodsLocStart;
    ArrayList<Integer> methodsLocStop;
    ArrayList<String> methodsName;
    private BufferedWriter writer;
    
    ArrayList<Integer> arrayIfStart=new ArrayList<>();
    ArrayList<Integer> arrayElseStart=new ArrayList<>();
    ArrayList<Integer> arrayIfEnd=new ArrayList<>();
    ArrayList<Integer> arrayDoStart=new ArrayList<>();
    ArrayList<Integer> arrayDoEnd=new ArrayList<>();
    
    ArrayList<Integer> arraySelectCasesStart=new ArrayList<>();
    ArrayList<Integer> arraySelectCasesEnd=new ArrayList<>();
    ArrayList<Integer> arrayCasesStart=new ArrayList<>();
    ArrayList<Integer> arrayCasesEnd=new ArrayList<>();
    
    boolean inIf=false;
    
    int countIf=0;
    int countDo=0;
    int countSelectCases=0;
    int countCases=0;

    public fortranParserSemi(File file, boolean f90, ArrayList<Integer> methodsLocStart, ArrayList<Integer> methodsLocStop,
            ArrayList<String> methodsName, ArrayList<Integer> arrayIfStart, ArrayList<Integer> arrayElseStart,
            ArrayList<Integer> arrayIfEnd, ArrayList<Integer> arrayDoStart, ArrayList<Integer> arrayDoEnd,
            ArrayList<Integer> arraySelectStart, ArrayList<Integer> arraySelectEnd, ArrayList<Integer> arrayCaseStart,
            ArrayList<Integer> arrayCaseEnd) {
        this.file=file;
        this.f90=f90;
        this.methodsLocStop= methodsLocStop;
        this.methodsLocStart= methodsLocStart;
        this.methodsName= methodsName;
        
        this.arrayIfStart= arrayIfStart;
        this.arrayElseStart= arrayElseStart;
        this.arrayIfEnd= arrayIfEnd;
        this.arrayDoStart= arrayDoStart;
        this.arrayDoEnd= arrayDoEnd;
        
        this.arraySelectCasesStart= arraySelectStart;
        this.arraySelectCasesEnd= arraySelectEnd;
        this.arrayCasesStart= arrayCaseStart;
        this.arrayCasesEnd= arrayCaseEnd;
    }
    
    public void parse(){
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            boolean methodDel= false;
            boolean first=false;
            ArrayList<String> methodParam=new ArrayList<>();
            String methodToPrint="";
            
            writer = new BufferedWriter(new FileWriter(file.getName()+"_parsed.txt", true));
            while ((line = br.readLine()) != null) {
                countLOC ++;
                if (!line.trim().isEmpty()){
                    if( !isCommentLine(line.trim()) ){
                        if(line.contains("!")){
                            String[] newLine= line.split("!");
                            this.line=newLine[0];
                        }
                        
                        // Check if is line of Method start
                        for(int i=0;i<methodsLocStart.size(); i++){
                            if(methodsLocStart.get(i)==(countLOC)){
                                if(f90){
                                    methodToPrint="Method:"+methodsName.get(i)+"(";
                                    String[] newLine= line.split(methodsName.get(i),2);
                                    if(newLine[1].trim().equals(""))
                                        line="";
                                    else
                                        line= newLine[1].trim().substring(1).trim();
                                }
                                else{
                                    methodToPrint="Method:"+methodsName.get(i)+"(";
                                    String[] newLine= line.split(methodsName.get(i),2);
                                    line= newLine[1].trim();
                                }
                                first=true;
                                methodDel=true;
                                break;
                            } 
                        }
                        
                        // Method Decleration
                        if(methodDel){
                            if(f90){
                                 if(!line.equals("") && line.trim().charAt(line.trim().length()-1)=='&') {
                                    addMethodParam(methodParam);
                                }
                                else{
                                    addMethodParam(methodParam);
                                    
                                    //System.out.println(methodToPrint.substring(0, methodToPrint.length()-1)+"();");
                                    writer.append(methodToPrint.substring(0, methodToPrint.length()-1)+"();"
                                            +System.lineSeparator());
                                    //ToDo
                                    // find type first
                                    //
                                    //for(String str: methodParam){
                                    //    System.out.println("parameter#"+str+";");
                                    //}
                                    methodToPrint="";
                                    methodParam.clear();
                                    methodDel=false;
                                }
                            }
                            else{
                                if(line.contains("&") || first){
                                    addMethodParam(methodParam);
                                    first=false;
                                }
                                else {
                                    String tempParamType="";
                                    for(String str: methodParam){
                                        tempParamType+="int,";
                                    }
                                    if(methodParam.isEmpty())
                                        tempParamType="";
                                    else
                                        tempParamType = tempParamType.substring(0, tempParamType.length() - 1);
                                    //System.out.println(methodToPrint.substring(0, methodToPrint.length()-1)+"();");
                                    writer.append(methodToPrint.substring(0, methodToPrint.length()-1)+"("+
                                            tempParamType+");"
                                            +System.lineSeparator());
                                    for(String str: methodParam){
                                        //ToDo
                                        // find type first
                                        //
                                        //System.out.println("parameter#"+str+";");
                                        writer.append("parameter#"+str+";"+System.lineSeparator());
                                    }
                                    methodToPrint="";
                                    methodParam.clear();
                                    methodDel=false;
                                    usageForLine();
                                }
                            }
                        }
                        
                        //For everything use "usage:"
                        else {
                            usageForLine();
                        }
                        
                    }
                }
            }
            br.close();
            writer.close();
            
            
            //
            // Get refactoring new Methods with
            //   block of if/for/while
            //
            // Refactorings new Methods
            /*ArrayList<Integer> posibleNewMethodsStart=new ArrayList<>();
            ArrayList<Integer> posibleNewMethodsEnd=new ArrayList<>();
            calculateRefactoringsNewMethods(posibleNewMethodsStart, posibleNewMethodsEnd);
            for(int i=0; i<posibleNewMethodsStart.size(); i++){
                System.out.println(posibleNewMethodsStart.get(i)+ " - " +posibleNewMethodsEnd.get(i)
                        + "  = "+ (posibleNewMethodsEnd.get(i)-posibleNewMethodsStart.get(i)) );
            }
            */
            
        } catch (IOException ex) {
            error=true;
            Logger.getLogger(cParserSemi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * It calculates the new methods that can be extracted
     * @param posibleNewMethodsStart
     * @param posibleNewMethodsEnd 
     */
    private void calculateRefactoringsNewMethods(ArrayList<Integer> posibleNewMethodsStart, ArrayList<Integer> posibleNewMethodsEnd) {
        int line=0;
        boolean smallCondition=false;
        String condition="";
        while(line<countLOC){
            int start = arrayIfStart.indexOf(line);
            if(start>=0){
                if(arrayIfEnd.get(start)-line > exa2pro.PieChart.calculateThresholds().get("LOC")){
                    posibleNewMethodsStart.add(line);
                    posibleNewMethodsEnd.add(arrayIfEnd.get(start));
                    //posibleNewMethods.put(line, arrayIfEnd.get(start));
                }
                else{
                    condition="IF";
                    smallCondition= true;
                }
            }
            else if(arrayDoStart.indexOf(line)>=0){
                start=arrayDoStart.indexOf(line);
                if(arrayDoEnd.get(start)-line > 20){
                    posibleNewMethodsStart.add(line);
                    posibleNewMethodsEnd.add(arrayDoEnd.get(start));
                    //posibleNewMethods.put(line, arrayDoEnd.get(start));
                }
                else{
                    condition="DO";
                    smallCondition= true;
                }
            }
            else if(arraySelectCasesStart.indexOf(line)>=0){
                start=arraySelectCasesStart.indexOf(line);
                if(arraySelectCasesEnd.get(start)-line > 20){
                    posibleNewMethodsStart.add(line);
                    posibleNewMethodsEnd.add(arraySelectCasesEnd.get(start));
                    //posibleNewMethods.put(line, arraySelectCasesEnd.get(start));
                }
                else{
                    condition="SELECT";
                    smallCondition= true;
                }
            }
            else if(arrayCasesStart.indexOf(line)>=0){
                start=arrayCasesStart.indexOf(line);
                if(arrayCasesEnd.get(start)-line > 20){
                    posibleNewMethodsStart.add(line);
                    posibleNewMethodsEnd.add(arrayCasesEnd.get(start));
                    //posibleNewMethods.put(line, arrayCasesEnd.get(start));
                }
                else{
                    condition="CASE";
                    smallCondition= true;
                }
            }
            
            if(smallCondition){
                switch(condition){
                    case "IF":
                        line= arrayIfEnd.get(start)+1;
                        break;
                    case "DO":
                        line= arrayDoEnd.get(start)+1;
                        break;
                    case "SELECT":
                        line= arraySelectCasesEnd.get(start)+1;
                        break;
                    case "CASE":
                        line= arrayCasesEnd.get(start)+1;
                        break;
                }
                smallCondition= false;
                condition="";
            }
            else
                line++;
        }
    }

     /**
     * It adds the parameters of the method
     * @param methodParam the array of parameters
     */
    private void addMethodParam(ArrayList<String> methodParam) {
        line=line.replace("&", "").trim();
        line=line.replace(")", "").trim();
        line=line.replace("(", "").trim();
        String[] var= line.split(",");
        for(String str: var){
            if(!str.trim().equals(""))
                methodParam.add(str.trim());
        }
    }
    
    /**
     * It writes the usages of parameters
     */
    private void usageForLine() throws IOException{
        if(continueDeclaringTheSameParameter){
            if(line.contains(")"))
                continueDeclaringTheSameParameter=false;
        }
        else if(line.trim().toLowerCase().startsWith("call ") && !(line.toLowerCase().contains("if") || line.toLowerCase().contains("do") )){
            if(line.contains("(")){
                String[] callMethodSplit= line.toLowerCase().split("call ")[1].split("\\(");
                writer.append("Invocation#"+callMethodSplit[0]+"#"+countLOC+";"+System.lineSeparator());
                String[] callParam= callMethodSplit[1].replace(")", "").replace("&", "").replace(";", "")
                        .replaceAll("\\+|\\-|\\*|\\%|\\/", ",").split(",");
                for(String param: callParam){
                    param=param.trim();
                    if(!param.equals("") && !isNumeric(param)){
                        if(!param.substring(0, 1).equals("\"") && !param.replace("\"", "").trim().equals(""))
                            writer.append("Usage#"+param.replace("\"", "").trim()+"#"+countLOC+";"+System.lineSeparator());
                    }
                }
            }
            else{
                writer.append("Invocation#"+line.split("call ")[1]+"#"+countLOC+";"+System.lineSeparator());
            }
        }
        else if(line.contains("::") || continueDeclaringPar){
            String temp= line;
            if(line.contains("::"))
                temp= line.split("::")[1];
            
            String[] param = temp.replaceFirst("\\([0-9,:]*?\\)", "").split(",");
            
            for(int i=0; i<param.length; i++){
                if(param[i].contains("=")){
                    writer.append("Declaration#"+param[i].split("=")[0].trim()+"#"+countLOC+";"+System.lineSeparator());
                    continueDeclaringTheSameParameter = param[i].split("=")[1].contains("(") && param[i].split("=")[1].contains("&");
                }
                else if(!param[i].equals("&"))
                    writer.append("Declaration#"+param[i].trim()+"#"+countLOC+";"+System.lineSeparator());
            }
            continueDeclaringPar = line.contains("&");
            
        }
        else{
            if(line.contains("call ") && !line.matches(".*\\\".*call.*?\\\".*")){
                String[] callMethodSplit= line.split("call ")[1].split("\\(");
                writer.append("Invocation#"+callMethodSplit[0]+"#"+countLOC+";"+System.lineSeparator());
                if(callMethodSplit.length>1){
                    String[] callParam= callMethodSplit[1].replace(")", "").replace("&", "")
                            .replaceAll("\\+|\\-|\\*|\\%|\\/", ",").split(",");
                    for(String param: callParam){
                        param=param.trim();
                        if(!param.equals("") && !isNumeric(param)){
                            if(!param.substring(0, 1).equals("\"") && !param.replace("\"", "").trim().equals(""))
                                writer.append("Usage#"+param.replace("\"", "").trim()+"#"+countLOC+";"+System.lineSeparator());
                        }
                    }
                }
            }
            replaceWithSpaces();
            String[] lineTable= line.trim().replace("(", " ").split(" ");
            
            //conects in one string to get both types
            if(lineTable[0].equals("end") && lineTable.length>1){
                lineTable[0]="";
                lineTable[1]="end"+lineTable[1];
            }
            if(lineTable[0].equals("select")){
                lineTable[0]="";
                lineTable[1]="select"+lineTable[1];
            }
            
            //for each word
            for(String str: lineTable){
                if(!str.equals("")){
                    if(!isNumeric(str)){
                        if(Pattern.matches("if|endif|do|enddo|selectcase|endselect|case",str.toLowerCase())){
                            //if starts
                            if(arrayIfStart.contains(countLOC)){
                                writer.append("BEGIN_IF#"+countLOC+";"+System.lineSeparator());
                                int temp=countLOC;
                                while(temp!=arrayIfEnd.get(countIf)){
                                    if(arrayElseStart.contains(temp)){
                                        writer.append("BEGIN_ELSE#"+temp+";"+System.lineSeparator());
                                    }
                                    temp++;
                                }
                                writer.append("END_IF#"+arrayIfEnd.get(countIf)+";"+System.lineSeparator());
                                countIf++;
                                
                                if(line.toLowerCase().contains("then"))
                                    inIf=true;
                            }
                            //do starts
                            else if(arrayDoStart.contains(countLOC)){
                                writer.append("BEGIN_WHILE#"+countLOC+";"+System.lineSeparator());
                                writer.append("END_WHILE#"+arrayDoEnd.get(countDo)+";"+System.lineSeparator());
                                countDo++;
                            }
                            //select case (switch) starts
                            else if(arraySelectCasesStart.contains(countLOC)){
                                writer.append("BEGIN_SWITCH#"+countLOC+";"+System.lineSeparator());
                                writer.append("END_SWITCH#"+arraySelectCasesEnd.get(countSelectCases)+";"+System.lineSeparator());
                                countSelectCases++;
                            }
                            //case starts
                            else if(arrayCasesStart.contains(countLOC)){
                                writer.append("BEGIN_CASE#"+countLOC+";"+System.lineSeparator());
                                writer.append("END_CASE#"+arrayCasesEnd.get(countCases)+";"+System.lineSeparator());
                                countCases++;
                            }
                        }
                        //to get the variables in the if statement
                        else if(inIf){
                            if(str.equalsIgnoreCase("then")){
                                inIf=false;
                            }
                            else
                                writer.append("Usage-IF#"+str+"#"+countLOC+";"+System.lineSeparator());
                        }
                        //the rest usage
                        else{
                            if(!line.toLowerCase().contains("end subroutine") || !line.toLowerCase().contains("endsubroutine"))
                                writer.append("Usage#"+str+"#"+countLOC+";"+System.lineSeparator());
                        }
                    }
                }
            }
        
        }
    }

    /**
     * It deletes all non important
     */
    private void replaceWithSpaces(){
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
    }
    
    /**
     * It returns if there is a comment
     * @param word word to analyze
     */
    private boolean isCommentLine(String word){
        if(word.charAt(0)=='#')
            return true;
        if(f90)
            return word.charAt(0) == '!';
        else
            return (word.charAt(0) == 'C' || word.charAt(0) == 'c' || word.charAt(0) == '!' || word.charAt(0) == '*');
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
