FROM maven:3.6.3-jdk-11-slim  AS build
RUN cp src /usr/src/app/src
RUN cp pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM gcr.io/distroless/java
COPY --from=build /usr/src/app/target/PGCI-1.0.0.jar /usr/app/PGCI-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/PGCI-1.0.0.jar"]