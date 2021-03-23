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

/**
 *
 * @author Nikos
 */
public class CSVWriteForForecastingSystem {

    public ArrayList<ArtifactsForecastingSystem> artifacts = new ArrayList<>();
    List<String[]> dataLines;
    private Project project;

    public CSVWriteForForecastingSystem(Project project) {
        this.project = project;
        for (Project p : project.getCredentials().getProjects()) {
            int[] data = new int[7];
            data[0] = p.getprojectReport().getTotalCodeSmells();
            data[1] = p.getprojectReport().getTotalLinesOfCode();
            data[2] = p.getprojectReport().getTotalComplexity();
            data[3] = p.getprojectReport().getDuplicated_blocks();
            data[4] = (int) Math.round(p.getprojectReport().getTotalDebt_Index());
            data[5] = p.getprojectReport().getReliability_remediation_effort();
            data[6] = p.getprojectReport().getSecurity_remediation_effort();
            ArtifactsForecastingSystem art = new ArtifactsForecastingSystem(data);
            artifacts.add(art);
        }

        File csvOutputFile = new File(Exa2Pro.TDForecasterPath + "/data/"
                + project.getCredentials().getProjectName() + ".csv");
        try {
            Files.deleteIfExists(csvOutputFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        write();
    }

    private void write() {
        dataLines = new ArrayList<>();
        dataLines.add(new String[]{"code_smells", "ncloc", "complexity", "duplicated_blocks", "sqale_index",
            "reliability_remediation_effort", "security_remediation_effort"});

        for (ArtifactsForecastingSystem ar : artifacts) {
            dataLines.add(new String[]{"" + ar.getCode_smells(), "" + ar.getNcloc(), "" + ar.getComplexity(), "" + ar.getDuplicated_blocks(),
                "" + ar.getSqale_index(), "" + ar.getReliability_remediation_effort(), "" + ar.getSecurity_remediation_effort()});
        }

        givenDataArray_whenConvertToCSV_thenOutputCreated(project.getCredentials().getProjectName());
    }

    public void givenDataArray_whenConvertToCSV_thenOutputCreated(String fileName) {
        File csvOutputFile = new File(Exa2Pro.TDForecasterPath + "/data/" + fileName + ".csv");
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
