# 📊 Detailed Logging Examples

This document shows the **enhanced detailed logs** that track every step, API call, parameter, and processing detail when the backend handles requests.

---

## 🎯 What's Logged

The enhanced logging now shows:

✅ **Every API call** made to BigQuery
✅ **All parameters** passed to methods
✅ **Request details** (endpoints, methods, paths)
✅ **Processing steps** with intermediate data
✅ **Response details** (status, data counts)
✅ **Data transformation** details
✅ **Complete request flow** from HTTP to BigQuery and back

---

## 📝 Example 1: List Datasets Request

### **User Action:** Opens application, frontend requests datasets

### **Complete Log Output:**

```
2025-12-16 19:30:00.100 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [TIMING] ########## Received GET /api/bigquery/datasets ##########
2025-12-16 19:30:00.101 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ╔══════════════════════════════════════════════════════════
2025-12-16 19:30:00.101 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ HTTP REQUEST RECEIVED
2025-12-16 19:30:00.101 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ╠══════════════════════════════════════════════════════════
2025-12-16 19:30:00.102 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ Endpoint: GET /api/bigquery/datasets
2025-12-16 19:30:00.102 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ Purpose: Retrieve all datasets from BigQuery project
2025-12-16 19:30:00.102 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ Request Time: Tue Dec 16 19:30:00 CET 2025
2025-12-16 19:30:00.102 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ╠══════════════════════════════════════════════════════════
2025-12-16 19:30:00.103 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ CONTROLLER: Processing request
2025-12-16 19:30:00.103 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → No query parameters
2025-12-16 19:30:00.103 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → No path variables
2025-12-16 19:30:00.103 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → Calling service layer...
2025-12-16 19:30:00.104 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║
2025-12-16 19:30:00.104 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ CONTROLLER → SERVICE: Delegating to BigQueryService.listDatasets()
2025-12-16 19:30:00.104 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ ┌────────────────────────────────────────────────────
2025-12-16 19:30:00.104 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ │ Entering SERVICE LAYER...
2025-12-16 19:30:00.105 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ └────────────────────────────────────────────────────

2025-12-16 19:30:00.105 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [TIMING] ========== Starting listDatasets operation ==========
2025-12-16 19:30:00.106 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ╔══════════════════════════════════════════════════════════
2025-12-16 19:30:00.106 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║ BACKEND PROCESSING: List All Datasets
2025-12-16 19:30:00.106 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ╠══════════════════════════════════════════════════════════

2025-12-16 19:30:00.107 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║ Step 1/3: Acquiring BigQuery Client
2025-12-16 19:30:00.107 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [TIMING] Starting BigQuery client initialization
2025-12-16 19:30:00.108 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ┌─────────────────────────────────────────────────────
2025-12-16 19:30:00.108 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │ BACKEND: Initializing BigQuery Client
2025-12-16 19:30:00.108 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ├─────────────────────────────────────────────────────
2025-12-16 19:30:00.109 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │ Step 1: Loading service account credentials
2025-12-16 19:30:00.109 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   → Key Path: ./service-account-key.json
2025-12-16 19:30:00.109 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   → Project ID: ehc-alberto-diazraya-35c897
2025-12-16 19:30:00.110 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   → Service Account: datacloud2sa@ehc-alberto-diazraya-35c897.iam.gserviceaccount.com
2025-12-16 19:30:00.234 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [TIMING] Credentials loaded in 125 ms
2025-12-16 19:30:00.235 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   ✓ Credentials loaded successfully
2025-12-16 19:30:00.235 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │ Step 2: Building BigQuery client
2025-12-16 19:30:00.235 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   → Creating BigQueryOptions builder
2025-12-16 19:30:00.236 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   → Setting project: ehc-alberto-diazraya-35c897
2025-12-16 19:30:00.236 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   → Attaching credentials
2025-12-16 19:30:00.456 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   ✓ BigQuery client built successfully
2025-12-16 19:30:00.456 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] │   ✓ Ready to make API calls to BigQuery
2025-12-16 19:30:00.457 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [TIMING] BigQuery client initialized in 349 ms (credentials: 125ms, client: 221ms)
2025-12-16 19:30:00.457 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] └─────────────────────────────────────────────────────
2025-12-16 19:30:00.458 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [TIMING] Step 1/3: Client acquired in 351 ms

2025-12-16 19:30:00.459 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║ Step 2/3: Making API call to BigQuery
2025-12-16 19:30:00.459 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ┌─ API CALL DETAILS ─────────────────────────────
2025-12-16 19:30:00.460 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ API Method: BigQuery.listDatasets()
2025-12-16 19:30:00.460 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Target Project: ehc-alberto-diazraya-35c897
2025-12-16 19:30:00.460 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Endpoint: BigQuery Data API v2
2025-12-16 19:30:00.461 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Operation: LIST_DATASETS
2025-12-16 19:30:00.461 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Request Type: REST API Call
2025-12-16 19:30:00.461 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   └────────────────────────────────────────────────
2025-12-16 19:30:00.462 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   → Sending request to Google BigQuery API...

2025-12-16 19:30:01.234 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [TIMING] Step 2/3: Dataset list retrieved in 775 ms
2025-12-16 19:30:01.235 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ✓ API Response received from BigQuery
2025-12-16 19:30:01.235 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ✓ Status: SUCCESS

2025-12-16 19:30:01.236 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║ Step 3/3: Processing API Response
2025-12-16 19:30:01.236 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   → Iterating through BigQuery datasets...

2025-12-16 19:30:01.237 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ┌─ DATASET #1 ──────────────────────────────────
2025-12-16 19:30:01.237 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Processing dataset from BigQuery response
2025-12-16 19:30:01.238 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Raw Dataset ID: DatasetId{project=ehc-alberto-diazraya-35c897, dataset=analytics_data}
2025-12-16 19:30:01.238 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Dataset Name: analytics_data
2025-12-16 19:30:01.239 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ → Extracted Fields:
2025-12-16 19:30:01.239 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • datasetId: analytics_data
2025-12-16 19:30:01.239 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • projectId: ehc-alberto-diazraya-35c897
2025-12-16 19:30:01.240 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • friendlyName: Analytics Data
2025-12-16 19:30:01.240 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • description: Production analytics dataset
2025-12-16 19:30:01.240 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • location: US
2025-12-16 19:30:01.241 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • creationTime: 1701234567890
2025-12-16 19:30:01.241 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ ✓ Dataset object created and added to list
2025-12-16 19:30:01.241 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   └────────────────────────────────────────────────

2025-12-16 19:30:01.242 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ┌─ DATASET #2 ──────────────────────────────────
2025-12-16 19:30:01.242 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Processing dataset from BigQuery response
2025-12-16 19:30:01.243 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Raw Dataset ID: DatasetId{project=ehc-alberto-diazraya-35c897, dataset=logs_data}
2025-12-16 19:30:01.243 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Dataset Name: logs_data
2025-12-16 19:30:01.244 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ → Extracted Fields:
2025-12-16 19:30:01.244 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • datasetId: logs_data
2025-12-16 19:30:01.244 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • projectId: ehc-alberto-diazraya-35c897
2025-12-16 19:30:01.245 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • friendlyName: (none)
2025-12-16 19:30:01.245 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • description: (none)
2025-12-16 19:30:01.245 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • location: US
2025-12-16 19:30:01.246 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • creationTime: 1701234567891
2025-12-16 19:30:01.246 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ ✓ Dataset object created and added to list
2025-12-16 19:30:01.246 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   └────────────────────────────────────────────────

2025-12-16 19:30:01.250 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [TIMING] Step 3/3: Processed 2 datasets in 14 ms

2025-12-16 19:30:01.251 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║
2025-12-16 19:30:01.251 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║ Processing Summary:
2025-12-16 19:30:01.251 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   • Total datasets found: 2
2025-12-16 19:30:01.252 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   • All datasets converted to model objects
2025-12-16 19:30:01.252 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   • Ready to serialize to JSON for response
2025-12-16 19:30:01.253 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ╠══════════════════════════════════════════════════════════
2025-12-16 19:30:01.253 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║ OPERATION COMPLETE
2025-12-16 19:30:01.253 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ✓ Client initialization: 351 ms
2025-12-16 19:30:01.254 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ✓ BigQuery API call: 775 ms
2025-12-16 19:30:01.254 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ✓ Response processing: 14 ms
2025-12-16 19:30:01.254 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ✓ Total operation time: 1140 ms
2025-12-16 19:30:01.255 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ✓ Datasets returned: 2
2025-12-16 19:30:01.255 [http-nio-8080-exec-1] INFO  c.m.i.service.BigQueryService - [DETAIL] ╚══════════════════════════════════════════════════════════

2025-12-16 19:30:01.256 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ ┌────────────────────────────────────────────────────
2025-12-16 19:30:01.256 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ │ Returned from SERVICE LAYER
2025-12-16 19:30:01.257 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ └────────────────────────────────────────────────────
2025-12-16 19:30:01.257 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║
2025-12-16 19:30:01.257 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ SERVICE → CONTROLLER: Received response
2025-12-16 19:30:01.258 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ Service call completed in 1153 ms
2025-12-16 19:30:01.258 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ Datasets received: 2
2025-12-16 19:30:01.259 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → Dataset IDs: [analytics_data, logs_data]
2025-12-16 19:30:01.259 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║
2025-12-16 19:30:01.260 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ CONTROLLER: Preparing HTTP response
2025-12-16 19:30:01.260 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → Converting 2 datasets to JSON
2025-12-16 19:30:01.260 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → Creating ResponseEntity with HTTP 200 OK
2025-12-16 19:30:01.262 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ JSON serialization completed in 2 ms
2025-12-16 19:30:01.262 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ Response entity created
2025-12-16 19:30:01.263 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ╠══════════════════════════════════════════════════════════
2025-12-16 19:30:01.263 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ HTTP RESPONSE READY
2025-12-16 19:30:01.263 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ Status Code: 200 OK
2025-12-16 19:30:01.264 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ Content-Type: application/json
2025-12-16 19:30:01.264 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ Body: List<Dataset> with 2 items
2025-12-16 19:30:01.264 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   ✓ Total request time: 1164 ms
2025-12-16 19:30:01.265 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ Breakdown:
2025-12-16 19:30:01.265 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   • Service layer: 1153 ms (99%)
2025-12-16 19:30:01.265 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   • JSON serialization: 2 ms (0%)
2025-12-16 19:30:01.266 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [DETAIL] ╚══════════════════════════════════════════════════════════

2025-12-16 19:30:01.266 [http-nio-8080-exec-1] INFO  c.m.i.controller.BigQueryController - [TIMING] ########## GET /api/bigquery/datasets completed in 1164 ms (service: 1153 ms, serialization: 2 ms, datasets: 2) ##########
```

