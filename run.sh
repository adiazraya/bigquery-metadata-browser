#!/bin/bash

echo "======================================"
echo "Starting IncidenciaBQ Application"
echo "======================================"
echo ""

# Parse command line arguments for Simba JDBC version
MAVEN_PROFILE=""
if [ "$1" != "" ]; then
    MAVEN_PROFILE="-P $1"
    echo "Using Simba JDBC profile: $1"
    echo ""
fi

# Set environment variable
export GOOGLE_APPLICATION_CREDENTIALS="./service-account-key.json"

# Check if JAR exists or if profile specified (rebuild needed)
if [ ! -f "target/incidencia-bq-1.0.0.jar" ] || [ "$MAVEN_PROFILE" != "" ]; then
    echo "Building application with profile: ${1:-simba-current (default)}"
    mvn clean package -DskipTests $MAVEN_PROFILE
    echo ""
fi

echo "Starting server on http://localhost:8080"
echo "Press Ctrl+C to stop"
echo ""
echo "Available Simba JDBC profiles:"
echo "  - simba-current (default): Latest version"
echo "  - simba-1.6.1: Version 1.6.1.1002"
echo "  - simba-default: Original JAR name"
echo ""

# Run the application
mvn spring-boot:run $MAVEN_PROFILE




