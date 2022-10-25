#!/bin/bash

docker-compose -f docker/docker-compose-local.yml -p job-alerts down
echo "Removing volume job-alerts_postgres_data"
docker volume rm job-alerts_postgres_data
