/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.sips.run.tools;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sips.run.SIPSRun;
import sips.run.Visitor;

/**
 *
 * @author nika
 */
public class ParseJavaFile implements Runnable {

    File file;
    String projectName;
    String args[];

    public ParseJavaFile(File file, String projectName, String... args) {
        this.file = file;
        this.projectName = projectName;
        this.args = args;
    }

    @Override
    public void run() {
        try (FileInputStream in = new FileInputStream(file)) {
            // parse the file
            CompilationUnit cu = JavaParser.parse(in);
            // visit and print the methods names
            // new MethodVisitor().visit(cu, args);
            Visitor flv = new Visitor(file);
            flv.visit(cu, args);
            LevelDetector ld= new LevelDetector(file);
            SIPSRun.levelDetectorExecutor.submit(ld);
            
        } catch (IOException ex) {
            Logger.getLogger(SIPSRun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ParseJavaFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
