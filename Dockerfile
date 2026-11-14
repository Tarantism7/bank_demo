# for jar
#FROM eclipse-temurin:25-jdk
#
#WORKDIR /app
#
#COPY target/bankcards-1.0.0.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "app.jar"]

# for project
FROM maven:3.9-eclipse-temurin-25

WORKDIR /app

COPY . .

EXPOSE 8080

CMD ["mvn", "spring-boot:run"]