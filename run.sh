#!/bin/bash

echo "======================================"
echo "Starting IncidenciaBQ Application"
echo "======================================"
echo ""

# Set environment variable
export GOOGLE_APPLICATION_CREDENTIALS="./service-account-key.json"

# Check if JAR exists
if [ ! -f "target/incidencia-bq-1.0.0.jar" ]; then
    echo "JAR file not found. Building application..."
    mvn clean package -DskipTests
    echo ""
fi

echo "Starting server on http://localhost:8080"
echo "Press Ctrl+C to stop"
echo ""

# Run the application
mvn spring-boot:run



