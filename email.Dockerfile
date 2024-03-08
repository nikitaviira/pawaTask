FROM amazoncorretto:21 AS build
WORKDIR /app
COPY email-service /app/email-service
COPY kafka /app/kafka
COPY gradle /app/gradle
COPY build.gradle gradlew gradlew.bat gradle.properties settings.gradle /app/
RUN ./gradlew :email-service:clean :email-service:build -x test

FROM amazoncorretto:21 AS runtime
WORKDIR /app
COPY --from=build /app/email-service/build/libs/*.jar /app/email-service.jar
CMD ["java", "-jar", "/app/email-service.jar"]