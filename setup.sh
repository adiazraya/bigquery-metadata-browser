#!/bin/bash

echo "======================================"
echo "IncidenciaBQ - Setup Script"
echo "======================================"
echo ""

# Check Java
echo "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "✓ Java version: $JAVA_VERSION"
else
    echo "✗ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Maven
echo "Checking Maven installation..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo "✓ $MVN_VERSION"
else
    echo "✗ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo ""
echo "======================================"
echo "Service Account Configuration"
echo "======================================"
echo ""

# Check for service account key
if [ ! -f "service-account-key.json" ]; then
    echo "⚠ Service account key not found!"
    echo ""
    echo "Please follow these steps:"
    echo "1. Go to Google Cloud Console"
    echo "2. Navigate to IAM & Admin > Service Accounts"
    echo "3. Find: datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com"
    echo "4. Create and download a JSON key"
    echo "5. Save it as 'service-account-key.json' in this directory"
    echo ""
    echo "An example file has been created: service-account-key.json.example"
    echo ""
    read -p "Press Enter once you've added the service account key..."
    
    if [ ! -f "service-account-key.json" ]; then
        echo "✗ Service account key still not found. Exiting."
        exit 1
    fi
fi

echo "✓ Service account key found"
echo ""

# Set environment variable
export GOOGLE_APPLICATION_CREDENTIALS="./service-account-key.json"
echo "✓ GOOGLE_APPLICATION_CREDENTIALS set"
echo ""

echo "======================================"
echo "Building Application"
echo "======================================"
echo ""

# Clean and build
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "======================================"
    echo "✓ Build Successful!"
    echo "======================================"
    echo ""
    echo "To run the application:"
    echo "  ./run.sh"
    echo ""
    echo "Or manually:"
    echo "  mvn spring-boot:run"
    echo ""
    echo "Once running, open: http://localhost:8080"
    echo ""
else
    echo ""
    echo "✗ Build failed. Please check the errors above."
    exit 1
fi



