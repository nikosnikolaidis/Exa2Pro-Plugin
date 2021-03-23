/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvControlers;

/**
 *
 * @author Nikos
 */
public class ArtifactsForecastingFiles {
    private String class_name;
    private String project_version;
    private String class_id;
    private String code_smells;
    private String ncloc;
    private String complexity;
    private String duplicated_blocks;
    private String sqale_index;
    private String reliability_remediation_effort;
    private String security_remediation_effort;
    private String total_principal;

    public ArtifactsForecastingFiles(String[] data) {
        this.class_name= data[0];
        this.project_version= data[1];
        this.class_id= data[2];
        this.code_smells= data[3];
        this.ncloc= data[4];
        this.complexity= data[5];
        this.duplicated_blocks= data[6];
        this.sqale_index= data[7];
        this.reliability_remediation_effort= data[8];
        this.security_remediation_effort= data[9];
        this.total_principal= data[10];
    }

    public String getClass_name() {
        return class_name;
    }
    public String getProject_version() {
        return project_version;
    }
    public String getClass_id() {
        return class_id;
    }
    public String getCode_smells() {
        return code_smells;
    }
    public String getNcloc() {
        return ncloc;
    }
    public String getComplexity() {
        return complexity;
    }
    public String getDuplicated_blocks() {
        return duplicated_blocks;
    }
    public String getSqale_index() {
        return sqale_index;
    }
    public String getReliability_remediation_effort() {
        return reliability_remediation_effort;
    }
    public String getSecurity_remediation_effort() {
        return security_remediation_effort;
    }
    public String getTotal_principal() {
        return total_principal;
    }
}
