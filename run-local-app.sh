#!/bin/bash
set -e

docker-compose -f docker/docker-compose-local.yml down
echo "Removing volume job-alerts_postgres_data"
docker volume rm job-alerts_postgres_data -f
./gradlew clean bootJar
docker build -t ibaiul/job-alerts-local .
docker-compose -f docker/docker-compose-local.yml up -d
docker logs job-alerts-app -f
