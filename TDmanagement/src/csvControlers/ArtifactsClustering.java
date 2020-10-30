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
public class ArtifactsClustering {
    
    private String method;
    private String attributes;
    private String invocations;
    
    public ArtifactsClustering(String[] data){
        this.method= data[0];
        this.attributes= data[1];
        this.invocations= data[2];
    }

    public String getMethod() {
        return method;
    }

    public String getAttributes() {
        return attributes;
    }
    
    public String getInvocations() {
        return invocations;
    }
}
