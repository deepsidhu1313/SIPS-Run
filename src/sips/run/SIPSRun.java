/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sips.run;

import in.co.s13.sips.run.settings.GlobalValues;
import in.co.s13.sips.run.tools.GetJavaFiles;
import in.co.s13.sips.run.tools.ParseJavaFile;
import in.co.s13.sips.run.tools.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class SIPSRun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // parse the file
//        File f = new File("MatMul.java");

        if (args.length > 0) {
            ArrayList<String> arguments = new ArrayList<>();
            Collections.addAll(arguments, args);
            String manifestFile;
            JSONObject manifestJSON;
            if (arguments.size() > 0) {
                if (arguments.contains("--generate-manifest")) {
                    JSONObject manifest = new JSONObject();
                    manifest.put("PROJECT", "");
                    manifest.put("MAIN", "");
                    JSONArray libs = new JSONArray();
                    libs.put("lib1.jar");
                    manifest.put("LIB", libs);
                    manifest.put("ATTCH", new JSONArray());
                    manifest.put("ARGS", new JSONArray());
                    manifest.put("JVMARGS", new JSONArray());
                    manifest.put("OUTPUTFREQUENCY", 100);
                    manifest.put("SCHEDULER", "chunk");
                    Util.write("manifest.json", manifest.toString(4));
                    System.out.println("Put SIPS lib1 jar file in the directory to provide SIPS support");
                    System.exit(0);
                }

                if (arguments.contains("--manifest")) {
                    int index = arguments.indexOf("--manifest");
                    manifestFile = arguments.get(index + 1);
                    manifestJSON = Util.readJSONFile(manifestFile);
                } else {
                    manifestJSON = Util.readJSONFile("manifest.json");

                }
                GlobalValues.MANIFEST_JSON = manifestJSON;

            } else {
                manifestJSON = Util.readJSONFile("manifest.json");
                GlobalValues.MANIFEST_JSON = manifestJSON;
            }
            GetJavaFiles getJavaFiles = new GetJavaFiles();
            ArrayList<String> javaFiles = getJavaFiles.getJavaFiles(".");
            for (int i = 0; i < javaFiles.size(); i++) {
                String get = javaFiles.get(i);
                ParseJavaFile parseJavaFile = new ParseJavaFile(new File(get), manifestJSON.getString("PROJECT", new File(".").getName()), args);
            }
        }
    }

}
