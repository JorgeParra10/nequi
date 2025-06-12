
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/NEQUI-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java","-jar","/app/app.jar"]
