/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvControlers;

import exa2pro.Exa2Pro;
import exa2pro.Report;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class CSVReadANDWrite {

    private String projectName;
    private ArrayList<CodeFile> files;
    public ArrayList<Artifacts> artifacts= new ArrayList<>();
    
    List<String[]> dataLines;

    public CSVReadANDWrite(ArrayList<CodeFile> files,String projectName) {
        this.projectName = projectName;
        this.files=files;
        File tempFile= new File(projectName+".csv");
        if(tempFile.exists())
            read();
        else
            getFromSonarQube();
        write();
    }

    //gets all the metrics from SonarQube for each file
    private void getFromSonarQube() {
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/measures/component_tree?pageSize=500&component="
                            +projectName+"&metricKeys=functions,complexity,ncloc,comment_lines_density,"
                            + "code_smells,sqale_index");
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
                JSONArray jsonarr_1 = (JSONArray) jobj.get("components");
                for(int i=0;i<jsonarr_1.size();i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    JSONArray jsonarr_2 = (JSONArray) jsonobj_1.get("measures");
                    
                    if(jsonobj_1.get("key").toString().contains(".")){
                        String[] data= new String[7];
                        data[0] = jsonobj_1.get("key").toString();
                        for(int k=0;k<jsonarr_2.size();k++){
                            JSONObject jsonobj_2 = (JSONObject)jsonarr_2.get(k);
                            switch(jsonobj_2.get("metric").toString()){
                                case "functions":
                                    data[1]=jsonobj_2.get("value").toString();
                                    break;
                                case "complexity":
                                    data[2]=jsonobj_2.get("value").toString();
                                    break;
                                case "ncloc":
                                    data[3]=jsonobj_2.get("value").toString();
                                    break;
                                case "comment_lines_density":
                                    data[4]=jsonobj_2.get("value").toString();
                                    break;
                                case "code_smells":
                                    data[5]=jsonobj_2.get("value").toString();
                                    break;
                                case "sqale_index":
                                    data[6]=jsonobj_2.get("value").toString();
                                    break;
                            }
                        }
                        for(int k=0; k<data.length; k++){
                            if(data[k]==null)
                                data[k]="0";
                        }
                        Artifacts art= new Artifacts(data);
                        artifacts.add(art);
                    }
                    
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void read() {
        BufferedReader csvReader;
        try {
            csvReader = new BufferedReader(new FileReader(projectName+".csv"));
            String row;
            int line=1;
            while ((row = csvReader.readLine()) != null) {
                if(line>1){
                    String[] data = row.split(",");
                    Artifacts art= new Artifacts(data);
                    artifacts.add(art);
                }
                line++;
            }
                csvReader.close();
        } catch (IOException ex) {
            Logger.getLogger(CSVReadANDWrite.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    
    private void write(){
        dataLines = new ArrayList<>();
        dataLines.add(new String[]{"Artifacts","Functions","Complexity","Ncloc","Comments Density (%)",
                "Code Smells","Technical Debt (min)","Fan-Out","LCOM2"});
        ArrayList<String> codeFilesAdded= new ArrayList<>();
        for(Artifacts ar: artifacts){
            boolean found=false;
            for(CodeFile cf: files){
                if(cf.file.getAbsolutePath().endsWith(ar.getPath().split(":")[1].replace("/", "\\")) ||
                        cf.file.getAbsolutePath().endsWith(ar.getPath().split(":")[1].replace("/", "\\")
                                .replace("temp_fortran_", "").replaceFirst("\\_", "\\\\"))){
                    dataLines.add(new String[]{ar.getPath(),""+ar.getFunctions(),""+ar.getComplexity(),
                        ""+ar.getNcloc(),""+ar.getCommentsDensity(),""+ar.getCodeSmells(),
                        ""+ar.getTechnicalDebt(),""+cf.fanOut,""+cf.cohesion});
                    found=true;
                    codeFilesAdded.add(cf.file.getAbsolutePath());
                    break;
                }
            }
            if(!found){
                dataLines.add(new String[]{ar.getPath(),""+ar.getFunctions(),""+ar.getComplexity(),
                        ""+ar.getNcloc(),""+ar.getCommentsDensity(),""+ar.getCodeSmells(),
                        ""+ar.getTechnicalDebt() });
            }
        }
        
        //for fortran files that are temp
        for(CodeFile cf: files){
            if(!codeFilesAdded.contains(cf.file.getAbsolutePath()))
                dataLines.add(new String[]{cf.file.getAbsolutePath(),"","","","","","",
                    ""+cf.fanOut,""+cf.cohesion});
        }
        
        givenDataArray_whenConvertToCSV_thenOutputCreated(projectName);
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(String projectName) {
        File csvOutputFile = new File(projectName+"_output.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVoutput.class.getName()).log(Level.SEVERE, null, ex);
        }
        //assertTrue(csvOutputFile.exists());
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
