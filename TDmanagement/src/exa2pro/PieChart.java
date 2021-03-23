package exa2pro;

import exa2pro.Exa2Pro;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import parsers.CodeFile;


public class PieChart extends ApplicationFrame {

    Project project;
    String metric;
    Double threshold;
    public ChartPanel chartPanel;

    public PieChart(Project p, String applicationTitle, String metric, String chartTitle, Double threshold) {
        super(applicationTitle);
        this.project = p;
        this.metric= metric;
        this.threshold= threshold;
        JFreeChart pieChart = ChartFactory.createPieChart(
                metric+chartTitle,
                createDataset());

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Healthy", Color.GREEN);
        plot.setSectionPaint("Problematic", Color.RED);
        
        chartPanel = new ChartPanel( pieChart );
        setContentPane( chartPanel );
    }

    private DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        int n=0;
        int p=0;
        int total=0;
        
            if (metric.equals("CBF")){
                System.out.println("FO threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    if(cf.fanOut>threshold)
                        n++;
                    else
                        p++;
                    total++;
                }
            }
            else if (metric.equals("LOC")){
                System.out.println("LOC threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    if(cf.totalLines>threshold)
                        n++;
                    else
                        p++;
                    total++;
                }
            }
            else if (metric.equals("LCOP")){
                System.out.println("LCOP threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    if(cf.lcop!=-1){
                        if(cf.lcop>threshold)
                            n++;
                        else
                            p++;
                        total++;
                    }
                }
            }
            else if (metric.equals("CC")){
                System.out.println("CC threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    for (HashMap.Entry pair : cf.methodsCC.entrySet()) {
                        if((Integer)pair.getValue()>threshold)
                            n++;
                        else
                            p++;
                        total++;
                    }
                }
            }
            else if (metric.equals("LCOL")){
                System.out.println("LCOL threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    for (HashMap.Entry pair : cf.methodsLCOL.entrySet()) {
                        if((Double)pair.getValue()>threshold)
                            n++;
                        else
                            p++;
                        total++;
                    }
                }
            }
            
        
        dataset.setValue("Healthy", (p*1.0)/total );
        dataset.setValue("Problematic", (n*1.0)/total );
        return dataset;
    }
    
    
    /*
    * Calculate Thresholds
    */
    public static HashMap<String, Double> calculateThresholds(){
        ArrayList<Integer> FO=new ArrayList<>();
        //ArrayList<Double> Co=new ArrayList<>();
        ArrayList<Integer> CC=new ArrayList<>();
        ArrayList<Double> procLCOL=new ArrayList<>();
//        ArrayList<Integer> LOC=new ArrayList<>();
        ArrayList<Integer> FileLOC=new ArrayList<>();
        ArrayList<Integer> LCOP=new ArrayList<>();
        for(ProjectCredentials pC: Exa2Pro.projecCredentialstList){
            Project p= pC.getProjects().get(pC.getProjects().size()-1);
            for(CodeFile cf: p.getprojectFiles()){
                FO.add(cf.fanOut);
                //Co.add(cf.cohesion);
                if(cf.lcop!=-1)
                    LCOP.add(cf.lcop);
                for(String key : cf.methodsLOC.keySet()){
                    CC.add(cf.methodsCC.get(key));
                    //LOC.add(cf.methodsLOC.get(key));
                    if(cf.methodsLCOL.containsKey(key.split(" ")[key.split(" ").length-1]))
                    	procLCOL.add(cf.methodsLCOL.get(key.split(" ")[key.split(" ").length-1]));
                }
                FileLOC.add(cf.totalLines);
            }
        }
        HashMap<String, Double> temp=new HashMap<>();
//        temp.put("FanOut", (sumFO*1.0)/totalfiles);
//        temp.put("LCOL", (sumCo*1.0)/totalfiles);
//        temp.put("CC", (sumCC*1.0)/totalmethods);
//        temp.put("LOC", (sumLOC*1.0)/totalmethods);
        
        Collections.sort(FO);
        //Collections.sort(Co);
        Collections.sort(CC);
        //Collections.sort(LOC);
        Collections.sort(procLCOL);
        Collections.sort(FileLOC);
        Collections.sort(LCOP);
        int f= (int) Math.floor(FO.size()*0.9);
        //int c= (int) Math.floor(Co.size()*0.9);
        int m= (int) Math.floor(CC.size()*0.9);
        //int l= (int) Math.floor(LOC.size()*0.9);
        int cProc= (int) Math.floor(procLCOL.size()*0.9);
        int lFile= (int) Math.floor(FileLOC.size()*0.9);
        int l1= (int) Math.floor(LCOP.size()*0.9);
        temp.put("FanOut", 1.0*FO.get(f));
        //temp.put("LCOL", 1.0*Co.get(c));
        temp.put("LCOL", 1.0*procLCOL.get(cProc));
        temp.put("CC", 1.0*CC.get(m));
        //temp.put("LOC", 1.0*LOC.get(l));
        temp.put("LOC", 1.0*FileLOC.get(lFile));
        if(!LCOP.isEmpty())
            temp.put("LCOP", 1.0*LCOP.get(l1));
        else
            temp.put("LCOP", 0.0);
        
        return temp;
    }

}
