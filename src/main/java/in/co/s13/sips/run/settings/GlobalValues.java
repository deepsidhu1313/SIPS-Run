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
package in.co.s13.sips.run.settings;

import in.co.s13.SIPS.db.SQLiteJDBC;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

/**
 *
 * @author nika
 */
public class GlobalValues {

    /**
     * Meta Info Operating System
     */
    public static String OS = System.getProperty("os.name").toLowerCase();
    public static int OS_Name = 0;
    public static JSONObject MANIFEST_JSON;
    public static SQLiteJDBC sqljdbcTask = new SQLiteJDBC();
    public static ExecutorService taskParserExecutor = Executors.newFixedThreadPool(1);
    public static AtomicInteger taskCounter = new AtomicInteger(0);
    

}
