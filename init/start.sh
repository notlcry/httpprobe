#!/bin/sh
nohup /opt/jdk/jdk1.8.0_111/bin/java -jar /opt/httpProbe.jar $1 $2 1000 1 1 &
