/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sips.run;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import in.co.s13.SIPS.db.SQLiteJDBC;
import in.co.s13.sips.run.settings.GlobalValues;
import static in.co.s13.sips.run.settings.GlobalValues.taskCounter;

import in.co.s13.sips.run.tools.Util;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

/**
 *
 * @author Nika
 */
public class Visitor extends VoidVisitorAdapter {

    int forcounter = 0;
    int qualcounter = 0;
    int objccounter = 0;
    int importcounter = 0;
    int packdeccounter = 0;
    int coricounter = 0;
    int concounter = 0;
    int whilecounter = 0;
    int varcounter = 0;
    int vardeccounter = 0;
    int arrayaccounter = 0;
    int arraycrcounter = 0;
    int arrayincounter = 0, assigncounter = 0, binarycounter = 0, booleancounter = 0, castcounter = 0, charlitcounter = 0, conditonalcounter = 0, doublelitcounter = 0, ecnclosedcouter = 0,
            expressioncounter = 0, fieldacccounter = 0, instanceofcounter = 0, intlitcounter = 0, intlitmincounter = 0, litcounter = 0, longlitcounter = 0,
            longlitminvalcounter = 0, markeranncounter = 0, membvalcounter = 0, nameexpcounter = 0, normalannoexpcounter = 0, nulllitcounter = 0,
            singlemembanncounter = 0, stringlitcounter = 0, superexpcounter = 0, thisexpcounter = 0, unarycounter = 0;

    int annotationcounter = 0;
    int returncounter = 0;
    int classcounter = 0, syntaxCounter = 0, methodcounter = 0, methodcallcounter = 0, objmethodcounter = 0, valmethodcounter = 0;
    SQLiteJDBC sqljdbc = new SQLiteJDBC();
    String databaseLoc, databaseLoc2, databaseLoc3, tasksDBLoc;
//    File dir;
    String sipsObjectName = "";
    String createdbsyntax = "CREATE TABLE SYNTAX "
            + "(ID INT PRIMARY KEY     NOT NULL,"
            + " Counter            INT     NOT NULL, "
            + " BeginColumn           INT    NOT NULL, "
            + " BeginLine            INT     NOT NULL, "
            + " EndColumn        INT, "
            + " EndLine        INT, "
            + " String        TEXT, "
            + " TimeStamp         BIGINT,"
            + "Category TEXT,"
            + "Level    INT,"
            + "SIM TEXT)";
    String createdbMeta = "CREATE TABLE META "
            + "(ID INTEGER PRIMARY KEY  AUTOINCREMENT   NOT NULL,"
            + " PARENT  TEXT, "
            + " FILE    TEXT, "
            + " TimeStamp         BIGINT"
            + ")";
    String insertdbsyntax = "INSERT INTO SYNTAX "
            + "(ID , Counter ,BeginColumn, BeginLine ,EndColumn , EndLine,String   , TimeStamp, Category,Level,SIM )";

    String createdbforloop = "CREATE TABLE FORLOOP "
            + "(ID INT PRIMARY KEY     NOT NULL,"
            + " ForLoopCounter            INT     NOT NULL, "
            + " BeginColumn           INT    NOT NULL, "
            + " BeginLine            INT     NOT NULL, "
            + " Body        TEXT, "
            + " Class        TEXT, "
            + " Compare        TEXT, "
            + " DATA        TEXT, "
            + " EndColumn      INT, "
            + " EndLine        INT, "
            + " Init        TEXT, "
            + " UpdateValue        TEXT, "
            + " String        TEXT, "
            + " TimeStamp         BIGINT)";

    String createdbwhileloop = "CREATE TABLE WHILELOOP "
            + "(ID INT PRIMARY KEY     NOT NULL,"
            + " WhileLoopCounter            INT     NOT NULL, "
            + " BeginColumn           INT    NOT NULL, "
            + " BeginLine            INT     NOT NULL, "
            + " Body        TEXT, "
            + " Class        TEXT, "
            + " Condition        TEXT, "
            + " DATA        TEXT, "
            + " EndColumn      INT, "
            + " EndLine        INT, "
            + " String        TEXT, "
            + " TimeStamp         BIGINT)";

