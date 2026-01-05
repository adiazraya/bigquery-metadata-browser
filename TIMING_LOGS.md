# Timing Logs Documentation

This document explains the comprehensive timing logs implemented throughout the IncidenciaBQ application to track performance at every step.

## 🎯 Purpose

Timing logs help you:
- **Identify bottlenecks** in the application
- **Monitor performance** of BigQuery queries
- **Track API response times**
- **Debug slow operations**
- **Optimize critical paths**

---

## 📊 Logging Levels

### Backend (Java/Spring Boot)

The application uses **three logging markers** for easy filtering:

| Marker | Purpose | Example |
|--------|---------|---------|
| `[TIMING]` | General timing information | Connection, query execution |
| `##########` | Controller-level requests | HTTP endpoints |
| `==========` | Service-level operations | Business logic methods |

### Frontend (JavaScript)

Browser console logs use similar markers:

| Marker | Purpose |
|--------|---------|
| `[TIMING]` | All timing-related logs |
| `##########` | Main operations (loadDatasets, loadTables) |

---

## 🔍 Backend Timing Logs

### 1. Controller Layer (BigQueryController)

Tracks HTTP request/response cycle timing.

#### Example: List Datasets Endpoint

```
[TIMING] ########## Received GET /api/bigquery/datasets ##########
[TIMING] ########## GET /api/bigquery/datasets completed in 2847 ms (service: 2843 ms, serialization: 2 ms, datasets: 15) ##########
```

**Breakdown:**
- `Total time`: End-to-end request handling
- `Service time`: Time spent in service layer
- `Serialization time`: JSON conversion time
- `Count`: Number of items returned

#### Example: List Tables Endpoint

```
[TIMING] ########## Received GET /api/bigquery/datasets/my_dataset/tables ##########
[TIMING] ########## GET /api/bigquery/datasets/my_dataset/tables completed in 1523 ms (service: 1520 ms, serialization: 1 ms, tables: 42) ##########
```

#### Example: Test Connection

```
[TIMING] ########## Received GET /api/bigquery/test ##########
[TIMING] ########## GET /api/bigquery/test completed in 1234 ms (service: 1230 ms) ##########
```

---

### 2. Service Layer (BigQueryService)

Tracks detailed operation timing with step-by-step breakdown.

#### Example: List Datasets Operation

```
[TIMING] ========== Starting listDatasets operation ==========
[TIMING] Starting BigQuery connection establishment
[TIMING] JDBC Driver loaded in 45 ms
[TIMING] Connection established successfully in 1200 ms (total: 1245 ms)
[TIMING] Step 1/4: Connection acquired in 1245 ms
[TIMING] Step 2/4: Statement created in 12 ms
[TIMING] Step 3/4: Query executed in 1450 ms
[TIMING] Step 4/4: Processed 15 datasets in 23 ms
[TIMING] Resources cleanup took 8 ms
[TIMING] ========== listDatasets completed in 2738 ms (conn: 1245ms, stmt: 12ms, query: 1450ms, process: 23ms) ==========
```

**Step Breakdown:**
1. **Connection** - Establishing JDBC connection to BigQuery
2. **Statement** - Creating SQL statement object
3. **Query** - Executing the SQL query
4. **Process** - Parsing results into Java objects
5. **Cleanup** - Closing resources

#### Example: List Tables Operation

```
[TIMING] ========== Starting listTables operation for dataset: my_dataset ==========
[TIMING] Starting BigQuery connection establishment
[TIMING] JDBC Driver loaded in 32 ms
[TIMING] Connection established successfully in 980 ms (total: 1012 ms)
[TIMING] Step 1/4: Connection acquired in 1012 ms
[TIMING] Step 2/4: Statement created in 8 ms
[TIMING] Step 3/4: Query executed in 456 ms
[TIMING] Step 4/4: Processed 42 tables in 15 ms
[TIMING] Resources cleanup took 5 ms
[TIMING] ========== listTables for dataset 'my_dataset' completed in 1496 ms (conn: 1012ms, stmt: 8ms, query: 456ms, process: 15ms) ==========
```

#### Example: Connection Test

```
[TIMING] ========== Starting connection test ==========
[TIMING] Starting BigQuery connection establishment
[TIMING] JDBC Driver loaded in 38 ms
[TIMING] Connection established successfully in 1100 ms (total: 1138 ms)
[TIMING] ========== Connection test completed in 1150 ms (metadata: 12 ms) ==========
```

---

## 💻 Frontend Timing Logs

### Browser Console Logs

Open your browser's Developer Tools (F12) and check the Console tab.

#### Example: Load Datasets

