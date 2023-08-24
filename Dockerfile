FROM eclipse-temurin:17-alpine

ENV TZ="Europe/Madrid"

RUN mkdir -p /usr/local/newrelic
ARG NEWRELIC_AGENT_VERSION
ADD https://download.newrelic.com/newrelic/java-agent/newrelic-agent/${NEWRELIC_AGENT_VERSION}/newrelic-agent-${NEWRELIC_AGENT_VERSION}.jar /usr/local/newrelic/newrelic.jar
RUN chmod 444 /usr/local/newrelic/newrelic.jar
ENV JAVA_OPTS="$JAVA_OPTS -javaagent:/usr/local/newrelic/newrelic.jar"
ADD ./newrelic/newrelic.yml /usr/local/newrelic/

RUN addgroup -S app && adduser -S app -G app
USER app

ARG JAR_FILE=build/libs/job-alerts-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app/job-alerts.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/job-alerts.jar ${APP_OPTS}"]
