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
public class Artifacts {
    
    private String path;
    private int functions;
    private int complexity;
    private int Ncloc;
    private double commentsDensity;
    private int codeSmells;
    private int technicalDebt;
    
    public Artifacts(String[] data){
        this.path= data[0];
        this.functions= Integer.parseInt(data[1]);
        this.complexity= Integer.parseInt(data[2]);
        this.Ncloc= Integer.parseInt(data[3]);
        this.commentsDensity= Double.parseDouble(data[4]);
        this.codeSmells= Integer.parseInt(data[5]);
        this.technicalDebt= Integer.parseInt(data[6]);
    }

    public String getPath() {
        return path;
    }

    public int getFunctions() {
        return functions;
    }

    public int getComplexity() {
        return complexity;
    }

    public int getNcloc() {
        return Ncloc;
    }

    public double getCommentsDensity() {
        return commentsDensity;
    }

    public int getCodeSmells() {
        return codeSmells;
    }

    public int getTechnicalDebt() {
        return technicalDebt;
    }
    
    
}
