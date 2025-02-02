FROM gradle:8.10-jdk17 AS builder

ENV GRADLE_USER_HOME=/opt/gradle/caches

WORKDIR /opt/app

COPY build.gradle.kts gradlew settings.gradle.kts ./
COPY gradle gradle/

RUN ./gradlew dependencies

COPY . .

RUN ./gradlew clean build -x test

FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/app

COPY --from=builder /opt/app/build/libs/*.jar /opt/app/app.jar

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]