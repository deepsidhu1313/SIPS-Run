/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sips.run;

import in.co.s13.sips.run.tools.Util;
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
                    manifest.put("LIB", new JSONArray());
                    manifest.put("ATTCH", new JSONArray());
                    manifest.put("ARGS", new JSONArray());
                    manifest.put("JVMARGS", new JSONArray());
                    manifest.put("OUTPUTFREQUENCY", 100);
                    manifest.put("SCHEDULER", "chunk");
                    Util.write("manifest.json", manifest.toString(4));
                    System.exit(0);
                }

                if (arguments.contains("--manifest")) {
                    int index = arguments.indexOf("--manifest");
                    manifestFile = arguments.get(index + 1);
                    manifestJSON = Util.readJSONFile(manifestFile);
                }

            } else {

            }
        }
    }

}
