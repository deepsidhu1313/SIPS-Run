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
import in.co.s13.sips.scheduler.LoadScheduler;
import in.co.s13.sips.scheduler.Scheduler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static JSONObject manifestJSON = null;
    public static JSONObject settingsJSON = null;
//    public static JSONObject recentJobsJSON = null;
    public static String UUID, API_KEY;
    public static String RECENT_JOBS_DB = System.getProperty("user.home") + "/.sips/sips-run-recent.db";

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        SIPSRun sipsRun = new SIPSRun(args);
    }

    public SIPSRun(String[] args) throws InterruptedException {
        System.out.println("***************************************************************"
                + "\n***************** SIPS-RUN ************************"
                + "\n***************************************************************"
        );
        String manifestFile = "manifest.json";
        File SIPSDir = new File(System.getProperty("user.home") + "/.sips/");
        if (!SIPSDir.exists()) {
            SIPSDir.mkdirs();
        }
        settingsJSON = Util.readJSONFile(System.getProperty("user.home") + "/.sips/sips-run.json");
        //recentJobsJSON = Util.readJSONFile(System.getProperty("user.home") + "/.sips/sips-run-recent.json");
        UUID = settingsJSON.getString("UUID", "");
        if (UUID.trim().length() < 1) {
            UUID = Util.generateNodeUUID();
            settingsJSON.remove("UUID");
            settingsJSON.put("UUID", UUID);
            Util.write(System.getProperty("user.home") + "/.sips/sips-run.json", settingsJSON.toString(4));
        }
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
                if (arguments.contains("--clean")) {
                    System.out.println("Deleting .build directory " + Util.deleteDirectory(new File(MANIFEST_FILE.getParentFile(), ".build/")));
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

                if (arguments.contains("--get-job-status")) {
                    System.out.println("Last Job Status:\n ");
                    System.exit(0);
                }

                if (arguments.contains("--view-recent-jobs")) {
                    System.out.println("Recent Jobs:\n ");
                    System.exit(0);
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
        System.out.println("***************************************************************"
                + "\n******************* Preparing Files ***************************"
                + "\n***************************************************************"
        );
        prepareFiles(args);
        System.out.println("***************************************************************"
                + "\n***************** Requesting Job Token ************************"
                + "\n***************************************************************"
        );
        String jobToken = createJobToken();
        System.out.println("***************************************************************"
                + "\n** Received Job Token " + jobToken + " **"
                + "\n************** Use This Token to get Status *******************"
                + "\n***************************************************************"
        );
        System.out.println("***************************************************************"
                + "\n************* Uploading Job to " + manifestJSON.getJSONObject("MASTER").getString("HOST") + " ********************"
                + "\n***************************************************************"
        );
        System.out.println("***************************************************************"
                + "\n************* Generating Checksums ********************"
                + "\n***************************************************************"
        );
        generateChecksums(new File(MANIFEST_FILE.getParentFile(), ".build/").getAbsolutePath());
        uploadJob(jobToken);
    }

    public void generateChecksums(String path) throws InterruptedException {
        int TASK_LIMIT = (Runtime.getRuntime().availableProcessors() - 2) < 1 ? 1 : (Runtime.getRuntime().availableProcessors() - 2);
        ExecutorService executorService = Executors.newFixedThreadPool(TASK_LIMIT);
        File file = new File(path);
        if (!file.exists()) {
            System.err.println("File or Dir Doesn't Exist : " + file.getAbsolutePath());
        }

        if (file.isDirectory()) {
            executorService.submit(() -> {
                try {
                    generateChecksums(file.getAbsolutePath());
                } catch (InterruptedException ex) {
                    Logger.getLogger(SIPSRun.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            File files[] = file.listFiles();
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    executorService.submit(() -> {
                        try {
                            generateChecksums(file1.getAbsolutePath());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SIPSRun.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } else {
                    executorService.submit(() -> {
                        Util.getCheckSum(file1.getAbsolutePath());
                    });
                }
            }
        } else {
            executorService.submit(() -> {
                Util.getCheckSum(file.getAbsolutePath());
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    public void prepareFiles(String[] args) throws InterruptedException {
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
        /**
         * Prepare files for simulation
         */
        for (int i = 0; i < javaFiles.size(); i++) {
            String get = javaFiles.get(i);
            PrepareFiles prepareFile = new PrepareFiles(PrepareFiles.MODE.COMMENT, new File(get));
        }
        SimulateProject simulate = new SimulateProject();
        simulate.generateScripts(manifestJSON.getString("PROJECT", ""), manifestJSON.getJSONArray("ARGS", new JSONArray()), manifestJSON.getString("MAIN", ""), manifestJSON.getJSONArray("JVMARGS", new JSONArray()));
        simulate.simulate();
        Util.copyFolder(new File(MANIFEST_FILE.getParentFile().getAbsolutePath() + "/src"), (new File(new File(MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build"), ("src/"))));
        for (int i = 0; i < javaFiles.size(); i++) {
            String get = javaFiles.get(i);
            PrepareFiles prepareFile = new PrepareFiles(PrepareFiles.MODE.UNCOMMENT, new File(get));
        }
        try {
            createSchedulerObject();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException ex) {
            Logger.getLogger(SIPSRun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String createJobToken() {
        JSONObject requestJson = new JSONObject();
        requestJson.put("Command", "CREATE_JOB_TOKEN");
        JSONObject requestBody = new JSONObject();
        requestBody.put("UUID", UUID);
        requestBody.put("SCHEDULER", manifestJSON.getJSONObject("SCHEDULER", new JSONObject()).getString("Name", "NotFound"));
        requestBody.put("JOB_NAME", manifestJSON.getString("PROJECT", "NotFound"));
        requestJson.put("Body", requestBody);
        String ipaddress = manifestJSON.getJSONObject("MASTER").getString("HOST");
        int taskPort = manifestJSON.getJSONObject("MASTER").getInt("TASK-PORT");
        JSONObject reply = sendCommand(ipaddress, taskPort, requestJson);
        String token = reply.getJSONObject("Response").getString("Token", "NotFound");
        return token;
    }

    public void uploadJob(String jobToken) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("Command", "UPLOAD_JOB");
        JSONObject requestBody = new JSONObject();
        requestBody.put("UUID", UUID);
        requestBody.put("JobToken", jobToken);
        requestJson.put("Body", requestBody);
        String ipaddress = manifestJSON.getJSONObject("MASTER").getString("HOST");
        int taskPort = manifestJSON.getJSONObject("MASTER").getInt("TASK-PORT");
        JSONObject reply = sendCommand(ipaddress, taskPort, requestJson);

    }

    public void uploadScheduler(String jobToken) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("Command", "UPLOAD_SCHEDULER");
        JSONObject requestBody = new JSONObject();
        requestBody.put("UUID", UUID);
        requestJson.put("Body", requestBody);
        String ipaddress = manifestJSON.getJSONObject("MASTER").getString("HOST");
        int taskPort = manifestJSON.getJSONObject("MASTER").getInt("TASK-PORT");
        JSONObject reply = sendCommand(ipaddress, taskPort, requestJson);

    }

    public void getJobStatus(String jobToken) {
        String ipaddress = manifestJSON.getJSONObject("MASTER").getString("HOST");
        int apiPort = manifestJSON.getJSONObject("MASTER").getInt("API-PORT");
        String apiKey = manifestJSON.getJSONObject("MASTER").getString("API-KEY");
        JSONObject requestJson = new JSONObject();
        requestJson.put("Command", "JOB_STATUS");
        JSONObject requestBody = new JSONObject();
        requestBody.put("UUID", UUID);
        requestBody.put("API_KEY", apiKey);
        requestJson.put("Body", requestBody);
        JSONObject reply = sendCommand(ipaddress, apiPort, requestJson);

    }

    public JSONObject sendCommand(String host, int port, JSONObject requestJson) {
        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(host, port));
            try (OutputStream os = socket.getOutputStream(); DataInputStream dIn = new DataInputStream(socket.getInputStream()); DataOutputStream outToServer = new DataOutputStream(os)) {
                String sendmsg = requestJson.toString();
                byte[] bytes = sendmsg.getBytes("UTF-8");
                outToServer.writeInt(bytes.length);
                outToServer.write(bytes);

                int length = dIn.readInt();                    // read length of incoming message
                byte[] message = new byte[length];

                if (length > 0) {
                    dIn.readFully(message, 0, message.length); // read the message
                }
                JSONObject reply = new JSONObject(new String(message));
                //System.out.println(""+reply.toString(4));
                return reply.getJSONObject("Body", new JSONObject());
            } catch (Exception e) {
                Logger.getLogger(SIPSRun.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (IOException ex) {
            Logger.getLogger(SIPSRun.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new JSONObject();
    }

    public void createSchedulerObject() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        File schedulerDir = new File(MANIFEST_FILE.getParentFile().getAbsoluteFile(), "scheduler");
        String scheduler = "";
        if (schedulerDir.exists()) {
            scheduler = manifestJSON.getJSONObject("SCHEDULER", new JSONObject()).getString("Name", "NotFound");
            if (!scheduler.startsWith("in.co.s13.sips.schedulers.") && scheduler.trim().length() > 0) {
                File file = new File(schedulerDir.getAbsolutePath());

                //convert the file to URL format
                URL url = file.toURI().toURL();
                URL[] urls = new URL[]{url};

                ClassLoader cl = new URLClassLoader(urls);
                Class cls = cl.loadClass(scheduler);

                LoadScheduler loadScheduler = new LoadScheduler((Scheduler) cls.newInstance());
                Util.serialize(loadScheduler, new File(MANIFEST_FILE.getParentFile().getAbsoluteFile(), ".build/.simulated/" + scheduler + ".obj").toString());
            }
        }
    }

    public static void generateManifest(String projectName) {
        JSONObject manifest = new JSONObject();
        manifest.put("PROJECT", projectName);
        manifest.put("MAIN", "" + projectName.replaceAll(System.lineSeparator(), ".").replaceAll("-", "_"));
        JSONArray libs = new JSONArray();
        libs.put("SIPS-lib-0.1-SNAPSHOT-jar-with-dependencies.jar");
        manifest.put("LIB", libs);
        manifest.put("ATTCH", new JSONArray());
        manifest.put("ARGS", new JSONArray());
        manifest.put("JVMARGS", new JSONArray());
        manifest.put("OUTPUTFREQUENCY", 100);
        JSONObject scheduler = new JSONObject();
        scheduler.put("Name", "in.co.s13.sips.schedulers.chunk");
        scheduler.put("MaxNodes", "4");
        manifest.put("SCHEDULER", scheduler);
        JSONObject master = new JSONObject();
        master.put("HOST", "127.0.0.1");
        master.put("TASK-PORT", "13133");
        master.put("API-PORT", "13139");
        master.put("API-KEY", "");
        manifest.put("MASTER", master);
        Util.write(((projectName.trim().length() > 0 ? projectName + "/" : ""))
                + "manifest.json", manifest.toString(4)
        );
        System.out.println("Put SIPS lib jar file in the directory to provide SIPS support");

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
