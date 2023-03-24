FROM gradle:8.0.2-jdk17-alpine AS build
WORKDIR ./client

RUN npm run build

RUN mkdir -p ../server/src/main/resources/static && cp -a ./build/. ../server/src/main/resources/static

WORKDIR ./server

COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle build -x test --no-daemon
WORKDIR /home/gradle/src

FROM eclipse-temurin:17-jdk-alpine

EXPOSE 8080
EXPOSE 3000

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]