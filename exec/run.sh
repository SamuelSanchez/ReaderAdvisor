#!/bin/sh

#Turn Echo off
stty -echo

#Turn Echo on
#stty -echo

#Running the Java Application
javaw readerAdvisor.MainApp -server -XX:+PrintGCDetails -Dfrontend=epFrontEnd -Dmicrophone[keepLastAudio]=true -DconfigurationFile=script/software.properties