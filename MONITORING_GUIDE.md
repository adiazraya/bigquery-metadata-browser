# Application Monitoring Guide

Since you cannot access Google Cloud Console audit logs due to permissions, here are all the ways you can monitor your application's BigQuery API usage.

---

## 1. Application Logs (Already Configured!)

### Location
```bash
/Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/logs/incidencia-bq.log
```

### Real-Time Monitoring

**Watch live activity:**
```bash
tail -f logs/incidencia-bq.log
```

**Watch only timing information:**
```bash
tail -f logs/incidencia-bq.log | grep TIMING
```

**Watch only API calls:**
```bash
tail -f logs/incidencia-bq.log | grep "API CALL\|SQL QUERY"
```

**Watch for errors:**
```bash
tail -f logs/incidencia-bq.log | grep -i error
```

**Watch pagination events (API slowness):**
```bash
tail -f logs/incidencia-bq.log | grep PAGINATION
```

---

## 2. Log Analysis Commands

### Today's Activity Summary

```bash
# Count total requests today
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | grep "Received GET" | wc -l

# Count API requests
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | grep "GET /api/bigquery/" | wc -l

# Count JDBC requests
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | grep "GET /api/bigquery-jdbc/" | wc -l
```

### Performance Analysis

```bash
# Average API response time for dataset listing
grep "listDatasets completed" logs/incidencia-bq.log | \
  grep -oP '\d+ ms' | grep -oP '^\d+' | \
  awk '{sum+=$1; count++} END {print "Average: " sum/count " ms"}'

# Find slowest queries
grep "completed in" logs/incidencia-bq.log | \
  grep -oP '\d+ ms' | sort -n | tail -10
```

### API vs JDBC Comparison

```bash
# Compare average response times
echo "=== API Average ==="
grep "\[TIMING\].*api.*completed" logs/incidencia-bq.log | \
  grep -oP '\d+ ms' | awk '{sum+=$1; n++} END {print sum/n " ms"}'

echo "=== JDBC Average ==="
grep "\[TIMING\]\[JDBC\].*completed" logs/incidencia-bq.log | \
  grep -oP '\d+ ms' | awk '{sum+=$1; n++} END {print sum/n " ms"}'
```

### Error Tracking

```bash
# Count errors in last hour
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | \
  grep -i "error\|exception\|failed" | wc -l

# Show recent errors
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | \
  grep -i "error\|exception" | tail -20
```

---

## 3. Monitoring Scripts

### Script 1: Real-Time Dashboard

Create a file `monitor_dashboard.sh`:

```bash
#!/bin/bash

# Clear screen and show header
clear
echo "=========================================="
echo "  BigQuery Application Monitor"
echo "  Press Ctrl+C to exit"
echo "=========================================="

while true; do
  # Move cursor to top
  tput cup 3 0
  
  # Get current timestamp
  echo "Last updated: $(date '+%Y-%m-%d %H:%M:%S')"
  echo ""
  
  # Requests in last minute
  echo "📊 Activity (Last Minute):"
  ONE_MIN_AGO=$(date -v-1M '+%H:%M' 2>/dev/null || date -d '1 minute ago' '+%H:%M')
  API_COUNT=$(grep "$ONE_MIN_AGO" logs/incidencia-bq.log | grep "GET /api/bigquery/" | wc -l | xargs)
  JDBC_COUNT=$(grep "$ONE_MIN_AGO" logs/incidencia-bq.log | grep "GET /api/bigquery-jdbc/" | wc -l | xargs)
  echo "  • API requests: $API_COUNT"
  echo "  • JDBC requests: $JDBC_COUNT"
  echo ""
  
  # Recent response times
  echo "⚡ Recent Response Times:"
  grep "completed in" logs/incidencia-bq.log | tail -5 | while read line; do
    if [[ $line =~ ([0-9]+)\ ms ]]; then
      TIME="${BASH_REMATCH[1]}"
      if [ "$TIME" -gt 10000 ]; then
        echo "  • ${TIME}ms ⚠️  SLOW"
      elif [ "$TIME" -gt 5000 ]; then
        echo "  • ${TIME}ms ⚠️"
      else
        echo "  • ${TIME}ms ✓"
      fi
    fi
  done
  echo ""
  
  # Recent errors
  echo "❌ Recent Errors:"
  ERROR_COUNT=$(grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | grep -i "error" | wc -l | xargs)
  echo "  • Total today: $ERROR_COUNT"
  echo ""
  
  # Latest activity
  echo "📝 Latest Activity:"
  tail -3 logs/incidencia-bq.log | grep "Received GET" | tail -1 | \
    sed 's/.*Received GET/  • GET/' | cut -c1-60
  echo ""
  
  # Wait 5 seconds before refresh
  sleep 5
done
```

### Script 2: Daily Summary Report

Create a file `daily_report.sh`:

