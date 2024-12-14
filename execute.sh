javac -cp ./lib/json-simple-1.1.1.jar -d ./target/classes ./src/main/java/org/example/*.java
java -cp ./target/classes:./lib/json-simple-1.1.1.jar org.example.SGBD