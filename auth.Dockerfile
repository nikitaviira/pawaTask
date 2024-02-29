FROM amazoncorretto:21 AS build
WORKDIR /app
COPY ./ /app
RUN ./gradlew :auth-service:clean :auth-service:build -x test

FROM amazoncorretto:21 AS runtime
WORKDIR /app
COPY --from=build /app/auth-service/build/libs/*.jar /app/auth-service.jar
CMD ["java", "-jar", "/app/auth-service.jar"]