#!/bin/bash
#playing with java 9 modules, building manually
set -o errexit

main() {
    [ -d mods ] && rm -r mods/
    mkdir mods
    REPO="$(mvn -B help:evaluate -Dexpression=settings.localRepository | grep -v ^\\[)"
    cp \
        "$REPO"/ch/qos/logback/logback-classic/1.2.3/*.jar \
        "$REPO"/ch/qos/logback/logback-core/1.2.3/*.jar \
        "$REPO"/org/slf4j/slf4j-api/1.7.25/*.jar \
        mods/

    for module in service-api cache http-client service-yr service-ruter sms-service
    do
        build $module
    done
    build app com.github.tingstad.weather.app.MainWeb
    echo 'java --module-path mods/ --module com.github.tingstad.weather.app 8080'
    echo 'java --module-path mods/ --module com.github.tingstad.weather.app/com.github.tingstad.weather.app.MainJob'
}

build() {
    name="$1"
    main="$([ -n "$2" ] && echo "--main-class=$2 " || echo "")"
    [ -d $name/target ] && rm -r $name/target/
    javac --module-path mods/ -d $name/target/classes/ $(find $name/src/main/java/ -name \*.java)
    [ -d $name/src/main/resources ] && cp $name/src/main/resources/* $name/target/classes
    jar --create --file mods/weather-$name-1.0-SNAPSHOT.jar --module-version=1.0 ${main}-C $name/target/classes/ .
    jar --file mods/weather-$name-1.0-SNAPSHOT.jar --describe-module
}

main

