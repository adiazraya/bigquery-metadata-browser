#!/bin/bash

# Quick script to switch Simba JDBC driver versions
# Usage: ./switch_simba.sh [version]
# Examples:
#   ./switch_simba.sh current
#   ./switch_simba.sh 1.6.1
#   ./switch_simba.sh default

VERSION=${1:-current}

echo "======================================"
echo "Simba JDBC Driver Version Switcher"
echo "======================================"
echo ""

case $VERSION in
    current|latest)
        PROFILE="simba-current"
        echo "Switching to: Current/Latest version"
        ;;
    1.6.1|old|older)
        PROFILE="simba-1.6.1"
        echo "Switching to: Version 1.6.1.1002 (older)"
        ;;
    default|original)
        PROFILE="simba-default"
        echo "Switching to: Original JAR name"
        ;;
    *)
        echo "Unknown version: $VERSION"
        echo ""
        echo "Available options:"
        echo "  current, latest    - Use current/latest version (default)"
        echo "  1.6.1, old, older  - Use version 1.6.1.1002"
        echo "  default, original  - Use original JAR name"
        echo ""
        echo "Usage: ./switch_simba.sh [version]"
        exit 1
        ;;
esac

echo "Profile: $PROFILE"
echo ""

# Clean and rebuild with the selected profile
echo "Cleaning previous build..."
mvn clean -q

echo "Building with $PROFILE..."
mvn package -DskipTests -P $PROFILE

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Successfully built with $PROFILE"
    echo ""
    echo "Verifying JAR contents..."
    jar tf target/incidencia-bq-1.0.0.jar | grep -i "googlebigquery" | head -3
    echo ""
    echo "To run the application:"
    echo "  java -jar target/incidencia-bq-1.0.0.jar"
    echo "  OR"
    echo "  ./run.sh $PROFILE"
else
    echo ""
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi



