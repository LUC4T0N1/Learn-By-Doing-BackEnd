#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
RUN ls -la
RUN pwd
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM registry.access.redhat.com/ubi8/openjdk-11:1.11
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --chown=185 target/LBD-railway.jar /deployments/

EXPOSE 8080
USER 185
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/LBD-railway.jar"



