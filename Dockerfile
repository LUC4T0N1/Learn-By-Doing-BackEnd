FROM openjdk:11.0.6
ADD ./PGCI-1.0.0.jar PGCI-1.0.0.jar
ENTRYPOINT ["java","-jar","PGCI-1.0.0.jar"]