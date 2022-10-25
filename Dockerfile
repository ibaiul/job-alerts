FROM eclipse-temurin:17-alpine

RUN addgroup -S app && adduser -S app -G app
USER app

ARG JAR_FILE=build/libs/JobAlerts-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app/job-alerts.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/job-alerts.jar ${APP_OPTS}"]