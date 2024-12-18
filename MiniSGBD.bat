javac -cp .\lib\json-simple-1.1.1.jar -d .\bin .\src\main\java\org\example\*.java
java -cp .\lib\json-simple-1.1.1.jar;.\bin org.example.SGBD ./configDB.json
