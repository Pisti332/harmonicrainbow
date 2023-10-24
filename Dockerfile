FROM eclipse-temurin:latest
RUN mkdir /opt/harmonicrainbow
COPY target/harmonicrainbow-0.0.1-SNAPSHOT.jar /app/harmonicrainbow-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/app/harmonicrainbow-0.0.1-SNAPSHOT.jar"]