    String createdbvars = "CREATE TABLE VARIABLES "
            + "(ID INT PRIMARY KEY     NOT NULL,"
            + " VariableCounter            INT     NOT NULL, "
            + " Annotations            TEXT, "
            + " BeginColumn           INT    NOT NULL, "
            + " BeginLine            INT     NOT NULL, "
            + " Class        TEXT, "
            + " DATA        TEXT, "
            + " Modifier      INT, "
            + " Type       TEXT, "
            + " Var        TEXT, "
            + " String        TEXT, "
            + " TimeStamp         BIGINT)";
    String createTasks = "CREATE TABLE TASKS "
            + "(ID INT PRIMARY KEY     NOT NULL,"
            + " BeginColumn           INT    NOT NULL, "
            + " BeginLine            INT     NOT NULL, "
            + " Class        TEXT, "
            + " EndColumn      INT, "
            + " EndLine        INT, "
            + " Name        TEXT, "
            + " Resources      TEXT DEFAULT '[\"CPU\"]', "
            + " DependsOn      TEXT DEFAULT '[]', "
            + " File        TEXT,"
            + " Length         DECIMAL DEFAULT '1.0',"
            + " Timeout         NUMERIC DEFAULT '1000000',"
            + " TimeStamp         NUMERIC)";
//    String homeDir = System.getProperty("user.home") + "/.SIPS";
    String homeDir = ".build";
    File file;
    String parentDir = "";

    public Visitor(File file) throws IOException {
        this.file = file;
//        sqljdbc.setVerbose(true);
        homeDir = SIPSRun.MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build";
        parentDir = file.getAbsoluteFile().getParentFile().getAbsolutePath();
//        System.out.println("Length: "+parentDir.length());
        parentDir = parentDir.substring(parentDir.lastIndexOf("src") + 3);
//        System.out.println("Parent Dir: "+parentDir);
        databaseLoc = homeDir + "/.parsed/" + parentDir + "/" + file.getName().substring(0, file.getName().lastIndexOf(".")) + "-parsed.db";
        File dbfile = new File(databaseLoc);
        if (dbfile.exists()) {
            boolean b = dbfile.delete();
            System.out.println("Deleted File : " + databaseLoc + " : " + b);
            if (!b) {
                Util.deleteFile(dbfile.getAbsolutePath());
            }
        }
        if (!dbfile.getParentFile().exists()) {
            dbfile.getParentFile().mkdirs();
        }
        databaseLoc2 = homeDir + "/.simulated/" + parentDir + "/" + file.getName().substring(0, file.getName().lastIndexOf(".")) + "-sim.db";
        File dbfile2 = new File(databaseLoc2);
        if (dbfile2.exists()) {
            boolean b = dbfile2.delete();
            System.out.println("Deleted File : " + databaseLoc2 + " : " + b);
            if (!b) {
                Util.deleteFile(dbfile2.getAbsolutePath());

            }
        }
        if (!dbfile2.getParentFile().exists()) {
            dbfile2.getParentFile().mkdirs();
        }

        tasksDBLoc = homeDir + "/.parsed/" + "tasks.db";
        File taskDBFile = new File(tasksDBLoc);

        if (!taskDBFile.getParentFile().exists()) {
            taskDBFile.getParentFile().mkdirs();
        }
        sqljdbc.createtable(databaseLoc, createdbMeta);
        sqljdbc.closeConnection();
        String sql = "INSERT INTO META "
                + "(PARENT , FILE , TimeStamp) VALUES('" + parentDir + "','" + file.getName() + "','" + System.currentTimeMillis() + "')";

        sqljdbc.createtable(databaseLoc, sql);
        sqljdbc.closeConnection();

    }

    public void closedb() throws SQLException {
        sqljdbc.closeConnection();
    }

