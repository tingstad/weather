web: java --module-path $(ls app/target/*.jar):$(mvn -B -f app/pom.xml dependency:build-classpath -DincludeScope=runtime|grep -v ^\\[) --module com.github.tingstad.weather.app/com.github.tingstad.weather.app.MainWeb $PORT
web: java --module-path $(ls app/target/*.jar):app/target/dependency/ --module com.github.tingstad.weather.app/com.github.tingstad.weather.app.MainWeb $PORT

