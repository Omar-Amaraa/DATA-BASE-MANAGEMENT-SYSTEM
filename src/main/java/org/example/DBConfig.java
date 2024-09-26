package org.example;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DBConfig {
    String dbpath;

    public DBConfig(String dbpath) {
        this.dbpath = dbpath;
    }

    public String getDbpath() {
        return dbpath;
    }

    public static DBConfig loadDBConfig(String fichier_config){
        DBConfig config = new DBConfig(fichier_config);

        JSONParser jsonP = new JSONParser();
        try {
            JSONObject jsonO = (JSONObject)jsonP.parse(new FileReader(fichier_config));
            String name = (String) jsonO.get("name");
            String age = (String) jsonO.get("age");
            String address = (String) jsonO.get("address");
            System.out.println("Name :"+ name);
            System.out.println("Age: "+ age);
            System.out.println("Address: "+ address);
            return config;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;


    }
}

