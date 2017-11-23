#!/bin/sh
javac -cp ".:bcprov-jdk15on-155.jar:bcpg-jdk15on-155.jar" ../Applet/src/Applet.java
sleep 1
# jar cfm Applet.jar bcprov-jdk15on-155.jar bcpg-jdk15on-155.jar manifest.txt Applet.class
jarsigner -keystore "backupKeystore"  "Applet.jar" nikita
appletviewer index.html
