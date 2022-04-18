FROM openjdk:11-jdk-slim
EXPOSE 9082
ADD target/articles-service-0.0.1-SNAPSHOT.jar /usr/local/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container", "-jar", "/usr/local/app.jar"]
