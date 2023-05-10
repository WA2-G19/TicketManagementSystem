FROM node:16.19 AS buildClient
WORKDIR /client

COPY ./client/package.json .
COPY ./client/package-lock.json .
RUN npm install --silent

COPY ./client .

RUN npm run build

FROM gradle:8.0.2-jdk17 AS buildServer
WORKDIR /server

COPY --chown=gradle:gradle ./server .
RUN mkdir -p ./src/main/resources/static
COPY --from=buildClient /client/build/. ./src/main/resources/static
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:17-jdk

EXPOSE 8080

WORKDIR /app

COPY --from=buildServer /server/build/libs/*.jar ./spring-boot-application.jar

RUN echo "spring.datasource.url=jdbc:postgresql://database:5432/TicketManagementSystem" >> application.properties
RUN echo "keycloakBaseUrl=http://keycloak:8080" >> application.properties

ENTRYPOINT ["java","-jar","/app/spring-boot-application.jar"]