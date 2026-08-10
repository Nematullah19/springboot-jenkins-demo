
FROM eclipse-temurin:21-jre-ubi9-minimal

WORKDIR /app

COPY target/jenkins-demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

USER 10001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
