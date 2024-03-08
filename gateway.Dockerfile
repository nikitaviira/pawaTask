FROM amazoncorretto:21 AS build
WORKDIR /app
COPY gateway /app/gateway
COPY gradle /app/gradle
COPY build.gradle gradlew gradlew.bat gradle.properties settings.gradle /app/
RUN ./gradlew :gateway:clean :gateway:build -x test

FROM amazoncorretto:21 AS runtime
WORKDIR /app
COPY --from=build /app/gateway/build/libs/*.jar /app/gateway-service.jar
CMD ["java", "-jar", "/app/gateway-service.jar"]