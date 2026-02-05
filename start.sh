#!/bin/bash

# Script to quickly start the application with Neo4j for local testing
set -e

echo "Starting Spring GraphQL Neo4j Starter..."

# Start Neo4j using Docker Compose
echo "1. Starting Neo4j database..."
docker-compose up -d

# Wait for Neo4j to be ready
echo "2. Waiting for Neo4j to be ready..."
sleep 10

# Start the Spring Boot application
echo "3. Starting Spring Boot application..."
mvn spring-boot:run
