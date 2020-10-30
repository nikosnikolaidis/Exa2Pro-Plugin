/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
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

/**
 *
 * @author Nikos
 */
public class BubbleChartForecasting {
    public JPanel chartPanel;
    
    ArrayList<String> fileNames= new ArrayList<>();
    ArrayList<Double> changeProneness= new ArrayList<>();
    ArrayList<Double> expectedComplexityChange= new ArrayList<>();
    ArrayList<Double> forecastingFile= new ArrayList<>();
    
    public BubbleChartForecasting(int horizon, int files){
        JFreeChart jfreechart = ChartFactory.createBubbleChart(
            "Files/Modules",
            "Expected Complexity Change",
            "Change Proneness",
            createDatasetBubble(horizon,files),
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
    
    private XYZDataset createDatasetBubble(int horizon, int files) {
        getFromDBFiles(horizon, files);
        
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
    
    private void getFromDBFiles(int horizon, int files){
        fileNames.clear();
        changeProneness.clear();
        expectedComplexityChange.clear();
        forecastingFile.clear();
            
        try {
            URL url = new URL("http://160.40.52.130:5001/TDForecaster/FileForecasting?horizon="+ horizon
                    +"&project=metalwalls_measures&project_files="+ files +"&regressor=lasso&ground_truth=no");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200) {
            	System.err.println("http://160.40.52.130:5001/TDForecaster/FileForecasting?horizon="+ horizon
                    +"&project=metalwalls_measures&project_files="+ files +"&regressor=lasso&ground_truth=no");
            }
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                while(sc.hasNext()){
                    inline+=sc.nextLine();
                }
                sc.close();
                JSONParser parse = new JSONParser();
                JSONObject jobj = (JSONObject)parse.parse(inline);
                
                //metrics
                JSONObject jobj2= (JSONObject) jobj.get("results");
                JSONArray jsonarr_2 = (JSONArray) jobj2.get("change_metrics");
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
                JSONArray jsonarr_1 = (JSONArray) jobj2.get("forecasts");
                for(int i=0; i<jsonarr_1.size(); i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    JSONArray jsonarr_3= (JSONArray) jsonobj_1.get((String)jsonobj_1.keySet().iterator().next());
                    JSONObject jsonobj_2= (JSONObject)jsonarr_3.get(jsonarr_3.size()-1);
                    forecastingFile.add( Double.parseDouble(jsonobj_2.get("value").toString()) );
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(BubbleChartForecasting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