    public void visit(ForStmt n, Object arg) {

        String sql = "" + createdbforloop;
        if (forcounter == 0) {
            sqljdbc.createtable(databaseLoc, sql);
            sqljdbc.closeConnection();

        }
        sql = "INSERT INTO ForLoop (ID , ForLoopCounter ,BeginColumn, BeginLine,Body ,Class, Compare ,DATA , EndColumn , EndLine  , Init  ,UpdateValue , String , TimeStamp) "
                + "VALUES (" + forcounter + ", " + forcounter + ", " + n.getBegin().get().column + ", " + n.getBegin().get().line + ",'" + n.getBody() + "','" + n.getClass() + "','" + n.getCompare().get().toString() + "','" + n.getRange().get() + "'," + n.getEnd().get().column + "," + n.getEnd().get().line + ",'" + n.getInitialization().get(0) + "','" + n.getUpdate().get(0) + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";

        sqljdbc.insert(databaseLoc, sql);
        sqljdbc.closeConnection();

        //forlist.add(forloop);
        sql = "" + createdbsyntax;
        if (syntaxCounter == 0) {

            sqljdbc.createtable(databaseLoc, sql);
            sqljdbc.closeConnection();

        }

        forcounter++;

        super.visit(n, arg);
    }

    public void visit(WhileStmt n, Object arg) {
        createdbwhileloop = "CREATE TABLE WHILELOOP "
                + "(ID INT PRIMARY KEY     NOT NULL,"
                + " WHILELOOPCOUNTER            INT     NOT NULL, "
                + " BeginColumn           INT    NOT NULL, "
                + " BeginLine            INT     NOT NULL, "
                + " Body        TEXT, "
                + " Class        TEXT, "
                + " Condition        TEXT, "
                + " DATA        TEXT, "
                + " EndColumn      INT, "
                + " EndLine        INT, "
                + " String        TEXT, "
                + " TimeStamp         BIGINT)";

        String sql = "" + createdbwhileloop;
        if (whilecounter == 0) {
            sqljdbc.createtable(databaseLoc, sql);
        }

        sql = "INSERT INTO WHILELOOP (ID , WHILELOOPCOUNTER ,BeginColumn, BeginLine,Body,Class,Condition,DATA , EndColumn , EndLine  , String , TimeStamp) "
                + "VALUES ('" + whilecounter + "',' " + whilecounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','" + n.getBody() + "','" + n.getClass() + "','" + n.getCondition() + "','" + n.getMetaModel() + "','" + n.getEnd().get().column
                + "','" + n.getEnd().get().line + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";
        sqljdbc.insert(databaseLoc, sql);
        sqljdbc.closeConnection();

        sql = "" + createdbsyntax;
        if (syntaxCounter == 0) {

            sqljdbc.createtable(databaseLoc, sql);
            sqljdbc.closeConnection();

        }

        whilecounter++;
        super.visit(n, arg);

    }

