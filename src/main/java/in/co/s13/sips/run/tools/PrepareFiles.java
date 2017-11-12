/* 
 * Copyright (C) 2017 Navdeep Singh Sidhu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package in.co.s13.sips.run.tools;

import in.co.s13.SIPS.db.SQLiteJDBC;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import sips.run.SIPSRun;

public class PrepareFiles {

    public static String OS = System.getProperty("os.name").toLowerCase();
    public static int OS_Name = 0;
    String dbloc = "";
    SQLiteJDBC db = new SQLiteJDBC();
    ArrayList<Integer> commentBeginLine = new ArrayList<>();
    ArrayList<Integer> commentEndLine = new ArrayList<>();
    ArrayList<Integer> commentBeginColumn = new ArrayList<>();
    ArrayList<Integer> commentEndColumn = new ArrayList<>();
    ArrayList<Integer> commentLevel = new ArrayList<>();
    ArrayList<Integer> uncommentBeginLine = new ArrayList<>();
    ArrayList<Integer> uncommentEndLine = new ArrayList<>();
    ArrayList<Integer> uncommentBeginColumn = new ArrayList<>();
    ArrayList<Integer> uncommentEndColumn = new ArrayList<>();
    ArrayList<Integer> uncommentLevel = new ArrayList<>();
    int lastCommentBeginLine = 0, lastCommentEndLine = 0, lastCommentLevel = 0;

    public static enum MODE {
        COMMENT(0),
        UNCOMMENT(1);
        private int value;

        MODE(int value) {
            this.value = value;
        }

    };

    public PrepareFiles(MODE mode, File file) {

        System.out.println(OS);
        System.out.println(file);
        String homeDir = SIPSRun.MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build";
                String parentDir=file.getAbsoluteFile().getParentFile().getAbsolutePath();
//        System.out.println("Length: "+parentDir.length());
        parentDir=parentDir.substring(parentDir.lastIndexOf("src")+3);
//        System.out.println("Parent Dir: "+parentDir);
        dbloc = homeDir + "/.parsed/" + parentDir + "/" + file.getName().substring(0, file.getName().lastIndexOf(".")) + "-parsed.db";
       
        if (isWindows()) {
            System.out.println("This is Windows");
            OS_Name = 0;

//            dbloc = file.substring(0, file.lastIndexOf("."));
//            dbloc += "-parsing.db";

        } else if (isMac()) {
            System.out.println("This is Mac");
            OS_Name = 1;
        } else if (isUnix()) {
            System.out.println("This is Unix or Linux");

            OS_Name = 2;
//            if (file.contains("/")) {
//                dbloc = file.substring(0, file.lastIndexOf("."));
//            }
//            dbloc += "-parsing.db";

        } else if (isSolaris()) {
            System.out.println("This is Solaris");
            OS_Name = 3;
        } else {
            System.out.println("Your OS is not support!!");
            OS_Name = 4;

        }

        String sql = "SELECT * FROM SYNTAX";
        try {
            ResultSet rs = db.select(dbloc, sql);
            //System.out.println("Commenting Done");

            while (rs.next()) {
                System.out.println("" + rs.getString("Category"));
//                if (    rs.getString("Category").contains("ForLoop") || rs.getString("Category").contains("WhileLoop") ||
//                        rs.getString("Category").contains("SimulateSection"))
                {
                    if (mode==(MODE.COMMENT) ) {
                        if (rs.getString("SIM").equalsIgnoreCase("TRUE")) {

                        } else {
                            commentBeginLine.add(rs.getInt("BeginLine"));
                            commentBeginColumn.add(rs.getInt("BeginColumn"));
                            commentEndLine.add(rs.getInt("EndLine"));
                            commentEndColumn.add(rs.getInt("EndColumn"));
                            commentLevel.add(rs.getInt("Level"));
                        }
                    } else if (mode == MODE.UNCOMMENT) {
                        if (rs.getString("SIM").equalsIgnoreCase("TRUE")) {
                            commentBeginLine.add(rs.getInt("BeginLine"));
                            commentBeginColumn.add(rs.getInt("BeginColumn"));
                            commentEndLine.add(rs.getInt("EndLine"));
                            commentEndColumn.add(rs.getInt("EndColumn"));
                            commentLevel.add(rs.getInt("Level"));

                            //   Commentator cm = new Commentator(file, rs.getInt("BeginLine"), rs.getInt("BeginColumn"), rs.getInt("EndLine"), rs.getInt("EndColumn"));
                            //  System.out.println("Commenting Done");
                        } else {

                            uncommentBeginLine.add(rs.getInt("BeginLine"));
                            uncommentBeginColumn.add(rs.getInt("BeginColumn"));
                            uncommentEndLine.add(rs.getInt("EndLine"));
                            uncommentEndColumn.add(rs.getInt("EndColumn"));
                            uncommentLevel.add(rs.getInt("Level"));

                            //  UnCommentator cm = new UnCommentator(file, rs.getInt("BeginLine"), rs.getInt("BeginColumn"), rs.getInt("EndLine"), rs.getInt("EndColumn"));
                            //  System.out.println("UnCommenting Done");
                        }
                    }

                }

            }
            rs.close();
            db.closeConnection();

            System.out.println("comment Bl " + commentBeginLine);
            System.out.println("comment El " + commentEndLine);
            System.out.println("comment LVl " + commentLevel);
            System.out.println("uncomment Bl " + uncommentBeginLine);
            System.out.println("uncomment El " + uncommentEndLine);
            System.out.println("uncomment LVl " + uncommentLevel);

            if (mode == MODE.COMMENT) {
                for (int i = 0; i < commentBeginLine.size(); i++) {
                    if ((commentBeginLine.get(i) >= lastCommentBeginLine) && (commentEndLine.get(i) <= lastCommentEndLine) && (commentLevel.get(i) > lastCommentLevel)) {

                    } else {
                        Commentator cm = new Commentator(file.getAbsolutePath(), commentBeginLine.get(i), commentBeginColumn.get(i), commentEndLine.get(i), commentEndColumn.get(i));
                        System.out.println("Commenting Done");
                        lastCommentBeginLine = commentBeginLine.get(i);
                        lastCommentEndLine = commentEndLine.get(i);
                        lastCommentLevel = commentLevel.get(i);
                    }

                }

            } else if (mode == MODE.UNCOMMENT) {

                for (int i = 0; i < commentBeginLine.size(); i++) {
                    if ((commentBeginLine.get(i) >= lastCommentBeginLine) && (commentEndLine.get(i) <= lastCommentEndLine) && (commentLevel.get(i) > lastCommentLevel)) {

                    } else {
                        Commentator cm = new Commentator(file.getAbsolutePath(), commentBeginLine.get(i), commentBeginColumn.get(i), commentEndLine.get(i), commentEndColumn.get(i));
                        System.out.println("Commenting Done");
                        lastCommentBeginLine = commentBeginLine.get(i);
                        lastCommentEndLine = commentEndLine.get(i);
                        lastCommentLevel = commentLevel.get(i);

                    }

                }
                for (int i = 0; i < uncommentBeginLine.size(); i++) {
                    UnCommentator cm = new UnCommentator(file.getAbsolutePath(), uncommentBeginLine.get(i), uncommentBeginColumn.get(i), uncommentEndLine.get(i), uncommentEndColumn.get(i));
                    System.out.println("UnCommenting Done");

                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }

    }

    public static void main(String[] args) {
        int i = Integer.parseInt(args[0]);
//        PrepareFiles l = new PrepareFiles(MODE.COMMENT, args[1]);
    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

    }

    public static boolean isSolaris() {

        return (OS.indexOf("sunos") >= 0);

    }

}
