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
public class ArtifactsForecastingSystem {

    private int code_smells;
    private int ncloc;
    private int complexity;
    private int duplicated_blocks;
    private int sqale_index;
    private int reliability_remediation_effort;
    private int security_remediation_effort;

    public ArtifactsForecastingSystem(int[] data) {
        this.code_smells = data[0];
        this.ncloc = data[1];
        this.complexity = data[2];
        this.duplicated_blocks = data[3];
        this.sqale_index = data[4];
        this.reliability_remediation_effort = data[5];
        this.security_remediation_effort = data[6];
    }

    public int getCode_smells() {
        return code_smells;
    }
    public int getNcloc() {
        return ncloc;
    }
    public int getComplexity() {
        return complexity;
    }
    public int getDuplicated_blocks() {
        return duplicated_blocks;
    }
    public int getSqale_index() {
        return sqale_index;
    }
    public int getReliability_remediation_effort() {
        return reliability_remediation_effort;
    }
    public int getSecurity_remediation_effort() {
        return security_remediation_effort;
    }
}
