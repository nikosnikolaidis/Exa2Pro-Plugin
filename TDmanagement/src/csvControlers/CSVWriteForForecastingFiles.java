/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvControlers;

import exa2pro.Exa2Pro;
import exa2pro.Project;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
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
public class CSVWriteForForecastingFiles {
    public ArrayList<ArtifactsForecastingFiles> artifacts = new ArrayList<>();
    List<String[]> dataLines;
    private Project project;

    public CSVWriteForForecastingFiles(Project project) {
        this.project = project;
        for (Project p: project.getCredentials().getProjects()) {
            for(String s: p.getprojectReport().getTdOfEachFile().keySet()){
                String[] data = new String[11];
                data[0]= s;
                data[1]= p.getProjectVersion();
                data[2]= s;
                data[3]= ""+p.getprojectReport().getCodeSmellsOfEachFile().get(s);
                //ncloc
                data[4]="";
                data[5]="";
                for(CodeFile cf: project.getprojectFiles()){
                    if(cf.file.getName().equals(s.replace("file:", ""))){
                        data[4]= ""+cf.totalLines;
                        int totalCC= 0;
                        totalCC = cf.methodsCC.keySet().stream().map((meth) -> cf.methodsCC.get(meth)).reduce(totalCC, Integer::sum);
                        data[5]= ""+totalCC;
                    }
                }
                if(data[4].equals(""))
                    System.out.println("ncloc empty");
                if(data[5].equals(""))
                    System.out.println("cc empty");
                //complex
                data[6]= ""+p.getprojectReport().getDuplicatedBblocksOfEachFile().get(s);
                data[7]= ""+p.getprojectReport().getTdOfEachFile().get(s);
                data[8]= ""+p.getprojectReport().getReliabilityEffortOfEachFile().get(s);
                data[9]= ""+p.getprojectReport().getSecurityEffortOfEachFile().get(s);
                int total_principal= p.getprojectReport().getReliabilityEffortOfEachFile().get(s) +
                            p.getprojectReport().getSecurityEffortOfEachFile().get(s) +
                            p.getprojectReport().getTdOfEachFile().get(s);
                data[10]= ""+total_principal;
                
                ArtifactsForecastingFiles art = new ArtifactsForecastingFiles(data);
                artifacts.add(art);
            }
        }
    
        File csvOutputFile = new File(Exa2Pro.TDForecasterPath + "/data/"
                + project.getCredentials().getProjectName() + "_class.csv");
        try {
            Files.deleteIfExists(csvOutputFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        write();
    }

    private void write() {
        dataLines = new ArrayList<>();
        dataLines.add(new String[]{"class_name", "project_version", "class_id", "code_smells", "ncloc", "complexity", "duplicated_blocks",
                "sqale_index", "reliability_remediation_effort", "security_remediation_effort", "total_principal"});

        for (ArtifactsForecastingFiles ar : artifacts) {
            dataLines.add(new String[]{""+ar.getClass_name(), ""+ar.getProject_version(), ""+ar.getClass_id(), ""+ar.getCode_smells(),
                ""+ar.getNcloc(), ""+ar.getComplexity(), ""+ar.getDuplicated_blocks(), ""+ar.getSqale_index(), ""+ar.getReliability_remediation_effort(),
                ""+ar.getSecurity_remediation_effort(), ""+ar.getTotal_principal()
            });
        }

        givenDataArray_whenConvertToCSV_thenOutputCreated(project.getCredentials().getProjectName());
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(String fileName) {
        File csvOutputFile = new File(Exa2Pro.TDForecasterPath + "/data/" + fileName + "_class.csv");
        try {
            if (csvOutputFile.createNewFile()) {
                try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                    dataLines.stream().map(this::convertToCSV)
                            .forEach(pw::println);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CSVWriteForClustering.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data).map(this::escapeSpecialCharacters).collect(Collectors.joining(";"));
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
