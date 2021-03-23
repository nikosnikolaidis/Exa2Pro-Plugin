/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Nikos
 */
public class LineChartAdmit {
    public JPanel chartPanel;
    
    private ArrayList<Double> val;
    private ArrayList<Integer> totalBenefits;
    private ArrayList<Integer> totalCosts;
    
    public LineChartAdmit(ArrayList<Double> val, ArrayList<Integer> totalBenefits, ArrayList<Integer> totalCosts){
        this.val = val;
        this.totalBenefits = totalBenefits;
        this.totalCosts = totalCosts;
        
        String chartTitle = "Cost-Benefit Analysis for Expected Increase";

        XYDataset dataset = createDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "", "", dataset);
        customizeChart(chart);
        chartPanel= new ChartPanel(chart);
    }
    
    // this method creates the data as time seris
    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Cost");
        XYSeries series2 = new XYSeries("Benefit");
        
        for (int i=0; i<val.size(); i++) {
            series1.add(val.get(i), totalCosts.get(i));
            series2.add(val.get(i), totalBenefits.get(i));
        }
        
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        
        return dataset;
    }
    
    // here we make some customization
    private void customizeChart(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.5f));
        renderer.setSeriesStroke(1, new BasicStroke(1.5f));

        plot.setRenderer(renderer);
    }
}
