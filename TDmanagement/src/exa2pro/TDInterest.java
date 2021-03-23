/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class TDInterest {

    Project project;
    double avgNewLines;
    double totalInterest = 0.0;

    public TDInterest(Project project) {
        this.project = project;
        calculateAvgNewLines();
        calculateTotal();
    }

    public double getTotal() {
        return totalInterest;
    }

    private void calculateTotal() {
        HashMap<String, Integer> tdOfFiles = project.getprojectReport().getTdOfEachFile();
        //for each file
        if (!project.getprojectReport().getTdOfEachFile().isEmpty() && project.getCredentials().getProjects().size() > 1) {
            for (CodeFile file : project.getprojectFiles()) {

                //calculate similarity
                HashMap<String, Double> similarityOfFiles = new HashMap<>();
                int sumCC = 0;
                for (String key : file.methodsLOC.keySet()) {
                    sumCC += file.methodsCC.get(key);
                }
                int size = file.methodsLOC.size();
                if (size == 0) {
                    size = 1;
                }
                double avCC = (sumCC * 1.0) / size;
                for (CodeFile file2 : project.getprojectFiles()) {
                    if (!file.file.getAbsolutePath().equals(file2.file.getAbsolutePath())) {
                        int sumCC2 = 0;
                        for (String key2 : file2.methodsLOC.keySet()) {
                            sumCC2 += file2.methodsCC.get(key2);
                        }
                        int size2 = file2.methodsLOC.size();
                        if (size2 == 0) {
                            size2 = 1;
                        }
                        double avCC2 = (sumCC2 * 1.0) / size2;

                        double tempCC = avCC;
                        if (tempCC == 0) {
                            tempCC = 1;
                        }
                        double tempLCOL = file.cohesion;
                        if (tempLCOL == 0) {
                            tempLCOL = 1;
                        }
                        int tempNOP = file.methodsLOC.size();
                        if (tempNOP == 0) {
                            tempNOP = 1;
                        }
                        int tempTD = tdOfFiles.get(file.file.getName());
                        if (tempTD == 0) {
                            tempTD = 1;
                        }

                        double similarity = (Math.abs(file.totalLines - file2.totalLines) * 1.0 / file.totalLines
                                + Math.abs(avCC - avCC2) * 1.0 / tempCC
                                + Math.abs(tdOfFiles.get(file.file.getName()) - tdOfFiles.get(file2.file.getName())) * 1.0 / tempTD) / 3;
                        similarityOfFiles.put(file2.file.getAbsolutePath(), 1 - similarity);
                    }
                }

                //keep top 3
                Map<String, Double> sortedSimilarity = similarityOfFiles.entrySet().stream()
                        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
                        .collect(
                                toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                                        LinkedHashMap::new));
                List<Map.Entry<String, Double>> list
                        = new ArrayList<Map.Entry<String, Double>>(sortedSimilarity.entrySet());

                ArrayList<CodeFile> filesForCompare = new ArrayList<>();
                if(list.size()>3) {
                    for (CodeFile fileTemp : project.getprojectFiles()) {
                        if (list.get(0).getKey().equals(fileTemp.file.getAbsolutePath()) && list.get(0).getValue() >= 0.5) {
                            filesForCompare.add(fileTemp);
                            break;
                        }
                    }
                    for (CodeFile fileTemp : project.getprojectFiles()) {
                        if ((list.get(1).getKey().equals(fileTemp.file.getAbsolutePath())
                                || list.get(2).getKey().equals(fileTemp.file.getAbsolutePath())) && list.get(0).getValue() >= 0.5) {
                            filesForCompare.add(fileTemp);
                        }
                    }
                    if (filesForCompare.isEmpty()) {
                        for (CodeFile fileTemp : project.getprojectFiles()) {
                            if (list.get(0).getKey().equals(fileTemp.file.getAbsolutePath())) {
                                filesForCompare.add(fileTemp);
                                break;
                            }
                        }
                    }
                }else{
                    for (CodeFile fileTemp : project.getprojectFiles()) {
                        for(Map.Entry<String, Double> mapEn: list) {
                            if (mapEn.getKey().equals(fileTemp.file.getAbsolutePath())) {
                                filesForCompare.add(fileTemp);
                                    break;
                            }
                        }
                    }
                }

                //get optimal metrics
                int sumCCopt = 0;
                for (String key : filesForCompare.get(0).methodsLOC.keySet()) {
                    sumCCopt += filesForCompare.get(0).methodsCC.get(key);
                }
                int sizeOpt = filesForCompare.get(0).methodsLOC.size();
                if (sizeOpt == 0) {
                    sizeOpt = 1;
                }
                double avCCopt = sumCCopt * 1.0 / sizeOpt;

                double optimalLOC = filesForCompare.get(0).totalLines;
                double optimalCC = avCCopt;
                int optimalFO = filesForCompare.get(0).fanOut;
                double optimalLCOL = filesForCompare.get(0).cohesion;
                double optimalLCOP = filesForCompare.get(0).lcop;

                for (int i = 1; i < filesForCompare.size(); i++) {
                    if (filesForCompare.get(i).fanOut < optimalFO) {
                        optimalFO = filesForCompare.get(i).fanOut;
                    }
                    if ((filesForCompare.get(i).cohesion != 0 && filesForCompare.get(i).cohesion < optimalLCOL)
                            || optimalLCOL == 0) {
                        optimalLCOL = filesForCompare.get(i).cohesion;
                    }
                    if (filesForCompare.get(i).totalLines != 0 && filesForCompare.get(i).totalLines < optimalLOC) {
                        optimalLOC = filesForCompare.get(i).totalLines;
                    }
                    sumCCopt = 0;
                    for (String key : filesForCompare.get(i).methodsLOC.keySet()) {
                        sumCCopt += filesForCompare.get(i).methodsCC.get(key);
                    }
                    sizeOpt = filesForCompare.get(i).methodsLOC.size();
                    if (sizeOpt == 0) {
                        sizeOpt = 1;
                    }
                    avCCopt = sumCCopt * 1.0 / sizeOpt;
                    if (avCCopt < optimalCC) {
                        optimalCC = avCCopt;
                    }
                    if ((filesForCompare.get(i).lcop != -1 && filesForCompare.get(i).lcop < optimalLCOP)
                            || optimalLCOP == -1) {
                        optimalLCOP = filesForCompare.get(i).lcop;
                    }
                }

                int investFO = 0;
                if (file.fanOut == 0 && optimalFO == 0) {
                    investFO = 1;
                }
                int investCC = 0;
                if (avCC == 0 && optimalCC == 0) {
                    investCC = 1;
                }
                int investLCOP = 0;
                if (file.lcop == 0 && optimalLCOP == 0) {
                    investLCOP = 1;
                }

                //normalize
                if (optimalLOC == 0) {
                    optimalLOC = 1.0;
                }
                if (optimalCC == 0) {
                    optimalCC = 1.0;
                }
                if (optimalFO == 0) {
                    optimalFO = 1;
                }
                if (optimalLCOL == 0) {
                    optimalLCOL = file.cohesion;
                }
                if (optimalLCOP == 0) {
                    optimalLCOP = 1;
                }

                //calculate the interest per LOC
                //get difference optimal to actual
                double sumInterestPerLOC = 0;
                if (file.lcop != -1 && optimalLCOP != -1) {
                    if (investLCOP == 1) {
                        sumInterestPerLOC += (investLCOP - optimalLCOP) * 1.0 / optimalLCOP;
                    } else {
                        sumInterestPerLOC += (file.lcop - optimalLCOP) * 1.0 / optimalLCOP;
                    }
                }

                if (investFO == 1) {
                    sumInterestPerLOC += (investFO - optimalFO) * 1.0 / optimalFO;
                } else {
                    sumInterestPerLOC += (file.fanOut - optimalFO) * 1.0 / optimalFO;
                }

                sumInterestPerLOC += (file.totalLines - optimalLOC) * 1.0 / optimalLOC;

                if (investCC == 1) {
                    sumInterestPerLOC += (investCC - optimalCC) * 1.0 / optimalCC;
                } else {
                    sumInterestPerLOC += (avCC - optimalCC) * 1.0 / optimalCC;
                }

                double avgInterestPerLOC = (sumInterestPerLOC) / 3;
                if (file.lcop != -1 && optimalLCOP != -1) {
                    avgInterestPerLOC = (sumInterestPerLOC) / 4;
                }

                //calculate the interest in AVG LOC
                double interestInAvgLOC = avgInterestPerLOC * avgNewLines;

                //calculate the interest in hours
                double interestInHours = interestInAvgLOC / 25;
                //calculate the interest in dollars
                double interestInEuros = interestInHours * 39.44;

                totalInterest += interestInEuros;
                //System.out.println(interestInEuros);
            }
        }
        
        System.out.println("Total Interest: " + totalInterest);
    }

    /**
     * calculate the avg new lines of code in the history of project
     */
    private void calculateAvgNewLines() {
        int sumNewLines = 0;
        int sumFiles = 0;
        double sumAvg = 0;
        for (Project proj : project.getCredentials().getProjects()) {
            sumNewLines += proj.getprojectReport().getNewLinesOfCode();
            sumFiles += proj.getprojectFiles().size();
            sumAvg += sumNewLines * 1.0 / sumFiles;
        }
        avgNewLines = sumAvg / (project.getCredentials().getProjects().size() - 1);
    }

}
