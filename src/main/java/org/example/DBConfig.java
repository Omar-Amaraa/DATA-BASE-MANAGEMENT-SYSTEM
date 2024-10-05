package org.example;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DBConfig {
    private String dbpath;
    private int pagesize;
    private int dm_maxfilesize;

    public DBConfig(String dbpath, int pagesize, int dm_maxfilesize) {
        this.dbpath = dbpath;
        this.pagesize = pagesize;
        this.dm_maxfilesize = dm_maxfilesize;
    }

    public String getDbpath() {
        return dbpath;
    }
    public int getPagesize() {
        return pagesize;
    }
    public  int getDm_maxfilesize() {
        return dm_maxfilesize;
    }

    public static DBConfig LoadDBConfig(String fichier_config) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fichier_config)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            String dbpath = (String) jsonObject.get("dbpath");
            Long pagesize = (Long) jsonObject.get("pagesize");
            Long dm_maxfilesize = (Long) jsonObject.get("dm_maxfilesize");

            if (dbpath != null && pagesize != null && dm_maxfilesize != null) {
                return new DBConfig(dbpath, pagesize.intValue(), dm_maxfilesize.intValue());
            } else {
                throw new IllegalArgumentException("Invalid configuration file: required fields not found");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading configuration file", e);
        }
    }
    }
