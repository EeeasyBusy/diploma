FROM openjdk:17-jdk-alpine

EXPOSE 8080

COPY target/demoTest-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]