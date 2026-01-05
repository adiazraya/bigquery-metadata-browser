#!/bin/bash

echo "╔══════════════════════════════════════════════════════════════════════════════╗"
echo "║              Simba JDBC 403 Error - Diagnostic & Fix Script                  ║"
echo "╚══════════════════════════════════════════════════════════════════════════════╝"
echo ""

# Check which version is currently in use
echo "📦 Checking current Simba JDBC version..."
CURRENT_JAR=$(jar tf target/incidencia-bq-1.0.0.jar 2>/dev/null | grep "googlebigquery-jdbc")
if [ -n "$CURRENT_JAR" ]; then
    JAR_SIZE=$(jar tf target/incidencia-bq-1.0.0.jar 2>/dev/null | grep -c "com/simba")
    echo "   Found: $CURRENT_JAR"
    echo "   Size indicator: $JAR_SIZE classes"
else
    echo "   ⚠️  No JAR built yet"
fi
echo ""

# Check available versions
echo "📁 Available Simba JDBC versions:"
ls -lh lib/GoogleBigQueryJDBC42*.jar 2>/dev/null | awk '{print "   - " $9, "(" $5 ")"}'
echo ""

# Check service account key
echo "🔑 Checking service account key..."
if [ -f "service-account-key.json" ]; then
    echo "   ✅ service-account-key.json exists"
    
    # Check if it's valid JSON
    if jq empty service-account-key.json 2>/dev/null; then
        echo "   ✅ Valid JSON format"
        PROJECT_ID=$(jq -r '.project_id' service-account-key.json)
        CLIENT_EMAIL=$(jq -r '.client_email' service-account-key.json)
        echo "   📧 Service Account: $CLIENT_EMAIL"
        echo "   🏢 Project: $PROJECT_ID"
    else
        echo "   ❌ Invalid JSON format!"
    fi
    
    # Check if absolute path is needed
    FULL_PATH=$(pwd)/service-account-key.json
    echo "   💡 Absolute path: $FULL_PATH"
else
    echo "   ❌ service-account-key.json NOT found!"
fi
echo ""

# Check recent errors in logs
echo "📋 Recent errors from logs:"
if [ -f "logs/incidencia-bq.log" ]; then
    echo "   Last 403 error:"
    tail -200 logs/incidencia-bq.log | grep -A 3 "403\|Access Denied" | tail -10 | sed 's/^/   │ /'
else
    echo "   ⚠️  No log file found"
fi
echo ""

# Suggest fixes
echo "╔══════════════════════════════════════════════════════════════════════════════╗"
echo "║                           RECOMMENDED FIXES                                   ║"
echo "╚══════════════════════════════════════════════════════════════════════════════╝"
echo ""
echo "🔧 FIX #1: Try the other Simba JDBC version"
echo "   The 403 error often happens with version incompatibility."
echo ""
echo "   Current size indicator: $JAR_SIZE classes"
echo ""
echo "   Try this:"
if [ "$JAR_SIZE" -gt "100" ]; then
    echo "   $ ./switch_simba.sh 1.6.1    # Switch to older version"
else
    echo "   $ ./switch_simba.sh current  # Switch to newer version"
fi
echo "   $ ./run.sh"
echo ""

echo "🔧 FIX #2: Use absolute path for service account key"
echo "   Some Simba versions require absolute paths."
echo ""
echo "   Edit application.properties and change:"
echo "   FROM: bigquery.service.account.key.path=./service-account-key.json"
echo "   TO:   bigquery.service.account.key.path=$FULL_PATH"
echo ""

echo "🔧 FIX #3: Verify BigQuery permissions"
echo "   Ensure service account has these roles:"
echo "   - roles/bigquery.dataViewer"
echo "   - roles/bigquery.metadataViewer"
echo "   - roles/bigquery.user"
echo ""
echo "   Check with:"
echo "   $ gcloud projects get-iam-policy $PROJECT_ID \\"
echo "       --flatten='bindings[].members' \\"
echo "       --filter='bindings.members:$CLIENT_EMAIL'"
echo ""

echo "🔧 FIX #4: Test with API method instead"
echo "   Compare JDBC vs API to isolate the issue:"
echo "   1. Open: http://localhost:8080"
echo "   2. Use 'API Method' tab"
echo "   3. If API works → It's a JDBC driver issue"
echo ""

echo "═══════════════════════════════════════════════════════════════════════════════"
echo ""
echo "🚀 Quick action: Run the most likely fix now?"
echo ""
read -p "Switch to alternate Simba version and rebuild? (y/n) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "Switching Simba version and rebuilding..."
    
    if [ "$JAR_SIZE" -gt "100" ]; then
        ./switch_simba.sh 1.6.1
    else
        ./switch_simba.sh current
    fi
    
    echo ""
    echo "✅ Done! Now run: ./run.sh"
fi

echo ""
echo "📚 For more details, see: JDBC_403_ERROR_FIX.md"
echo ""



