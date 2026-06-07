#FROM maven:3.9-eclipse-temurin-21 AS build
#
#WORKDIR /app
#
#COPY target/*.jar app.jar
#
#RUN mvn clean package -DskipTests
#
#FROM eclipse-temurin:21-jre
#
#WORKDIR /app
#
#COPY --from=build /app/target/*.jar app.jar
#
#EXPOSE 8080
#
#CMD ["java","-jar","app.jar"]



FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/SecureOrganDonation-0.0.1-SNAPSHOT.jar"]