---

## 📝 Example 2: List Tables Request

### **User Action:** Clicks on "analytics_data" dataset

### **Key Log Sections:**

```
2025-12-16 19:30:05.100 [http-nio-8080-exec-2] INFO  c.m.i.controller.BigQueryController - [TIMING] ########## Received GET /api/bigquery/datasets/analytics_data/tables ##########
2025-12-16 19:30:05.101 [http-nio-8080-exec-2] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║ Request Parameters:
2025-12-16 19:30:05.102 [http-nio-8080-exec-2] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → PATH VARIABLE: datasetId = 'analytics_data'
2025-12-16 19:30:05.102 [http-nio-8080-exec-2] INFO  c.m.i.controller.BigQueryController - [DETAIL] ║   → Full path: /api/bigquery/datasets/analytics_data/tables

... [Service layer processing] ...

2025-12-16 19:30:05.500 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ┌─ API CALL DETAILS ─────────────────────────────
2025-12-16 19:30:05.501 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ API Method: BigQuery.listTables()
2025-12-16 19:30:05.501 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Target Project: ehc-alberto-diazraya-35c897
2025-12-16 19:30:05.502 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Target Dataset: analytics_data
2025-12-16 19:30:05.502 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Full Dataset Path: ehc-alberto-diazraya-35c897.analytics_data
2025-12-16 19:30:05.503 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Operation: LIST_TABLES

... [API call to BigQuery] ...

2025-12-16 19:30:05.890 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   ┌─ TABLE #1 ─────────────────────────────────────
2025-12-16 19:30:05.891 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ Table Name: user_events
2025-12-16 19:30:05.892 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ → Extracted Fields:
2025-12-16 19:30:05.892 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • tableId: user_events
2025-12-16 19:30:05.893 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • datasetId: analytics_data
2025-12-16 19:30:05.893 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • type: TABLE
2025-12-16 19:30:05.894 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │   • numRows: 1234567
2025-12-16 19:30:05.894 [http-nio-8080-exec-2] INFO  c.m.i.service.BigQueryService - [DETAIL] ║   │ → Full table path: ehc-alberto-diazraya-35c897.analytics_data.user_events
```

