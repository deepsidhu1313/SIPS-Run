/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sips.run;

import in.co.s13.sips.run.tools.Util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class SimulateProject {

    JSONObject manifestJson;
    File buildDir;

    public SimulateProject(JSONObject manifestJson) {
        this.manifestJson = manifestJson;
        buildDir = new File(SIPSRun.MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build/");

    }

    public void simulate() {

    }

    public void copyFiles() {
        File files[] = SIPSRun.MANIFEST_FILE.getParentFile().listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (!file.getName().equalsIgnoreCase(".build") && file.isDirectory()) {
                Util.copyFolder(file, buildDir);
            } else if (!file.getName().equalsIgnoreCase(".build") && file.isFile()) {
                Util.copyFileUsingStream(file, new File(buildDir, file.getName()));
            }
        }
    }

    public void generateScripts(int id, JSONArray arg, String main, JSONArray jvmargs) {
        File f = new File(SIPSRun.MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build/build.xml");
        {
            if (f.exists()) {
                return;
            }

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
                        + "<project default=\"run\" basedir=\".\" name=\"" + id + "\">\n"
                        + "  <!--this file was created by Eclipse Runnable JAR Export Wizard-->\n"
                        + "  <!--ANT 1.7 is required                                        -->\n"
                        + "\n"
                        + "  <target name=\"compile\" depends=\"comment\">\n"
                        + "    <javac srcdir=\"src\" destdir=\"src\" includes=\"**/*.java\" target=\"1.8\">\n"
                        + "\n"
                        + "        <classpath refid=\"classpath.base\" />\n"
                        + "    </javac>\n"
                        + "\n"
                        + "  </target>\n"
                        + "<target name=\"run\" depends=\"compile\">\n"
                        + "      <java fork=\"true\" failonerror=\"yes\" classname=\"" + main.trim() + "\">\n"
                        + JVMARGS.toString()
                        + ARGS.toString()
                        + "        <classpath refid=\"classpath.base\" />\n"
                        + "<classpath>\n"
                        + "        <pathelement path=\"${classpath.base}\"/>\n"
                        + "        <pathelement location=\"src\"/>\n"
                        + "    </classpath>      </java>\n"
                        + "   </target>"
                        //                        + "\n<target name=\"comment\">\n"
                        //                        + " <java fork=\"true\" classname= \"lib1.Lib1\">\n"
                        //                        + "            <classpath>\n"
                        //                        + "             <path location=\"libs/lib1.jar\"/>\n"
                        //                        + "            </classpath>\n"
                        //                        + "            <arg value=\"0\" />\n"
                        //                        + "        <arg value=\"${arg1}\" />\n"
                        //                        + "        </java> \n"
                        //                        + "</target>\n"
                        //                        + "\n<target name=\"uncomment\"  depends=\"run\">\n"
                        //                        + " <java fork=\"true\" classname= \"lib1.Lib1\">\n"
                        //                        + "            <classpath>\n"
                        //                        + "             <path location=\"libs/lib1.jar\"/>\n"
                        //                        + "            </classpath>\n"
                        //                        + "            <arg value=\"1\" />\n"
                        //                        + "        <arg value=\"${arg1}\" />\n"
                        //                        + "        </java>\n "
                        //                        + "</target>\n"
                        + "  <!-- Libraries on which your code depends -->\n"
                        + "  <path id=\"classpath.base\">                                                                                                                           \n"
                        + "     <fileset dir=\"libs\">                                                                                                                          \n"
                        + "         <include name=\"**/*.jar\" />                                                                                                          \n"
                        + "     </fileset>                                                                                                                                   \n"
                        + "</path>  \n"
                        + "</project>");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SimulateProject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
