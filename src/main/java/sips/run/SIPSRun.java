/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sips.run;

import in.co.s13.sips.run.settings.GlobalValues;
import in.co.s13.sips.run.tools.GetJavaFiles;
import in.co.s13.sips.run.tools.ParseJavaFile;
import in.co.s13.sips.run.tools.PrepareFiles;
import in.co.s13.sips.run.tools.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class SIPSRun {

    public static File MANIFEST_FILE;
    public static ArrayList<String> javaFiles;
    public static ExecutorService levelDetectorExecutor;

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        SIPSRun sipsRun = new SIPSRun(args);
    }

    public SIPSRun(String[] args) throws InterruptedException {
        System.out.println("SIPS-Run");
        String manifestFile = "manifest.json";
        JSONObject manifestJSON = null;
        if (args.length > 0) {
            ArrayList<String> arguments = new ArrayList<>();
            Collections.addAll(arguments, args);

            if (arguments.size() > 0) {
                if (arguments.contains("--generate-manifest")) {
                    generateManifest("");
                    System.exit(0);
                }

                if (arguments.contains("--create")) {
                    createProject(arguments.get(arguments.indexOf("--create") + 1));
                    System.exit(0);
                }

                if (arguments.contains("--manifest")) {
                    int index = arguments.indexOf("--manifest");
                    manifestFile = arguments.get(index + 1);
                    manifestJSON = Util.readJSONFile(manifestFile);
                } else {
                    if (!new File(manifestFile).exists()) {
                        System.err.println("manifest.json Not Found");
                        System.exit(1);
                    }
                    manifestJSON = Util.readJSONFile(manifestFile);

                }
                GlobalValues.MANIFEST_JSON = manifestJSON;

            }
        } else {
            if (!new File(manifestFile).exists()) {
                System.err.println("manifest.json Not Found");
                System.exit(1);
            }
            manifestJSON = Util.readJSONFile(manifestFile);
            GlobalValues.MANIFEST_JSON = manifestJSON;
        }
        MANIFEST_FILE = new File(new File(manifestFile).getAbsolutePath());
        GetJavaFiles getJavaFiles = new GetJavaFiles();
        javaFiles = getJavaFiles.getJavaFiles(new File(MANIFEST_FILE.getParentFile(), "src").getAbsolutePath());
        System.out.println("List of Java Files:\n" + javaFiles);

        ExecutorService parserExecutor = Executors.newFixedThreadPool(javaFiles.size());
        levelDetectorExecutor = Executors.newFixedThreadPool(javaFiles.size());
        for (int i = 0; i < javaFiles.size(); i++) {
            String get = javaFiles.get(i);
            ParseJavaFile parseJavaFile = new ParseJavaFile(new File(get), manifestJSON.getString("PROJECT", new File(".").getName()), args);
            parserExecutor.submit(parseJavaFile);
        }
        parserExecutor.shutdown();
        parserExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        levelDetectorExecutor.shutdown();
        levelDetectorExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        Util.copyFolder(new File(MANIFEST_FILE.getParentFile().getAbsolutePath() + "/src"), (new File(new File(MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build"), ("src/"))));
        Util.copyFolder(new File(MANIFEST_FILE.getParentFile().getAbsolutePath() + "/lib"), (new File(new File(MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build"), ("lib/"))));
        javaFiles = getJavaFiles.getJavaFiles(new File(MANIFEST_FILE.getParentFile(), ".build/src").getAbsolutePath());
        System.out.println("List of Java Files:\n" + javaFiles);
        for (int i = 0; i < javaFiles.size(); i++) {
            String get = javaFiles.get(i);
            PrepareFiles prepareFile = new PrepareFiles(PrepareFiles.MODE.COMMENT, new File(get));
        }
    }

    public static void generateManifest(String projectName) {
        JSONObject manifest = new JSONObject();
        manifest.put("PROJECT", projectName);
        manifest.put("MAIN", "" + projectName.replaceAll(System.lineSeparator(), ".").replaceAll("-", "_"));
        JSONArray libs = new JSONArray();
        libs.put("lib1.jar");
        manifest.put("LIB", libs);
        manifest.put("ATTCH", new JSONArray());
        manifest.put("ARGS", new JSONArray());
        manifest.put("JVMARGS", new JSONArray());
        manifest.put("OUTPUTFREQUENCY", 100);
        JSONObject scheduler = new JSONObject();
        scheduler.put("Name", "chunk");
        scheduler.put("MaxNodes", "4");
        manifest.put("SCHEDULER", scheduler);
        manifest.put("HOST", "127.0.0.1");
        Util.write(((projectName.trim().length() > 0 ? projectName + "/" : ""))
                + "manifest.json", manifest.toString(4)
        );
        System.out.println("Put SIPS lib1 jar file in the directory to provide SIPS support");

    }

    public static void createProject(String name) {
        File projectDir = new File(name);
        if (projectDir.exists()) {
            System.out.println("Project " + name + "already exist in the folder");
            return;
        }
        projectDir.mkdirs();
        File libDir = new File(name + "/lib");
        libDir.mkdirs();
        File srcDir = new File(name + "/src");
        srcDir.mkdirs();
        generateManifest(name);
    }

}
