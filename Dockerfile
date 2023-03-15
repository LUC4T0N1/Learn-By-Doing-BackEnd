#
# Build stage
#
FROM maven:3.6.3-jdk-11 AS build
# COPY src /home/app/src
# COPY pom.xml /home/app
RUN mvn  clean package

#
# Package stage
#
FROM openjdk:11
COPY --from=build /home/app/target/PGCI-1.0.0.jar /usr/local/lib/demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/demo.jar"]