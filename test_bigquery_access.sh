#!/bin/bash

# Script to test and verify BigQuery access is working
# Even if you can't see logs in Google Cloud Console

echo "=========================================="
echo "BigQuery Access Verification Test"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8080"

echo "1. Testing API Version (REST)..."
echo "   Endpoint: GET /api/bigquery/datasets"
START=$(date +%s)
DATASETS_API=$(curl -s ${BASE_URL}/api/bigquery/datasets)
END=$(date +%s)
DURATION=$((END - START))
COUNT_API=$(echo $DATASETS_API | jq 'length' 2>/dev/null || echo "0")
echo "   ✓ Returned: $COUNT_API datasets in ${DURATION}s"
echo ""

echo "2. Testing JDBC Version..."
echo "   Endpoint: GET /api/bigquery-jdbc/datasets"
START=$(date +%s)
DATASETS_JDBC=$(curl -s ${BASE_URL}/api/bigquery-jdbc/datasets)
END=$(date +%s)
DURATION=$((END - START))
COUNT_JDBC=$(echo $DATASETS_JDBC | jq 'length' 2>/dev/null || echo "0")
echo "   ✓ Returned: $COUNT_JDBC datasets in ${DURATION}s"
echo ""

echo "3. Testing Table Listing (API)..."
echo "   Endpoint: GET /api/bigquery/datasets/GeneralItems/tables"
START=$(date +%s)
TABLES_API=$(curl -s ${BASE_URL}/api/bigquery/datasets/GeneralItems/tables)
END=$(date +%s)
DURATION=$((END - START))
COUNT_TABLES_API=$(echo $TABLES_API | jq 'length' 2>/dev/null || echo "0")
echo "   ✓ Returned: $COUNT_TABLES_API tables in ${DURATION}s"
echo ""

echo "4. Testing Table Listing (JDBC)..."
echo "   Endpoint: GET /api/bigquery-jdbc/datasets/GeneralItems/tables"
START=$(date +%s)
TABLES_JDBC=$(curl -s ${BASE_URL}/api/bigquery-jdbc/datasets/GeneralItems/tables)
END=$(date +%s)
DURATION=$((END - START))
COUNT_TABLES_JDBC=$(echo $TABLES_JDBC | jq 'length' 2>/dev/null || echo "0")
echo "   ✓ Returned: $COUNT_TABLES_JDBC tables in ${DURATION}s"
echo ""

echo "=========================================="
echo "Summary:"
echo "=========================================="
if [ "$COUNT_API" -gt 0 ] && [ "$COUNT_JDBC" -gt 0 ]; then
    echo "✅ SUCCESS: Both API and JDBC are accessing BigQuery!"
    echo ""
    echo "Your application IS querying BigQuery successfully."
    echo "You just can't see it in Google Cloud Console due to permissions."
    echo ""
    echo "To monitor activity, check the application logs:"
    echo "  tail -f logs/incidencia-bq.log"
else
    echo "❌ ISSUE: One or both methods failed"
    echo "   API datasets: $COUNT_API"
    echo "   JDBC datasets: $COUNT_JDBC"
fi
echo "=========================================="