```javascript
[TIMING] ########## Page loaded, initializing application ##########
[TIMING] Page initialization: 5ms
[TIMING] ########## Starting loadDatasets operation ##########
[TIMING] Step 1/4: UI preparation: 2ms
[TIMING] Fetching datasets from API...
[TIMING] Step 2/4: API call (fetch): 2847ms
[TIMING] Step 3/4: JSON parsing: 3ms
[TIMING] Step 4/4: UI rendering: 12ms { count: 15 }
[TIMING] ########## loadDatasets total: 2864ms {
  datasets: 15,
  breakdown: {
    apiCall: "2847ms",
    parsing: "3ms",
    rendering: "12ms"
  }
}
[TIMING] ########## loadDatasets completed: 2864ms ##########
```

**Breakdown:**
- **UI preparation**: Clearing previous data, showing loading
- **API call**: Network request time
- **JSON parsing**: Converting response to JavaScript objects
- **UI rendering**: Creating and adding DOM elements

#### Example: Load Tables

```javascript
[TIMING] ########## Dataset selected: my_dataset ##########
[TIMING] Step 1/2: UI selection update: 1ms
[TIMING] ########## Dataset selection completed: 1ms { datasetId: "my_dataset" }
[TIMING] ########## Starting loadTables for dataset: my_dataset ##########
[TIMING] Step 1/4: UI preparation: 1ms
[TIMING] Fetching tables for dataset 'my_dataset' from API...
[TIMING] Step 2/4: API call (fetch): 1523ms
[TIMING] Step 3/4: JSON parsing: 2ms
[TIMING] Step 4/4: UI rendering: 8ms { count: 42 }
[TIMING] ########## loadTables for 'my_dataset' total: 1534ms {
  dataset: "my_dataset",
  tables: 42,
  breakdown: {
    apiCall: "1523ms",
    parsing: "2ms",
    rendering: "8ms"
  }
}
[TIMING] ########## loadTables for 'my_dataset' completed: 1534ms ##########
```

---

## 📈 Performance Metrics

### Expected Timings (Local Development)

| Operation | Expected Time | Notes |
|-----------|--------------|-------|
| JDBC Driver Load | 30-50 ms | First time only |
| BigQuery Connection | 800-1500 ms | Network dependent |
| List Datasets Query | 1000-2000 ms | Depends on # of datasets |
| List Tables Query | 400-1000 ms | Depends on dataset size |
| JSON Serialization | < 5 ms | Usually negligible |
| UI Rendering | < 20 ms | Per operation |

### Expected Timings (Production/Heroku)

| Operation | Expected Time | Notes |
|-----------|--------------|-------|
| BigQuery Connection | 500-1200 ms | Better network |
| List Datasets Query | 800-1800 ms | Varies by region |
| List Tables Query | 300-800 ms | Varies by dataset |
| Total API Response | 1000-3000 ms | End-to-end |

---

## 🔍 How to View Timing Logs

### Backend Logs

#### Local Development

Logs appear in the terminal where you run the application:

```bash
./run.sh
# or
mvn spring-boot:run
```

Look for lines starting with `[TIMING]`.

#### Heroku Production

```bash
heroku logs --tail
```

Filter for timing logs:

```bash
heroku logs --tail | grep TIMING
```

### Frontend Logs

1. Open your browser
2. Press F12 (Developer Tools)
3. Go to the **Console** tab
4. Look for lines starting with `[TIMING]`

Filter in console:

```javascript
// In browser console, run:
console.defaultLog = console.log;
console.log = function(...args) {
    if (args[0]?.includes('[TIMING]')) {
        console.defaultLog.apply(console, args);
    }
};
```

---

## 📊 Analyzing Performance

### Identifying Bottlenecks

1. **Connection Time High (>2000ms)**
   - Check network connectivity
   - Verify service account permissions
   - Consider connection pooling

2. **Query Time High (>3000ms)**
   - BigQuery processing time
   - Large dataset with many schemas
   - Consider caching results

3. **Processing Time High (>100ms)**
   - Too many results
   - Complex object creation
   - Consider pagination

4. **Rendering Time High (>50ms)**
   - Too many DOM elements
   - Consider virtual scrolling
   - Optimize CSS selectors

### Complete Request Trace

For a typical dataset listing request:

```
Client Browser
   ↓ [User clicks refresh]
   
Frontend JavaScript
   [TIMING] loadDatasets started
   ↓ [HTTP Request] ~0-10ms
   
Network
   ↓ [Transmission] ~50-200ms
   
Controller
   [TIMING] GET /api/bigquery/datasets received
   ↓ [Method call] ~1ms
   
Service
   [TIMING] listDatasets started
   ↓ Connection: ~1200ms
   ↓ Statement: ~10ms
   ↓ Query: ~1500ms
   ↓ Process: ~20ms
   [TIMING] listDatasets completed: ~2730ms
   ↑
   
Controller
   [TIMING] Serialization: ~2ms
   [TIMING] Total: ~2735ms
   ↑
   
Network
   ↑ [Transmission] ~50-200ms
   
Frontend JavaScript
   [TIMING] Parsing: ~3ms
   [TIMING] Rendering: ~15ms
   [TIMING] Total: ~2800ms
   ↑
   
Client Browser
   [Results displayed]
```

