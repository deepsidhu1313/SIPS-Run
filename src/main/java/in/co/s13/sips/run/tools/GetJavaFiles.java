/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.sips.run.tools;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author nika
 */
public class GetJavaFiles {

    public GetJavaFiles() {

    }

    public ArrayList<String> getJavaFiles(String dir) {
        ArrayList<String> files = new ArrayList<>();
        File folder = new File(dir);
        File file[] = folder.listFiles();
        for (int i = 0; i < file.length; i++) {
            File file1 = file[i];
            if (file1.isDirectory()) {
                files.addAll(getJavaFiles(file1.getAbsolutePath()));
            } else {
                if (file1.getName().toLowerCase().endsWith(".java")) {
                    files.add(file1.getAbsolutePath());
                }
            }
        }
        return files;
    }
}
