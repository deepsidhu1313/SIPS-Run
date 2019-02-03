/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sips.run;

import in.co.s13.sips.run.tools.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

/**
 *
 * @author nika
 */
public class SimulateProject {

//    JSONObject manifestJson;
    File buildDir;

    public SimulateProject() {
//        this.manifestJson = manifestJson;
        buildDir = new File(SIPSRun.MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build/");

    }

    public void simulate() {
        ProcessBuilder pb = null;
        Process p = null;
        boolean success = true;
        String cmd2 = "";
        Long startTime = System.currentTimeMillis();
        String loc = buildDir.getAbsolutePath();
        if (Util.isWindows()) {
            String scriptloc = "" + loc + "/simulate.bat";
            String cmd[] = {scriptloc, loc};
            pb = new ProcessBuilder(cmd);
            //  pb.directory(new File(controlpanel.settings.PWD));

        } else if (Util.isUnix()) {
            String scriptloc = "" + loc + "/simulate.sh";
            String cmd[] = {"/bin/bash", scriptloc, loc};
            pb = new ProcessBuilder(cmd);
            //pb.directory(new File(controlpanel.settings.PWD));
        }

        try {
            p = pb.start();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");

            String s = null;
            String output = "";
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);

                output += "\n" + s;

            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                success = false;
                System.out.println(s);
                output += "\n" + s;
            }

            ////settings.outPrintln("Process executed");
            if (!success) {

                System.out.println("\n Process " + " Simulation Failed");

            } else {
                System.out.println("\n Process " + " Simulation Succeded");

            }
            int exitValue = p.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
            Long stopTime = System.currentTimeMillis();
        } catch (IOException ex) {
            Logger.getLogger(SimulateProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimulateProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateScripts(String name, JSONArray arg, String main, JSONArray jvmargs) {
        File f = new File(buildDir.getAbsolutePath() + "/build.xml");
        {
//            if (f.exists()) {
//                return;
//            }

            try (PrintStream out = new PrintStream(f)) {
                StringBuilder ARGS = new StringBuilder();
                StringBuilder JVMARGS = new StringBuilder();
                for (int i = 0; i < arg.length(); i++) {
                    String arg1 = arg.getString(i);
                    ARGS.append("           <arg line=\"");
                    ARGS.append(arg1);
                    ARGS.append("\"/>\n");
                }

                for (int i = 0; i < jvmargs.length(); i++) {
                    String arg1 = jvmargs.getString(i);
                    JVMARGS.append("         <jvmarg value=\"");
                    JVMARGS.append(arg1);
                    JVMARGS.append("\"/>\n");
                }

                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                        + "<project default=\"run\" basedir=\".\" name=\"" + name + "\">\n"
                        + "  <!--this file was created by Eclipse Runnable JAR Export Wizard-->\n"
                        + "  <!--ANT 1.7 is required                                        -->\n"
                        + "\n"
                        + "<target name=\"clean\">\n"
                        + "        <delete dir=\"build\"/>\n"
                        + "    </target>\n"
                        + "  <target name=\"compile\">\n"
                        + "<mkdir dir=\"build\"/>"
                        + "    <javac srcdir=\"src\" destdir=\"build\" includes=\"**/*.java\" target=\"1.10\">\n"
                        + "\n"
                        + "        <classpath refid=\"classpath.base\" />\n"
                        + "    </javac>\n"
                        + "\n"
                        + "  </target>\n"
                        + "  <target name=\"run\" depends=\"compile\">\n"
                        + "      <java fork=\"true\" failonerror=\"yes\" classname=\"" + main.trim() + "\">\n"
                        + JVMARGS.toString()
                        + ARGS.toString()
                        + "        <classpath refid=\"classpath.base\" />\n"
                        + "<classpath>\n"
                        + "        <pathelement path=\"${classpath.base}\"/>\n"
                        + "        <pathelement location=\"build\"/>\n"
                        + "    </classpath>      </java>\n"
                        + "   </target>"
                        + "  <!-- Libraries on which your code depends -->\n"
                        + "  <path id=\"classpath.base\">                                                                                                                           \n"
                        + "     <fileset dir=\"lib\">                                                                                                                          \n"
                        + "         <include name=\"**/*.jar\" />                                                                                                          \n"
                        + "     </fileset>                                                                                                                                   \n"
                        + "</path>  \n"
                        + "</project>");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SimulateProject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (Util.isUnix()) {
            File f3 = new File(buildDir.getAbsolutePath() + "/simulate.sh");
            if (f3.exists()) {
                f3.delete();
            }

            try (PrintStream out2 = new PrintStream(f3)) {
                out2.println("#!/bin/bash ");
                out2.println("PATH=/bin:/usr/bin:/usr/local/bin");
                out2.println("WORK=${PWD}/");
                out2.println("cd  \"${1}/\"");
                out2.println("ant");
                out2.println("ant clean");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SimulateProject.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (Util.isWindows()) {
            File f4 = new File(buildDir.getAbsolutePath() + "/simulate.bat");
            if (f4.exists()) {
                f4.delete();
            }
            {
                try (PrintStream out = new PrintStream(f4)) {
                    out.println("@echo off ");
//                    out.println("set PFRAMEWORK_HOME=%~dp0");
                    out.println("set arg1=%~1 ");
//                    out.println("set arg2=%2 ");
//                    out.println("set arg3=%3 ");
//                    out.println("set arg4=%4 ");
                    //  out.println("java -jar lib1.jar 0 %arg4%");
                    out.println("cd /d %arg1%");
                    out.println("CALL  ant ");
                    out.println("CALL  ant clean");
                    // out.println("java -cp .;%PFRAMEWORK_HOME%lib1.jar %arg3%");
                    //  out.println(" cd %PFRAMEWORK_HOME%");
                    //  out.println("java -jar lib1.jar 1 %arg4%");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SimulateProject.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }
}