---

## 🛠️ Customizing Timing Logs

### Enable/Disable Timing Logs

#### Backend

Edit `src/main/resources/application.properties`:

```properties
# Disable timing logs
logging.level.com.mercadolibre.incidenciabq=WARN

# Enable verbose timing logs
logging.level.com.mercadolibre.incidenciabq=DEBUG
```

#### Frontend

Edit `src/main/resources/static/app.js`:

```javascript
// Disable all timing logs
const ENABLE_TIMING_LOGS = false;

function logTiming(operation, startTime, additionalInfo = {}) {
    if (!ENABLE_TIMING_LOGS) return;
    const duration = Date.now() - startTime;
    console.log(`[TIMING] ${operation}: ${duration}ms`, additionalInfo);
    return duration;
}
```

### Export Timing Data

#### Backend to File

Add to `application.properties`:

```properties
# Log to file
logging.file.name=logs/timing.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

#### Frontend to Console

```javascript
// Collect timing data
const timingData = [];

function logTiming(operation, startTime, additionalInfo = {}) {
    const duration = Date.now() - startTime;
    timingData.push({ operation, duration, timestamp: new Date(), ...additionalInfo });
    console.log(`[TIMING] ${operation}: ${duration}ms`, additionalInfo);
    return duration;
}

// Export data
function exportTimingData() {
    console.table(timingData);
    // Or download as JSON
    const dataStr = JSON.stringify(timingData, null, 2);
    const dataBlob = new Blob([dataStr], {type: 'application/json'});
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'timing-data.json';
    link.click();
}
```

---

## 📚 Use Cases

### 1. Performance Testing

Run operations and collect timing data to establish baselines:

```bash
# Backend
heroku logs --tail | grep TIMING > performance-baseline.log

# Analyze
grep "completed in" performance-baseline.log | awk '{print $NF}' | sort -n
```

### 2. Monitoring Production

Set up alerts for slow operations:

```javascript
// Frontend
function logTiming(operation, startTime, additionalInfo = {}) {
    const duration = Date.now() - startTime;
    
    // Alert if too slow
    if (duration > 5000) {
        console.warn(`⚠️ SLOW OPERATION: ${operation} took ${duration}ms`);
        // Send to monitoring service
    }
    
    console.log(`[TIMING] ${operation}: ${duration}ms`, additionalInfo);
    return duration;
}
```

### 3. A/B Testing

Compare performance between different implementations:

```
Version A - listDatasets: 2847ms
Version B - listDatasets: 1823ms
Improvement: 36% faster
```

---

## 🎯 Best Practices

1. **Always include context** in timing logs (dataset name, count, etc.)
2. **Log at appropriate levels** (INFO for important, DEBUG for detailed)
3. **Include breakdown** of sub-operations
4. **Use consistent markers** for easy filtering
5. **Clean up resources** and log cleanup time
6. **Handle errors** and log time before failure

---

## 🆘 Troubleshooting

### No Timing Logs Appearing

**Backend:**
- Check logging level: `logging.level.com.mercadolibre=INFO`
- Ensure application is running
- Check console output

**Frontend:**
- Open Developer Tools (F12)
- Check Console tab
- Ensure no console filters active

### Timing Values Seem Wrong

- System clock issues
- Timezone differences
- Logging overhead (minimal, <1ms)

### Too Many Logs

Filter by marker:
```bash
# Backend
heroku logs --tail | grep "##########"

# Shows only endpoint-level logs
```

---

## 📊 Example Performance Report

```
=== Performance Report ===
Date: 2025-12-16
Environment: Local Development

Operation: List Datasets
  Executions: 10
  Average: 2,734 ms
  Min: 2,450 ms
  Max: 3,120 ms
  Breakdown:
    - Connection: 1,200 ms (44%)
    - Query: 1,450 ms (53%)
    - Processing: 84 ms (3%)

Operation: List Tables
  Executions: 25
  Average: 1,523 ms
  Min: 1,234 ms
  Max: 2,100 ms
  Breakdown:
    - Connection: 980 ms (64%)
    - Query: 456 ms (30%)
    - Processing: 87 ms (6%)
```

---

## 🎓 Summary

Timing logs provide complete visibility into application performance:

✅ **Backend**: Connection, query, processing, serialization
✅ **Frontend**: API calls, parsing, rendering
✅ **End-to-End**: Complete request traces
✅ **Actionable**: Easy to identify bottlenecks
✅ **Filterable**: Clear markers for each layer

Use these logs to monitor, optimize, and troubleshoot your application!






