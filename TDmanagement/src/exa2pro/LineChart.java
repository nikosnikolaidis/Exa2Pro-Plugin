package exa2pro;

import java.util.ArrayList;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import parsers.CodeFile;


public class LineChart extends ApplicationFrame {

    ProjectCredentials projectC;
    public ChartPanel chartPanel;
    private String metric;

    public LineChart(ProjectCredentials pC, String metric, String applicationTitle, String chartTitle) {
        super(applicationTitle);
        this.projectC = pC;
        this.metric=metric;
        
        String value= "Hours";
        if(daysValue() && metric.equals("TD"))
            value= "Days";
        else
            value= "Metric";
        
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Version", value,
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);
        
        chartPanel = new ChartPanel( lineChart );
        //chartPanel.setPreferredSize( new java.awt.Dimension( 600, 500) );
        setContentPane( chartPanel );
    }

    private boolean daysValue(){
        ArrayList<Project> allProject = projectC.getProjects();
        boolean days=false;
        for (Project p : allProject) {
            if(p.getprojectReport().getTotalDebt().contains("d")){
                days=true;
                break;
            }
        }
        return days;
    }
    
    private DefaultCategoryDataset createDataset() {
        ArrayList<Project> allProject = projectC.getProjects();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int version = 1;
        boolean days=daysValue();
        
        for (Project p : allProject) {
            double num=0;
            switch (metric) {
                case "TD":
                {
                    String s=p.getprojectReport().getTotalDebt();
                    double st = Double.parseDouble(s.replace("min", "").replace("h", "").replace("d", ""));
                    if(s.contains("d")){
                        num= st;
                    }
                    else if(s.contains("h") && days){
                        num= st/8;
                    }
                    else if(s.contains("h") && !days){
                        num= st;
                    }
                    else if(s.contains("min") && days){
                         num= st/(8*60);
                    }
                    else if(s.contains("min") && !days){
                        num= st/60;
                    }
                    break;
                }
                case "Issues":
                    num= p.getprojectReport().getTotalCodeSmells();
                    break;
                case "Fan-Out":
                    {
                        int sum=0;
                        sum = p.getprojectFiles().stream().map((cf) -> cf.fanOut).reduce(sum, Integer::sum);
                        num= sum/p.getprojectFiles().size();
                        break;
                    }
                case "LOC":
                    {
                        int sum=0;
                        sum = p.getprojectFiles().stream().map((cf) -> cf.totalLines).reduce(sum, Integer::sum);
                        num= sum*1.0/p.getprojectFiles().size();
                        break;
                    }
                case "LCOP":
                    {
                        int sum=0;
                        int i=0;
                        for(CodeFile cf: p.getprojectFiles()){
                            if(cf.lcop!=-1){
                                sum+= cf.lcop;
                                i++;
                            }
                        }
                        if(i==0)
                        	i=1;
                        num= sum/i;
                        break;
                    }
                case "CC":
                    {
                        int sum=0;
                        int i=0;
                        for(CodeFile cf: p.getprojectFiles()){
                            for (HashMap.Entry pair : cf.methodsCC.entrySet()) {
                                sum+= (Integer)pair.getValue();
                                i++;
                            }
                        }
                        num= sum/i;
                        break;
                    }
                case "LCOL":
                    {
                        int sum=0;
                        int i=0;
                        for(CodeFile cf: p.getprojectFiles()){
                            for (HashMap.Entry pair : cf.methodsLCOL.entrySet()) {
                                sum+= (Double)pair.getValue();
                                i++;
                            }
                        }
                        num= sum/i;
                        break;
                    }
                default:
                    break;
            }
            
            dataset.setValue(num, "Rate", "V." + version);
            version++;
        }

        return dataset;
    }

}