---

## 🎯 What Information You Get

### **HTTP Layer (Controller)**
- ✅ Endpoint called
- ✅ Request timestamp
- ✅ Parameters (path variables, query params)
- ✅ Validation results
- ✅ Response status code
- ✅ Response content type
- ✅ Time breakdown (service vs serialization)

### **Service Layer (BigQueryService)**
- ✅ Operation name
- ✅ Input parameters
- ✅ Client initialization details
- ✅ API method called
- ✅ Target project/dataset
- ✅ Operation type (LIST_DATASETS, LIST_TABLES)
- ✅ Request type (REST API Call)
- ✅ API response status
- ✅ Each item processed with all fields
- ✅ Total items processed
- ✅ Complete time breakdown

### **BigQuery Client**
- ✅ Credentials loading
- ✅ Service account details
- ✅ Project ID
- ✅ Client building steps
- ✅ API endpoint
- ✅ Request/response details

### **Data Processing**
- ✅ Each dataset/table processed individually
- ✅ All extracted fields shown
- ✅ Data transformation steps
- ✅ Object creation confirmation
- ✅ List building progress

---

## 📊 Log Markers

| Marker | Meaning | Where Used |
|--------|---------|------------|
| `[TIMING]` | Performance timing | All layers |
| `[DETAIL]` | Detailed step information | All layers |
| `##########` | HTTP request/response boundary | Controller |
| `╔══════╗` | Operation start/end | Service/Controller |
| `┌────┐` | Sub-operation | Service |
| `→` | Action or flow | All |
| `✓` | Success | All |
| `✗` | Failure | Error cases |

---

## 🔍 How to Read the Logs

### Flow Pattern:
```
1. HTTP Request arrives (Controller)
   ↓
2. Controller delegates to Service
   ↓
3. Service initializes BigQuery client
   ↓
4. Service makes API call to BigQuery
   ↓
5. BigQuery processes and returns data
   ↓
6. Service processes response (each item)
   ↓
7. Service returns to Controller
   ↓
8. Controller serializes to JSON
   ↓
9. HTTP Response sent to client
```

### Each step shows:
- **What** is happening
- **Where** it's happening (layer)
- **When** it happened (timestamp)
- **How long** it took (timing)
- **What data** was involved (parameters, results)

---

## 💡 Benefits

✅ **Complete Visibility** - See every step of request processing
✅ **Easy Debugging** - Identify exactly where issues occur
✅ **Performance Analysis** - Know which step is slow
✅ **API Transparency** - See actual BigQuery API calls
✅ **Data Flow** - Track data transformations
✅ **Production Ready** - Monitor live requests

---

## 🚀 Try It Yourself

Run the application and watch the logs:

```bash
./run.sh
```

Then open your browser and use the application. You'll see detailed logs for every operation!

**The logs tell you the complete story of what's happening in the backend!** 📊






