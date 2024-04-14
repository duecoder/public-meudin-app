#
# Build stage
#
FROM ubuntu:latest AS build

COPY . /meudin
WORKDIR /meudin

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk maven && \
    mvn clean install && \
    rm -rf /var/lib/apt/lists/*

COPY pom.xml /meudin
COPY src /meudin/src
#
# Package stage
#
FROM openjdk:17-jdk-slim
EXPOSE 8081

WORKDIR /app
COPY --from=build /meudin/target/meudin-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
