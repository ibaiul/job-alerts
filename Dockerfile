FROM eclipse-temurin:17-alpine

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ENV JAVA_OPTS="$JAVA_OPTS -javaagent:/usr/local/newrelic/newrelic.jar"
ADD ./newrelic/newrelic.yml /usr/local/newrelic/

RUN addgroup -S app && adduser -S app -G app
USER app

ARG JAR_FILE=build/libs/JobAlerts-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app/job-alerts.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/job-alerts.jar ${APP_OPTS}"]