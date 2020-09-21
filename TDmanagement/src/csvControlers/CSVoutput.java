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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class CSVoutput {

    List<String[]> dataLines;

    public CSVoutput(ArrayList<CodeFile> files, String projectName) {
        dataLines = new ArrayList<>();
        dataLines.add(new String[]{"Artifacts","Fan-Out","LCOM2"});
        for(CodeFile cf: files){
            dataLines.add(new String[]{cf.file.getAbsolutePath(),""+cf.fanOut,""+cf.cohesion});
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
