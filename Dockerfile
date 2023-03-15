FROM maven:3.6.3-jdk-11-slim  AS build
WORKDIR /app
COPY src ./src
COPY pom.xml .
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip

FROM gcr.io/distroless/java
COPY --from=build /usr/src/app/target/PGCI-1.0.0.jar /usr/app/PGCI-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/PGCI-1.0.0.jar"]