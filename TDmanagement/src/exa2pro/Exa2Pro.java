/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exa2pro;

//import panels_frames.HomeFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nikos
 */
public class Exa2Pro {

    private static String OS = System.getProperty("os.name").toLowerCase();
    public static ArrayList<ProjectCredentials> projecCredentialstList = new ArrayList<>();
    public static String iCodePath;
    public static String sonarPath;
    public static String sonarURL;
    public static String sonarScannerPath;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        getProjetsFromFile();
        getSettingFromFile();

        if(isWindows())
            sonarScannerPath= System.getProperty("user.dir") + "\\sonar-scanner-4.2-windows\\bin\\sonar-scanner.bat";
        else
            sonarScannerPath= System.getProperty("user.dir")+ "/sonar-scanner-4.2-linux/bin/sonar-scanner";
        
//        HomeFrame homeFrame = new HomeFrame();
//        homeFrame.setVisible(true);
    }

    //find the OS of the machine
    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    // Get saved Projects from file
    public static void getProjetsFromFile() {
        FileInputStream fi;
        try {
            fi = new FileInputStream(new File("myProjects.txt"));

            ObjectInputStream oi = new ObjectInputStream(fi);

            projecCredentialstList = (ArrayList<ProjectCredentials>) oi.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("exa2pro.Exa2Pro.getProjetsFromFile()");
        }
    }

    // Get setting from file
    public static void getSettingFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("mySettings.txt"))) {
            String line;
            boolean urlFound=false;
            while ((line = br.readLine()) != null) {
                if (line.contains("sonar.host.url=")) {
                    sonarURL = line.replace("sonar.host.url=", "");
                    urlFound=true;
                } else if (line.contains("sonar.icode.path=")) {
                    iCodePath = line.replace("sonar.icode.path=", "");
                } else if (line.contains("sonar.path=")){
                    sonarPath = line.replace("sonar.path=", "");
                }
            }
            if(!urlFound)
                sonarURL="http://localhost:9000";
        } catch (IOException ex) {
            System.out.println("exa2pro.Exa2Pro.getSettingFromFile()");
            sonarURL="http://localhost:9000";
        }
    }

    /**
     * Save settings
     * @param url the SonarQube url
     * @param icode the icode path
     */
    public static void saveSettingsToFile(String url, String icode, String sonarP) {
        try {
            //save sonarqube url/path and icode path to my setting file
            BufferedWriter writer = new BufferedWriter(new FileWriter("mySettings.txt"));
            writer.write("sonar.host.url=" + url + System.lineSeparator());
            writer.append("sonar.path=" + sonarP + System.lineSeparator());
            writer.append("sonar.icode.path=" + icode + System.lineSeparator());
            writer.close();

            //save sonarqube url to conf\sonar-scanner.properties
            if (isWindows()) {
                List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(System.getProperty("user.dir")
                        +"\\sonar-scanner-4.2-windows\\conf\\sonar-scanner.properties"), StandardCharsets.UTF_8));
                for (int i = 0; i < fileContent.size(); i++) {
                    if (fileContent.get(i).contains("sonar.host.url=")) {
                        fileContent.set(i, "sonar.host.url="+url);
                        break;
                    }
                }
                Files.write(Paths.get(System.getProperty("user.dir")
                        +"\\sonar-scanner-4.2-windows\\conf\\sonar-scanner.properties")
                        , fileContent, StandardCharsets.UTF_8);
            }
            else{
                List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(System.getProperty("user.dir")
                        +"/sonar-scanner-4.2-linux/conf/sonar-scanner.properties"), StandardCharsets.UTF_8));
                for (int i = 0; i < fileContent.size(); i++) {
                    if (fileContent.get(i).contains("sonar.host.url=")) {
                        fileContent.set(i, "sonar.host.url="+url);
                        break;
                    }
                }
                Files.write(Paths.get(System.getProperty("user.dir")
                        +"/sonar-scanner-4.2-linux/conf/sonar-scanner.properties")
                        , fileContent, StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            Logger.getLogger(Exa2Pro.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
