#!/bin/bash
#playing with java 9 modules, building manually
set -o errexit

main() {
    [ -d mods ] && rm -r mods/
    mkdir mods

    for module in service-api ;do
        build $module
    done
    build app com.github.tingstad.weather.app.Main
    echo 'java --module-path mods/ --module com.github.tingstad.weather.app 8080'
}

build() {
    name="$1"
    main="$([ -n "$2" ] && echo "--main-class=$2 " || echo "")"
    [ -d $name/target ] && rm -r $name/target/
    javac --module-path mods/ -d $name/target/classes/ $(find $name/src/main/java/ -name \*.java)
    jar --create --file mods/weather-$name-1.0-SNAPSHOT.jar --module-version=1.0 ${main}-C $name/target/classes/ .
    jar --file mods/weather-$name-1.0-SNAPSHOT.jar --describe-module
}

main

