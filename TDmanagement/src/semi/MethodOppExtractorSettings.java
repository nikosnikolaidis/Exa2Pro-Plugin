package semi;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Antonis Gkortzis (s2583070, antonis.gkortzis@gmail.com)
 */
public class MethodOppExtractorSettings extends javax.swing.JFrame {
    private MethodOppExtractor parentFrame;
    private double max_dif;
    private double min_overlap;
    private String cohesion_metric;
    private double sig_diff;
    
    public void setParentFrame(MethodOppExtractor parentFrame){
        this.parentFrame = parentFrame;
    }
    
    public double getMaxSizeDifference() {
        return Double.parseDouble(this.maxDifferenceInSIze.getText());
    }
    
    public double getMinOverlap() {
        return Double.parseDouble(this.minOverlapTextField.getText());
    }
    
    public String getCohesionMetric() {
        return (String)this.cohesionMetricComboBox.getSelectedItem();
    }
    
    public int getCohesionMetricIndex() {
        return this.cohesionMetricComboBox.getSelectedIndex();
    }
    
    
    public double getSignifficantDiff() {
        return Double.parseDouble(this.signifficantDiffTextField.getText());
    }
    /**
     * Creates new form Settings
     */
    public MethodOppExtractorSettings() {
        super("Method opportunities extractor Settings");
        initComponents();
         //setting the default values to each setting
        setDefaultValues();
        updateComponents();
        this.minOverlapTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                verifyMinOverlapInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verifyMinOverlapInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                verifyMinOverlapInput();
            }
            
            });
        this.signifficantDiffTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                verifySigDifferenceInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verifySigDifferenceInput(); 
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                verifySigDifferenceInput();
            }
        });
        
        this.maxDifferenceInSIze.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                verifyMaxDifferenceInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                verifyMaxDifferenceInput(); 
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                verifyMaxDifferenceInput();
            }
        });
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        minOverlapTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cohesionMetricComboBox = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        signifficantDiffTextField = new javax.swing.JTextField();
        applyClusteringButton = new javax.swing.JButton();
        restoreDefaultsButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        maxDifferenceInSIze = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel2.setText("Maximum difference in size [percentage]");

        jLabel3.setText("Minimum overlap between opportunities");

        minOverlapTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        minOverlapTextField.setText("0.1");
        minOverlapTextField.setName(""); // NOI18N

        jLabel4.setText("[0.1 ... 1.0]");

        jLabel5.setText("Cohesion metric");

        cohesionMetricComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "COB", "LCOM1", "LCOM2", "LCOM3", "LCOM4", "LCOM5", "TCC", "LCC", "DCD", "DCI", "CC", "COH", "SCOM", "LSCC" }));
        cohesionMetricComboBox.setSelectedItem("lcom2");

        jLabel7.setText("Signifficant difference");

        signifficantDiffTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        signifficantDiffTextField.setText("0.05");
        signifficantDiffTextField.setPreferredSize(new java.awt.Dimension(22, 20));
        signifficantDiffTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signifficantDiffTextFieldActionPerformed(evt);
            }
        });

        applyClusteringButton.setText("Apply grouping");
        applyClusteringButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyClusteringButtonActionPerformed(evt);
            }
        });

        restoreDefaultsButton.setText("Restore defaults");
        restoreDefaultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreDefaultsButtonActionPerformed(evt);
            }
        });

        jLabel8.setText("[0.01 ... 0.1]");

        maxDifferenceInSIze.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        maxDifferenceInSIze.setText("0.2");
        maxDifferenceInSIze.setName(""); // NOI18N

        jLabel6.setText("[0.1 ... 1.0]");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(signifficantDiffTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(cohesionMetricComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxDifferenceInSIze, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(minOverlapTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(0, 11, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(applyClusteringButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(restoreDefaultsButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(maxDifferenceInSIze, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minOverlapTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cohesionMetricComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(signifficantDiffTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyClusteringButton)
                    .addComponent(restoreDefaultsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        updateComponents();
    }//GEN-LAST:event_formWindowOpened

    private void restoreDefaultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreDefaultsButtonActionPerformed
        // TODO add your handling code here:
        setDefaultValues();
        updateComponents();
    }//GEN-LAST:event_restoreDefaultsButtonActionPerformed

    private void applyClusteringButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyClusteringButtonActionPerformed
        // TODO add your handling code here:
//        this.max_dif = (int)this.maxSizeDiffSpinner.getValue();
        this.max_dif = Double.parseDouble(this.maxDifferenceInSIze.getText());
        this.min_overlap = Double.parseDouble(this.minOverlapTextField.getText());
        this.cohesion_metric = (String)this.cohesionMetricComboBox.getSelectedItem();
        //        this.ident_accuracy = (String)this.identificationAccurComboBox.getSelectedItem();
        this.sig_diff = Double.parseDouble(this.signifficantDiffTextField.getText());
        parentFrame.clusterOpportunitiesWithParameters();
    }//GEN-LAST:event_applyClusteringButtonActionPerformed

    private void signifficantDiffTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signifficantDiffTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_signifficantDiffTextFieldActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyClusteringButton;
    private javax.swing.JComboBox cohesionMetricComboBox;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField maxDifferenceInSIze;
    private javax.swing.JTextField minOverlapTextField;
    private javax.swing.JButton restoreDefaultsButton;
    private javax.swing.JTextField signifficantDiffTextField;
    // End of variables declaration//GEN-END:variables

    private void verifyMinOverlapInput() {
        boolean flag = false; 
        try {
            double input = Double.parseDouble(this.minOverlapTextField.getText());
            flag = input >= 0 && input <= 1;
        } catch (Exception ex) {
            flag = false;
        }
        
        if(flag){
            this.applyClusteringButton.setEnabled(true);
            this.minOverlapTextField.setForeground(Color.black);
        } else {
            this.applyClusteringButton.setEnabled(false);
            this.minOverlapTextField.setForeground(Color.red);
        }
    }
    
    private void verifySigDifferenceInput() {
        boolean flag = false; 
        try {
            double input = Double.parseDouble(this.signifficantDiffTextField.getText());
            flag = input >= 0.01 && input <= 0.1;
        } catch (Exception ex) {
            flag = false;
        }
        
        if(flag) {
            this.applyClusteringButton.setEnabled(true);
            this.signifficantDiffTextField.setForeground(Color.black);
        } else {
            this.applyClusteringButton.setEnabled(false);
            this.signifficantDiffTextField.setForeground(Color.red);
        }
    }
    
    private void verifyMaxDifferenceInput() {
        boolean flag = false; 
        try {
            double input = Double.parseDouble(this.maxDifferenceInSIze.getText());
            flag = input >= 0.1 && input <= 1;
        } catch (Exception ex) {
            flag = false;
        }
        
        if(flag){
            this.applyClusteringButton.setEnabled(true);
            this.maxDifferenceInSIze.setForeground(Color.black);
        } else {
            this.applyClusteringButton.setEnabled(false);
            this.maxDifferenceInSIze.setForeground(Color.red);
        }
    }

    private void setDefaultValues() {
        this.max_dif = 0.2;
        this.min_overlap = 0.1;
        this.cohesion_metric = "LCOM2";
        this.sig_diff = 0.01;
    }

    private void updateComponents() {
//        this.maxSizeDiffSpinner.setValue(this.max_dif);
        this.maxDifferenceInSIze.setText(String.valueOf(this.max_dif));
        this.minOverlapTextField.setText(String.valueOf(this.min_overlap));
        this.cohesionMetricComboBox.setSelectedItem(this.cohesion_metric);
//        this.identificationAccurComboBox.setSelectedItem(this.ident_accuracy);
        this.signifficantDiffTextField.setText(String.valueOf(this.sig_diff));
    }
}
