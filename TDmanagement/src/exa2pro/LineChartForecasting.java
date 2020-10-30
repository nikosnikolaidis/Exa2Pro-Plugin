/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Nikos
 */
public class LineChartForecasting {
    public JPanel chartPanel;
    
    HashMap<Integer,Double> pastVersionValues= new HashMap<>();
    HashMap<Integer,Double> newVersionValues= new HashMap<>();
    
    public LineChartForecasting(int horizon){
        String chartTitle = "Forecasting";
        String xAxisLabel = "version";
        String yAxisLabel = "TD";

        XYDataset dataset = createDataset(horizon);

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, 
                xAxisLabel, yAxisLabel, dataset);

        customizeChart(chart);

        chartPanel= new ChartPanel(chart);
    }
    
    private XYDataset createDataset(int horizon) {    // this method creates the data as time seris 
        getFromDBProject(horizon);
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Past");
        XYSeries series2 = new XYSeries("Forecasting");
        
        for(int key: pastVersionValues.keySet()){
            series1.add(key, pastVersionValues.get(key));
        }
        
        for(int key: newVersionValues.keySet()){
            series2.add(key, newVersionValues.get(key));
        }
        
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        
        return dataset;
    }
    
    private void customizeChart(JFreeChart chart) {   // here we make some customization
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);

        // sets thickness for series (using strokes)
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));

        // sets paint color for plot outlines
        plot.setOutlinePaint(Color.BLUE);
        plot.setOutlineStroke(new BasicStroke(2.0f));

        // sets renderer for lines
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
    }
    
    private void getFromDBProject(int horizon) {
        pastVersionValues.clear();
        newVersionValues.clear();
        
        try {
            URL url = new URL("http://160.40.52.130:5001/TDForecaster/SystemForecasting?horizon="+ horizon
                    + "&project=metalwalls_measures&regressor=ridge&ground_truth=yes");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200) {
            	System.err.println("http://160.40.52.130:5001/TDForecaster/SystemForecasting?horizon="+ horizon
                    + "&project=metalwalls_measures&regressor=ridge&ground_truth=yes");
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
                
                //forecasting values
                JSONObject jobj2= (JSONObject) jobj.get("results");
                JSONArray jsonarr_2 = (JSONArray) jobj2.get("forecasts");
                for(int i=0; i<jsonarr_2.size(); i++){
                    JSONObject jsonobj_2 = (JSONObject)jsonarr_2.get(i);
                    newVersionValues.put( Integer.parseInt(jsonobj_2.get("version").toString()),
                                Double.parseDouble(jsonobj_2.get("value").toString()) );
                }
                
                //past values
                JSONObject jobj1= (JSONObject) jobj.get("results");
                JSONArray jsonarr_1 = (JSONArray) jobj1.get("ground_truth");
                for(int i=0; i<jsonarr_1.size(); i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    pastVersionValues.put( Integer.parseInt(jsonobj_1.get("version").toString()),
                                Double.parseDouble(jsonobj_1.get("value").toString()) );
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(LineChartForecasting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
