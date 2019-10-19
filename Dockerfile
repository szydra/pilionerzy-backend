FROM openjdk:8-jdk-alpine
LABEL maintainer="szymon.draga@gmail.com"
VOLUME /tmp
EXPOSE 8080
COPY target/pilionerzy*.jar pilionerzy.jar
COPY application.properties questions.yaml ./
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/pilionerzy.jar"]
