package org.example;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DBConfig {
    String dbpath;
    long pagesize;
    long dm_maxfilesize;

    public DBConfig(String dbpathlong,long pagesize, long dm_maxfilesize) {
        this.dbpath = dbpath;
        this.pagesize = pagesize;
        this.dm_maxfilesize = dm_maxfilesize;
    }

    public long getPagesize() {
        return pagesize;
    }

    public long getDm_maxfilesize() {
        return dm_maxfilesize;
    }

    public String getDbpath() {
        return dbpath;
    }

    public static DBConfig loadDBConfig(String fichier_config){

        JSONParser jsonP = new JSONParser();
        try {
            JSONObject jsonO = (JSONObject) jsonP.parse(new FileReader(fichier_config));
            String dbpath = (String) jsonO.get("dbpath");
            long pagesize = (long) jsonO.get("pagesize");
            long dm_maxfilesize = (long) jsonO.get("dm_maxfilesize");

            return new DBConfig(dbpath, pagesize, dm_maxfilesize);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }


    }
}

