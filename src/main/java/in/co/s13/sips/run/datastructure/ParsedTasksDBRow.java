/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.sips.run.datastructure;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;

public class ParsedTasksDBRow {

    private int id;
    private String name;
    private JSONArray resources = new JSONArray();
    private JSONArray files = new JSONArray();
    private float weight = -1.0f;
    private long timeout = TimeUnit.DAYS.toSeconds(1);
    private ConcurrentHashMap<String, Integer> beginLine = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> beginColumn = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> endColumn = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> endLine = new ConcurrentHashMap<>();

    public ParsedTasksDBRow(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
