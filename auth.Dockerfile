FROM amazoncorretto:21 AS build
WORKDIR /app
COPY auth-service /app/auth-service
COPY kafka /app/kafka
COPY gradle /app/gradle
COPY build.gradle gradlew gradlew.bat gradle.properties settings.gradle /app/
RUN ./gradlew :auth-service:clean :auth-service:build -x test

FROM amazoncorretto:21 AS runtime
WORKDIR /app
COPY --from=build /app/auth-service/build/libs/*.jar /app/auth-service.jar
CMD ["java", "-jar", "/app/auth-service.jar"]