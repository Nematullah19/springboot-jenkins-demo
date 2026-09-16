FROM eclipse-temurin:21-jre-noble

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        softhsm2 \
        opensc && \
    rm -rf /var/lib/apt/lists/* && \
    chmod 755 /etc/softhsm && \
    chmod 755 /var/lib/softhsm && \
    mkdir -p /etc/hsm-lab && \
    chmod 755 /etc/hsm-lab

WORKDIR /app

COPY target/jenkins-demo-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8081

EXPOSE 8081

USER 10001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
