package org.example;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class Main {

        public static void main(String[] args) {
            JSONParser jsonP = new JSONParser();
            try {
                JSONObject jsonO = (JSONObject)jsonP.parse(new FileReader("C:/PROJET_BDDA/files/dataset_1.json"));

                String name = (String) jsonO.get("name");
                String age = (String) jsonO.get("age");
                String address = (String) jsonO.get("address");
                System.out.println("Name :"+ name);
                System.out.println("Age: "+ age);
                System.out.println("Address: "+ address);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

}