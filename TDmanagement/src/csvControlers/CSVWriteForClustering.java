/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvControlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class CSVWriteForClustering {
    public ArrayList<ArtifactsClustering> artifacts= new ArrayList<>();
    private CodeFile file;
    
    List<String[]> dataLines;
    
    public CSVWriteForClustering(CodeFile file){
        this.file=file;
        
        String[] data= new String[3];
        for(String method: this.file.methodsLOC.keySet()){
            data[0]=method;
            data[1]="";
            data[2]="";
            for(String attInMeth: this.file.attributesInMethods){
                String[] table= attInMeth.split(" ",2);
                if(table[1].equalsIgnoreCase(method)){
                    data[1]= data[1] + table[0]+";";
                }
            }
            if(!data[1].equals("")){
                data[1]= data[1].substring(0, data[1].length() - 1);
            }
            for (String entry : this.file.methodInvocations) {
                String[] table= entry.split(";",2);
                if(table[1].equalsIgnoreCase(method)){
                    data[2]= data[2] + table[0]+";";
                }
            }
            if(!data[2].equals("")){
                data[2]= data[2].substring(0, data[2].length() - 1);
            }
            ArtifactsClustering art= new ArtifactsClustering(data);
            artifacts.add(art);
        }
        
        write();
    }

    private void write(){
        dataLines = new ArrayList<>();
        dataLines.add(new String[]{"Method","Attributes","Invocations"});
        
        for(ArtifactsClustering ar: artifacts){
            dataLines.add(new String[]{ar.getMethod(),""+ar.getAttributes(),""+ar.getInvocations() });
        }
        
        String fileName= this.file.file.getParentFile().getParentFile().getName()
                +"."+ this.file.file.getParentFile().getName()+"."+ this.file.file.getName();
        givenDataArray_whenConvertToCSV_thenOutputCreated(fileName);
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(String fileName) {
        File csvOutputFile = new File(System.getProperty("user.dir")+"/clustering_"+ fileName+".csv");
        try {
            if (csvOutputFile.createNewFile()) {
                try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                    dataLines.stream()
                            .map(this::convertToCSV)
                            .forEach(pw::println);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CSVWriteForClustering.class.getName()).log(Level.SEVERE, null, ex);
        }
        //assertTrue(csvOutputFile.exists());
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
