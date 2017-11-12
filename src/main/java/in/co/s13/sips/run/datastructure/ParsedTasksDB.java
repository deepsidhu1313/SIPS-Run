/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.sips.run.datastructure;

import in.co.s13.SIPS.db.SQLiteJDBC;
import java.util.concurrent.ConcurrentHashMap;


public class ParsedTasksDB {

    private ConcurrentHashMap<String, ParsedTasksDBRow> db = new ConcurrentHashMap<>();

    public ParsedTasksDB() {

    }

    public void add(String key, ParsedTasksDBRow value) {
        this.db.put(key, value);
    }

    public ParsedTasksDBRow get(String key) {
        return this.db.get(key);
    }

    public void toSQLiteDB(String file) {
        SQLiteJDBC sqliteDB= new SQLiteJDBC();
        
    }

}
