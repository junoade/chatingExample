FROM openjdk:17
LABEL authors="junhochoi"

ARG JAR_FILE=build/libs/chatserver-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]