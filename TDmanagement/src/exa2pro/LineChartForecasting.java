/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import csvControlers.CSVWriteForForecastingSystem;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class LineChartForecasting {
    public JPanel chartPanel;
    private Project project;
    private int horizon;
    private boolean hasResults;
    
    HashMap<Integer,Double> pastVersionValues= new HashMap<>();
    HashMap<Integer,Double> newVersionValues= new HashMap<>();
    
    public LineChartForecasting(Project project, int horizon) {
        this.project= project;
        this.horizon= horizon;
        String chartTitle = "Forecasting";
        String xAxisLabel = "version";
        String yAxisLabel = "TD";

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, 
                xAxisLabel, yAxisLabel, dataset);

        customizeChart(chart);

        chartPanel= new ChartPanel(chart);
    }
    
    /**
     * Create the data as time series
     * @return 
     */
    private XYDataset createDataset() {
        runForcasting();
        
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
    
    /**
     * Customizations of chart
     * @param chart 
     */
    private void customizeChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets color and thickness for each series
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
    }
    
    private void runForcasting() {
        pastVersionValues.clear();
        newVersionValues.clear();
        
        //write csv file
        new CSVWriteForForecastingSystem(project);
        
        //run forecasting
        //For Windows
        if ( Exa2Pro.isWindows() ){
            Process proc;
            try {
                //start script
                Process proc1 = Runtime.getRuntime().exec("cmd /c \"cd " + Exa2Pro.TDForecasterPath + 
                            " && "+ Exa2Pro.pythonRun +" td_forecaster_cli.py system "+ horizon +" "+
                            project.getCredentials().getProjectName() +" 10 ridge --ground_truth --write_file \"");
                hasResults=true;
                BufferedReader readerError = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
                String lineError;
                while ((lineError = readerError.readLine()) != null) {
                    System.out.println(lineError);
                }
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
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
        //For Linux
        else{
            try {
                //start clustering scrips
                ProcessBuilder pbuilder1 = new ProcessBuilder(new String[]{Exa2Pro.pythonRun, 
                    Exa2Pro.TDForecasterPath+ "/td_forecaster_cli.py", "system", horizon+"",
                    project.getCredentials().getProjectName(), "10", "ridge", "--ground_truth", "--write_file"});
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
        if(hasResults) {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(new File(Exa2Pro.TDForecasterPath+"/output/"+
                    project.getCredentials().getProjectName()+"_forecasts.json")))
            {
                //Read JSON file
                JSONObject obj = (JSONObject) jsonParser.parse(reader);

                JSONArray jsonarr_2 = (JSONArray) obj.get("forecasts");
                for(int i=0; i<jsonarr_2.size(); i++){
                    JSONObject jsonobj_2 = (JSONObject)jsonarr_2.get(i);
                    newVersionValues.put( Integer.parseInt(jsonobj_2.get("version").toString()),
                                Double.parseDouble(jsonobj_2.get("value").toString()) );
                }

                JSONArray jsonarr_1 = (JSONArray) obj.get("ground_truth");
                for(int i=0; i<jsonarr_1.size(); i++){
                    JSONObject jsonobj_1 = (JSONObject)jsonarr_1.get(i);
                    pastVersionValues.put( Integer.parseInt(jsonobj_1.get("version").toString()),
                                 Double.parseDouble(jsonobj_1.get("value").toString()) );
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
