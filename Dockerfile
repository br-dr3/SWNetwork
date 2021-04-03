FROM openjdk:8-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080 5005
ENTRYPOINT ["java", "-jar", "/app.jar"]