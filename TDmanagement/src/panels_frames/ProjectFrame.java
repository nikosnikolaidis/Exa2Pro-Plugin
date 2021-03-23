/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.PieChart;
import exa2pro.Project;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class ProjectFrame extends javax.swing.JFrame {
    
    Project project;
    HomeFrame homeFrame;

    /**
     * Creates new form ProjectFrame
     */
    public ProjectFrame() {
        initComponents();
    }
    /**
     * Creates new form Home
     * @param p the project that was selected to view
     */
    public ProjectFrame(Project p, HomeFrame homeFrame){
        this.project=p;
        this.homeFrame= homeFrame;
        initComponents();
        
        populateJLabels();
        addPieChart();
    }
    
    private void addPieChart(){
        HashMap<String, Double> temp= PieChart.calculateThresholds();
        jPanel6.removeAll();
        PieChart chart = new PieChart(project,"Pie","CBF"," of Files", temp.get("FanOut"));
        javax.swing.GroupLayout jPanelChartLayout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel7.removeAll();
        PieChart chart1 = new PieChart(project,"Pie","LCOL"," of Files", temp.get("LCOL"));
        javax.swing.GroupLayout jPanelChartLayout1 = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanelChartLayout1);
        jPanelChartLayout1.setHorizontalGroup(
            jPanelChartLayout1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout1.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart1.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout1.setVerticalGroup(
            jPanelChartLayout1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout1.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart1.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel11.removeAll();
        PieChart chart5 = new PieChart(project,"Pie","LCOP"," of Files", temp.get("LCOP"));
        javax.swing.GroupLayout jPanelChartLayout5 = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanelChartLayout5);
        jPanelChartLayout5.setHorizontalGroup(
            jPanelChartLayout5.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout5.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart5.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout5.setVerticalGroup(
            jPanelChartLayout5.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout5.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart5.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel8.removeAll();
        PieChart chart2 = new PieChart(project,"Pie","CC"," of Methods", temp.get("CC"));
        javax.swing.GroupLayout jPanelChartLayout2 = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanelChartLayout2);
        jPanelChartLayout2.setHorizontalGroup(
            jPanelChartLayout2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout2.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart2.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout2.setVerticalGroup(
            jPanelChartLayout2.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout2.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart2.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
        jPanel9.removeAll();
        PieChart chart3 = new PieChart(project,"Pie","LOC"," of Methods", temp.get("LOC"));
        javax.swing.GroupLayout jPanelChartLayout3 = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanelChartLayout3);
        jPanelChartLayout3.setHorizontalGroup(
            jPanelChartLayout3.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout3.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart3.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanelChartLayout3.setVerticalGroup(
            jPanelChartLayout3.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelChartLayout3.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(chart3.chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }
    
    
    //Populate all JLabels
    private void populateJLabels() {
        DecimalFormat round = new DecimalFormat("#,###.#");
        jLabelProjectName.setText(project.getCredentials().getProjectName());
        jLabelTotallLines.setText(project.getprojectReport().getTotalLinesOfCode()+"");
        jTextArea1.setText(project.getprojectReport().getLinesOfCodeForAllLanguages());
        jLabelDateAnalysis.setText(project.getprojectReport().getDate()+"");
        jLabelCodeSmells.setText(project.getprojectReport().getTotalCodeSmells()+"");
        jLabelTechnicalDebt.setText(project.getprojectReport().getTotalDebt());
        if(project.getprojectReport().getTotalTDInterest()!=0)
            jLabelTDInterest.setText(round.format(project.getprojectReport().getTotalTDInterest()) + " €");
        else
            jLabelTDInterest.setText("-");
        jLabelSourceCodeDebt.setText(round.format(project.getprojectReport().getTDPrincipalSourceCodeDebt()) + " €");
        jLabelDesignDebt.setText(round.format(project.getprojectReport().getTDPrincipalDesignDebt()) + " €");
        
        //print the metrics of system
        double sumLOC=0;
        int sumLCOP=0;
        int countNonUndif=0;
        int sumCC=0;
        int sumLCOL=0;
        int sumFO=0;
        int c=0;
        for(CodeFile cf: project.getprojectFiles()){
            sumLOC+= cf.totalLines;
            sumFO+= cf.fanOut;
            if(cf.lcop!=-1){
                sumLCOP+= cf.lcop;
                countNonUndif++;
            }
            for (Map.Entry<String, Integer> entry : cf.methodsCC.entrySet()) {
                sumCC+= entry.getValue();
                c++;
            }
            for (Map.Entry<String, Double> entry : cf.methodsLCOL.entrySet()) {
                sumLCOL+= entry.getValue();
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.#");
        jLabelCC.setText( df.format(sumCC*1.0/c) +"");
        jLabelLCOL.setText( df.format(sumLCOL*1.0/c) +"");
        jLabelFO.setText( df.format(sumFO*1.0/project.getprojectFiles().size()) +"");
        jLabelLOC.setText( df.format(sumLOC/project.getprojectFiles().size()) +"");
        if(countNonUndif==0)
            jLabelLCOP.setText("-");
        else
            jLabelLCOP.setText(df.format(sumLCOP/countNonUndif) +"");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelProjectName = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanelButtonOverview = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanelButtonIssues = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanelButtonProgress = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanelButtonRefactorings = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanelButtonMetrics = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanelButtonForecasting = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanelButtonAdmit = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanelButtonMore = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanelParent = new javax.swing.JPanel();
        jPanelOverview = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelTotallLines = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelDateAnalysis = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabelCodeSmells = new javax.swing.JLabel();
        jLabelTechnicalDebt = new javax.swing.JLabel();
        jLabelCC = new javax.swing.JLabel();
        jLabelLOC = new javax.swing.JLabel();
        jLabelFO = new javax.swing.JLabel();
        jLabelLCOL = new javax.swing.JLabel();
        jLabelLCOP = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabelTDInterest = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabelSourceCodeDebt = new javax.swing.JLabel();
        jLabelDesignDebt = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Exa2Pro");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabelProjectName.setFont(new java.awt.Font("Times New Roman", 1, 16)); // NOI18N
        jLabelProjectName.setText("jLabel1");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jPanelButtonOverview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanelButtonOverview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonOverviewMouseClicked(evt);
            }
        });

        jLabel15.setText("Overview");
        jPanelButtonOverview.add(jLabel15);

        jPanel3.add(jPanelButtonOverview);

        jPanelButtonIssues.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonIssues.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonIssues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonIssuesMouseClicked(evt);
            }
        });

        jLabel14.setText("Issues");
        jPanelButtonIssues.add(jLabel14);

        jPanel3.add(jPanelButtonIssues);

        jPanelButtonProgress.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonProgress.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonProgress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonProgressMouseClicked(evt);
            }
        });

        jLabel16.setText("Evolution");
        jPanelButtonProgress.add(jLabel16);

        jPanel3.add(jPanelButtonProgress);

        jPanelButtonRefactorings.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonRefactorings.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonRefactorings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonRefactoringsMouseClicked(evt);
            }
        });

        jLabel17.setText("Refactorings");
        jPanelButtonRefactorings.add(jLabel17);

        jPanel3.add(jPanelButtonRefactorings);

        jPanelButtonMetrics.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMetrics.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMetrics.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonMetricsMouseClicked(evt);
            }
        });

        jLabel22.setText("Metrics");
        jPanelButtonMetrics.add(jLabel22);

        jPanel3.add(jPanelButtonMetrics);

        jPanelButtonForecasting.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonForecasting.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonForecasting.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonForecastingMouseClicked(evt);
            }
        });

        jLabel19.setText("Forecasting");
        jPanelButtonForecasting.add(jLabel19);

        jPanel3.add(jPanelButtonForecasting);

        jPanelButtonAdmit.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonAdmit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonAdmit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonAdmitMouseClicked(evt);
            }
        });

        jLabel24.setText("Admit");
        jPanelButtonAdmit.add(jLabel24);

        jPanel3.add(jPanelButtonAdmit);

        jPanelButtonMore.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMore.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMore.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelButtonMoreMouseClicked(evt);
            }
        });

        jLabel18.setText("Manage Project");
        jPanelButtonMore.add(jLabel18);

        jPanel3.add(jPanelButtonMore);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelProjectName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabelProjectName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelParent.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabel2.setText("Overview");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        jLabel1.setText("About");

        jLabelTotallLines.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabelTotallLines.setText("jLabel3");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel3.setText("Total Lines of Code");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel4.setText("Lines of Code per Language");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel5.setText("Last analysis");

        jLabelDateAnalysis.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabelDateAnalysis.setText("jLabel6");

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane7.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotallLines)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabelDateAnalysis)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTotallLines)
                        .addGap(22, 22, 22)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDateAnalysis)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 13)); // NOI18N
        jLabel6.setText("System Measures");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel7.setText("Code Smells");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel8.setText("Technical Debt");

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel9.setText("CC");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel10.setText("LOC");

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel11.setText("CBF");

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel12.setText("LCOL");

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel13.setText("LCOP");

        jLabelCodeSmells.setText("jLabel12");

        jLabelTechnicalDebt.setText("jLabel13");

        jLabelCC.setText("jLabel14");

        jLabelLOC.setText("jLabel11");

        jLabelFO.setText("jLabel12");

        jLabelLCOL.setText("jLabel13");

        jLabelLCOP.setText("jLabel14");

        jPanel10.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel6);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 197, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel7);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 197, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel8);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 197, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel9);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 286, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 197, Short.MAX_VALUE)
        );

        jPanel10.add(jPanel11);

        jLabel20.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel20.setText("TD Interest");

        jLabelTDInterest.setText("jLabel13");

        jLabel21.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel21.setText("Source Code Debt");

        jLabelSourceCodeDebt.setText("jLabel13");

        jLabelDesignDebt.setText("jLabel13");

        jLabel23.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel23.setText("Design Debt");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabelTechnicalDebt)
                            .addComponent(jLabel7)
                            .addComponent(jLabelCodeSmells))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabelCC)
                            .addComponent(jLabel20)
                            .addComponent(jLabelTDInterest))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel21)
                                    .addComponent(jLabelSourceCodeDebt))
                                .addGap(38, 38, 38)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabelDesignDebt)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelLOC)
                                    .addComponent(jLabel10))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabelFO))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabelLCOL))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelLCOP)
                                    .addComponent(jLabel13))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(0, 0, 0)
                        .addComponent(jLabelTDInterest))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(0, 0, 0)
                        .addComponent(jLabelTechnicalDebt))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(0, 0, 0)
                        .addComponent(jLabelSourceCodeDebt))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(0, 0, 0)
                        .addComponent(jLabelDesignDebt)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(jLabel11)
                                .addComponent(jLabel12)
                                .addComponent(jLabel13)))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelCC)
                            .addComponent(jLabelLOC)
                            .addComponent(jLabelFO)
                            .addComponent(jLabelLCOL)
                            .addComponent(jLabelLCOP)
                            .addComponent(jLabelCodeSmells))))
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanelOverviewLayout = new javax.swing.GroupLayout(jPanelOverview);
        jPanelOverview.setLayout(jPanelOverviewLayout);
        jPanelOverviewLayout.setHorizontalGroup(
            jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelOverviewLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelOverviewLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelOverviewLayout.setVerticalGroup(
            jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOverviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelOverviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelParent.add(jPanelOverview, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelParent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanelParent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanelButtonOverviewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonOverviewMouseClicked
        reverseBorders();
        jPanelButtonOverview.setBackground(new Color(240, 240, 240));
        jPanelButtonOverview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(jPanelOverview);
        jPanelParent.repaint();
        jPanelParent.revalidate();

    }//GEN-LAST:event_jPanelButtonOverviewMouseClicked

    private void jPanelButtonIssuesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonIssuesMouseClicked
        reverseBorders();
        jPanelButtonIssues.setBackground(new Color(240, 240, 240));
        jPanelButtonIssues.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelIssues(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonIssuesMouseClicked

    private void jPanelButtonProgressMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonProgressMouseClicked
        reverseBorders();
        jPanelButtonProgress.setBackground(new Color(240, 240, 240));
        jPanelButtonProgress.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelProgress(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonProgressMouseClicked

    private void jPanelButtonRefactoringsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonRefactoringsMouseClicked
        reverseBorders();
        jPanelButtonRefactorings.setBackground(new Color(240, 240, 240));
        jPanelButtonRefactorings.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelRefactorings(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonRefactoringsMouseClicked

    private void jPanelButtonMoreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonMoreMouseClicked
        reverseBorders();
        jPanelButtonMore.setBackground(new Color(240, 240, 240));
        jPanelButtonMore.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelMore(project,this));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonMoreMouseClicked

    private void jPanelButtonMetricsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonMetricsMouseClicked
        reverseBorders();
        jPanelButtonMetrics.setBackground(new Color(240, 240, 240));
        jPanelButtonMetrics.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelMetrics(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonMetricsMouseClicked

    private void jPanelButtonForecastingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonForecastingMouseClicked
        reverseBorders();
        jPanelButtonForecasting.setBackground(new Color(240, 240, 240));
        jPanelButtonForecasting.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.add(new JPanelForecasting(project));
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonForecastingMouseClicked

    private void jPanelButtonAdmitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelButtonAdmitMouseClicked
        reverseBorders();
        jPanelButtonAdmit.setBackground(new Color(240, 240, 240));
        jPanelButtonAdmit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        
        jPanelParent.removeAll();
        jPanelParent.repaint();
        jPanelParent.revalidate();
    }//GEN-LAST:event_jPanelButtonAdmitMouseClicked

    private void reverseBorders(){
        jPanelButtonIssues.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonIssues.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonOverview.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonOverview.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonProgress.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonProgress.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonRefactorings.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonRefactorings.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMetrics.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMetrics.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonForecasting.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonForecasting.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonAdmit.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonAdmit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelButtonMore.setBackground(new java.awt.Color(255, 255, 255));
        jPanelButtonMore.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProjectFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProjectFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCC;
    private javax.swing.JLabel jLabelCodeSmells;
    private javax.swing.JLabel jLabelDateAnalysis;
    private javax.swing.JLabel jLabelDesignDebt;
    private javax.swing.JLabel jLabelFO;
    private javax.swing.JLabel jLabelLCOL;
    private javax.swing.JLabel jLabelLCOP;
    private javax.swing.JLabel jLabelLOC;
    private javax.swing.JLabel jLabelProjectName;
    private javax.swing.JLabel jLabelSourceCodeDebt;
    private javax.swing.JLabel jLabelTDInterest;
    private javax.swing.JLabel jLabelTechnicalDebt;
    private javax.swing.JLabel jLabelTotallLines;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelButtonAdmit;
    private javax.swing.JPanel jPanelButtonForecasting;
    private javax.swing.JPanel jPanelButtonIssues;
    private javax.swing.JPanel jPanelButtonMetrics;
    private javax.swing.JPanel jPanelButtonMore;
    private javax.swing.JPanel jPanelButtonOverview;
    private javax.swing.JPanel jPanelButtonProgress;
    private javax.swing.JPanel jPanelButtonRefactorings;
    private javax.swing.JPanel jPanelOverview;
    private javax.swing.JPanel jPanelParent;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
    
}
