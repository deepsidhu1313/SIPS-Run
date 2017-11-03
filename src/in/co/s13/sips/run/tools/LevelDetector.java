/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.sips.run.tools;

import in.co.s13.SIPS.db.SQLiteJDBC;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sips.run.SIPSRun;

/**
 *
 * @author Nika
 */
public class LevelDetector implements Runnable {

    SQLiteJDBC db = new SQLiteJDBC();
//    ArrayList<CheckBoxTreeItem> itemlist = new ArrayList();
//    CheckBoxTreeItem<String> cbt[] = new CheckBoxTreeItem[1000];
    ArrayList<Integer> selecteditems = new ArrayList<>();
    String databaseLoc, sql;
    ArrayList<Integer> nodesUpper = new ArrayList();
    ArrayList<Integer> id1 = new ArrayList();
    ArrayList<Integer> id2 = new ArrayList(), bc1 = new ArrayList(), bc2 = new ArrayList(), bl1 = new ArrayList(), bl2 = new ArrayList(), ec1 = new ArrayList(), ec2 = new ArrayList(), el1 = new ArrayList(), el2 = new ArrayList(), lvl1 = new ArrayList(), lvl2 = new ArrayList();
    ArrayList<String> stringv = new ArrayList<>();
    ArrayList<String> category = new ArrayList<>();
    ArrayList<Boolean> isinTree = new ArrayList<>();
//    CheckBoxTreeItem<String> rootItem;
    String filename;
//    int tabnumber = 0, PID;
    String homeDir = ".build";

    public LevelDetector(File file) throws SQLException {
        homeDir = SIPSRun.MANIFEST_FILE.getParentFile().getAbsolutePath() + "/.build";
        String parentDir = file.getAbsoluteFile().getParentFile().getAbsolutePath();
//        System.out.println("Length: "+parentDir.length());
        parentDir = parentDir.substring(parentDir.lastIndexOf("src") + 3);
        System.out.println(file.getParentFile().getAbsolutePath().lastIndexOf("src") + " Parent Dir:" + parentDir);
        databaseLoc = homeDir + "/.parsed/" + parentDir + "/" + file.getName().substring(0, file.getName().lastIndexOf(".")) + "-parsed.db";
        System.out.println(databaseLoc);
        filename = "" + file.getName();
//        tabnumber = counter;
//        PID = pid;
    }

    public void updateLevels() throws SQLException {
        sql = "SELECT * FROM SYNTAX";
        ResultSet rs = db.select(databaseLoc, sql);
        int level = 1;
        int linecheck = 0;
        int linelev = 0;
        //db.closeConnection();
        while (rs.next()) {
            id1.add(rs.getInt("ID"));
            bl1.add(rs.getInt("BeginLine"));
            bc1.add(rs.getInt("BeginColumn"));
            el1.add(rs.getInt("EndLine"));
            ec1.add(rs.getInt("EndColumn"));
            lvl1.add(rs.getInt("Level"));
            id2.add(rs.getInt("ID"));
            bl2.add(rs.getInt("BeginLine"));
            bc2.add(rs.getInt("BeginColumn"));
            el2.add(rs.getInt("EndLine"));
            ec2.add(rs.getInt("EndColumn"));
            lvl2.add(rs.getInt("Level"));
            stringv.add(rs.getString("String"));
            category.add(rs.getString("Category"));
            isinTree.add(Boolean.FALSE);
        }
        db.closeConnection();
        for (int i = 0; i < id1.size() - 1; i++) {

            int id = id1.get(i);
            //  settings.outPrintln("ID of 1st is " + id);
            int bl = bl1.get(i);
            //  settings.outPrintln("BL of 1st is " + bl);
            int bc = bc1.get(i);
            //  settings.outPrintln("BC of 1st is" + bc);
            int el = el1.get(i);
            //  settings.outPrintln("Endline of 1st is " + el);
            int ec = ec1.get(i);
            //   settings.outPrintln("EC of 1st is " + ec);
            int lvl = lvl1.get(i);
            //   settings.outPrintln("Level Of 1st is " + lvl);

            linecheck = el;

            for (int p = i; p <= id2.size() - 1; p++) {
                int ids = id2.get(p);
                // settings.outPrintln("ID of 2nd is " + ids);
                int bls = bl2.get(p);
                // settings.outPrintln("BL of 2nd is " + bls);
                int bcs = bc2.get(p);
                // settings.outPrintln("BC of 2nd is"+ bcs);
                int els = el2.get(p);
                // settings.outPrintln("Endline of 2nd is " + els);
                int ecs = ec2.get(p);
                //    settings.outPrintln("EC of 2nd is " + ecs);
                int lvls = lvl2.get(p);
                //       settings.outPrintln("Level Of 2nd is " + lvls);
                if (els > linecheck) {
                    linecheck = els;
                    linelev = lvls;
                }
                //  if(id<ids) //  
                {
                    //if(((bl<=bls)&&(el>=els))&&((bc<bcs)&&(ec>ecs)))
                    //  if(((bl<bls)&&(((bc<bcs)&&((el>els)||((el==els)&&(ec>ecs))))||((bc==bcs)&&((el>els)||((el==els)&&(ec>ecs))))||((bc>bcs)&&((el>els)||((el==els)&&(ec>ecs))))))||((bl==bls)&&(bc<bcs)&&((el>els)||((el==els)&&(ec>ecs)))))
                    if ((((bl < bls) && (el > els)) || ((bl == bls) && (bc < bcs) && (el >= els) && (ec > ecs))) && (bls < el)) {
                        // if(id<ids)
                        {

                            level = lvl + 1;
                            lvl2.set(p, level);
                            lvl1.set(p, level);
                            //settings.outPrintln("Statisfied second Condition");
                            sql = "UPDATE SYNTAX set Level = " + level + " where ID = " + ids + ";";
                            db.update(databaseLoc, sql);
                        }
                    } else // if(bls>el) 
                    {

                        if (lvl <= lvls) {
                            level = lvl;
                            //   lvl2.set(p, level);
                            //  lvl1.set(p, level);

                            //  settings.outPrintln("Statisfied first Condition");
                            //   sql = "UPDATE SYNTAX set Level = " + level + " where ID = " + ids + ";";
                            //  db.Update(databaseLoc, sql);
                        }

                    }

                }

            }

        }
        db.closeConnection();
    }

