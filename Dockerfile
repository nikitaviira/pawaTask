FROM amazoncorretto:21 AS build
ARG SERVICE_NAME
WORKDIR /app
COPY ${SERVICE_NAME} /app/${SERVICE_NAME}
COPY kafka /app/kafka
COPY gradle /app/gradle
COPY build.gradle gradlew gradlew.bat gradle.properties settings.gradle /app/
RUN ./gradlew :${SERVICE_NAME}:clean :${SERVICE_NAME}:build -x test

FROM amazoncorretto:21 AS runtime
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/${SERVICE_NAME}/build/libs/*.jar /app/${SERVICE_NAME}.jar