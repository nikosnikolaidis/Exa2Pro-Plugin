package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtraParseUtils {
	
	public ExtraParseUtils() {
		
	}

	private ArrayList<String> fileToArray(String filePath) {
		File parsedFile;
		Scanner input;
		try {
			parsedFile = new File(filePath);
			input = new Scanner(new FileInputStream(parsedFile));
		} catch (Exception exc) {
			System.out.println("File not found!");
			return null;
		}
		
		ArrayList<String> fileLines = new ArrayList<String>();
	
		while(input.hasNextLine()) {
			fileLines.add(input.nextLine());
		}
		
		input.close();
		return fileLines;
	}
	
	public void convertSimpleIfsToLine(String filePath) {
		System.out.println("******8fule "+filePath);
		ArrayList<String> lines = fileToArray(filePath);
		if(lines==null) {
			return;
		}
		
		//ArrayList<String> newLines = new ArrayList<String>();
		int dummyVar = 0;
		
		boolean flag = false;
		int idx = 0;
		int beginIf = 0;
		int endIf = 0;
		
		for(int i=0; i<lines.size(); i++) {
			//String line = lines.get(i);
			
			if(flag) {
				String[] split = lines.get(i).split("#");
				//System.out.println(split[split.length-1].replace(";", ""));
				try {
					if(endIf<Integer.valueOf(split[split.length-1].replace(";", ""))) {
						
						List<String> newLns = new ArrayList<String>();
						for(int r=idx+2; r<i; r++) {
							String[] temp = lines.get(r).split("#");
							String newLine = lines.get(r).replace("#"+temp[temp.length-1], "#");//+(beginIf+1)+";");
							newLns.add(newLine);
							lines.remove(idx+2);
						}

						String newDummyVar = "Usage#dummyVar"+dummyVar+"#";
						for(String ln : newLns) {
							lines.add(idx+2, ln+""+endIf+";");
						}
						lines.add(idx+2, newDummyVar+""+endIf+";");
						
						for(int k=endIf-1; k>beginIf; k--) {
							lines.add(idx+2, newDummyVar+""+k+";");
						}
						
						for(String ln : newLns) {
							lines.add(idx+2, ln+""+beginIf+";");
						}
						lines.add(idx+2, newDummyVar+""+beginIf+";");
						
						flag = false;
						dummyVar++;
					}	
			
					/*
					if(endIf<Integer.valueOf(split[split.length-1].replace(";", ""))) {
						for(int r=idx; r<i; r++) {
							String[] temp = lines.get(r).split("#");
							String newLine = lines.get(r).replace("#"+temp[temp.length-1], "#"+(beginIf+1)+";");
							lines.set(r, newLine);//.replace("#"+temp[temp.length-1], "#"+beginIf);
							lines.set(r, newLine+"  ***TEST DEBUG");
						}
						//System.out.println("Grouped lines "+beginIf +" to "+endIf);
						flag = false;
						tmpVar++;
					}					
					 */
				}catch(Exception e) {
					flag = false;
				}
			}
			try{
				if(!lines.get(i).startsWith("BEGIN_IF#")) {
					continue;
				}
				
				if(!lines.get(i+1).startsWith("END_IF#")) {
					i++;
					continue;
				}
				if(flag) {
					//System.out.println("New inner IF inside "+beginIf +" and "+endIf);
				}
				
				idx = i;
				flag = true;
				String[] split = lines.get(i).split("#");
				beginIf = Integer.valueOf(split[split.length-1].replace(";", ""));//-1; //***TODO
				split = lines.get(i+1).split("#");
				endIf = Integer.valueOf(split[split.length-1].replace(";", ""));
				
			}catch(Exception e) {
				return;
			}
			
		}
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		System.out.println("****************************************************************************");
		writeFile(lines, filePath);
	}
	
	
	private void writeFile(ArrayList<String> lines, String path) {
            FileWriter writer;
            try {
                writer = new FileWriter(path);
                for (String str : lines) {
                    writer.write(str + System.lineSeparator());
                }
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(ExtraParseUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
}
