@echo off
set arg1=%1
javac -cp .\lib\json-simple-1.1.1.jar -d .\bin .\src\main\java\org\example\*.java
java -cp .\lib\json-simple-1.1.1.jar;.\bin org.example.SGBD %arg1%
