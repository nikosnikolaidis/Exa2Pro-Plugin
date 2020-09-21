/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Nikos
 */
public abstract class CodeFile implements Serializable{
    public File file;
    public int fanOut;
    public int lcop;
    public HashSet<String> attributes;
    public HashSet<String> attributesInMethods;
    public HashMap<String, String> methodInvocations;
    public HashMap<String, String> commonBlockDeclaration;
    public HashMap<String, Integer> methodsLOC;
    public HashMap<String, Integer> methodsCC;
    public double cohesion;
    public ArrayList<String> opportunities;
    
    public CodeFile(File file){
        this.file=file;
        methodsLOC= new HashMap<>();
        methodsCC= new HashMap<>();
        methodInvocations= new HashMap<>();
        commonBlockDeclaration= new HashMap<>();
        attributes= new HashSet<>();
        attributesInMethods= new HashSet<>();
    }
    
    public abstract void parse();
    public abstract void calculateCohesion();
    public abstract void calculateOpportunities(boolean fast);
    
}
