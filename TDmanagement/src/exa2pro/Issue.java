/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nikos
 */
public class Issue implements Serializable, Comparable<Issue>{
    private String issueRule;
    private String issueName;
    private String issueSeverity;
    private String issueDebt;
    private String issueType;
    private String issueDirectory;
    private String issueStartLine;
    private String issueEndLine;

    public Issue(String issueRule, String issueName, String issueSeverity, String issueDebt, String issueType, 
            String issueDirectory, String issueStartLine, String issueEndLine) {
        this.issueRule= issueRule;
        this.issueName = issueName;
        this.issueSeverity = issueSeverity;
        this.issueDebt = issueDebt;
        this.issueType = issueType;
        this.issueDirectory = issueDirectory;
        this.issueStartLine = issueStartLine;
        this.issueEndLine = issueEndLine;
    }
    
    /**
     * Return the raw Lines of Code for this issue from Sonar Qube
     */
    public String getLinesOfCodeFromSonarQube(){
        try {
            URL url = new URL(Exa2Pro.sonarURL+"/api/sources/raw?key="
                    +issueDirectory);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: "+responsecode);
            else{
                Scanner sc = new Scanner(url.openStream());
                String inline="";
                int i=0;
                while(sc.hasNext()){
                    i++;
                    String a=sc.nextLine();
                    if(i== Integer.parseInt(this.issueStartLine)){
                        inline+= a + System.lineSeparator();
                    }
                    if(i== Integer.parseInt(this.issueEndLine)){
                        break;
                    }
                }
                sc.close();
                return inline;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    
    @Override
    public int compareTo(Issue o) {
        String str=issueDebt;
        str = str.replaceAll("[^-?0-9]+", " ");
        String[] strArray= str.trim().split(" ");
        String str1=o.getIssueDebt();
        str1 = str1.replaceAll("[^-?0-9]+", " ");
        String[] str1Array= str1.trim().split(" ");
        if(strArray.length > str1Array.length)
            return -1;
        else if(strArray.length < str1Array.length)
            return 1;
        else{
            for(int i=0; i<strArray.length; i++){
                if( Integer.parseInt(strArray[i]) > Integer.parseInt(str1Array[i]) )
                    return -1;
                else if( Integer.parseInt(strArray[i]) < Integer.parseInt(str1Array[i]) )
                    return 1;
            }
        }
        return 0;
    }
    
    // Getters
    public String getIssueRule(){
        return issueRule;
    }
    public String getIssueName() {
        return issueName;
    }
    public String getIssueSeverity() {
        return issueSeverity;
    }
    public String getIssueDebt() {
        return issueDebt;
    }
    public String getIssueType() {
        return issueType;
    }
    public String getIssueDirectory() {
        return issueDirectory;
    }
    public String getIssueStartLine() {
        return issueStartLine;
    }
    public String getIssueEndLine() {
        return issueEndLine;
    }
}
