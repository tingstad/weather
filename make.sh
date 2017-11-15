#!/bin/bash
#playing with java 9 modules, building manually
set -o errexit

[ -d mods ] && rm -r mods/
mkdir mods

[ -d service/target ] && rm -r service/target/
javac -d service/target/classes/ $(find service/src/main/java/ -name \*.java)
jar --create --file mods/weather-service-1.0-SNAPSHOT.jar --module-version=1.0 -C service/target/classes/ .
jar --file mods/weather-service-1.0-SNAPSHOT.jar --describe-module

[ -d app/target ] && rm -r app/target/
javac --module-path mods/ -d app/target/classes/ $(find app/src/main/java/ -name \*.java)
jar --create --file mods/weather-app-1.0-SNAPSHOT.jar --module-version=1.0 --main-class=com.github.tingstad.weather.app.Main -C app/target/classes/ .
jar --file mods/weather-app-1.0-SNAPSHOT.jar --describe-module

echo 'java --module-path mods/ --module com.github.tingstad.weather.app 8080'