    public void visit(MethodCallExpr n, Object Args) {
        if (("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
            String sql = "";

            sql = "CREATE TABLE METHODCALL "
                    + "(ID INT PRIMARY KEY     NOT NULL,"
                    + " MethodCCounter            INT     NOT NULL, "
                    + "Args TEXT,"
                    + "BeginColumn INT,"
                    + " BeginLine INT,"
                    + "Class TEXT,"
                    + "DATA TEXT,"
                    + " EndColumn         INT,"
                    + " EndLine        INT,"
                    + "Scope        TEXT,"
                    + "TypeArgs        TEXT,"
                    + "Name        TEXT,"
                    + " String        TEXT, "
                    + " TimeStamp         BIGINT)";
            if (methodcallcounter == 0) {
                sqljdbc.createtable(databaseLoc, sql);
            }

            sql = "INSERT INTO METHODCALL (ID , MethodCCounter ,Args,BeginColumn, BeginLine,Class,DATA , EndColumn , EndLine,Scope,TypeArgs,Name, String , TimeStamp) "
                    + "VALUES ('" + methodcallcounter + "',' " + methodcallcounter + "',' " + n.getArguments() + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "',' " + n.getClass() + "','" + n.getNameAsString() + "','" + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.getScope().get() + "','" + n.getTypeArguments() + "','" + n.getName() + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";
            sqljdbc.insert(databaseLoc, sql);
            sqljdbc.closeConnection();
            methodcallcounter++;
//        System.out.println("" + n.getScope().get());
            if (n.getNameAsString().contains("saveValues") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "CREATE TABLE VAL" + valmethodcounter + " "
                        + "(ID INT PRIMARY KEY     NOT NULL,"
                        + "NAME TEXT,"
                        + "VALUE TEXT"
                        + ")";

                sqljdbc.createtable(databaseLoc2, sql);
                for (int i = 0; i <= n.getArguments().size() - 1; i++) {

                    sql = "INSERT INTO VAL" + valmethodcounter + "(ID, NAME) VALUES ('" + i + "','" + n.getArgument(i) + "');";
                    sqljdbc.insert(databaseLoc2, sql);
                }
                sqljdbc.closeConnection();

                if (valmethodcounter == 0) {
                    sql = "CREATE  TABLE SAVVAL"
                            + "(ID INT PRIMARY KEY     NOT NULL,"
                            + "BeginColumn INT,"
                            + " BeginLine INT,"
                            + " EndColumn         INT,"
                            + " EndLine        INT,"
                            + "Name        TEXT,"
                            + " String        TEXT, "
                            + " TimeStamp         BIGINT)";
                    sqljdbc.createtable(databaseLoc, sql);
                }

                sql = "INSERT INTO SAVVAL (ID ,BeginColumn, BeginLine, EndColumn , EndLine,Name, String , TimeStamp) "
                        + "VALUES ('" + valmethodcounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "',' " + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.getName() + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";
                sqljdbc.insert(databaseLoc, sql);
                sqljdbc.closeConnection();

                valmethodcounter++;

            }

            if (n.getNameAsString().contains("saveObject") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "CREATE TABLE OBJ" + objmethodcounter + " "
                        + "(ID INT PRIMARY KEY     NOT NULL,"
                        + "NAME TEXT,"
                        + "VALUE LONGBLOB)";

                sqljdbc.createtable(databaseLoc2, sql);
                for (int i = 0; i <= n.getArguments().size() - 1; i++) {

                    sql = "INSERT INTO OBJ" + objmethodcounter + "(ID, NAME) VALUES ('" + i + "','" + n.getArgument(i) + "');";
                    sqljdbc.insert(databaseLoc2, sql);
                }
                sqljdbc.closeConnection();
                objmethodcounter++;
            }
            if (n.getNameAsString().contains("simulateLoop") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {

                    sqljdbc.createtable(databaseLoc, sql);

                }

                sql = "" + insertdbsyntax + "VALUES ('" + syntaxCounter + "',' " + syntaxCounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','"
                        + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.toString() + "','" + System.currentTimeMillis() + "','SimulateLoop','0' ,'NULL' );";
                sqljdbc.insert(databaseLoc, sql);
                sqljdbc.closeConnection();
                syntaxCounter++;
            }

            if (n.getNameAsString().contains("resolveObject") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {

                    sqljdbc.createtable(databaseLoc, sql);

                }

                sql = "" + insertdbsyntax + "VALUES ('" + syntaxCounter + "',' " + syntaxCounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','"
                        + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.toString() + "','" + System.currentTimeMillis() + "','ResolveObject','0' ,'FALSE' );";
                sqljdbc.insert(databaseLoc, sql);
                sqljdbc.closeConnection();
                syntaxCounter++;
            }

            if (n.getNameAsString().contains("simulateSection") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {

                    sqljdbc.createtable(databaseLoc, sql);

                }

                sql = "" + insertdbsyntax + "VALUES ('" + syntaxCounter + "',' " + syntaxCounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','"
                        + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + "','" + System.currentTimeMillis() + "','SimulateSection','0' ,'NULL' );";
                sqljdbc.insert(databaseLoc, sql);
                sqljdbc.closeConnection();
                syntaxCounter++;
            }

            if (n.getNameAsString().contains("endSimulateSection") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {

                    sqljdbc.createtable(databaseLoc, sql);

                }
                sql = "SELECT * FROM SYNTAX WHERE BeginLine<='" + n.getBegin().get().line + "' AND EndLine<='" + n.getEnd().get().line + "' AND Category='SimulateSection'  ORDER BY ID DESC;";
                ResultSet rs = sqljdbc.select(databaseLoc, sql);
                int id = Integer.MIN_VALUE;
                try {
                    if (rs.next()) {
                        id = rs.getInt("ID");
                    }
                    rs.close();
                    sqljdbc.closeConnection();
                    sql = "UPDATE SYNTAX SET EndLine='" + n.getEnd().get().line + "', EndColumn='" + n.getEnd().get().column + "', SIM='TRUE' WHERE ID='" + id + "';";
                    sqljdbc.update(databaseLoc, sql);
                    sqljdbc.closeConnection();

                } catch (SQLException ex) {
                    Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
                }
                //syntaxCounter++;
            }
            if (n.getNameAsString().contains("parallelFor") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {

                    sqljdbc.createtable(databaseLoc, sql);

                }

                sql = "" + insertdbsyntax + "VALUES ('" + syntaxCounter + "',' " + syntaxCounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','"
                        + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + "','" + System.currentTimeMillis() + "','ParallelFor','0' ,'NULL' );";
                sqljdbc.insert(databaseLoc, sql);
                sqljdbc.closeConnection();
                syntaxCounter++;
            }

            if (n.getNameAsString().contains("endParallelFor") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {

                    sqljdbc.createtable(databaseLoc, sql);

                }
                sql = "SELECT * FROM SYNTAX WHERE BeginLine<='" + n.getBegin().get().line + "' AND EndLine<='" + n.getEnd().get().line + "'AND Category='ParallelFor'  ORDER BY ID DESC;";
                ResultSet rs = sqljdbc.select(databaseLoc, sql);
                int id = Integer.MIN_VALUE;
                try {
                    if (rs.next()) {
                        id = rs.getInt("ID");
                    }
                    rs.close();
                    sqljdbc.closeConnection();
                    sql = "UPDATE SYNTAX SET EndLine='" + n.getEnd().get().line + "', EndColumn='" + n.getEnd().get().column + "', SIM='FALSE' WHERE ID='" + id + "';";
                    sqljdbc.update(databaseLoc, sql);
                    sqljdbc.closeConnection();

                } catch (SQLException ex) {
                    Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
                }
                //syntaxCounter++;
            }
            if (n.getNameAsString().contains("defineTask") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {

                    sqljdbc.createtable(databaseLoc, sql);

                }

                sql = "" + insertdbsyntax + "VALUES ('" + syntaxCounter + "',' " + syntaxCounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','"
                        + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + "','" + System.currentTimeMillis() + "','Task','0' ,'NULL' );";
                sqljdbc.insert(databaseLoc, sql);
                sqljdbc.closeConnection();
                syntaxCounter++;
                GlobalValues.taskParserExecutor.submit(() -> {

                    String sql2 = "" + createTasks;
                    if (taskCounter.get() == 0) {
                        GlobalValues.sqljdbcTask.createtable(tasksDBLoc, sql2);
                    }
                    String taskName = n.getArgument(0).toString();
                    taskName = taskName.substring(1, taskName.length() - 1);
                    sql2 = "INSERT INTO TASKS (ID,BeginColumn,BeginLine,Class,Name,File,Timestamp)"
                            + " VALUES ('"
                            + GlobalValues.taskCounter.get() + "','" + n.getBegin().get().column + "','" + n.getBegin().get().line + "','" + file.getAbsolutePath() + "','" + taskName + "','" + parentDir + "/" + file.getName() + "','" + System.currentTimeMillis() + "');";
                    GlobalValues.sqljdbcTask.insert(tasksDBLoc, sql2);
                    GlobalValues.sqljdbcTask.closeConnection();
                    taskCounter.incrementAndGet();
                });
            }
            if (n.getNameAsString().contains("setTaskResourcePriority") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {

                GlobalValues.taskParserExecutor.submit(() -> {
                    String sql2 = "" + createTasks;
                    if (taskCounter.get() == 0) {
                        GlobalValues.sqljdbcTask.createtable(tasksDBLoc, sql2);
                    }
                    String taskName = n.getArgument(0).toString();
                    taskName = taskName.substring(1, taskName.length() - 1);

                    sql2 = "SELECT * FROM TASKS WHERE Name='" + taskName + "';";
                    ResultSet rs = GlobalValues.sqljdbcTask.select(tasksDBLoc, sql2);
                    JSONArray resources2;
                    ArrayList<String> list = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            resources2 = new JSONArray(rs.getString("Resources"));
                            for (int i = 0; i < resources2.length(); i++) {
                                String get = resources2.getString(i);
                                if (!list.contains(get)) {
                                    list.add(get);
                                }
                            }

                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    GlobalValues.sqljdbcTask.closeConnection();

                    NodeList<Expression> args = n.getArguments();
                    JSONArray resources = new JSONArray();
                    for (int i = 1; i < args.size(); i++) {
                        Expression get = args.get(i);
                        String resource = get.toString();
                        resource = resource.substring(1, resource.length() - 1);

                        if (!list.contains(resource)) {
                            list.add(resource);
                        }
                    }

                    for (int i = 0; i < list.size(); i++) {
                        String get = list.get(i);
                        resources.put(get);
                    }

                    sql2 = "UPDATE TASKS SET Resources='" + resources.toString() + "' WHERE Name='" + taskName + "' AND Class='" + file.getAbsolutePath() + "';";
                    GlobalValues.sqljdbcTask.update(tasksDBLoc, sql2);
                    GlobalValues.sqljdbcTask.closeConnection();
                });
            }

            if (n.getNameAsString().contains("setTaskDependency") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {

                GlobalValues.taskParserExecutor.submit(() -> {
                    String sql2 = "" + createTasks;
                    if (taskCounter.get() == 0) {
                        GlobalValues.sqljdbcTask.createtable(tasksDBLoc, sql2);
                    }
                    String taskName = n.getArgument(0).toString();
                    taskName = taskName.substring(1, taskName.length() - 1);

                    sql2 = "SELECT * FROM TASKS WHERE Name='" + taskName + "';";
                    ResultSet rs = GlobalValues.sqljdbcTask.select(tasksDBLoc, sql2);
                    JSONArray dependencies2;
                    ArrayList<String> list = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            dependencies2 = new JSONArray(rs.getString("DependsOn"));
                            for (int i = 0; i < dependencies2.length(); i++) {
                                String get = dependencies2.getString(i);
                                if (!list.contains(get)) {
                                    list.add(get);
                                }
                            }

                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    GlobalValues.sqljdbcTask.closeConnection();

                    NodeList<Expression> args = n.getArguments();
                    JSONArray dependencies = new JSONArray();
                    for (int i = 1; i < args.size(); i++) {
                        Expression get = args.get(i);
                        String dependency = get.toString();
                        dependency = dependency.substring(1, dependency.length() - 1);

                        if (!list.contains(dependency)) {
                            list.add(dependency);
                        }
                    }

                    for (int i = 0; i < list.size(); i++) {
                        String get = list.get(i);
                        dependencies.put(get);
                    }

                    sql2 = "UPDATE TASKS SET DependsOn='" + dependencies.toString() + "' WHERE Name='" + taskName + "' AND Class='" + file.getAbsolutePath() + "';";
                    GlobalValues.sqljdbcTask.update(tasksDBLoc, sql2);
                    GlobalValues.sqljdbcTask.closeConnection();
                });
            }
            if (n.getNameAsString().contains("endTask") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                sql = "" + createdbsyntax;
                if (syntaxCounter == 0) {
                    sqljdbc.createtable(databaseLoc, sql);
                }
                sql = "SELECT * FROM SYNTAX WHERE BeginLine<='" + n.getBegin().get().line + "' AND EndLine<='" + n.getEnd().get().line + "' AND Category='Task' ORDER BY ID DESC;";
                ResultSet rs = sqljdbc.select(databaseLoc, sql);
                int id = Integer.MIN_VALUE;
                try {
                    if (rs.next()) {
                        id = rs.getInt("ID");
                    }
                    rs.close();
                    sqljdbc.closeConnection();
                    sql = "UPDATE SYNTAX SET EndLine='" + n.getEnd().get().line + "', EndColumn='" + n.getEnd().get().column + "', SIM='FALSE' WHERE ID='" + id + "';";
                    sqljdbc.update(databaseLoc, sql);
                    sqljdbc.closeConnection();

                } catch (SQLException ex) {
                    Logger.getLogger(Visitor.class.getName()).log(Level.SEVERE, null, ex);
                }

                GlobalValues.taskParserExecutor.submit(() -> {
                    String sql2 = "" + createTasks;
                    if (taskCounter.get() == 0) {
                        GlobalValues.sqljdbcTask.createtable(tasksDBLoc, sql2);
                    }
                    String taskName = n.getArgument(0).toString();
                    taskName = taskName.substring(1, taskName.length() - 1);

                    sql2 = "UPDATE TASKS SET EndColumn='" + n.getEnd().get().column + "',EndLine='" + n.getEnd().get().line + "' WHERE Name='" + taskName + "' AND Class='" + file.getAbsolutePath() + "';";
                    GlobalValues.sqljdbcTask.update(tasksDBLoc, sql2);
                    GlobalValues.sqljdbcTask.closeConnection();
                });
            }
            if (n.getNameAsString().contains("setDuration") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {

                GlobalValues.taskParserExecutor.submit(() -> {

                    String sql2 = "" + createTasks;
                    if (taskCounter.get() == 0) {
                        GlobalValues.sqljdbcTask.createtable(tasksDBLoc, sql2);
                    }
                    String taskName = n.getArgument(0).toString();
                    taskName = taskName.substring(1, taskName.length() - 1);

                    sql2 = "UPDATE TASKS SET Length='" + n.getArgument(1) + "' WHERE Name='" + taskName + "' AND Class='" + file.getAbsolutePath() + "';";
                    GlobalValues.sqljdbcTask.update(tasksDBLoc, sql2);
                    GlobalValues.sqljdbcTask.closeConnection();
                });
            }
            if (n.getNameAsString().contains("setTimeout") && ("" + n.getScope().get()).trim().equals(sipsObjectName.trim())) {
                GlobalValues.taskParserExecutor.submit(() -> {
                    String sql2 = "" + createTasks;
                    if (taskCounter.get() == 0) {
                        GlobalValues.sqljdbcTask.createtable(tasksDBLoc, sql2);
                    }
                    String taskName = n.getArgument(0).toString();
                    taskName = taskName.substring(1, taskName.length() - 1);

                    sql2 = "UPDATE TASKS SET Timeout='" + n.getArgument(1) + "' WHERE Name='" + taskName + "' AND Class='" + file.getAbsolutePath() + "';";
                    GlobalValues.sqljdbcTask.update(tasksDBLoc, sql2);
                    GlobalValues.sqljdbcTask.closeConnection();
                });

            }

        }
        super.visit(n, Args);

    }

    public void visit(VariableDeclarationExpr n, Object Args) {
        createdbvars = "CREATE TABLE VARIABLES "
                + "(ID INT PRIMARY KEY     NOT NULL,"
                + " VariableCounter            INT     NOT NULL, "
                + " Annotations            TEXT, "
                + " BeginColumn           INT    NOT NULL, "
                + " BeginLine            INT     NOT NULL, "
                + " Class        TEXT, "
                + " DATA        TEXT, "
                + " EndColumn            INT   , "
                + " EndLine            INT    , "
                + " Modifier      INT, "
                + "Type TEXT, "
                + " Var        TEXT, "
                + " String        TEXT, "
                + " TimeStamp         BIGINT)";
        String sql = "" + createdbvars;
        if (varcounter == 0) {
            sqljdbc.createtable(databaseLoc, sql);
        }

        if (("" + n.getElementType()).equalsIgnoreCase("SIPS")) {
            sipsObjectName = ("" + n.getVariable(0).getNameAsString());
        }
        String vars = "";
        NodeList<VariableDeclarator> list = n.getVariables();
        for (int i = 0; i < list.size(); i++) {
            VariableDeclarator get = list.get(i);
            vars += (get.getNameAsString()) + ",";
        }
        sql = "INSERT INTO VARIABLES (ID , VariableCounter ,Annotations,BeginColumn, BeginLine,Class,DATA , EndColumn , EndLine  ,Modifier,Type,Var, String , TimeStamp) "
                + "VALUES ('" + varcounter + "',' " + varcounter + "',' " + n.getAnnotations() + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','" + n.getClass() + "','" + n.getAnnotations() + "','" + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.getModifiers() + "','" + n.getElementType() + "','" + vars + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";

        sqljdbc.insert(databaseLoc, sql);
        sqljdbc.closeConnection();
        varcounter++;
        super.visit(n, Args);
    }

    public void visit(ClassExpr n, Object Args) {

        String sql = "CREATE TABLE CLASS "
                + "(ID INT PRIMARY KEY     NOT NULL,"
                + " ClassCounter            INT     NOT NULL, "
                + " BeginColumn           INT    NOT NULL, "
                + " BeginLine            INT     NOT NULL, "
                + " Class        TEXT, "
                + " DATA        TEXT, "
                + " EndColumn        INT, "
                + " EndLine        INT, "
                + " Type       TEXT, "
                + " String        TEXT, "
                + " TimeStamp         BIGINT)";
        if (classcounter == 0) {
            sqljdbc.createtable(databaseLoc, sql);
        }
        sql = "INSERT INTO CLASS (ID , ClassCounter ,BeginColumn, BeginLine,Class,DATA , EndColumn , EndLine  ,Type, String , TimeStamp) "
                + "VALUES ('" + classcounter + "',' " + classcounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','" + n.getClass() + "','" + n.getAllContainedComments() + "','" + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.getType() + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";

        sqljdbc.insert(databaseLoc, sql);
        sqljdbc.closeConnection();
        sql = "" + createdbsyntax;
        if (syntaxCounter == 0) {

            sqljdbc.createtable(databaseLoc, sql);

        }
        sqljdbc.closeConnection();

        sql = "" + insertdbsyntax + "VALUES ('" + syntaxCounter + "',' " + syntaxCounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','"
                + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.toString() + "','" + System.currentTimeMillis() + "',' ClassExpression','0' ,'NULL' );";

        sqljdbc.insert(databaseLoc, sql);
        sqljdbc.closeConnection();
        syntaxCounter++;

        classcounter++;
        super.visit(n, Args);

    }

    public void visit(MethodDeclaration n, Object Args) {

        super.visit(n, Args);

    }

    public void visit(VariableDeclarator n, Object Args) {
        String sql = "CREATE TABLE VARDEC "
                + "(ID INT PRIMARY KEY     NOT NULL,"
                + " VARDECCOUNTER           INT     NOT NULL, "
                + " BeginColumn           INT    NOT NULL, "
                + " BeginLine            INT     NOT NULL, "
                + " Class        TEXT, "
                + " DATA        TEXT, "
                + " EndColumn      INT, "
                + " EndLine        INT, "
                + " VARID        TEXT, "
                + "INIT TEXT, "
                + " String        TEXT, "
                + " TimeStamp         BIGINT)";;
        if (vardeccounter == 0) {
            sqljdbc.createtable(databaseLoc, sql);
            sqljdbc.closeConnection();

        }
        sql = "INSERT INTO VARDEC (ID , VARDECCOUNTER ,BeginColumn, BeginLine,Class,DATA , EndColumn , EndLine ,VARID,INIT,String , TimeStamp) "
                + "VALUES ('" + vardeccounter + "',' " + vardeccounter + "', '" + n.getBegin().get().column + "','" + n.getBegin().get().line + "','" + n.getClass() + "','" + n.getNameAsString() + "','" + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.getName() + "','" + ((n.getInitializer().isPresent()) ? n.getInitializer().get() : "") + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";

        sqljdbc.insert(databaseLoc, sql);
        sqljdbc.closeConnection();
        vardeccounter++;
        super.visit(n, Args);
    }

    @Override
    public void visit(BinaryExpr n, Object Args) {

        String sql = "";

        sql = "CREATE TABLE BINARYEXP "
                + "(ID INT PRIMARY KEY     NOT NULL,"
                + " BinaryCounter            INT     NOT NULL, "
                + " BeginColumn           INT    NOT NULL, "
                + " BeginLine            INT     NOT NULL, "
                + " Class        TEXT, "
                + " DATA        TEXT, "
                + " EndColumn        INT, "
                + " EndLine        INT, "
                + " Left       TEXT, "
                + " Operator       TEXT, "
                + "Right TEXT, "
                + " String        TEXT, "
                + " TimeStamp         BIGINT)";
        if (binarycounter == 0) {
            sqljdbc.createtable(databaseLoc, sql);
        }
        sql = "INSERT INTO BINARYEXP (ID , BinaryCounter ,BeginColumn, BeginLine,Class,DATA , EndColumn , EndLine  ,Left,Operator,Right, String , TimeStamp) "
                + "VALUES ('" + binarycounter + "',' " + binarycounter + "',' " + n.getBegin().get().column + "',' " + n.getBegin().get().line + "','" + n.getClass() + "','" + n.toString() + "','" + n.getEnd().get().column + "','" + n.getEnd().get().line + "','" + n.getLeft() + "','" + n.getOperator() + "','" + n.getRight() + "','" + n.toString() + "','" + System.currentTimeMillis() + "' );";

        sqljdbc.insert(databaseLoc, sql);
        sqljdbc.closeConnection();

        binarycounter++;
        super.visit(n, Args);

    }

}
