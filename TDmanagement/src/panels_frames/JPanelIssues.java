/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels_frames;

import csvControlers.Artifacts;
import exa2pro.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JRadioButton;
import parsers.CodeFile;

/**
 *
 * @author Nikos
 */
public class JPanelIssues extends javax.swing.JPanel {
    Project project;
    DefaultListModel<Issue> defaultListModel;
    ButtonGroup groupRadioFiles;
    ArrayList<javax.swing.JCheckBox> group;
    
    /**
     * Creates new form JPanelIssues
     */
    public JPanelIssues(Project p) {
        project= p;
        initComponents();
        jLabelIssuesN.setText(project.getprojectReport().getTotalCodeSmells()+"");
        
        group = new ArrayList<>();
        group.add(jCheckBoxFortran);
        group.add(jCheckBoxC);
        group.add(jCheckBoxCpp);
        group.add(jCheckBoxOther);
        
        populateIssueList();
        addActionToCheckLanguage();
        addCheckBoxesFile();
        addCheckBoxesRules();
    }

    //Populate List with Projects
    private void populateIssueList() {
        if(!Exa2Pro.projecCredentialstList.isEmpty()){
            defaultListModel= new DefaultListModel<>();
            Collections.sort(project.getprojectReport().getIssuesList());
            project.getprojectReport().getIssuesList().forEach((i) -> {
                defaultListModel.addElement(i);
            });
            jListCodeSmells.setModel(defaultListModel);
            jListCodeSmells.setCellRenderer(new PanelIssueList(project));
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelIssuesN = new javax.swing.JLabel();
        jLabelIssues = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListCodeSmells = new javax.swing.JList<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanelLanguages = new javax.swing.JPanel();
        jCheckBoxFortran = new javax.swing.JCheckBox();
        jCheckBoxC = new javax.swing.JCheckBox();
        jCheckBoxCpp = new javax.swing.JCheckBox();
        jCheckBoxOther = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelFiles = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanelRules = new javax.swing.JPanel();

        jLabelIssuesN.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabelIssuesN.setText("s");

        jLabelIssues.setFont(new java.awt.Font("Times New Roman", 3, 18)); // NOI18N
        jLabelIssues.setText(" Issues");

        jListCodeSmells.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListCodeSmellsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListCodeSmells);

        jLabel19.setText("Language");

        jLabel20.setText("File");

        jLabel21.setText("Rule");

        jPanelLanguages.setLayout(new java.awt.GridLayout(0, 1));

        jCheckBoxFortran.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        jCheckBoxFortran.setSelected(true);
        jCheckBoxFortran.setText("Fortran");
        jPanelLanguages.add(jCheckBoxFortran);

        jCheckBoxC.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        jCheckBoxC.setSelected(true);
        jCheckBoxC.setText("C");
        jPanelLanguages.add(jCheckBoxC);

        jCheckBoxCpp.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        jCheckBoxCpp.setSelected(true);
        jCheckBoxCpp.setText("C++");
        jPanelLanguages.add(jCheckBoxCpp);

        jCheckBoxOther.setFont(new java.awt.Font("Times New Roman", 0, 11)); // NOI18N
        jCheckBoxOther.setSelected(true);
        jCheckBoxOther.setText("Other");
        jPanelLanguages.add(jCheckBoxOther);

        jPanelFiles.setLayout(new java.awt.GridLayout(0, 1));
        jScrollPane2.setViewportView(jPanelFiles);

        jPanelRules.setLayout(new java.awt.GridLayout(0, 1));
        jScrollPane3.setViewportView(jPanelRules);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelLanguages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelLanguages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelIssuesN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelIssues)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 968, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelIssues)
                            .addComponent(jLabelIssuesN))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jListCodeSmellsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListCodeSmellsMouseClicked
        Issue selectedIssue= jListCodeSmells.getSelectedValue();
        System.out.println("in "+selectedIssue.getIssueDirectory());
        System.out.println("from "+selectedIssue.getIssueStartLine()+ " till "+selectedIssue.getIssueEndLine());
        System.out.println(selectedIssue.getLinesOfCodeFromSonarQube());
    }//GEN-LAST:event_jListCodeSmellsMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxC;
    private javax.swing.JCheckBox jCheckBoxCpp;
    private javax.swing.JCheckBox jCheckBoxFortran;
    private javax.swing.JCheckBox jCheckBoxOther;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabelIssues;
    private javax.swing.JLabel jLabelIssuesN;
    private javax.swing.JList<Issue> jListCodeSmells;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelFiles;
    private javax.swing.JPanel jPanelLanguages;
    private javax.swing.JPanel jPanelRules;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

    
    /*
    ** Check the file name for the Language
    */
    private boolean isLanguageC(String[] str){
         return (str[str.length-1].equalsIgnoreCase("c") || str[str.length-1].equalsIgnoreCase("h") ||
                 str[str.length-1].equalsIgnoreCase("cu") || str[str.length-1].equalsIgnoreCase("hcu") );
    }
    private boolean isLanguageCpp(String[] str){
         return (str[str.length-1].equalsIgnoreCase("cpp") || str[str.length-1].equalsIgnoreCase("hpp") ||
                    str[str.length-1].equalsIgnoreCase("cc") || str[str.length-1].equalsIgnoreCase("cp") ||
                    str[str.length-1].equalsIgnoreCase("cxx") || str[str.length-1].equalsIgnoreCase("c++") ||
                    str[str.length-1].equalsIgnoreCase("hh") || str[str.length-1].equalsIgnoreCase("h++") ||
                    str[str.length-1].equalsIgnoreCase("hp") || str[str.length-1].equalsIgnoreCase("hxx") );
    }
    private boolean isLanguageFortran77(String[] str){
         return (str[str.length-1].equalsIgnoreCase("f") || str[str.length-1].equalsIgnoreCase("f77")
                        || str[str.length-1].equalsIgnoreCase("for") || str[str.length-1].equalsIgnoreCase("fpp")
                        || str[str.length-1].equalsIgnoreCase("ftn"));
    }
    private boolean isLanguageFortran90(String[] str){
         return (str[str.length-1].equalsIgnoreCase("F90"));
    }

    
    /**
    * Action Listener for the boxes
    */
    private void addActionToCheckLanguage(){
        for(javax.swing.JCheckBox box: group){
            box.addActionListener((ActionEvent e) -> {
                defaultListModel.removeAllElements();
                Collections.sort(project.getprojectReport().getIssuesList());
                for(Issue i: project.getprojectReport().getIssuesList()){
                    String[] str= i.getIssueDirectory().split("\\.");
                    if(jCheckBoxFortran.isSelected() && (isLanguageFortran77(str) || isLanguageFortran90(str)) ){
                        defaultListModel.addElement(i);
                    }
                    if(jCheckBoxC.isSelected() && (isLanguageC(str)) ){
                        defaultListModel.addElement(i);
                    }
                    if(jCheckBoxCpp.isSelected() && (isLanguageCpp(str)) ){
                        defaultListModel.addElement(i);
                    }
                    if(jCheckBoxOther.isSelected() &&
                            (!isLanguageC(str) && !isLanguageCpp(str) && !isLanguageFortran90(str) && !isLanguageFortran77(str)) ){
                        defaultListModel.addElement(i);
                    }
                }
                jLabelIssuesN.setText(defaultListModel.getSize()+"");
            });
        }
    }

    
    /**
     * Populate Panel with filter of Files
    */
    private void addCheckBoxesFile(){
        //get instances in Hash Map and sort
        HashMap<String,Integer> instances=new HashMap<>();
        for(Issue issue: project.getprojectReport().getIssuesList()){
            if(instances.containsKey(issue.getIssueDirectory()))
                instances.replace(issue.getIssueDirectory(), instances.get(issue.getIssueDirectory())+1);
            else
                instances.put(issue.getIssueDirectory(), 1);
        }
        HashMap<String, Integer> sortedInstances= instances.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        
        //add All radio button
        groupRadioFiles = new ButtonGroup();
        JRadioButton rb1= new JRadioButton("All", true);
        rb1.setFont(jCheckBoxCpp.getFont());
        rb1.addActionListener((ActionEvent e) -> {
            if(rb1.isSelected()){
                defaultListModel.removeAllElements();
                project.getprojectReport().getIssuesList().forEach((i) -> {
                    defaultListModel.addElement(i);
                });
                jLabelIssuesN.setText(defaultListModel.getSize()+"");
            }
        });
        groupRadioFiles.add(rb1);
        jPanelFiles.add(rb1);
        
        //add Files radio button
        for(String str: sortedInstances.keySet()){
            if(sortedInstances.get(str)<10)
                break;
            
            JRadioButton rb;
            String[] name= str.split(":")[1].split("\\.");
            if(name[name.length-1].equalsIgnoreCase("f90") || name[name.length-1].equalsIgnoreCase("f") || name[name.length-1].equalsIgnoreCase("f77")
                            || name[name.length-1].equalsIgnoreCase("for") || name[name.length-1].equalsIgnoreCase("fpp")
                            || name[name.length-1].equalsIgnoreCase("ftn")){
                CodeFile cf= project.getFortranFilesIndexed().get(Integer.parseInt(name[0]));
                rb= new JRadioButton(sortedInstances.get(str)+"  "+cf.file.getAbsolutePath().replace(project.getCredentials().getProjectDirectory(), ""));
            }
            else
                rb= new JRadioButton(sortedInstances.get(str)+"  "+str.split(":")[1]);
            
            rb.setFont(jCheckBoxCpp.getFont());
            rb.addActionListener((ActionEvent e) -> {
                if(rb.isSelected()){
                    String file= rb.getText().split("  ")[1];
                    String[] fileTable = file.split("\\.");
                    
                    if(fileTable[fileTable.length-1].equalsIgnoreCase("f90") || fileTable[fileTable.length-1].equalsIgnoreCase("f") 
                            || fileTable[fileTable.length-1].equalsIgnoreCase("f77") || fileTable[fileTable.length-1].equalsIgnoreCase("for")
                            || fileTable[fileTable.length-1].equalsIgnoreCase("fpp") || fileTable[fileTable.length-1].equalsIgnoreCase("ftn")){
                        
                        for(Integer key: project.getFortranFilesIndexed().keySet()){
                            if(project.getFortranFilesIndexed().get(key).file.getAbsolutePath().endsWith(file)){
                                file= key + "." + project.getFortranFilesIndexed().get(key).file.getName().split("\\.")
                                                [project.getFortranFilesIndexed().get(key).file.getName().split("\\.").length-1];
                            }
                        }
                    }
                    defaultListModel.removeAllElements();
                    int k=0;
                    for(Issue i: project.getprojectReport().getIssuesList()){
                        if(i.getIssueDirectory().split(":")[1].equals(file)){
                            defaultListModel.addElement(i);
                            k++;
                        }
                    }
                    jLabelIssuesN.setText(k+"");
                }
            });
            groupRadioFiles.add(rb);
            jPanelFiles.add(rb);
        }
    }
    
    
    /**
     * Populate Panel with filter of Files
    */
    private void addCheckBoxesRules() {
        //get instances in Hash Map and sort
        HashMap<String,Integer> instances=new HashMap<>();
        
        for(Issue issue: project.getprojectReport().getIssuesList()){
            if(instances.containsKey(issue.getIssueRule()))
                instances.replace(issue.getIssueRule(), instances.get(issue.getIssueRule())+1);
            else
                instances.put(issue.getIssueRule(), 1);
        }
        
        HashMap<String, Integer> sortedInstances= instances.entrySet()
        .stream()
        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
        .collect(
            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
        
        //add All radio button
        ButtonGroup groupRadioRules = new ButtonGroup();
        JRadioButton rb1= new JRadioButton("All", true);
        rb1.setFont(jCheckBoxCpp.getFont());
        rb1.addActionListener((ActionEvent e) -> {
            if(rb1.isSelected()){
                defaultListModel.removeAllElements();
                project.getprojectReport().getIssuesList().forEach((i) -> {
                    defaultListModel.addElement(i);
                });
                jLabelIssuesN.setText(defaultListModel.getSize()+"");
            }
        });
        groupRadioRules.add(rb1);
        jPanelRules.add(rb1);
        
        //add Files radio button
        for(String str: sortedInstances.keySet()){
            if(sortedInstances.get(str)<20)
                break;
            JRadioButton rb= new JRadioButton(sortedInstances.get(str)+"  "+str);
            rb.setFont(jCheckBoxCpp.getFont());
            rb.addActionListener((ActionEvent e) -> {
                if(rb.isSelected()){
                    String rule = rb.getText().split("  ")[1];
                    defaultListModel.removeAllElements();
                    int k=0;
                    for(Issue i: project.getprojectReport().getIssuesList()){
                        if(i.getIssueRule().equals(rule)){
                            defaultListModel.addElement(i);
                            k++;
                        }
                    }
                    jLabelIssuesN.setText(defaultListModel.size()+"");
                }
            });
            groupRadioRules.add(rb);
            jPanelRules.add(rb);
        }
    }

    
    private ArrayList<String> parseNewFiles() {
        ArrayList<String> newFiles=new ArrayList<>();
        try {
            BufferedReader br=new BufferedReader(new FileReader("filesChangAdd-qr.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.contains("Add> ")){
                    newFiles.add(line.split(":")[1].replace(",", "."));
                }
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(JPanelIssues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newFiles;
    }
    private ArrayList<String> parseChangedFiles() {
        ArrayList<String> changedFiles=new ArrayList<>();
        try {
            BufferedReader br=new BufferedReader(new FileReader("filesChangAdd-qr.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.contains("Change> ")){
                    changedFiles.add(line.split(":")[1].replace(",", "."));
                }
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(JPanelIssues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return changedFiles;
    }
}
