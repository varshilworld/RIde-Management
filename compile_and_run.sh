#!/bin/bash
set -e
SRC=src
BIN=bin
mkdir -p $BIN
find $SRC -name "*.java" > sources.txt
javac -d $BIN @sources.txt
java -cp $BIN app.Main
