FROM adoptopenjdk/openjdk11:alpine-jre
LABEL maintainer="szymon.draga@gmail.com"
VOLUME /tmp
EXPOSE 8080
COPY target/pilionerzy*.jar pilionerzy.jar
COPY questions.yaml ./
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/pilionerzy.jar"]
