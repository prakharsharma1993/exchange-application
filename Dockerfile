FROM openjdk:17-oracle

WORKDIR /app

COPY target/Exchange-0.0.1-SNAPSHOT.jar app/Exchange-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "app/Exchange-0.0.1-SNAPSHOT.jar"]
