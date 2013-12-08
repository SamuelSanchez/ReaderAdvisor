@echo off

REM Running the Java Application
start javaw readerAdvisor.MainApp -server -XX:+PrintGCDetails -Dfrontend=epFrontEnd -Dmicrophone[keepLastAudio]=true -DconfigurationFile=script/software.properties