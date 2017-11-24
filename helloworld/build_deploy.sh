#!/bin/bash
javac HelloWorld.java
sleep 1
jar cfm Hello.jar manifest.txt HelloWorld.class
sleep 1
jarsigner -keystore ~/.keystore Hello.jar nikita
