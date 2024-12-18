package org.example;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DBConfig {
    private static String dbpath;
    private static int pagesize;
    private static int dm_maxfilesize;
    private static int bm_buffercount;
    private static String bm_policy;

    public DBConfig(String dbpath, int pagesize, int dm_maxfilesize, int bm_buffercount, String bm_policy) {
        DBConfig.dbpath = dbpath;
        DBConfig.pagesize = pagesize;
        DBConfig.dm_maxfilesize = dm_maxfilesize;
        DBConfig.bm_buffercount = bm_buffercount;
        DBConfig.bm_policy = bm_policy;
    }
    public DBConfig(String fichier_config) {
        DBConfig config = LoadDBConfig(fichier_config);
    }

    public static String getDbpath() {
        return dbpath;
    }
    public static int getPagesize() {
        return pagesize;
    }
    public static int getDm_maxfilesize() {
        return dm_maxfilesize;
    }
    public static int getBm_buffercount() {
        return bm_buffercount;
    }
    public static String getBm_policy() {
        return bm_policy;
    }

    public static void setBm_policy(String bm_policy) {
        DBConfig.bm_policy = bm_policy;
    }

    public static DBConfig LoadDBConfig(String fichier_config) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fichier_config)) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            String configDbpath = (String) jsonObject.get("dbpath");
            if (configDbpath == null || configDbpath.isEmpty()) {
                throw new IllegalArgumentException("Invalid configuration file: dbpath is missing or empty");
            }
            File dbPathFile = new File(configDbpath);
            if (!dbPathFile.exists()) {
                if (!dbPathFile.mkdirs()) {
                    throw new IOException("Failed to create directory: " + configDbpath);
                }
            }
            Long configPagesize = (Long) jsonObject.get("pagesize");
            if (configPagesize == null || configPagesize <= 0) {
                throw new IllegalArgumentException("Invalid configuration file: pagesize is missing or invalid");
            }

            Long configDm_maxfilesize = (Long) jsonObject.get("dm_maxfilesize");
            if (configDm_maxfilesize == null || configDm_maxfilesize <= 0) {
                throw new IllegalArgumentException("Invalid configuration file: dm_maxfilesize is missing or invalid");
            }

            Long configBm_buffercount = (Long) jsonObject.get("bm_buffercount");
            if (configBm_buffercount == null || configBm_buffercount <= 0) {
                throw new IllegalArgumentException("Invalid configuration file: bm_buffercount is missing or invalid");
            }

            String configBm_policy = (String) jsonObject.get("bm_policy");
            if (configBm_policy == null || configBm_policy.isEmpty()) {
                throw new IllegalArgumentException("Invalid configuration file: bm_policy is missing or empty");
            }

            return new DBConfig(configDbpath, configPagesize.intValue(), configDm_maxfilesize.intValue(), configBm_buffercount.intValue(), configBm_policy);
            
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading configuration file", e);
        }


    }
}

