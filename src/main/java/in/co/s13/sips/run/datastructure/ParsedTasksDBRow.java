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

    public JSONArray getResources() {
        return resources;
    }

    public void setResources(JSONArray resources) {
        this.resources = resources;
    }

    public JSONArray getFiles() {
        return files;
    }

    public void setFiles(JSONArray files) {
        this.files = files;
    }

    public ConcurrentHashMap<String, Integer> getBeginLine() {
        return beginLine;
    }

    public void setBeginLine(ConcurrentHashMap<String, Integer> beginLine) {
        this.beginLine = beginLine;
    }

    public ConcurrentHashMap<String, Integer> getBeginColumn() {
        return beginColumn;
    }

    public void setBeginColumn(ConcurrentHashMap<String, Integer> beginColumn) {
        this.beginColumn = beginColumn;
    }

    public ConcurrentHashMap<String, Integer> getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(ConcurrentHashMap<String, Integer> endColumn) {
        this.endColumn = endColumn;
    }

    public ConcurrentHashMap<String, Integer> getEndLine() {
        return endLine;
    }

    public void setEndLine(ConcurrentHashMap<String, Integer> endLine) {
        this.endLine = endLine;
    }

}