    public void printlevels() {
        for (int i = 0; i < lvl1.size() - 1; i++) {
            System.out.println("" + lvl1.get(i));
        }
        System.out.println("***************************************************");
        for (int i = 0; i < lvl2.size() - 1; i++) {
            System.out.println("" + lvl2.get(i));
        }
        System.out.println("***************************************************");
        for (int i = 0; i < lvl2.size() - 1; i++) {
            System.out.println(lvl1.get(i) + "\t" + lvl2.get(i));
        }

    }

//    public CheckBoxTreeItem generateTree() throws SQLException {
//        int maxlevel = 0;
//
//        rootItem = new CheckBoxTreeItem(filename);
//        rootItem.setExpanded(true);
//
//        for (int i = 0; i <= lvl1.size() - 1; i++) {
//            String text = "" + stringv.get(i);
//            int last = 0;
//            if (text.length() < 45) {
//                last = text.length();
//            } else {
//                last = 45;
//            }
//            if (lvl1.get(i) > maxlevel) {
//                maxlevel = lvl1.get(i);
//            }
//            if (lvl1.get(i) == 0) {
//                nodesUpper.add(i);
//            }
//            final int cur = i;
//            String name = "";
//            if (text.contains("{")) {
//                name = text.substring(0, text.indexOf("{")) + " : " + category.get(i);
//            } else {
//                name = text + " : " + category.get(i);
//            }
//            cbt[i] = new CheckBoxTreeItem(name);
//            cbt[i].setExpanded(true);
//            cbt[cur].selectedProperty().addListener(new ChangeListener<Boolean>() {
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    if (newValue) {
//                        settings.outPrintln("The selected item is " + cbt[cur].valueProperty().get());
//                        String str = cbt[cur].valueProperty().get();
//
//                        if (!isInSelectedList(cur) && (str.contains("ForLoop"))) {
//                            selecteditems.add(cur);
//                        }
//                    } else if (!newValue) {
//                        settings.outPrintln("The Unselected item is " + cbt[cur].valueProperty().get());
//                        for (int j = 0; j <= selecteditems.size() - 1; j++) {
//                            if (selecteditems.get(j) == cur) {
//                                selecteditems.remove(j);
//                            }
//
//                        }
//
//                    }
//                    for (int j = 0; j <= selecteditems.size() - 1; j++) {
//                        settings.outPrintln("Item slected are " + selecteditems.get(j));
//                    }
//                    Collections.sort(selecteditems);
//                    settings.outPrintln("" + selecteditems);
//                }
//            });
//        }
//
//        settings.outPrintln("Max Level Is " + maxlevel);
//        int minadd = 0;
//        for (int j = 0; j <= lvl1.size() - 1; j++) {
//            if ((lvl1.get(j) == maxlevel) || (j == lvl1.size() - 1)) {
//                for (int k = j; k >= 0; k--) {
//                    if (!isinTree.get(k)) {
//                        for (int p = k - 1; p >= 0; p--) {
//                            if (addnode(k, p)) {
//                                settings.outPrintln("Adding " + k + " to " + p);
//                                if (isinTree.get(p + 1)) {
//                                    cbt[p].getChildren().add(cbt[k]);
//
//                                } else {
//                                    cbt[p].getChildren().add(0, cbt[k]);
//
//                                }
//                                minadd = k;
//                                isinTree.set(k, Boolean.TRUE);
//                                break;
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//
//        for (int s = 0; s <= lvl1.size() - 1; s++) {
//            itemlist.add(cbt[s]);
//            if (lvl1.get(s) == 0) {
//                rootItem.getChildren().add(cbt[s]);
//            }
//        }
////        this.closedb();
//        return rootItem;
//    }
    public boolean addnode(int x, int y) {
        {
            if (lvl1.get(x) > lvl1.get(y)) {
                return true;
            }
        }
        return false;
    }

    public void closedb() throws SQLException {
        db.closeConnection();
    }

    public boolean isInSelectedList(int i) {
        for (int j = 0; j <= selecteditems.size() - 1; j++) {
            if (selecteditems.get(j) == i) {
                return true;
            }

        }
        return false;
    }

//    public ArrayList getTreeItems() {
//        return itemlist;
//    }
    public ArrayList getSelectedItemList() {
        return selecteditems;
    }

    public static void main(String[] args) throws SQLException {
        //  new LevelDetector(new File("MatrixDemo.java"), 0);

    }

    @Override
    public void run() {
//        if (ui.FXSplitTabs.parsingchanged.get(tabnumber)) 
        {
            try {
                updateLevels();

            } catch (SQLException ex) {
                Logger.getLogger(LevelDetector.class.getName()).log(Level.SEVERE, null, ex);
            }
//                try {
//                    ui.FXSplitTabs.root[tabnumber] = generateTree();
//                } catch (SQLException ex) {
//                    Logger.getLogger(LevelDetector.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                ui.FXSplitTabs.tree = new TreeView(ui.FXSplitTabs.root[tabnumber]);
//                ui.FXSplitTabs.tree.setEditable(true);
//                ui.FXSplitTabs.tree.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
//                Platform.runLater(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        ui.FXSplitTabs.LeftSplitPane.getItems().set(1, ui.FXSplitTabs.tree);
//                    }
//                });
//                SQLiteJDBC db = new SQLiteJDBC();
//                String sql = "SELECT * FROM SYNTAX WHERE Category LIKE '%SimulateLoop%' ;";
//                ResultSet rs = db.select(databaseLoc, sql);
//                ArrayList<Integer> bl = new ArrayList();
//                ArrayList<Integer> el = new ArrayList();
//                while (rs.next()) {
//                    bl.add(rs.getInt("BeginLine"));
//                    el.add(rs.getInt("EndLine"));
//                }
//                db.closeConnection();
//                for (int i = 0; i <= bl.size() - 1; i++) {
//                    SQLiteJDBC db2 = new SQLiteJDBC();
//                    StringBuilder sb = new StringBuilder("UPDATE SYNTAX set SIM = 'TRUE' WHERE (BeginLine < '");
//                    sb.append(bl.get(i));
//                    sb.append("' AND EndLine > '");
//                    sb.append(el.get(i));
//                    sb.append("' AND Category LIKE '%Loop%');");
//              //      String temp = "UPDATE SYNTAX set SIM = 'TRUE' WHERE (Level= ( SELECT MIN(LEVEL) FROM SYNTAX WHERE  (BeginLine < '43' "
//               //             + "AND EndLine > '43' AND Category LIKE '%Loop%'))AND BeginLine < '43' AND EndLine > '43' AND Category LIKE '%Loop%');";
//                    String sql3 = sb.toString();
//                    db2.update(databaseLoc, sql3);
//                    db2.closeConnection();
//                }
//            Util.copyFileUsingStream(new File(databaseLoc), new File("data/" + PID + databaseLoc.substring(databaseLoc.lastIndexOf("/src/"))));

//                ui.FXSplitTabs.parsingchanged.set(tabnumber, Boolean.FALSE);
        }
    }

}
