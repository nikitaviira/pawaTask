FROM amazoncorretto:21 AS build
WORKDIR /app
COPY ./ /app
RUN ./gradlew :gateway:clean :gateway:build -x test

FROM amazoncorretto:21 AS runtime
WORKDIR /app
COPY --from=build /app/gateway/build/libs/*.jar /app/gateway-service.jar
CMD ["java", "-jar", "/app/gateway-service.jar"]