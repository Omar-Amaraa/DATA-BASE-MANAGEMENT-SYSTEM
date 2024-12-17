package org.example;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DBConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dbpath;
    private int pagesize;
    private int dm_maxfilesize;
    private int bm_buffercount;
    private String bm_policy;

    public DBConfig(String dbpath, int pagesize, int dm_maxfilesize, int bm_buffercount, String bm_policy) {
        this.dbpath = dbpath;
        this.pagesize = pagesize;
        this.dm_maxfilesize = dm_maxfilesize;
        this.bm_buffercount = bm_buffercount;
        this.bm_policy = bm_policy;
    }
    public DBConfig(String fichier_config) {
        DBConfig config = LoadDBConfig(fichier_config);
        this.dbpath = config.getDbpath();
        this.pagesize = config.getPagesize();
        this.dm_maxfilesize = config.getDm_maxfilesize();
        this.bm_buffercount = config.getBm_buffercount();
        this.bm_policy = config.getBm_policy();
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
    public int getBm_buffercount() {
        return bm_buffercount;
    }
    public String getBm_policy() {
        return bm_policy;
    }

    public void setBm_policy(String bm_policy) {
        this.bm_policy = bm_policy;
    }

    public static DBConfig LoadDBConfig(String fichier_config) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fichier_config)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            String dbpath = (String) jsonObject.get("dbpath");
            Long pagesize = (Long) jsonObject.get("pagesize");
            Long dm_maxfilesize = (Long) jsonObject.get("dm_maxfilesize");
            Long bm_buffercount = (Long) jsonObject.get("bm_buffercount");
            String bm_policy = (String) jsonObject.get("bm_policy");

            if (dbpath != null && pagesize != null && dm_maxfilesize != null) {
                return new DBConfig(dbpath, pagesize.intValue(), dm_maxfilesize.intValue(), bm_buffercount.intValue(), bm_policy);
            } else {
                throw new IllegalArgumentException("Invalid configuration file: required fields not found");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading configuration file", e);
        }


    }
}