```bash
#!/bin/bash

TODAY=$(date +%Y-%m-%d)
echo "=========================================="
echo "  Daily Activity Report: $TODAY"
echo "=========================================="
echo ""

# Total requests
echo "📊 Total Requests:"
TOTAL=$(grep "$TODAY" logs/incidencia-bq.log | grep "Received GET" | wc -l | xargs)
API=$(grep "$TODAY" logs/incidencia-bq.log | grep "GET /api/bigquery/" | wc -l | xargs)
JDBC=$(grep "$TODAY" logs/incidencia-bq.log | grep "GET /api/bigquery-jdbc/" | wc -l | xargs)
echo "  • Total: $TOTAL"
echo "  • API (REST): $API"
echo "  • JDBC: $JDBC"
echo ""

# Performance
echo "⚡ Performance:"
echo "  API Average Response Time:"
grep "$TODAY" logs/incidencia-bq.log | grep "api.*completed in" | \
  grep -oP '\d+ ms' | awk '{sum+=$1; n++} END {printf "    %.0f ms\n", sum/n}'

echo "  JDBC Average Response Time:"
grep "$TODAY" logs/incidencia-bq.log | grep "JDBC.*completed in" | \
  grep -oP '\d+ ms' | awk '{sum+=$1; n++} END {printf "    %.0f ms\n", sum/n}'
echo ""

# Datasets accessed
echo "📁 Datasets Accessed:"
grep "$TODAY" logs/incidencia-bq.log | grep "datasets/.*/tables" | \
  grep -oP 'datasets/[^/]+' | sort | uniq -c | sort -rn | head -5 | \
  awk '{printf "  • %s: %d requests\n", $2, $1}'
echo ""

# Errors
echo "❌ Errors:"
ERROR_COUNT=$(grep "$TODAY" logs/incidencia-bq.log | grep -i "error\|exception\|failed" | wc -l | xargs)
echo "  • Total: $ERROR_COUNT"
if [ $ERROR_COUNT -gt 0 ]; then
  echo "  • Details:"
  grep "$TODAY" logs/incidencia-bq.log | grep -i "error" | \
    tail -5 | sed 's/^/    /'
fi
echo ""

# Slowest queries
echo "🐌 Slowest Operations (Top 5):"
grep "$TODAY" logs/incidencia-bq.log | grep "completed in" | \
  grep -oP '\d+ ms' | sort -n | tail -5 | \
  awk '{printf "  • %d ms (%.1f seconds)\n", $1, $1/1000}'
echo ""

echo "=========================================="
echo "Report generated at: $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="
```

### Script 3: Pagination Analysis (API Performance)

Create a file `analyze_pagination.sh`:

```bash
#!/bin/bash

echo "=========================================="
echo "  Pagination Analysis (API Performance)"
echo "=========================================="
echo ""

# Get recent pagination stats
echo "📈 Recent API Calls with Pagination:"
echo ""

grep "PAGINATION EVENT" logs/incidencia-bq.log | tail -20 | while read line; do
  if [[ $line =~ PAGINATION\ EVENT\ #([0-9]+).*\(([0-9]+)ms\) ]]; then
    EVENT_NUM="${BASH_REMATCH[1]}"
    DELAY="${BASH_REMATCH[2]}"
    echo "  • Event #$EVENT_NUM: ${DELAY}ms delay"
  fi
done

echo ""
echo "📊 Pagination Statistics (last operation):"

# Get the most recent dataset query with pagination
LAST_DATASET=$(grep "listTables for dataset" logs/incidencia-bq.log | tail -1)

if [[ $LAST_DATASET =~ dataset\ \'([^\']+)\' ]]; then
  DATASET="${BASH_REMATCH[1]}"
  echo "  Dataset: $DATASET"
  
  if [[ $LAST_DATASET =~ completed\ in\ ([0-9]+)\ ms ]]; then
    TOTAL_TIME="${BASH_REMATCH[1]}"
    echo "  Total time: ${TOTAL_TIME}ms ($(echo "scale=1; $TOTAL_TIME/1000" | bc)s)"
  fi
  
  # Count pagination events for this dataset
  PAGINATION_EVENTS=$(grep -A 30000 "listTables for dataset '$DATASET'" logs/incidencia-bq.log | \
    grep -c "PAGINATION EVENT" | head -1)
  echo "  Pagination events: $PAGINATION_EVENTS"
  
  # Calculate average per page
  if [ $PAGINATION_EVENTS -gt 0 ]; then
    AVG_PER_PAGE=$(echo "scale=0; $TOTAL_TIME / $PAGINATION_EVENTS" | bc)
    echo "  Average per page: ${AVG_PER_PAGE}ms"
  fi
fi

echo ""
echo "=========================================="
```

---

## 4. Log Rotation & Management

### Prevent Log Files from Getting Too Large

Create `logrotate.conf`:

```
/Users/alberto.diazraya/Documents/Projects/MercadoLibre/IncicenciaBQ/logs/*.log {
    daily
    rotate 7
    compress
    missingok
    notifempty
    create 0644 alberto.diazraya staff
}
```

### Manual Log Archive

