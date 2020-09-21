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
        
            if (metric.equals("FanOut")){
                System.out.println("FO threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    if(cf.fanOut>threshold)
                        n++;
                    else
                        p++;
                    total++;
                }
            }
            else if (metric.equals("LCOL")){
                System.out.println("LCOL threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    if(cf.cohesion>threshold)
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
            else if (metric.equals("LOC")){
                System.out.println("LOC threshold: "+threshold);
                for(CodeFile cf: project.getprojectFiles()){
                    for (HashMap.Entry pair : cf.methodsLOC.entrySet()) {
                        if((Integer)pair.getValue()>threshold)
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
        int sumFO=0;
        ArrayList<Integer> FO=new ArrayList<>();
        int sumCo=0;
        ArrayList<Double> Co=new ArrayList<>();
        int sumCC=0;
        ArrayList<Integer> CC=new ArrayList<>();
        int sumLOC=0;
        ArrayList<Integer> LOC=new ArrayList<>();
        ArrayList<Integer> LCOP=new ArrayList<>();
        int totalfiles=0;
        int totalmethods=0;
        for(ProjectCredentials pC: Exa2Pro.projecCredentialstList){
            Project p= pC.getProjects().get(pC.getProjects().size()-1);
            for(CodeFile cf: p.getprojectFiles()){
                sumFO += cf.fanOut;
                FO.add(cf.fanOut);
                sumCo += cf.cohesion;
                Co.add(cf.cohesion);
                if(cf.lcop!=-1)
                    LCOP.add(cf.lcop);
                totalfiles++;
                for(String key : cf.methodsLOC.keySet()){
                    sumCC += cf.methodsCC.get(key);
                    CC.add(cf.methodsCC.get(key));
                    sumLOC += cf.methodsLOC.get(key);
                    LOC.add(cf.methodsLOC.get(key));
                    totalmethods++;
                }
            }
        }
        HashMap<String, Double> temp=new HashMap<>();
//        temp.put("FanOut", (sumFO*1.0)/totalfiles);
//        temp.put("LCOL", (sumCo*1.0)/totalfiles);
//        temp.put("CC", (sumCC*1.0)/totalmethods);
//        temp.put("LOC", (sumLOC*1.0)/totalmethods);
        
        Collections.sort(FO);
        Collections.sort(Co);
        Collections.sort(CC);
        Collections.sort(LOC);
        Collections.sort(LCOP);
        int f= (int) Math.floor(FO.size()*0.9);
        int c= (int) Math.floor(Co.size()*0.9);
        int m= (int) Math.floor(CC.size()*0.9);
        int l= (int) Math.floor(LOC.size()*0.9);
        int l1= (int) Math.floor(LCOP.size()*0.9);
        temp.put("FanOut", 1.0*FO.get(f));
        temp.put("LCOL", 1.0*Co.get(c));
        temp.put("CC", 1.0*CC.get(m));
        temp.put("LOC", 1.0*LOC.get(l));
        temp.put("LCOP", 1.0*LCOP.get(l1));
        
        return temp;
    }

}
