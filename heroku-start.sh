#!/bin/bash

# Heroku startup script for BigQuery Metadata Browser
# This script handles service account credentials from environment variables

echo "Starting BigQuery Metadata Browser on Heroku..."

# Check if GOOGLE_APPLICATION_CREDENTIALS_JSON is set
if [ -n "$GOOGLE_APPLICATION_CREDENTIALS_JSON" ]; then
    echo "📝 Writing service account credentials to /tmp/service-account-key.json"
    echo "$GOOGLE_APPLICATION_CREDENTIALS_JSON" > /tmp/service-account-key.json
    
    # Verify it's valid JSON
    if echo "$GOOGLE_APPLICATION_CREDENTIALS_JSON" | jq empty 2>/dev/null; then
        echo "✅ Service account credentials validated"
    else
        echo "⚠️  WARNING: Service account JSON might be invalid"
    fi
    
    # Set environment variable to point to the file
    export GOOGLE_APPLICATION_CREDENTIALS=/tmp/service-account-key.json
else
    echo "⚠️  WARNING: GOOGLE_APPLICATION_CREDENTIALS_JSON not set!"
    echo "   The application may not be able to access BigQuery."
fi

# Start the application
echo "🚀 Starting application on port $PORT..."
exec java -Dserver.port=$PORT \
    -Dspring.profiles.active=production \
    $JAVA_OPTS \
    -jar target/incidencia-bq-1.0.0.jar

