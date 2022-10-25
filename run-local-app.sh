#!/bin/bash

./gradlew clean bootJar
docker build -t ibaiul/job-alerts-local .
docker-compose -f docker/docker-compose-local.yml -p job-alerts up
