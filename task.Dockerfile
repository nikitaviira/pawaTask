FROM amazoncorretto:21 AS build
WORKDIR /app
COPY ./ /app
RUN ./gradlew :task-service:clean :task-service:build -x test

FROM amazoncorretto:21 AS runtime
WORKDIR /app
COPY --from=build /app/task-service/build/libs/*.jar /app/task-service.jar
CMD ["java", "-jar", "/app/task-service.jar"]