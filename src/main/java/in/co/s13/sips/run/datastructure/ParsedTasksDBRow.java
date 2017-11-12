/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13.sips.run.datastructure;

import org.json.JSONArray;

public class ParsedTasksDBRow {
    private int id;
    private String name;
    private JSONArray resources;
    private JSONArray files;
    private float weight;
    private long timeout;
    
}
