/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import csvControlers.CSVWriteForForecastingFiles;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BubbleXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class BubbleChartForecasting {
    public JPanel chartPanel;
    private Project project;
    private int horizon;
    private int files;
    private boolean hasResults;
    
    ArrayList<String> fileNames= new ArrayList<>();
    ArrayList<Double> changeProneness= new ArrayList<>();
    ArrayList<Double> expectedComplexityChange= new ArrayList<>();
    ArrayList<Double> forecastingFile= new ArrayList<>();
    
    public BubbleChartForecasting(Project project, int horizon, int files) {
        this.project= project;
        this.horizon= horizon;
        this.files= files;
        
        JFreeChart jfreechart = ChartFactory.createBubbleChart(
            "Files/Modules",
            "Expected Complexity Change",
            "Change Proneness",
            createDatasetBubble(),
            PlotOrientation.HORIZONTAL,
            true, true, false);
         
        XYPlot xyplot = ( XYPlot )jfreechart.getPlot( );                 
        xyplot.setForegroundAlpha( 0.65F );                 
        XYItemRenderer xyitemrenderer = xyplot.getRenderer( );
        xyitemrenderer.setSeriesPaint( 0 , Color.blue );                 
        NumberAxis numberaxis = ( NumberAxis )xyplot.getDomainAxis( );                 
        numberaxis.setLowerMargin( 0.2 );                 
        numberaxis.setUpperMargin( 0.5 );                 
        NumberAxis numberaxis1 = ( NumberAxis )xyplot.getRangeAxis( );                 
        numberaxis1.setLowerMargin( 0.8 );                 
        numberaxis1.setUpperMargin( 0.9 );
        
        XYBubbleRenderer renderer=(XYBubbleRenderer)xyplot.getRenderer();
        BubbleXYItemLabelGenerator generator=new BubbleXYItemLabelGenerator("{0}",
                new DecimalFormat("0"), new DecimalFormat("0"), new DecimalFormat("0"));
        renderer.setDefaultItemLabelGenerator(generator);
        renderer.setDefaultItemLabelsVisible(true);
        
        
        ChartPanel chartpanel = new ChartPanel( jfreechart );
        chartpanel.setDomainZoomable( true );                 
        chartpanel.setRangeZoomable( true );
        this.chartPanel= chartpanel;
    }
    
    private XYZDataset createDatasetBubble() {
        runForcasting();
        
        if(hasResults){
            //normalize diameter
            double max= forecastingFile.get(0);
            double min= forecastingFile.get(0);
            for(Double d:forecastingFile) {
                    if(d>max)
                            max=d;
                    if(d<min)
                            min=d;
            }
            for(int i=0; i<forecastingFile.size(); i++) {
                    double value= forecastingFile.get(i);
                    forecastingFile.set(i, (0.02-0.002)/(max-min)*(value-min)+0.002);
            }
        }
        
        //create dataset
        DefaultXYZDataset defaultxyzdataset = new DefaultXYZDataset();
        for(int i=0; i<changeProneness.size(); i++){
        	defaultxyzdataset.addSeries(fileNames.get(i), new double[][] {
        		{expectedComplexityChange.get(i)},
        		{changeProneness.get(i)},
        		{forecastingFile.get(i)}
        	});
        }

        return defaultxyzdataset;
    }

    private void runForcasting() {
        fileNames.clear();
        changeProneness.clear();
        expectedComplexityChange.clear();
        forecastingFile.clear();
        
        //write csv file
        new CSVWriteForForecastingFiles(project);
        
        //run forecasting
        //For Windows
        if ( Exa2Pro.isWindows() ){
            Process proc;
            try {
                //start scrip
                Process proc1 = Runtime.getRuntime().exec("cmd /c \"cd " + Exa2Pro.TDForecasterPath + 
                            " && "+ Exa2Pro.pythonRun +" td_forecaster_cli.py file "+ horizon +" "+
                            project.getCredentials().getProjectName() +" "+ files +" ridge --write_file \"");
                hasResults=true;
                BufferedReader readerError = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
                String lineError;
                while ((lineError = readerError.readLine()) != null) {
//                    if(lineError.contains("ZeroDivisionError: float division by zero"))
//                        hasResults= false;
                    System.out.println(lineError);
                }
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    if(line1.contains("cannot provide reliable results for this project. Please reduce forecasting horizon."))
                        hasResults= false;
                    System.out.println(line1);
                }
            } catch (IOException ex) {
                Logger.getLogger(CodeFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //For Linux
        else{
            try {
                //start clustering scrips
                ProcessBuilder pbuilder1 = new ProcessBuilder(new String[]{Exa2Pro.pythonRun, 
                        Exa2Pro.TDForecasterPath+"/td_forecaster_cli.py", "file", horizon+"",
                        project.getCredentials().getProjectName(), files+"", "ridge", "--write_file"});
                hasResults=true;
                File err1 = new File("err1.txt");
                pbuilder1.redirectError(err1);
                pbuilder1.directory(new File(Exa2Pro.TDForecasterPath));
                Process p1 = pbuilder1.start();
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                String line1;
                while ((line1 = reader1.readLine()) != null) {
                    if(line1.contains("cannot provide reliable results for this project. Please reduce forecasting horizon."))
                        hasResults=false;
                    System.out.println(line1);
                }
            } catch (IOException ex) {
                Logger.getLogger(CodeFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //get results
        if(hasResults==true){
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(new File(Exa2Pro.TDForecasterPath+"/output/"+
                    project.getCredentials().getProjectName()+"_forecasts_class.json")))
            {
                //Read JSON file
                JSONObject obj = (JSONObject) jsonParser.parse(reader);
                
                //metrics
                JSONArray jsonarr_2 = (JSONArray) obj.get("change_metrics");
                for(int i=0; i<jsonarr_2.size(); i++){
                    JSONObject jsonobj_2 = (JSONObject)jsonarr_2.get(i);
                    String name= (String)jsonobj_2.keySet().iterator().next();
                    fileNames.add(name);
                    JSONObject jsonobj_3= (JSONObject) jsonobj_2.get(name);
                    changeProneness.add( Double.parseDouble(jsonobj_3.get("change_proneness_(CP)").toString()) );
                    expectedComplexityChange.add( Double.parseDouble(
                                jsonobj_3.get("expected_complexity_change_(ED-COMP)").toString()) );
                }
                
                //forecasting
                JSONArray jsonarr_1 = (JSONArray) obj.get("forecasts");
                for(int i=0; i<jsonarr_1.size(); i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    JSONArray jsonarr_3= (JSONArray) jsonobj_1.get((String)jsonobj_1.keySet().iterator().next());
                    JSONObject jsonobj_2= (JSONObject)jsonarr_3.get(jsonarr_3.size()-1);
                    forecastingFile.add( Double.parseDouble(jsonobj_2.get("value").toString()) );
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LineChartForecasting.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ParseException ex) {
                Logger.getLogger(LineChartForecasting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean hasChartPanel(){
        return hasResults;
    }
    
    public JPanel getChartPanel(){
        return chartPanel;
    }
}
