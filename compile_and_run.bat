\
    @echo off
    if not exist bin mkdir bin
    dir /b /s src\*.java > sources.txt
    javac -d bin @sources.txt
    java -cp bin app.Main
