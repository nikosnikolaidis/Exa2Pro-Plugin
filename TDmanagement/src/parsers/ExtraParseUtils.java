package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private ArrayList<String> fileToArray(String filePath) throws FileNotFoundException {
        File parsedFile;
        Scanner input;
        parsedFile = new File(filePath);
        input = new Scanner(new FileInputStream(parsedFile));

        ArrayList<String> fileLines = new ArrayList<String>();

        while (input.hasNextLine()) {
            fileLines.add(input.nextLine());
        }

        input.close();
        return fileLines;
    }

    public void convertSimpleIfsToLine(String filePath) throws FileNotFoundException {
        ArrayList<String> lines = fileToArray(filePath);
        if (lines == null) {
            return;
        }

        //ArrayList<String> newLines = new ArrayList<String>();
        boolean flag = false;
        int idx = 0;
        int beginIf = 0;
        int endIf = 0;

        for (int i = 0; i < lines.size(); i++) {
            //String line = lines.get(i);

            if (flag) {
                String[] split = lines.get(i).split("#");
                //System.out.println(split[split.length-1].replace(";", ""));
                try {
                    if (endIf < Integer.valueOf(split[split.length - 1].replace(";", ""))) {
                        for (int r = idx; r < i; r++) {
                            String[] temp = lines.get(r).split("#");
                            String newLine = lines.get(r).replace("#" + temp[temp.length - 1], "#" + (beginIf + 1) + ";");
                            lines.set(r, newLine);//.replace("#"+temp[temp.length-1], "#"+beginIf);
                        }
                        System.out.println("Grouped lines "+beginIf +" to "+endIf);
                        flag = false;
                    }
                } catch (Exception e) {
                    flag = false;
                }
            }

            if (!lines.get(i).startsWith("BEGIN_IF#")) {
                continue;
            }

            if (!lines.get(i + 1).startsWith("END_IF#")) {
                i++;
                continue;
            }
            if (flag) {
                //System.out.println("New inner IF inside "+beginIf +" and "+endIf);
            }

            idx = i;
            flag = true;
            String[] split = lines.get(i).split("#");
            beginIf = Integer.valueOf(split[split.length - 1].replace(";", ""));
            split = lines.get(i + 1).split("#");
            endIf = Integer.valueOf(split[split.length - 1].replace(";", ""));

        }

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