```bash
# Archive old logs
cd logs/
tar -czf archive-$(date +%Y%m%d).tar.gz *.log
mv archive-*.tar.gz archives/

# Clear current log (keep last 10000 lines)
tail -10000 incidencia-bq.log > incidencia-bq.log.tmp
mv incidencia-bq.log.tmp incidencia-bq.log
```

---

## 5. Custom Metrics Endpoint (Optional Enhancement)

You could add a `/metrics` endpoint to your application:

**Java code for `MetricsController.java`:**

```java
@RestController
@RequestMapping("/metrics")
public class MetricsController {
    
    @GetMapping
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Read last 1000 lines of log
        // Parse and aggregate metrics
        // Return as JSON
        
        return metrics;
    }
}
```

---

## 6. Third-Party Monitoring Tools

### Option A: Prometheus + Grafana
- Add Spring Boot Actuator
- Expose metrics endpoint
- Scrape with Prometheus
- Visualize with Grafana

### Option B: ELK Stack (Elasticsearch, Logstash, Kibana)
- Ship logs to Elasticsearch
- Create Kibana dashboards
- Set up alerts

### Option C: Datadog / New Relic
- Install agent
- Automatic monitoring
- Cloud-based dashboards

---

## 7. Simple Web-Based Monitor

Create an HTML page that polls your application:

**`monitor.html`:**

```html
<!DOCTYPE html>
<html>
<head>
    <title>BigQuery Monitor</title>
    <style>
        body { font-family: monospace; background: #1e1e1e; color: #d4d4d4; padding: 20px; }
        .metric { background: #2d2d2d; padding: 15px; margin: 10px 0; border-radius: 5px; }
        .success { color: #4ec9b0; }
        .warning { color: #dcdcaa; }
        .error { color: #f48771; }
    </style>
</head>
<body>
    <h1>🔍 BigQuery Application Monitor</h1>
    <div id="metrics"></div>
    
    <script>
        async function checkStatus() {
            const metrics = document.getElementById('metrics');
            
            try {
                // Test API
                const apiStart = Date.now();
                const apiRes = await fetch('/api/bigquery/datasets');
                const apiTime = Date.now() - apiStart;
                const apiData = await apiRes.json();
                
                // Test JDBC
                const jdbcStart = Date.now();
                const jdbcRes = await fetch('/api/bigquery-jdbc/datasets');
                const jdbcTime = Date.now() - jdbcStart;
                const jdbcData = await jdbcRes.json();
                
                metrics.innerHTML = `
                    <div class="metric">
                        <strong class="success">✓ API Status:</strong> OK<br>
                        Response time: ${apiTime}ms<br>
                        Datasets: ${apiData.length}
                    </div>
                    <div class="metric">
                        <strong class="success">✓ JDBC Status:</strong> OK<br>
                        Response time: ${jdbcTime}ms<br>
                        Datasets: ${jdbcData.length}
                    </div>
                    <div class="metric">
                        Last updated: ${new Date().toLocaleString()}
                    </div>
                `;
            } catch (e) {
                metrics.innerHTML = `<div class="metric"><strong class="error">✗ Error:</strong> ${e.message}</div>`;
            }
        }
        
        // Check immediately and every 10 seconds
        checkStatus();
        setInterval(checkStatus, 10000);
    </script>
</body>
</html>
```

---

## 8. Alerting

### Email Alerts for Errors

```bash
#!/bin/bash

# Check for errors in last 5 minutes
ERRORS=$(tail -5000 logs/incidencia-bq.log | grep -i "error\|exception" | wc -l)

if [ $ERRORS -gt 10 ]; then
  echo "⚠️  High error rate detected: $ERRORS errors" | \
    mail -s "BigQuery App Alert" your-email@example.com
fi
```

### Slack Notifications

```bash
#!/bin/bash

WEBHOOK_URL="https://hooks.slack.com/services/YOUR/WEBHOOK/URL"

# Send notification
curl -X POST $WEBHOOK_URL \
  -H 'Content-Type: application/json' \
  -d "{\"text\":\"BigQuery app status update: All systems operational\"}"
```

---

## 9. Quick Reference Commands

```bash
# Start monitoring (live view)
tail -f logs/incidencia-bq.log

# Check if app is running
curl -s http://localhost:8080/api/bigquery/datasets | jq 'length'

# Last 10 API calls
grep "Received GET" logs/incidencia-bq.log | tail -10

# Performance today
grep "$(date +%Y-%m-%d)" logs/incidencia-bq.log | grep "completed in"

# Count total requests
grep "Received GET" logs/incidencia-bq.log | wc -l

# Find slowest operations
grep "completed in" logs/incidencia-bq.log | grep -oP '\d+ ms' | sort -n | tail -10
```

---

## Summary

You have multiple options:

1. **Simplest**: Use `tail -f logs/incidencia-bq.log`
2. **Automated**: Run the monitoring scripts
3. **Comprehensive**: Set up ELK or Grafana
4. **Quick checks**: Use the provided commands

The logs contain ALL the information you need - you just need the right tools to view and analyze them!



