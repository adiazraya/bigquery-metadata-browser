#!/bin/bash

# Daily Activity Report for BigQuery Application
# Generates a summary of all activity for today

TODAY=$(date +%Y-%m-%d)
LOGFILE="logs/incidencia-bq.log"

echo "=========================================="
echo "  Daily Activity Report"
echo "  Date: $TODAY"
echo "=========================================="
echo ""

# Check if log file exists
if [ ! -f "$LOGFILE" ]; then
  echo "❌ Error: Log file not found at $LOGFILE"
  exit 1
fi

# Total requests
echo "📊 Total Requests:"
TOTAL=$(grep "$TODAY" "$LOGFILE" | grep "Received GET" | wc -l | tr -d ' ')
API=$(grep "$TODAY" "$LOGFILE" | grep "GET /api/bigquery/" | grep -v "jdbc" | wc -l | tr -d ' ')
JDBC=$(grep "$TODAY" "$LOGFILE" | grep "GET /api/bigquery-jdbc/" | wc -l | tr -d ' ')
echo "  • Total: ${TOTAL:-0}"
echo "  • API (REST): ${API:-0}"
echo "  • JDBC: ${JDBC:-0}"
echo ""

# Performance metrics
echo "⚡ Performance:"

# API average
API_AVG=$(grep "$TODAY" "$LOGFILE" | grep "\[TIMING\]" | grep -v "JDBC" | grep "completed in" | \
  grep -oP '\d+(?= ms)' | awk '{sum+=$1; n++} END {if(n>0) printf "%.0f", sum/n; else print "0"}')
echo "  API Average Response Time: ${API_AVG}ms"

# JDBC average
JDBC_AVG=$(grep "$TODAY" "$LOGFILE" | grep "\[TIMING\]\[JDBC\]" | grep "completed in" | \
  grep -oP '\d+(?= ms)' | awk '{sum+=$1; n++} END {if(n>0) printf "%.0f", sum/n; else print "0"}')
echo "  JDBC Average Response Time: ${JDBC_AVG}ms"

# Fastest and slowest
FASTEST=$(grep "$TODAY" "$LOGFILE" | grep "completed in" | grep -oP '\d+(?= ms)' | sort -n | head -1)
SLOWEST=$(grep "$TODAY" "$LOGFILE" | grep "completed in" | grep -oP '\d+(?= ms)' | sort -n | tail -1)
echo "  Fastest: ${FASTEST:-0}ms"
echo "  Slowest: ${SLOWEST:-0}ms ($(echo "scale=1; ${SLOWEST:-0}/1000" | bc 2>/dev/null || echo "0")s)"
echo ""

# Datasets accessed
echo "📁 Most Accessed Datasets:"
grep "$TODAY" "$LOGFILE" | grep "datasets/.*/tables" | \
  grep -oP 'datasets/[^/]+' | sed 's/datasets\///' | sort | uniq -c | sort -rn | head -5 | \
  awk '{printf "  • %s: %d requests\n", $2, $1}' || echo "  • No dataset queries today"
echo ""

# Pagination analysis
echo "📄 Pagination (API Performance):"
PAGINATION_EVENTS=$(grep "$TODAY" "$LOGFILE" | grep "PAGINATION EVENT" | wc -l | tr -d ' ')
echo "  • Total pagination events: ${PAGINATION_EVENTS:-0}"
if [ "${PAGINATION_EVENTS:-0}" -gt 0 ]; then
  PAGINATION_AVG=$(grep "$TODAY" "$LOGFILE" | grep "PAGINATION EVENT" | \
    grep -oP '\(([0-9]+)ms\)' | grep -oP '[0-9]+' | \
    awk '{sum+=$1; n++} END {if(n>0) printf "%.0f", sum/n; else print "0"}')
  echo "  • Average delay per page: ${PAGINATION_AVG}ms"
fi
echo ""

# Errors
echo "❌ Errors:"
ERROR_COUNT=$(grep "$TODAY" "$LOGFILE" | grep -i "error\|exception\|failed" | wc -l | tr -d ' ')
echo "  • Total: ${ERROR_COUNT:-0}"
if [ "${ERROR_COUNT:-0}" -gt 0 ]; then
  echo "  • Recent errors:"
  grep "$TODAY" "$LOGFILE" | grep -i "error" | tail -3 | \
    sed 's/^/    /' | cut -c1-100
fi
echo ""

# Top 5 slowest operations
echo "🐌 Slowest Operations:"
grep "$TODAY" "$LOGFILE" | grep "completed in" | \
  grep -oP '\d+(?= ms)' | sort -rn | head -5 | \
  awk '{printf "  • %d ms (%.1f seconds)\n", $1, $1/1000}' || echo "  • No operations recorded"
echo ""

# Hourly distribution
echo "📈 Hourly Request Distribution:"
for hour in {08..18}; do
  COUNT=$(grep "$TODAY $hour:" "$LOGFILE" | grep "Received GET" | wc -l | tr -d ' ')
  if [ "${COUNT:-0}" -gt 0 ]; then
    BAR=$(printf '█%.0s' $(seq 1 $((COUNT / 5 + 1))))
    printf "  %02d:00 - %02d:59 : %3d requests %s\n" $hour $hour $COUNT "$BAR"
  fi
done
echo ""

echo "=========================================="
echo "Report generated: $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="






