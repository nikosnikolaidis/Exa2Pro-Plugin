/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import exa2pro.Exa2Pro;
import static exa2pro.Exa2Pro.deletePreviousClasteringCSV;
import exa2pro.Project;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import static java.util.stream.Collectors.toMap;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class JPanelRefactorings extends javax.swing.JPanel {

    Project project;
    
    /**
     * Creates new form JPanelRefactorings
     */
    public JPanelRefactorings(Project project) {
        this.project=project;
        initComponents();
        
        populateMethodsLists();
    }

    //Populate all List for Methods and Files
    private void populateMethodsLists(){
        HashMap<String, Double> thresholds= exa2pro.PieChart.calculateThresholds();
        //create the models
        DefaultListModel<String> defaultListModelFanOut = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelLOC = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelLCOP = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelCC = new DefaultListModel<>();
        DefaultListModel<String> defaultListModelLCOL = new DefaultListModel<>();
        //create the lists for all the methods and files
        HashMap<String, Integer> allFilesLCOP = new HashMap<>();
        HashMap<String, Integer> allFilesFanOut = new HashMap<>();
        HashMap<String, Integer> allMethodsCC = new HashMap<>();
        HashMap<String, Integer> allMethodsLCOL = new HashMap<>();
        HashMap<String, Integer> allFilesLOC = new HashMap<>();
        for(CodeFile cf: project.getprojectFiles()){
            if(cf.fanOut >= thresholds.get("FanOut"))
                allFilesFanOut.put(cf.file.getName(), cf.fanOut);
            if(cf.lcop!=-1 && Math.round(cf.lcop * 10.0)/10.0 >= thresholds.get("LCOP"))
                allFilesLCOP.put(cf.file.getName(), cf.lcop);
            if(cf.totalLines>=thresholds.get("LOC"))
                allFilesLOC.put(cf.file.getName(), cf.totalLines);
            allMethodsCC.putAll(prefixHashMap(cf.methodsCC, cf.file.getName(), thresholds, "CC"));
            allMethodsLCOL.putAll(prefixHashMap(cf.methodsLCOL, cf.file.getName(), thresholds, "LCOL"));
        }
        
        //sort the lists
        HashMap<String, Integer> sortedCC= allMethodsCC.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Integer> sortedLCOL= allMethodsLCOL.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Integer> sortedFanOut= allFilesFanOut.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Integer> sortedLCOP= allFilesLCOP.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        HashMap<String, Integer> sortedFilesLOC= allFilesLOC.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        
        //add the items to the list
        System.out.println("CC: "+ sortedCC.size());
        sortedCC.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelCC.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("LCOL: "+ sortedLCOL.size());
        sortedLCOL.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelLCOL.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("FO: "+ sortedFanOut.size());
        sortedFanOut.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelFanOut.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("LCOP: "+ sortedLCOP.size());
        sortedLCOP.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelLCOP.addElement(item.getValue()+" "+item.getKey());
        });
        System.out.println("File LOC"+ sortedFilesLOC.size());
        sortedFilesLOC.entrySet().forEach((item) -> {
            System.out.println(item.getValue()+" "+item.getKey());
            defaultListModelLOC.addElement(item.getValue()+" "+item.getKey());
        });
        
        jListFilesFanOut.setModel(defaultListModelFanOut);
        jListFilesIncoherent.setModel(defaultListModelLCOL);
        jListMethodsComplex.setModel(defaultListModelCC);
        jListMethodsLOC.setModel(defaultListModelLOC);
        jListFilesLCOP.setModel(defaultListModelLCOP);
    }
    private HashMap prefixHashMap(HashMap source, String prefix, HashMap<String, Double> thresholds, String metric) {
        HashMap result = new HashMap();
        Iterator iter = source.entrySet().iterator();
        while(iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof Integer) {
            	if((Integer)value >= thresholds.get(metric))
            		result.put(prefix + '.' + key.toString().split(" ")[key.toString().split(" ").length-1], value);
            }
            else {
            	if((Double)value >= thresholds.get(metric))
            		result.put(prefix + '.' + key.toString().split(" ")[key.toString().split(" ").length-1], value);
            }
        }
        return result;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabelRefactorings = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListFilesFanOut = new javax.swing.JList<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jListMethodsLOC = new javax.swing.JList<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jListFilesLCOP = new javax.swing.JList<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jListFilesIncoherent = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListMethodsComplex = new javax.swing.JList<>();
        jPanel6 = new javax.swing.JPanel();
        jPanelMethodExtr = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListOpportunities = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        jLabel2.setText("Select a file and wait while calculating possible refactorings");

        jLabelRefactorings.setText("Refactorings");

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel10.setText("(CBF) Over Coupled Files/Modules");

        jListFilesFanOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListFilesFanOutMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(jListFilesFanOut);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel1);

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel13.setText("(LOC) Large Files/Modules");

        jListMethodsLOC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListMethodsLOCMousePressed(evt);
            }
        });
        jScrollPane5.setViewportView(jListMethodsLOC);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel4);

        jLabel14.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel14.setText("(LCOP) Large Files/Modules");

        jListFilesLCOP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListFilesLCOPMousePressed(evt);
            }
        });
        jScrollPane6.setViewportView(jListFilesLCOP);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel7);

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel12.setText("(LCOL) Long Procedures");

        jListFilesIncoherent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListFilesIncoherentMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(jListFilesIncoherent);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel2);

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel11.setText("(CC) Complex Procedures");

        jListMethodsComplex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jListMethodsComplexMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(jListMethodsComplex);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.add(jPanel3);

        jListOpportunities.setLayoutOrientation(javax.swing.JList.VERTICAL_WRAP);
        jScrollPane1.setViewportView(jListOpportunities);

        jLabel1.setText("Opportunity refactorings");

        javax.swing.GroupLayout jPanelMethodExtrLayout = new javax.swing.GroupLayout(jPanelMethodExtr);
        jPanelMethodExtr.setLayout(jPanelMethodExtrLayout);
        jPanelMethodExtrLayout.setHorizontalGroup(
            jPanelMethodExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMethodExtrLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanelMethodExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelMethodExtrLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );
        jPanelMethodExtrLayout.setVerticalGroup(
            jPanelMethodExtrLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMethodExtrLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMethodExtr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMethodExtr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelRefactorings)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelRefactorings)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jListFilesIncoherentMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListFilesIncoherentMousePressed
        String fileName= jListFilesIncoherent.getSelectedValue().split(" ")[1].split("\\.")[0]+"."+
        		jListFilesIncoherent.getSelectedValue().split(" ")[1].split("\\.")[1];
        String methodName= jListFilesIncoherent.getSelectedValue().split(" ")[1].split("\\.")[2];
        extractMethodOpp(fileName, methodName);
    }//GEN-LAST:event_jListFilesIncoherentMousePressed

    private void jListMethodsComplexMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListMethodsComplexMousePressed
        String fileName= jListMethodsComplex.getSelectedValue().split(" ")[1].split("\\.")[0]+"."+
        		jListMethodsComplex.getSelectedValue().split(" ")[1].split("\\.")[1];
        String methodName= jListMethodsComplex.getSelectedValue().split(" ")[1].split("\\.")[2];
        extractMethodOpp(fileName, methodName);
    }//GEN-LAST:event_jListMethodsComplexMousePressed

    private void jListFilesFanOutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListFilesFanOutMousePressed
        String fileName= jListFilesFanOut.getSelectedValue().split(" ")[1];
        int fanout= Integer.parseInt(jListFilesFanOut.getSelectedValue().split(" ")[0]);
        for(CodeFile cf:project.getprojectFiles()){
            if(cf.file.getName().equals(fileName) && cf.fanOut==fanout){
                extractFileOpp(cf);
            }
        }
    }//GEN-LAST:event_jListFilesFanOutMousePressed

    private void jListMethodsLOCMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListMethodsLOCMousePressed
        String fileName= jListMethodsLOC.getSelectedValue().split(" ")[1];
        int loc= Integer.parseInt(jListMethodsLOC.getSelectedValue().split(" ")[0]);
        for(CodeFile cf:project.getprojectFiles()){
            if(cf.file.getName().equals(fileName) && cf.totalLines==loc){
                extractFileOpp(cf);
            }
        }
    }//GEN-LAST:event_jListMethodsLOCMousePressed

    private void jListFilesLCOPMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListFilesLCOPMousePressed
        String fileName= jListFilesLCOP.getSelectedValue().split(" ")[1];
        int lcop= Integer.parseInt(jListFilesLCOP.getSelectedValue().split(" ")[0]);
        for(CodeFile cf:project.getprojectFiles()){
            if(cf.file.getName().equals(fileName) && cf.lcop==lcop){
                extractFileOpp(cf);
            }
        }
    }//GEN-LAST:event_jListFilesLCOPMousePressed

    private void extractFileOpp(CodeFile file){
        JFrame parent = new JFrame();
        JOptionPane optionPane = new JOptionPane();
        JSlider slider = new JSlider();
        Hashtable labelTable = new Hashtable();
        labelTable.put(10, new JLabel("0.1") );
        labelTable.put(40, new JLabel("0.4") );
        labelTable.put(70, new JLabel("0.7") );
        labelTable.put(99, new JLabel("0.99") );
        slider.setLabelTable(labelTable);
        slider.setMajorTickSpacing(30);
        slider.setMinimum(10);
        slider.setMaximum(99);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setValue(40);
        ChangeListener changeListener = new ChangeListener() {
          public void stateChanged(ChangeEvent changeEvent) {
            JSlider theSlider = (JSlider) changeEvent.getSource();
            if (!theSlider.getValueIsAdjusting()) {
              optionPane.setMessage(new Object[] { "Select a threshold: "+ (theSlider.getValue()*1.0/100), slider });
            }
          }
        };
        slider.addChangeListener(changeListener);
        
        optionPane.setMessage(new Object[] { "Select a threshold: "+ (slider.getValue()*1.0/100), slider });
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(parent, "Clustering");
        dialog.setVisible(true);
        
        if(optionPane.getValue()!=null && JOptionPane.OK_OPTION == (int)optionPane.getValue()){
            deletePreviousClasteringCSV();
             
            //parse and start scrips
            //file.parse();
            if (file.exportCSVofAtribute()){
                DefaultListModel<String> defaultListModelOpp = new DefaultListModel<>();
                //start clustering script
                System.out.println("Threshold: " + (slider.getValue()*1.0/100));
                ArrayList<String> clusters= file.runClustering(slider.getValue()*1.0/100);
                
                if(clusters.size()>0){
                    //Add Extract File Panel
                    jPanel6.removeAll();
                    PanelRefactoringExtractFileOpp p=new PanelRefactoringExtractFileOpp(clusters);

                    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
                    jPanel6.setLayout(jPanel6Layout);
                    jPanel6Layout.setHorizontalGroup(
                        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(p, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    );
                    jPanel6Layout.setVerticalGroup(
                        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(p, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    );

                    jPanel6.repaint();
                    jPanel6.revalidate();
                }
                else{
                    JOptionPane.showMessageDialog(this, "Not able to get refactoring opportunities for this file/module");
                }
            }
            else{
                JOptionPane.showMessageDialog(this, "Not able to get refactoring opportunities for this file/module");
            }
        }
    }
    
    private void extractMethodOpp(String fileName, String methodName){
        DefaultListModel<String> defaultListModelOpp = new DefaultListModel<>();
        
        boolean fast;
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this,
                    "Would you like to use the Faster Opportunity extractor, but in accuracy drawback?",
                    "Algorithm Option", dialogButton);
        if(dialogResult == 0) {
            fast=true;
        } else {
            fast=false;
        } 
        
        if(dialogResult != JOptionPane.CLOSED_OPTION) {
            for(CodeFile cf:project.getprojectFiles()){
               if(fileName.equals(cf.file.getName())){
                    cf.parse();
                    //cf.calculateCohesion();
                    cf.calculateOpportunities(fast, methodName);

                    cf.opportunities.forEach((opp) -> {
                        defaultListModelOpp.addElement(opp.split(" ", 2)[0]+"-"+opp.split(" ", 2)[1].replace("()", "():")+"  ");
                    });
                   
                    //Add Method Extract panel
                    jPanel6.removeAll();
                    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
                    jPanel6.setLayout(jPanel6Layout);
                    jPanel6Layout.setHorizontalGroup(
                        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanelMethodExtr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    );
                    jPanel6Layout.setVerticalGroup(
                        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanelMethodExtr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    );

                    jPanel6.repaint();
                    jPanel6.revalidate();
                    
                   jListOpportunities.setModel(defaultListModelOpp);
               }
           }
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelRefactorings;
    private javax.swing.JList<String> jListFilesFanOut;
    private javax.swing.JList<String> jListFilesIncoherent;
    private javax.swing.JList<String> jListFilesLCOP;
    private javax.swing.JList<String> jListMethodsComplex;
    private javax.swing.JList<String> jListMethodsLOC;
    private javax.swing.JList<String> jListOpportunities;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelMethodExtr;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    // End of variables declaration//GEN-END:variables
}
