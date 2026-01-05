#!/bin/bash

# Real-time BigQuery Application Monitor
# Shows live activity, response times, and errors

clear
echo "=========================================="
echo "  BigQuery Application Live Monitor"
echo "  Press Ctrl+C to exit"
echo "=========================================="
echo ""
echo "Starting monitoring..."
sleep 2

while true; do
  # Move cursor to beginning (refresh display)
  tput cup 4 0
  
  # Current timestamp
  echo "⏰ Last updated: $(date '+%Y-%m-%d %H:%M:%S')          "
  echo ""
  
  # Activity in last minute
  echo "📊 Activity (Last Minute):                "
  ONE_MIN_AGO=$(date -v-1M '+%H:%M' 2>/dev/null || date -d '1 minute ago' '+%H:%M' 2>/dev/null || date '+%H:%M')
  API_COUNT=$(grep "$ONE_MIN_AGO" logs/incidencia-bq.log 2>/dev/null | grep "GET /api/bigquery/" | wc -l | tr -d ' ')
  JDBC_COUNT=$(grep "$ONE_MIN_AGO" logs/incidencia-bq.log 2>/dev/null | grep "GET /api/bigquery-jdbc/" | wc -l | tr -d ' ')
  echo "  • API requests: ${API_COUNT:-0}                    "
  echo "  • JDBC requests: ${JDBC_COUNT:-0}                  "
  echo ""
  
  # Recent response times (last 5)
  echo "⚡ Recent Response Times:                  "
  tail -200 logs/incidencia-bq.log 2>/dev/null | grep "completed in" | tail -5 | while read line; do
    if [[ $line =~ ([0-9]+)\ ms ]]; then
      TIME="${BASH_REMATCH[1]}"
      SECONDS=$(echo "scale=1; $TIME/1000" | bc 2>/dev/null || echo "0")
      if [ "$TIME" -gt 10000 ]; then
        echo "  • ${TIME}ms (${SECONDS}s) ⚠️  VERY SLOW          "
      elif [ "$TIME" -gt 5000 ]; then
        echo "  • ${TIME}ms (${SECONDS}s) ⚠️  SLOW               "
      else
        echo "  • ${TIME}ms ✓                               "
      fi
    fi
  done
  # Fill remaining lines
  echo "                                             "
  echo "                                             "
  echo ""
  
  # Errors today
  echo "❌ Errors Today:                              "
  ERROR_COUNT=$(grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log 2>/dev/null | grep -i "error\|exception" | wc -l | tr -d ' ')
  echo "  • Total: ${ERROR_COUNT:-0}                               "
  echo ""
  
  # Latest requests
  echo "📝 Latest Requests:                           "
  tail -50 logs/incidencia-bq.log 2>/dev/null | grep "Received GET" | tail -3 | while read line; do
    if [[ $line =~ ([0-9]{2}:[0-9]{2}:[0-9]{2}).*GET\ (.+)\ ####### ]]; then
      TIME="${BASH_REMATCH[1]}"
      ENDPOINT="${BASH_REMATCH[2]}"
      echo "  • [$TIME] GET ${ENDPOINT:0:50}...     "
    fi
  done
  echo "                                             "
  echo "                                             "
  echo ""
  
  # Status indicator
  if curl -s http://localhost:8080/api/bigquery/datasets >/dev/null 2>&1; then
    echo "🟢 Application Status: RUNNING              "
  else
    echo "🔴 Application Status: STOPPED              "
  fi
  echo ""
  
  # Wait 5 seconds before refresh
  sleep 5
done






